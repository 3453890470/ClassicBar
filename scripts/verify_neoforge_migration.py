from __future__ import annotations

import re
import subprocess
import sys
from pathlib import Path, PurePosixPath


ROOT = Path(__file__).resolve().parent.parent


class CheckContext:
    def __init__(self) -> None:
        self.failures: list[str] = []
        self.notes: list[str] = []

    def fail(self, message: str) -> None:
        self.failures.append(message)

    def note(self, message: str) -> None:
        self.notes.append(message)


def read_text(path: Path, ctx: CheckContext) -> str:
    if not path.exists():
        ctx.fail(f"Missing required file: {path.relative_to(ROOT)}")
        return ""
    return path.read_text(encoding="utf-8")


def require_contains(text: str, needle: str, label: str, ctx: CheckContext) -> None:
    if needle not in text:
        ctx.fail(f"{label}: expected to contain {needle!r}")


def require_absent(text: str, needle: str, label: str, ctx: CheckContext) -> None:
    if needle in text:
        ctx.fail(f"{label}: should not contain {needle!r}")


def require_regex(text: str, pattern: str, label: str, ctx: CheckContext) -> None:
    if re.search(pattern, text, flags=re.MULTILINE) is None:
        ctx.fail(f"{label}: expected pattern {pattern!r}")


def extract_source_set_excludes(build_gradle: str) -> list[str]:
    return re.findall(r"exclude\s+'([^']+)'", build_gradle)


def path_matches_any(path: str, patterns: list[str]) -> bool:
    posix_path = PurePosixPath(path)
    return any(posix_path.match(pattern) for pattern in patterns)


def collect_java_matches(root: Path, needle: str) -> list[str]:
    matches: list[str] = []
    for file in root.rglob("*.java"):
        text = file.read_text(encoding="utf-8")
        if needle in text:
            matches.append(file.relative_to(ROOT).as_posix())
    return sorted(matches)


def require_absent_in_active_sources(java_matches: list[str], allowlist: list[str], label: str, ctx: CheckContext) -> None:
    active = [path for path in java_matches if not path_matches_any(path.replace("src/main/java/", ""), allowlist)]
    if active:
        ctx.fail(f"{label}: active compiled sources still contain forbidden references: {', '.join(active)}")
    elif java_matches:
        ctx.note(f"{label}: remaining references only exist in quarantined sources: {', '.join(java_matches)}")


def require_line_value(text: str, key: str, expected_literal: str, label: str, ctx: CheckContext) -> None:
    pattern = rf'{re.escape(key)}\s*=\s*{expected_literal}'
    require_regex(text, pattern, label, ctx)


def check_java_21(ctx: CheckContext) -> None:
    try:
        result = subprocess.run(
            ["java", "-version"],
            capture_output=True,
            text=True,
            encoding="utf-8",
            errors="replace",
            check=False,
        )
    except FileNotFoundError:
        ctx.fail("Java runtime not found on PATH")
        return

    output = (result.stderr or "") + (result.stdout or "")
    if result.returncode != 0:
        ctx.fail(f"java -version failed with code {result.returncode}: {output.strip()}")
        return

    match = re.search(r'version\s+"(?P<major>\d+)', output)
    if not match:
        ctx.fail(f"Could not parse Java version from output: {output.strip()}")
        return

    major = int(match.group("major"))
    if major < 21:
        ctx.fail(f"Java 21+ required, found Java {major}: {output.strip()}")
    else:
        ctx.note(f"Java runtime OK: {output.strip().splitlines()[0]}")


def main() -> int:
    ctx = CheckContext()

    build_gradle = read_text(ROOT / "build.gradle", ctx)
    gradle_properties = read_text(ROOT / "gradle.properties", ctx)
    settings_gradle = read_text(ROOT / "settings.gradle", ctx)
    wrapper_properties = read_text(ROOT / "gradle" / "wrapper" / "gradle-wrapper.properties", ctx)
    java21_script = read_text(ROOT / "scripts" / "with-java21.ps1", ctx)
    classic_bar = read_text(ROOT / "src" / "main" / "java" / "tfar" / "classicbar" / "ClassicBar.java", ctx)
    event_handler = read_text(ROOT / "src" / "main" / "java" / "tfar" / "classicbar" / "EventHandler.java", ctx)
    classic_bars_config = read_text(ROOT / "src" / "main" / "java" / "tfar" / "classicbar" / "config" / "ClassicBarsConfig.java", ctx)
    bar_overlay_impl = read_text(ROOT / "src" / "main" / "java" / "tfar" / "classicbar" / "impl" / "BarOverlayImpl.java", ctx)

    metadata_template = ROOT / "src" / "main" / "templates" / "META-INF" / "neoforge.mods.toml"
    metadata_resource = ROOT / "src" / "main" / "resources" / "META-INF" / "neoforge.mods.toml"
    old_mods_toml = ROOT / "src" / "main" / "resources" / "META-INF" / "mods.toml"

    active_metadata_path = metadata_template if metadata_template.exists() else metadata_resource
    metadata_text = read_text(active_metadata_path, ctx) if active_metadata_path.exists() else ""
    if not active_metadata_path.exists():
        ctx.fail("Missing NeoForge metadata file: expected src/main/templates/META-INF/neoforge.mods.toml or src/main/resources/META-INF/neoforge.mods.toml")

    if old_mods_toml.exists():
        ctx.fail("Legacy Forge metadata still present at src/main/resources/META-INF/mods.toml")

    check_java_21(ctx)

    quarantine_allowlist = extract_source_set_excludes(build_gradle)
    if not quarantine_allowlist:
        ctx.fail("build.gradle: expected sourceSets quarantine excludes for Phase 1")

    forge_refs = collect_java_matches(ROOT / "src" / "main" / "java", "net.minecraftforge")
    require_absent_in_active_sources(forge_refs, quarantine_allowlist, "Forge API quarantine", ctx)

    require_contains(build_gradle, "id 'net.neoforged.moddev' version '2.0.141'", "build.gradle", ctx)
    require_absent(build_gradle, "net.minecraftforge.gradle", "build.gradle", ctx)
    require_absent(build_gradle, "org.parchmentmc.librarian.forgegradle", "build.gradle", ctx)
    require_regex(build_gradle, r"JavaLanguageVersion\.of\(21\)|VERSION_21", "build.gradle Java toolchain", ctx)
    require_contains(build_gradle, "neoForge {", "build.gradle", ctx)
    require_contains(build_gradle, "version = project.neo_version", "build.gradle neoForge block", ctx)
    require_contains(build_gradle, "mods {", "build.gradle neoForge block", ctx)

    require_regex(gradle_properties, r"(?m)^minecraft_version=1\.21\.1$", "gradle.properties", ctx)
    require_regex(gradle_properties, r"(?m)^neo_version=21\.1\.228$", "gradle.properties", ctx)
    require_absent(gradle_properties, "forge_version=", "gradle.properties", ctx)

    require_contains(settings_gradle, "org.gradle.toolchains.foojay-resolver-convention", "settings.gradle", ctx)
    require_contains(settings_gradle, "https://maven.aliyun.com/repository/gradle-plugin", "settings.gradle pluginManagement", ctx)
    require_contains(settings_gradle, "https://maven.aliyun.com/repository/public", "settings.gradle repositories", ctx)
    require_contains(settings_gradle, "https://maven.neoforged.net/releases", "settings.gradle NeoForged mirror", ctx)
    require_contains(settings_gradle, "dependencyResolutionManagement", "settings.gradle", ctx)
    require_contains(wrapper_properties, "gradle-8.8-bin.zip", "gradle-wrapper.properties", ctx)
    require_contains(wrapper_properties, "networkTimeout=60000", "gradle-wrapper.properties", ctx)

    require_contains(gradle_properties, "-Dhttps.protocols=TLSv1.2,TLSv1.3", "gradle.properties TLS settings", ctx)
    require_contains(gradle_properties, "-Djavax.net.ssl.trustStoreType=Windows-ROOT", "gradle.properties trust store", ctx)
    require_contains(gradle_properties, "systemProp.http.connectionTimeout=60000", "gradle.properties timeouts", ctx)
    require_absent(gradle_properties, "systemProp.http.proxyHost=", "gradle.properties proxy host", ctx)
    require_absent(gradle_properties, "systemProp.http.proxyPort=", "gradle.properties proxy port", ctx)
    require_absent(gradle_properties, "systemProp.https.proxyHost=", "gradle.properties https proxy host", ctx)
    require_absent(gradle_properties, "systemProp.https.proxyPort=", "gradle.properties https proxy port", ctx)

    require_contains(java21_script, "[string]$JavaHome", "with-java21.ps1 JavaHome parameter", ctx)
    require_contains(java21_script, "$env:JAVA21_HOME", "with-java21.ps1 JAVA21_HOME fallback", ctx)
    require_contains(java21_script, "$env:JAVA_HOME", "with-java21.ps1 JAVA_HOME fallback", ctx)
    require_contains(java21_script, "Get-Command java", "with-java21.ps1 PATH java fallback", ctx)
    require_contains(java21_script, "[string]$ProxyHost", "with-java21.ps1 ProxyHost parameter", ctx)
    require_contains(java21_script, "[int]$ProxyPort", "with-java21.ps1 ProxyPort parameter", ctx)
    require_contains(java21_script, "$env:CLASSICBAR_PROXY_HOST", "with-java21.ps1 proxy env host", ctx)
    require_contains(java21_script, "$env:CLASSICBAR_PROXY_PORT", "with-java21.ps1 proxy env port", ctx)
    require_absent(java21_script, "zulu21.34.19-ca-jdk21.0.3-win_x64", "with-java21.ps1 hardcoded Java path", ctx)
    require_absent(java21_script, "127.0.0.1", "with-java21.ps1 hardcoded proxy", ctx)

    if metadata_text:
        require_contains(metadata_text, 'modId="classicbar"', str(active_metadata_path.relative_to(ROOT)), ctx)
        require_contains(metadata_text, 'modId="neoforge"', str(active_metadata_path.relative_to(ROOT)), ctx)
        require_contains(metadata_text, 'modId="minecraft"', str(active_metadata_path.relative_to(ROOT)), ctx)

    require_contains(classic_bar, "net.neoforged", "ClassicBar.java", ctx)
    require_absent(classic_bar, "net.minecraftforge", "ClassicBar.java", ctx)
    require_contains(classic_bar, "ClassicBarsConfig.CLIENT_SPEC", "ClassicBar.java", ctx)

    require_contains(event_handler, "RegisterGuiLayersEvent", "EventHandler.java", ctx)
    require_contains(event_handler, "LayeredDraw.Layer", "EventHandler.java", ctx)
    require_contains(event_handler, "VanillaGuiLayers.PLAYER_HEALTH", "EventHandler.java", ctx)
    require_contains(event_handler, "registerBelow(", "EventHandler.java", ctx)
    require_absent(event_handler, "RegisterGuiOverlaysEvent", "EventHandler.java", ctx)
    require_absent(event_handler, "IGuiOverlay", "EventHandler.java", ctx)
    require_absent(event_handler, "ForgeGui", "EventHandler.java", ctx)
    require_absent(event_handler, "VanillaGuiOverlay", "EventHandler.java", ctx)

    require_regex(classic_bars_config, r'displayIcons\s*=\s*builder(?:.|\n)*?define\("display_icons",\s*(true|false)\)', "ClassicBarsConfig display_icons config", ctx)
    require_regex(classic_bars_config, r'displayToughnessBar\s*=\s*builder.*define\("display_toughness_bar",\s*true\)', "ClassicBarsConfig display_toughness_bar key", ctx)
    if classic_bars_config.count('define("display_icons"') != 1:
        ctx.fail("ClassicBarsConfig: display_icons key should only be defined once")

    require_contains(bar_overlay_impl, "Color.reset();", "BarOverlayImpl shader reset", ctx)

    if ctx.failures:
        print("NeoForge migration verification: RED")
        for failure in ctx.failures:
            print(f"- {failure}")
        if ctx.notes:
            print("\nNotes:")
            for note in ctx.notes:
                print(f"- {note}")
        return 1

    print("NeoForge migration verification: GREEN")
    for note in ctx.notes:
        print(f"- {note}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
