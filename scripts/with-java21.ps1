[CmdletBinding(PositionalBinding = $false)]
param(
    [string]$JavaHome,
    [string]$ProxyHost,
    [int]$ProxyPort,
    [Parameter(Mandatory = $true, ValueFromRemainingArguments = $true)]
    [string[]]$Command
)

function Get-JavaVersionMajor {
    param([Parameter(Mandatory = $true)][string]$JavaExecutable)

    $output = & $JavaExecutable -version 2>&1
    if ($LASTEXITCODE -ne 0) {
        throw "Failed to execute '$JavaExecutable -version'."
    }

    $match = [regex]::Match(($output -join "`n"), 'version\s+"(?<major>\d+)')
    if (-not $match.Success) {
        throw "Could not parse Java version from '$JavaExecutable -version'."
    }

    return [int]$match.Groups['major'].Value
}

function Resolve-JavaExecutable {
    param([string]$RequestedJavaHome)

    if ($RequestedJavaHome) {
        return (Join-Path $RequestedJavaHome 'bin\java.exe')
    }

    if ($env:JAVA21_HOME) {
        return (Join-Path $env:JAVA21_HOME 'bin\java.exe')
    }

    if ($env:JAVA_HOME) {
        return (Join-Path $env:JAVA_HOME 'bin\java.exe')
    }

    $javaCommand = Get-Command java -ErrorAction SilentlyContinue
    if ($javaCommand) {
        return $javaCommand.Source
    }

    throw 'Java 21 not found. Pass -JavaHome, or set JAVA21_HOME, or set JAVA_HOME, or make Java 21 available on PATH.'
}

$javaExecutable = Resolve-JavaExecutable -RequestedJavaHome $JavaHome
if (-not (Test-Path $javaExecutable)) {
    throw "Resolved Java executable does not exist: $javaExecutable"
}

$javaMajor = Get-JavaVersionMajor -JavaExecutable $javaExecutable
if ($javaMajor -lt 21) {
    throw "Java 21+ required, but resolved '$javaExecutable' is Java $javaMajor. Pass -JavaHome with a Java 21 installation."
}

$resolvedJavaHome = Split-Path (Split-Path $javaExecutable -Parent) -Parent
$env:JAVA_HOME = $resolvedJavaHome
$env:PATH = "$(Split-Path $javaExecutable -Parent);$env:PATH"

$effectiveProxyHost = if ($ProxyHost) { $ProxyHost } elseif ($env:CLASSICBAR_PROXY_HOST) { $env:CLASSICBAR_PROXY_HOST } else { $null }
$effectiveProxyPort = if ($PSBoundParameters.ContainsKey('ProxyPort')) { $ProxyPort } elseif ($env:CLASSICBAR_PROXY_PORT) { [int]$env:CLASSICBAR_PROXY_PORT } else { $null }

if (($effectiveProxyHost -and -not $effectiveProxyPort) -or ($effectiveProxyPort -and -not $effectiveProxyHost)) {
    throw 'Proxy configuration requires both host and port. Pass -ProxyHost and -ProxyPort together, or set CLASSICBAR_PROXY_HOST and CLASSICBAR_PROXY_PORT together.'
}

$jvmArgs = @(
    '-Dhttps.protocols=TLSv1.2,TLSv1.3',
    '-Djavax.net.ssl.trustStoreType=Windows-ROOT',
    '-Dhttp.connectionTimeout=60000',
    '-Dhttp.socketTimeout=60000'
)

if ($effectiveProxyHost -and $effectiveProxyPort) {
    $jvmArgs += @(
        "-Dhttp.proxyHost=$effectiveProxyHost",
        "-Dhttp.proxyPort=$effectiveProxyPort",
        "-Dhttps.proxyHost=$effectiveProxyHost",
        "-Dhttps.proxyPort=$effectiveProxyPort"
    )
}

$env:JAVA_OPTS = (($env:JAVA_OPTS, ($jvmArgs -join ' ')) | Where-Object { $_ }) -join ' '
$env:GRADLE_OPTS = (($env:GRADLE_OPTS, ($jvmArgs -join ' ')) | Where-Object { $_ }) -join ' '

if ($Command.Length -gt 1) {
    & $Command[0] @($Command[1..($Command.Length - 1)])
} else {
    & $Command[0]
}

if ($null -ne $LASTEXITCODE) {
    exit $LASTEXITCODE
}
