# ClassicBar NeoForge 1.21.1 迁移任务计划

## 元数据

| 字段 | 值 |
|---|---|
| plan_id | classicbar_neoforge_1_21_1_migration |
| workflow_mode | standard |
| repo_path | E:\\GitHub\\ClassicBar |
| draft_path | E:\\GitHub\\ClassicBar\\plans\\.draft_plan_classicbar_neoforge_1_21_1_migration.md |
| plan_path | E:\\GitHub\\ClassicBar\\plans\\plan_classicbar_neoforge_1_21_1_migration.md |
| base_branch | 1.20.1-fixed |
| current_branch | migration/neoforge-1.21.1 |
| base_head | 44560169d41920202e76ac4c544ff0fd7f3af087 |
| latest_user_decision | 2026-04-28：纯客户端 mod 为当前事实源；此前“恢复可选服务端”方向已被用户重新决策覆盖 |
| status | 执行中；TASK-01 纯客户端收敛已通过审查；TASK-02 状态栏小图标独立资源化已通过复审；TASK-03 NeoForge Config 重构已通过最终复审；TASK-04 审查反馈最小修复已完成并复验，待复审 |
| next_hop | 返回 TASK-04 复审 |
| next_step | 复核 fail-fast jar 扫描、稳定用户文案、strict third-party dependency 禁止、pure client network verifier 说明与 TASK-05 README 支持矩阵硬阻塞；继续不恢复任何 third-party compat / network/server sync / README 收口 |

## 目标

按用户指定顺序完成 ClassicBar 迁移：
1. 迁移到 Minecraft 1.21.1 NeoForge；
2. 状态栏小图标改为 ClassicBar 自有独立资源文件，不再使用原版或第三方 mod 图标资源；独立资源要求仅针对小图标，允许复用 ClassicBar 自有状态栏条贴图（bar 背景/填充贴图），不要求重做或拆分 bar 贴图；
3. 使用 NeoForge `ModConfigSpec` / TOML / `ModConfig.Type.CLIENT` 重构配置文件，并纳入 per-bar 启用模式、per-bar 颜色覆盖层、mod support 开关与配置本地化 key。

## 范围

### 做
- 接管当前 dirty `migration/neoforge-1.21.1` 分支已有迁移成果并收敛。
- 保留并验证 NeoForge 1.21.1 / Java 21 / NeoForge 21.1.x 构建主线。
- 当前安装与支持策略为纯客户端 HUD 模组：dedicated server 不作为安装目标，不承诺 dedicated server 增强同步。
- 迁移 active vanilla HUD 到 NeoForge GUI layer。
- 保证 active vanilla 小图标均使用 `assets/classicbar/textures/gui/icons/*.png` 独立 PNG；该独立资源要求仅针对状态栏小图标。
- 允许复用 ClassicBar 自有状态栏条贴图（如 `assets/classicbar/textures/gui/health.png`）；不要求重做或拆分 bar 背景/填充贴图。
- 使用 NeoForge `ModConfigSpec` / TOML / `ModConfig.Type.CLIENT` 作为纯客户端主配置源，不恢复 network/server sync。
- 明确隔离未恢复的 third-party compat。
- 记录验证证据与未完成风险。

### 不做 / 延后
- 不在首轮恢复 Feathers 兼容：当前未找到 1.21.1 NeoForge 可用构件。
- 不把 Tough As Nails / Vampirism / ParCool 作为主线阻塞项；它们有 1.21.1 NeoForge 构件但需要后续逐项实测。
- 不直接重置或清理当前 dirty worktree。
- 不在未运行验证命令时声明通过。

## 当前事实基线

- 当前分支已是 `migration/neoforge-1.21.1`，HEAD 与 `1.20.1-fixed` 同为 `44560169d41920202e76ac4c544ff0fd7f3af087`。
- 明确基线：接管当前 dirty `migration/neoforge-1.21.1`，保护已有改动，不回滚，不清理未跟踪文件。
- 工作区存在大量未提交/未跟踪迁移改动，包含 NeoForge 构建、GUI layer、payload network、ModConfigSpec、独立 icons 等方向。
- 当前 active 主线只覆盖 vanilla HUD；`compat/**` 与 `impl/overlays/mod/**` 被构建排除或视作 quarantine。
- `META-INF/neoforge.mods.toml`、`assets/classicbar/textures/gui/icons/` 等交付文件必须纳入版本控制。
- 旧 JSON 配置读取/写入已被移除；旧配置迁移策略本轮按破坏性迁移文档化，后续可选只读导入器。
- 本计划修订阶段未运行 Gradle 构建或游戏 smoke，旧计划中的 GREEN 记录只作为历史背景，不作为本轮通过证据。
- 最新用户决策（2026-04-28）：纯客户端 mod 为当前事实源；此前“恢复可选服务端 / server optional sync”方向不再执行。
- 最新任务状态补充（2026-04-29）：用户已确认继续执行 TASK-04；TASK-03 NeoForge Config 重构已通过最终复审。TASK-04 只做 compat quarantine 与恢复策略守卫，不恢复任何 third-party compat，不恢复 network/server sync，也不进入 README/发布收口。

## 全局禁止项

- 禁止 `git reset --hard`。
- 禁止 `git clean`。
- 禁止强制 checkout 覆盖当前工作区。
- 禁止删除未跟踪 `plans/`、`scripts/`、`src/main/java/tfar/classicbar/client/`、`network` payload 文件、`META-INF/neoforge.mods.toml`、`assets/classicbar/textures/gui/icons/`。
- 禁止未验证即声明完成、可发布或构建通过。
- 禁止在 TASK-03 恢复 network/server sync、server config sync 或 dedicated server 支持承诺。
- 禁止在 TASK-03 恢复 `compat/**` 编译或声明 third-party compat 已恢复。
- 禁止在 TASK-03 进入 README/发布收口；README、支持矩阵与发布说明仍留到 TASK-05。

## 全局验证命令

> 本计划写入阶段不运行这些命令。只有实际运行并记录输出后，才能标记通过。

```powershell
gradlew.bat --version
gradlew.bat clean compileJava
gradlew.bat test
gradlew.bat build
```

可选运行验证：

```powershell
gradlew.bat runClient
```

`gradlew.bat runServer` 仅可作为“误装 server 不支持/不保证”的可选风险检查；不作为功能目标或完成门槛。

在 PowerShell 中也可以写作 `./gradlew.bat build` 或 `.\\gradlew.bat build`。

## 任务清单

### TASK-01：迁移到 Minecraft 1.21.1 NeoForge

**范围**
- 接管当前 dirty migration 分支。
- 执行前先记录 dirty worktree 接管快照，再继续后续迁移收敛。
- 收敛构建到 Minecraft `1.21.1`、NeoForge `21.1.x`、Java 21。
- 使用 NeoForge/ModDevGradle 或项目实际可构建的 NeoForge Gradle 方案。
- 使用 `META-INF/neoforge.mods.toml` 替代 Forge `mods.toml`。
- 将 active vanilla HUD 注册与渲染迁移到 `RegisterGuiLayersEvent` / `LayeredDraw.Layer` / `VanillaGuiLayers`。
- 纯客户端策略为当前事实源：`@Mod` 入口可以保持 client-only，metadata/dependencies side 应与 client-only 策略一致。
- dedicated server 不作为安装目标，不恢复 server optional sync 闭合目标，不承诺 dedicated server 增强同步。
- network/server tick 相关逻辑若保留，只能是单人/集成服务器内部增强，或从 pure client 主线隔离；第三方 network 暂不恢复。
- 维持 third-party compat quarantine，不让未迁移 Forge API 阻塞 active 编译。

**文件/目录**
- `build.gradle`
- `settings.gradle`
- `gradle.properties`
- `gradle/wrapper/gradle-wrapper.properties`
- `src/main/resources/META-INF/neoforge.mods.toml`
- `src/main/resources/pack.mcmeta`
- `src/main/java/tfar/classicbar/ClassicBar.java`
- `src/main/java/tfar/classicbar/EventHandler.java`
- `src/main/java/tfar/classicbar/client/**`
- `src/main/java/tfar/classicbar/api/**`
- `src/main/java/tfar/classicbar/impl/**`
- `src/main/java/tfar/classicbar/network/**`

**依赖**
- 用户已确认接管当前 dirty 分支。
- 不依赖 third-party compat 完成。

**DoD**
- 构建配置明确 `minecraft_version=1.21.1`、NeoForge `21.1.x`、Java 21。
- `neoforge.mods.toml` 存在并被纳入版本控制，metadata/dependencies side 与 client-only 策略一致。
- `@Mod` 入口可以是 client-only；dedicated server 不作为安装目标，dedicated server 装本 mod 不作为支持场景。
- 不承诺 dedicated server 增强同步，不以 server optional sync 作为 TASK-01 闭合目标。
- active source 不再依赖 Forge 1.20.1 API：`FMLJavaModLoadingContext`、`ModLoadingContext`、`RegisterGuiOverlaysEvent`、`IGuiOverlay`、`ForgeGui`、`VanillaGuiOverlay`、`SimpleChannel`、`NetworkRegistry`、`ForgeConfigSpec`。
- HUD 通过 NeoForge GUI layer 注册。
- active vanilla overlays 至少覆盖 health、armor、absorption、food、armor_toughness、mount health、air。
- network/server tick 相关逻辑若保留，只能是单人/集成服务器内部增强，或已从 pure client 主线隔离；不得把 dedicated server 安装/增强同步写成功能承诺。
- `gradlew.bat clean compileJava` 实际通过并记录输出后，TASK 才可标记静态编译通过。
- `gradlew.bat runClient` 是 TASK-01 纯客户端主线必跑验证；未实际运行并记录输出前，不得声明游戏内 HUD 已验证。
- `gradlew.bat runServer` 只作为“误装 server 不支持/不保证”的可选风险检查，不作为功能目标、支持承诺或完成门槛。

**验证命令**
```powershell
gradlew.bat --version
gradlew.bat clean compileJava
gradlew.bat test
gradlew.bat build
gradlew.bat runClient
```

可选风险检查：`gradlew.bat runServer` 仅用于观察误装 dedicated server 时的失败/风险表现，不作为支持场景验证。

**风险与回退**
- Gradle/NeoForge 版本解析失败：锁定明确 `21.1.x` 版本并调整 wrapper/plugin。
- GUI layer 顺序与原版 HUD 冲突：在 `RegisterGuiLayersEvent` 与 `RenderGuiLayerEvent.Pre` 之间选择最小冲突策略。
- network/server tick 逻辑与 pure client 主线冲突：移除、隔离或降级为单人/集成服务器内部增强，不恢复 dedicated server optional sync。
- dirty worktree 文件丢失风险：执行前后必须记录 `git status --short --branch` 与 `git diff --stat`。

**执行记录 / TASK-01（2026-04-27）**
- 状态：静态验证记录保留，但因服务端可选安装语义未闭合，不能作为完整 GREEN 推进到 TASK-02。
- 接管快照：`git status --short --branch` 显示当前分支为 `migration/neoforge-1.21.1`，dirty worktree 覆盖 build、NeoForge metadata、active HUD、network、icons、plans 等文件；`git diff --stat` 统计 `28 files changed, 475 insertions(+), 987 deletions(-)`，另有未跟踪 `plans/`、`scripts/`、`src/main/java/tfar/classicbar/client/`、`src/main/java/tfar/classicbar/network/ClassicBarNetwork.java`、`src/main/java/tfar/classicbar/network/VanillaFoodDataPayload.java`、`src/main/resources/META-INF/neoforge.mods.toml`、`src/main/resources/assets/classicbar/textures/gui/icons/`。
- `gradlew.bat --version`：PowerShell 直接执行 `gradlew.bat --version` 无法识别本地批处理，改用 `.\\gradlew.bat --version` 成功；输出为 Gradle `8.8`，launcher JVM `17.0.11`。补充 `./gradlew.bat -q javaToolchains` 确认本机可用 `Amazon Corretto JDK 21.0.6+7-LTS`，且 `build.gradle` 已声明 `java.toolchain.languageVersion = JavaLanguageVersion.of(21)`。
- `./gradlew.bat clean compileJava`：fresh 执行成功，`BUILD SUCCESSFUL`；编译阶段仅出现 `ClassicBarsConfig.java` 使用/覆盖已弃用 API 的 note，未阻塞任务。
- 可选验证：`./gradlew.bat test` => `compileTestJava NO-SOURCE`、`test NO-SOURCE`、`BUILD SUCCESSFUL`；`./gradlew.bat build` => `jarJar NO-SOURCE`、`BUILD SUCCESSFUL`。
- 范围事实：active source fresh 编译通过；对 `src/main/java`（排除 `compat/**` 与 `impl/overlays/mod/**`）检索未发现 `FMLJavaModLoadingContext`、`ModLoadingContext`、`RegisterGuiOverlaysEvent`、`IGuiOverlay`、`ForgeGui`、`VanillaGuiOverlay`、`SimpleChannel`、`NetworkRegistry`、`ForgeConfigSpec` 等旧 Forge API 引用；`sourceSets.main` 继续排除 quarantine 目录。
- 未验证项：未运行 `runClient` / `runServer`，未做游戏内 HUD 顺序/渲染 smoke，未恢复任何 third-party compat。
- 残留风险：Gradle launcher 当前仍显示 JVM 17；虽然 toolchain 21 已声明且本机可用 JDK 21 已被 Gradle 识别，但本记录未额外抓取 `compileJava` 的 toolchain 选择日志。若后续需要更强证据，可补跑 `./gradlew.bat clean compileJava --info` 或在 Java 21 launcher 下复验。

#### TASK-01 补充任务：server optional sync 架构闭合（已取消 / 不再执行）

**取消原因**
- 用户于 2026-04-28 重新决策为 `纯客户端 mod`，该决策是当前事实源。
- 因此，本补充任务不再作为 TASK-01 的执行目标、DoD、验证门槛或 TASK-02 前置条件。
- 下方记录仅保留为历史背景，不代表当前计划继续承诺 dedicated server 可选安装、server optional sync、payload 协商或 dedicated server 增强同步。

**当前替代下一步**
- 执行 TASK-01 纯客户端收敛修复与验证：让入口、metadata/dependencies side、network/server tick 保留策略与纯客户端 HUD 模组目标一致，并以 `runClient` 作为必跑验证。

**补充目标**
- 恢复“客户端必装、服务端可选安装以增强同步”的语义闭合，避免当前 `@Mod(dist = Dist.CLIENT)` 与 server tick food sync / server->client payload 逻辑并存导致的 dedicated server 语义冲突。
- 在不恢复 third-party compat 主线的前提下，仅闭合 vanilla food sync 所需的 common/server-safe 入口、client-only 注册边界与 payload 协商策略。

**补充范围**
- 拆分 common/server-safe 入口与 client-only GUI 注册，避免 mod 根入口携带客户端专属装载语义。
- 去掉或调整 `@Mod(dist = Dist.CLIENT)`，确保 dedicated server 安装时 mod 可加载，不因入口声明直接跳过装载。
- 所有 client-only 类、GUI layer 注册、HUD 渲染更新逻辑仅在 `Dist.CLIENT` 安全边界内加载。
- payload 注册迁移或保持在 common / mod event bus 可达路径；其中 client-side 更新 handler 必须安全隔离，避免 dedicated server 触发类加载。
- server tick sync 必须在 dedicated server 可运行，且不依赖客户端类或 client-only event bus。
- 对未安装客户端或 payload 不匹配的客户端，不能盲发同步包导致崩溃或断连；必须补充协商/守卫，或在实现上明确改为双方都需安装的依赖策略并记录取舍。

**补充 DoD**
- dedicated server 安装 ClassicBar 时，mod 能完成 server-safe 装载。
- client-only GUI / HUD / 渲染类不在 dedicated server 侧触发类加载。
- payload 注册、分发与 client 更新处理具备明确的 side-safe 边界。
- server tick sync 在 integrated server 与 dedicated server 语义一致可运行。
- 针对未安装客户端或 payload 不匹配客户端，已有可执行的守卫/协商/依赖策略，不存在“服务端盲发导致崩溃或断连”的已知裸风险。
- `runServer` / `runClient` / 混装 smoke 一并构成 TASK-01 闭合验证；在这些验证未执行前，不得把 TASK-01 重新标记为完整 GREEN。

**补充验证命令**
```powershell
gradlew.bat runServer
gradlew.bat runClient
```

补充说明：
- 需增加 dedicated server 安装 smoke。
- 需增加 client 安装连接带/不带服务端模组的混装 smoke，验证 payload 协商或守卫策略。
- 本补充任务完成前，TASK-02 暂缓，不进入小图标独立资源化闭合验证。

**执行记录 / TASK-01 补充任务（2026-04-27，历史记录；2026-04-28 已取消）**
- 状态：CANCELLED；因用户重新决策为 `纯客户端 mod`，server optional sync 补充任务已取消/不再执行。以下 2026-04-27 记录仅为历史背景；不再要求继续补跑混装 smoke，也不得据此承诺 dedicated server 支持或增强同步。
- 历史状态：PARTIAL；common/server-safe 入口、client-only 初始化边界、payload 注册与 dedicated server 装载 smoke 已闭合，但“已装客户端连接未装服务端”与“已装服务端连接未装客户端/协议不匹配”的双向混装联机 smoke 仍未实跑，未回标完整 GREEN。
- 接管快照：执行前记录 `git status --short --branch` => `## migration/neoforge-1.21.1`，dirty worktree 覆盖 build、HUD、config、network、metadata、icons、plans 等；`git diff --stat` => `28 files changed, 475 insertions(+), 987 deletions(-)`，另有未跟踪 `plans/`、`scripts/`、`src/main/java/tfar/classicbar/client/`、`src/main/java/tfar/classicbar/network/ClassicBarNetwork.java`、`src/main/java/tfar/classicbar/network/VanillaFoodDataPayload.java`、`src/main/resources/META-INF/neoforge.mods.toml`、`assets/classicbar/textures/gui/icons/`。
- 架构策略：
  - 去掉 `ClassicBar.java` 上的 `@Mod(dist = Dist.CLIENT)`，改为 common 入口，仅保留 config 注册、payload 注册、server tick sync / logout cleanup 注册。
  - 新增 `tfar.classicbar.client.ClassicBarClient` 作为 `@Mod(value = ClassicBar.MODID, dist = Dist.CLIENT)` 的 client-only 入口，承接 `EventHandler.bootstrap()`、config reload listener、GUI layer 注册与 vanilla layer disable。
  - `ClassicBarNetwork.registerPayloadHandlers` 继续挂在 common mod bus；dedicated server 可注册 payload type/codec，client 更新逻辑仍走 1.21.1 的 common handler 路径。
  - optional payload 守卫采用 `net.neoforged.neoforge.network.registration.NetworkRegistry.hasChannel(player.connection, VanillaFoodDataPayload.TYPE.id())` 后再 `PacketDistributor.sendToPlayer(...)`；这是 NeoForge 源码存在但带 `@ApiStatus.Internal` 的 internal API，当前未找到 1.21.1 官方文档化 public guard，可作为安全替代而非编造不存在的公开 API。
  - `META-INF/neoforge.mods.toml` 依赖 side 由 `CLIENT` 调整为 `BOTH`，允许 dedicated server 装载 common 入口。
- 关键实现结果：
  - dedicated server 可加载 mod 根入口，不再因入口 dist 限制直接跳过 ClassicBar。
  - common 入口不再直接引用 `EventHandler` / GUI layer 注册路径，避免 server 侧通过根入口触发 client-only 初始化。
  - server tick sync 仍由 `SyncHandler` 在 NeoForge event bus 注册并运行。
  - payload 发送前已有连接能力守卫，避免对未协商该 payload 的连接盲发已知 clientbound 包。
- 验证命令与结果：
  - `.\gradlew.bat clean compileJava` => `BUILD SUCCESSFUL`；保留 `ClassicBarsConfig.java` 使用/覆盖 deprecated API note。
  - `.\gradlew.bat test` => `test NO-SOURCE`，`BUILD SUCCESSFUL`。
  - `.\gradlew.bat build` => `jarJar NO-SOURCE`，`BUILD SUCCESSFUL`。
  - 并行首轮 `clean compileJava` / `test` / `build` 中，`test` 与 `build` 曾因 `createMinecraftArtifacts` 复制 `build\moddev\artifacts\neoforge-21.1.228*.jar.tmp` 时遇到 Windows 文件占用失败；顺序重跑 `clean compileJava -> test -> build` 后均 fresh 通过，判断为并行争用导致的验证噪音而非源码回归。
  - `.\gradlew.bat runServer --stacktrace`：成功进入 dedicated server 启动链，日志确认 mod 列表含 `Classic Bar 0.0NONE (classicbar)`，随后完成世界初始化并出现 `Done (...s)! For help, type "help"`；命令仅因长时间驻留被外部 timeout 终止。启动早期出现 `server.properties` 缺失的首次生成日志，但未阻断服务端继续启动。
  - `.\gradlew.bat runClient`：成功进入 client dev run，日志出现 `Registering Vanilla Overlays` 与 `Syncing Classic Bar Configs`，随后 integrated server 成功启动；命令因 GUI 持续运行被外部 timeout 终止。当前仅确认启动链通过，未人工检查 HUD 视觉与联机收发。
- 未验证项：
  - 未执行“客户端未装 / 服务端已装”的真实远程连接 smoke，尚未抓到 `hasChannel` 守卫在实际缺失客户端模组连接上的运行时证据。
  - 未执行“客户端已装 / 服务端未装”的远程连接 smoke，尚未确认客户端侧在无服务端 payload 时仅回退 vanilla 行为。
  - 未做 dedicated server + client 混装的实际饥饿/饱和度同步观测。
- 残留风险：
  - `NetworkRegistry.hasChannel(...)` 属 NeoForge internal API；后续升级 NeoForge 版本时需复查包名、签名与语义。
  - 当前 1.21.1 未找到官方公开的 per-player optional payload send guard；若后续拒绝 internal API，则需改为显式握手表或调整计划。
  - `runServer` / `runClient` 仅完成启动 smoke，尚不足以证明 HUD 顺序、渲染效果与联机同步语义全部正确。

**执行记录 / TASK-01 纯客户端收敛修复与验证（2026-04-28）**
- 状态：GREEN；按最新用户决策收敛为纯客户端 HUD 模组，dedicated server 不再作为安装目标，也不再承诺 server optional sync / payload 协商 / dedicated server 增强同步。
- 接管快照：执行前记录 `git status --short --branch --untracked-files=all` => `## migration/neoforge-1.21.1`，dirty worktree 覆盖 build、HUD、config、icons、plans、network 等；`git diff --stat` => `28 files changed, 408 insertions(+), 1040 deletions(-)`，另有未跟踪 `plans/`、`scripts/`、`src/main/java/tfar/classicbar/client/`、`src/main/java/tfar/classicbar/network/ClassicBarNetwork.java`、`src/main/java/tfar/classicbar/network/VanillaFoodDataPayload.java`、`src/main/resources/META-INF/neoforge.mods.toml`、`assets/classicbar/textures/gui/icons/`。
- RED 证明：新增 `src/test/java/tfar/classicbar/PureClientConvergenceTest.java`，先断言 `ClassicBar` 必须是 client-only 入口、common root 不得再注册 payload/server tick、`neoforge.mods.toml` 依赖 side 必须为 `CLIENT`，并要求 `ClassicBarNetwork.java` / `SyncHandler.java` / `VanillaFoodDataPayload.java` 已从 active source 删除。执行 `./gradlew.bat test --tests tfar.classicbar.PureClientConvergenceTest` 首次失败，3 个断言全部命中旧 optional server sync 残留。
- 关键实现：
  - `ClassicBar.java` 改为单 `@Mod(value = ClassicBar.MODID, dist = Dist.CLIENT)` client-only 入口，直接调用 `ClassicBarClient.init(modBus)`；去除 reflective bootstrap 与 common root server sync 注册。
  - 删除 `src/main/java/tfar/classicbar/network/ClassicBarNetwork.java`、`SyncHandler.java`、`VanillaFoodDataPayload.java`，不再保留 vanilla food payload / server tick optional sync 主线。
  - `META-INF/neoforge.mods.toml` 中 NeoForge 与 Minecraft dependency `side` 均改回 `CLIENT`，与纯客户端安装策略一致。
  - `build.gradle` 增加 JUnit 5 测试依赖与 `useJUnitPlatform()`，并向测试传递 `classicbar.projectDir`，让纯客户端收敛约束可被自动回归验证。
- GREEN 证明：
  - `./gradlew.bat test --tests tfar.classicbar.PureClientConvergenceTest` => `BUILD SUCCESSFUL`。
  - `./gradlew.bat clean compileJava` => `BUILD SUCCESSFUL`；保留 `ClassicBarsConfig.java` 使用/覆盖 deprecated API note。
  - `./gradlew.bat test` => `BUILD SUCCESSFUL`。
  - `./gradlew.bat build` => `BUILD SUCCESSFUL`。
  - `./gradlew.bat runClient --stacktrace` => `BUILD SUCCESSFUL`；日志确认使用 `OpenJDK 21.0.6` 启动，mod 列表出现 `Classic Bar 1.21.1-4 (classicbar)`，并出现 `Registering Vanilla Overlays`、`Syncing Classic Bar Configs`，随后 integrated server 启动并有玩家 `Dev` 登录/退出记录，说明纯客户端 HUD 主线可进入实际客户端运行链。
- 静态收敛检查：
  - 对 active source 目录 `src/main/java/tfar/classicbar/{ClassicBar.java,client/**,api/**,config/**,impl/overlays/vanilla/**,util/**}` 检索 `ClassicBarNetwork`、`SyncHandler`、`VanillaFoodDataPayload`、`hasChannel`、`RegisterPayloadHandlersEvent`、`PlayerTickEvent`、`PlayerLoggedOutEvent` 均无命中。
  - `presentOnServer` 仅残留在被 quarantine 的 `impl/overlays/mod/Thirst.java`，不属于当前 active vanilla HUD 编译主线。
- 未验证项：
  - 未运行 `runServer`；在纯客户端策略下，dedicated server 安装/运行不是功能验收目标。
  - 未进行人工 HUD 视觉比对或远程联机 smoke；本轮仅证明客户端启动链、HUD 初始化关键日志与集成服务器路径通过。
- 结论：TASK-01 已按“纯客户端 mod”为事实源完成收敛，可在用户确认后再进入 TASK-02；本记录不恢复、不承诺 dedicated server optional sync。

### TASK-02：状态栏小图标独立资源化

**状态说明**
- 已恢复推进并完成本轮验证；当前实现仅要求状态栏小图标独立 PNG，继续允许复用 ClassicBar 自有 bar 背景/填充贴图。

**范围**
- active vanilla 小图标全部使用 ClassicBar 自有独立 PNG 文件。
- 资源路径统一为 `assets/classicbar/textures/gui/icons/<name>.png`。
- 独立资源要求仅针对状态栏小图标；允许继续复用 ClassicBar 自有状态栏条贴图（如 `assets/classicbar/textures/gui/health.png`），不要求重做或拆分 bar 背景/填充贴图。
- 不使用 `minecraft:` 资源、不使用第三方 mod 图标、不使用旧合图硬编码 UV 作为小图标来源。
- compat overlays 若未恢复则保持 quarantine；恢复时必须同样迁移到独立 icon。

**文件/目录**
- `src/main/resources/assets/classicbar/textures/gui/icons/*.png`
- `src/main/java/tfar/classicbar/resources/BarIcons.java`
- `src/main/java/tfar/classicbar/util/ModUtils.java`
- `src/main/java/tfar/classicbar/impl/BarOverlayImpl.java`
- `src/main/java/tfar/classicbar/impl/overlays/vanilla/**`
- `src/main/java/tfar/classicbar/config/ClassicBarsConfig.java`

**依赖**
- TASK-01 active HUD 能编译或至少迁移结构稳定。

**DoD**
- active bar 默认 icon 全部指向 `classicbar:textures/gui/icons/<name>.png` 或 ClassicBar fallback。
- active icon 文件存在并纳入版本控制：`health.png`、`armor.png`、`absorption.png`、`food.png`、`armor_toughness.png`、`mount_health.png`、`air.png`、`fallback.png`。
- 每个 active 图标都必须存在可替换的独立 PNG 占位文件；文件名稳定、路径稳定，用户可直接替换资源文件；`fallback.png` 也必须作为可替换占位文件存在。
- 如保留 compat fallback，则 `blood.png`、`thirst.png`、`stamina.png`、`feathers.png` 也必须提供可替换的独立 PNG 占位文件并纳入版本控制；否则移除对应引用。
- active overlay 的 icon 绘制统一走独立 PNG，不再使用 vanilla/third-party atlas 或硬编码 icon UV。
- 状态栏条贴图（bar 背景/填充贴图）允许继续复用 ClassicBar 自有资源，不要求为 TASK-02 额外拆分或重做。
- icon config validator 只接受 `classicbar:` namespace、`textures/gui/icons/` 前缀和 `.png` 后缀。
- 绘制 icon 前后正确 reset shader color/alpha，避免被 bar 色彩污染。

**验证命令**
```powershell
gradlew.bat clean compileJava
gradlew.bat build
```
可选：运行自定义静态检查脚本（若存在）验证 icon 引用与文件存在性。

**风险与回退**
- 未跟踪 PNG 被误删：执行前列出 icons 目录并确保纳入版本控制。
- 图标视觉不清晰或透明度错误：保留 fallback 并通过 runClient smoke 检查。
- compat overlay 仍引用旧 atlas：未恢复 compat 时明确 quarantine，恢复时单独处理。

**执行记录 / TASK-02（2026-04-28）**
- 状态：GREEN；active vanilla 小图标保持 `classicbar:textures/gui/icons/*.png` 独立资源，`fallback.png` 与 compat fallback 占位 PNG 均存在；bar 背景/填充继续复用 `assets/classicbar/textures/gui/health.png`，未做拆分。
- 接管快照：执行前记录 `git status --short --branch --untracked-files=all` => `## migration/neoforge-1.21.1`，dirty worktree 覆盖 build、HUD、config、icons、plans、tests 等；`git diff --stat` => `28 files changed, 383 insertions(+), 1073 deletions(-)`，另有未跟踪 `plans/`、`scripts/`、`src/main/java/tfar/classicbar/client/`、`src/main/java/tfar/classicbar/resources/BarIcons.java`、`src/main/resources/META-INF/neoforge.mods.toml`、`src/main/resources/assets/classicbar/textures/gui/icons/`、`src/test/java/tfar/classicbar/`。
- RED/覆盖说明：当前 dirty 分支已自带 active icon PNG 与 `BarIcons`/`ClassicBarsConfig` 引用收敛，因此交付本体在执行前已基本满足 TASK-02。首轮 `python .\scripts\verify_independent_icons.py` 之所以 RED，是因为验证脚本仍引用旧路径 `src/main/java/tfar/classicbar/client/BarIcons.java`，属于“验证缺口”而非“实现缺口”。为避免伪 TDD，本轮补充 `src/test/java/tfar/classicbar/IndependentIconResourcesTest.java`，并加入 `verifierWouldFailIfAStablePlaceholderWasTemporarilyBroken`：它在临时目录复制 icons 后删除 `fallback.png`，明确证明验证逻辑会在占位文件被破坏时报错。
- 关键收敛：
  - 更新 `scripts/verify_independent_icons.py`，改为检查 `src/main/java/tfar/classicbar/resources/BarIcons.java`，并验证：active vanilla 默认 icon 常量、`classicbar:textures/gui/icons/*.png` 稳定路径、PNG 非空且 signature 正确、compat fallback 引用若保留则对应 PNG 必须存在。
  - 静态检查 active vanilla `renderIcon(...)` 仅允许 `ModUtils.drawStandaloneIcon(...)`，不允许在 icon 绘制路径中继续使用原版/第三方 atlas、`minecraft:` 资源、`graphics.blit(...)` 或旧硬编码 UV；同时保留 `build.gradle` 对 `compat/**` 与 `impl/overlays/mod/**` 的 quarantine 排除。
  - 未改 active bar 条贴图主线；`BarOverlayImpl.ICON_BAR` 继续指向 `classicbar:textures/gui/health.png`，符合“仅小图标独立资源化”的用户边界。
- 占位 PNG 清单：
  - active vanilla：`health.png`、`armor.png`、`absorption.png`、`food.png`、`armor_toughness.png`、`mount_health.png`、`air.png`、`fallback.png`
  - compat fallback 占位：`blood.png`、`thirst.png`、`stamina.png`、`feathers.png`
- 验证命令与结果：
  - `python .\scripts\verify_independent_icons.py`：首次 RED，命中旧 `client/BarIcons.java` 路径；修正脚本后复跑 => `Independent icon verification: GREEN`。
  - `./gradlew.bat test --tests tfar.classicbar.IndependentIconResourcesTest` => `BUILD SUCCESSFUL`；其中 mutation test 证明若临时破坏 `fallback.png`，验证逻辑会命中失败。
  - `./gradlew.bat clean compileJava` => `BUILD SUCCESSFUL`；保留 `ClassicBarsConfig.java` deprecated API note。
  - `./gradlew.bat test` => `BUILD SUCCESSFUL`。
  - `./gradlew.bat build` => `BUILD SUCCESSFUL`。
  - `./gradlew.bat runClient --stacktrace`：进入 client dev run，日志确认 `Classic Bar 1.21.1-4 (classicbar)` 已加载，并出现 `Registering Vanilla Overlays`、`Syncing Classic Bar Configs`、integrated server 启动与玩家 `Dev` 登录记录；命令因 GUI 常驻被 timeout 终止，因此仅记录启动链/HUD 初始化 smoke 通过。
- 静态 grep / 检查结论：
  - broad grep 在 `src/main/java/tfar/classicbar/impl/overlays/vanilla` 中仍能看到 `Health.java` 的 `ModUtils.drawTexturedModalRect(...)`，但这些命中位于 bar 背景/填充绘制，属于允许继续复用的 `textures/gui/health.png` 条贴图，不是 icon 绘制路径。
  - active icon 绘制路径已由 `IndependentIconResourcesTest` 与 `verify_independent_icons.py` 双重约束为 `renderIcon(...) -> ModUtils.drawStandaloneIcon(...)`。
  - `build.gradle` 仍显式排除 `tfar/classicbar/compat/**` 与 `tfar/classicbar/impl/overlays/mod/**`，quarantine 旧代码不参与 active compile。
- 未验证项：未做人工 HUD 像素级视觉比对，未实际将外部替换 PNG 放入资源包/开发目录后启动客户端做替换 smoke；本轮只验证稳定路径、文件存在性与客户端启动链。
- 残留风险：`runClient` 仅做到启动/HUD 初始化关键日志，未覆盖全部 HUD 视觉状态；运行日志中出现若干 `libpng warning: iCCP`，本轮未继续定位具体来源贴图，若后续做美术收口可再单独排查。

**执行记录 / TASK-02 审查反馈落实（2026-04-28）**
- 状态：GREEN；已落实本轮代码审查中的阻断/重要反馈，TASK-02 关键产物已纳入 index，当前建议进入复审。
- 审查前快照：`git status --short --branch --untracked-files=all` => `## migration/neoforge-1.21.1`，`src/main/java/tfar/classicbar/resources/BarIcons.java`、`src/main/resources/assets/classicbar/textures/gui/icons/*.png`、`scripts/verify_independent_icons.py`、`src/test/java/tfar/classicbar/IndependentIconResourcesTest.java` 等关键产物均为 `??`；`git diff --stat` => `28 files changed, 383 insertions(+), 1073 deletions(-)`。
- RED 证明：先在 `IndependentIconResourcesTest` 中补入 4 个 mutation 场景（错误尺寸、无 alpha、全不透明、全透明），执行 `./gradlew.bat test --tests tfar.classicbar.IndependentIconResourcesTest` 首次失败，4 个新断言全部命中旧 verifier 只校验 PNG signature 的缺口。
- 关键改动：
  - `scripts/verify_independent_icons.py`：补为完整 PNG 结构与像素校验，要求 signature / IHDR / IDAT / IEND 完整、可解压、9x9、8-bit、显式 alpha color type（4/6）、至少一枚透明像素与一枚非透明像素；compat placeholder 改为仅在 `BarIcons` 或 `ClassicBarsConfig` 仍引用对应 fallback 时才强制存在；脚本头注释明确所有 placeholder icons 由本项目脚本生成，非 Minecraft/第三方资源提取，compat fallback 仅为 reserved placeholder。
  - `src/test/java/tfar/classicbar/IndependentIconResourcesTest.java`：JUint 校验同步补为 PNG 结构检查 + `ImageIO` 解码检查，并加入上述 RED mutation tests；compat placeholder 维持与 verifier 同步的条件式要求。
  - `scripts/generate_phase2_icons.py`：顶部注释补 provenance 说明，并注明 `blood/thirst/stamina/feathers` 仅为 reserved compat placeholders，不表示 compat 已恢复。
  - `src/main/resources/assets/classicbar/textures/gui/icons/*.png`：用仓库内生成脚本重新生成 12 个 placeholder PNG，确保 active/compat fallback 占位资源满足 9x9 RGBA 与透明/非透明像素要求。
- 验证命令与结果：
  - `python .\scripts\verify_independent_icons.py` => `Independent icon verification: GREEN`；同时输出 compat fallback placeholders 仍被引用，因此 `blood.png`、`thirst.png`、`stamina.png`、`feathers.png` 当前仍 required，且它们只是 reserved replacement targets。
  - `./gradlew.bat test --tests tfar.classicbar.IndependentIconResourcesTest` => 首次 RED（4 个新增 mutation tests 失败），实现修正后复跑 `BUILD SUCCESSFUL`。
  - `./gradlew.bat clean compileJava` => `BUILD SUCCESSFUL`；保留 `ClassicBarsConfig.java` deprecated API note。
  - `./gradlew.bat test`：与 `build` 并行首轮执行时，因 Windows 文件占用导致 `Unable to delete directory 'build\\test-results\\test\\binary'` 失败；随后顺序重跑 `./gradlew.bat test` => `BUILD SUCCESSFUL`。
  - `./gradlew.bat build` => `BUILD SUCCESSFUL`。
  - `./gradlew.bat runClient --stacktrace` => `BUILD SUCCESSFUL`；日志确认 `Classic Bar 1.21.1-4 (classicbar)` 已加载，出现 `Registering Vanilla Overlays`、`Syncing Classic Bar Configs`，并完成 integrated server 启动与玩家 `Dev` 登录/退出记录。
- git add / 纳入版本控制范围：已 `git add` 计划文件、`scripts/generate_phase2_icons.py`、`scripts/verify_independent_icons.py`、`scripts/verify_neoforge_migration.py`、`scripts/with-java21.ps1`、`src/main/java/tfar/classicbar/client/**`、`src/main/java/tfar/classicbar/resources/BarIcons.java`、`src/main/resources/META-INF/neoforge.mods.toml`、`src/main/resources/assets/classicbar/textures/gui/icons/*.png`、`src/test/java/tfar/classicbar/IndependentIconResourcesTest.java`、`src/test/java/tfar/classicbar/PureClientConvergenceTest.java`；未添加 build 产物、日志或 TASK-03 相关新脚本。
- 结果复查：审查点要求的 TASK-02 关键产物已不再以 `??` 形式存在；若后续 `git status` 仍有未跟踪文件，应仅剩与 TASK-03/TASK-04 相关的未纳入项。
- 未验证项：仍未做人工 HUD 像素级视觉比对，未做“外部替换 PNG 后再启动客户端”的资源替换 smoke；NOTICE/UNLICENSE 授权收口按计划仍留待后续阶段。

### TASK-03：使用 NeoForge Config 格式重构配置文件

**目标**
- 在纯客户端 mod 事实源下，把 ClassicBar 配置主线收敛到 NeoForge `ModConfigSpec` / TOML / `ModConfig.Type.CLIENT`。
- 为每个状态栏建立稳定、可验证、可本地化的配置模型：启用模式、颜色覆盖层、文字/icon/layout 等现有配置共同进入 client TOML。
- 保留 compat quarantine：允许新增“是否启用某个 mod 支持”的预留/禁用开关，但不得恢复 compat 编译、不得声明任何 third-party compat 已恢复。
- 旧 JSON 配置策略仍按破坏性迁移文档化，不在 TASK-03 实现复杂导入器。

**范围**
- 使用 NeoForge `ModConfigSpec` / TOML / `ModConfig.Type.CLIENT` 作为 ClassicBar 主配置源。
- 通过 `ModContainer#registerConfig(ModConfig.Type.CLIENT, ...)` 注册 client config。
- 移除或隔离旧 `config/classicbar/*.json` runtime read/write 主路径。
- 配置至少覆盖 general、layout、bars 三类。
- `bars.<bar_id>.mode` 新增 per-bar 启用模式配置；每个状态栏至少支持三种相互独立的稳定值：`OVERRIDE`（覆盖原版状态栏）、`COMPAT`（同时显示原版与 ClassicBar）、`DISABLED`（不使用对应 ClassicBar 状态栏）。实现需设计为 enum 或等价稳定值，并能被测试/静态验证约束。
- `bars.<bar_id>.color_overlay` 新增 per-bar 颜色覆盖层配置；所有状态栏的颜色覆盖层都必须可配置，具备默认值与颜色格式验证。
- `mod_support.<mod_id>.enabled` 新增 mod support 开关配置；在 compat quarantine 期间这些开关只能作为预留/禁用状态或后续恢复入口，不得让 `compat/**` 参与 active compile，不得声明 compat 已恢复。
- 新增配置项/配置注解本地化要求：使用 NeoForge 1.21.1 官方 `.translation(...)` 或该版本正确方法为 config entry 提供 lang key；新增基础 lang 文件（至少 `src/main/resources/assets/classicbar/lang/en_us.json`），配置标题/说明等用户可见文本必须进入 lang，不得只硬编码在 Java 注释/comment 中。
- 执行 TASK-03 前或执行中必须外部资料确认 NeoForge 1.21.1 config translation 官方写法，再按确认结果落地；若 `.translation(...)` 在 1.21.1 中有替代 API，以官方写法为准并记录证据。
- 旧 JSON 配置本轮按破坏性迁移文档化；后续可选实现一次性只读导入器。
- 本任务不得恢复 network/server sync，不得恢复 compat 编译，不得进入 README/发布收口。

**文件/目录**
- `src/main/java/tfar/classicbar/config/ClassicBarsConfig.java`
- `src/main/java/tfar/classicbar/config/ConfigCache.java`
- `src/main/java/tfar/classicbar/api/BarSettings.java`
- `src/main/java/tfar/classicbar/ClassicBar.java`
- `src/main/java/tfar/classicbar/EventHandler.java`
- `src/main/resources/META-INF/neoforge.mods.toml`
- `src/main/resources/assets/classicbar/lang/en_us.json`
- `src/test/java/tfar/classicbar/**` 或 `scripts/**` 中用于配置结构、稳定值与 lang key 的验证文件
- 后续配置迁移说明文档可记录破坏性 JSON 迁移策略；不得在 TASK-03 进入 README/发布收口

**依赖**
- TASK-01 NeoForge loader/config 注册点稳定。
- TASK-02 icon resource id 规则确定。
- 外部资料确认 NeoForge 1.21.1 config translation 官方写法（例如 `ModConfigSpec.Builder#translation(...)` 或该版本正确替代方法）。

**DoD**
- active source 使用 `net.neoforged.neoforge.common.ModConfigSpec`，不使用 `ForgeConfigSpec`。
- 配置由 NeoForge `ModConfig.Type.CLIENT` client config 注册；不在 config 加载前 eager read 未初始化值。
- 存在 `general`、`layout`、`bars` 等分组。
- `layout.left_order` / `layout.right_order` 有 unknown bar id 与重复项处理策略。
- 每个 active bar 至少有 `show_text`、`icon`、`mode` 与 `color_overlay` 配置。
- 每个状态栏的 `mode` 至少支持三种相互独立的稳定值：`OVERRIDE`、`COMPAT`、`DISABLED`；实现为 enum 或等价稳定值，并有验证覆盖默认值、非法值处理/回退与渲染分支语义：覆盖=覆盖原版状态栏，兼容=同时显示原版与 ClassicBar，禁用=不使用对应 ClassicBar 状态栏。
- 所有状态栏的颜色覆盖层都可配置；每个 `color_overlay` 有明确默认值，颜色格式 validator 至少覆盖 `#RRGGBB` / `#AARRGGBB` 或项目选定的等价稳定格式，并对非法值 fallback 到安全默认值。
- 存在 mod support 开关配置（如 Tough As Nails / Vampirism / ParCool / Feathers 等已知 compat 入口或等价列表）；在 compat quarantine 期间默认可为禁用/预留，且这些开关不让 `compat/**` 进入 active compile，不构成“compat 已恢复”的声明。
- icon config 错误能 fallback 到安全 icon。
- 颜色有 hex validator；fraction 有 0..1 范围 validator。
- config loading/reloading 后刷新 bar settings、layout order 与 `ConfigCache`。
- active source 不再包含旧 JSON runtime persistence：`Gson`、`JsonReader`、`JsonWriter`、`FileReader`、`FileWriter`、`config/classicbar`。
- 配置项/配置注解使用 NeoForge 1.21.1 官方 `.translation(...)` 或该版本正确方法设置 translation key；新增并纳入基础 lang 文件（至少 `assets/classicbar/lang/en_us.json`），覆盖新增 config key 的用户可见名称/说明。
- 用户可见配置文本不得只硬编码在 Java 注释/comment 中；Java 注释只能解释开发意图，不作为配置 UI/lang 的唯一来源。
- 文档化旧 JSON 配置为破坏性迁移，不实现复杂导入器；若写迁移说明，只覆盖配置迁移事实，不进入 README/发布收口。

**验证命令**
```powershell
gradlew.bat clean compileJava
gradlew.bat test
gradlew.bat build
```
- 增加或更新自动化验证，覆盖：per-bar mode 三稳定值、per-bar color_overlay 默认值与格式 validator、mod support 开关不恢复 compat、config translation/lang key 覆盖、旧 JSON runtime persistence 已移除/隔离。
- 验证前记录外部资料确认结果：NeoForge 1.21.1 config translation 官方写法（`.translation(...)` 或正确替代方法）。
- 可选：`gradlew.bat runClient` 生成并检查 `classicbar-client.toml`，确认 client TOML 包含 per-bar mode/color_overlay、mod support 开关与 translation key 生效路径。

**风险与回退**
- 用户旧 JSON 配置失效：在 TASK-03 迁移说明/计划记录中说明破坏性迁移；README/changelog 收口留到 TASK-05，后续任务可补导入器。
- config translation API 版本差异：执行前外部资料确认 NeoForge 1.21.1 官方写法；若 `.translation(...)` 不适用，以官方替代方法改计划/实现并记录证据。
- per-bar `COMPAT` 模式与原版 HUD layer 取消逻辑冲突：把原版 layer disable 条件改为按 bar mode 逐项判断；必要时先对无法拆分的 vanilla layer 标记风险，不用恢复 server/network。
- mod support 开关被误解为 compat 已恢复：开关文案/lang 必须标明 quarantine/reserved 语义，active compile 仍排除 compat。
- 配置 key 拼写兼容问题，例如历史 `thirstr_bar_color`：需决定保留兼容或新增正确 key 并说明迁移。
- 配置 reload 后 HUD 未刷新：在 `ModConfigEvent.Loading/Reloading` 后触发 cache 重建。

**执行记录 / TASK-03（2026-04-29）**
- 状态：GREEN；ClassicBar 主配置已收敛为 NeoForge `ModConfigSpec` client TOML，active vanilla bars 全部具备 `show_text` / `icon` / `mode` / `color_overlay`，并补入 `mod_support` 预留开关、配置本地化、mode 驱动的 vanilla layer 取消逻辑与 client-side reload cache rebuild。
- 接管快照：执行前记录 `git status --short --branch --untracked-files=all` => `## migration/neoforge-1.21.1`，dirty worktree 覆盖 build、client HUD、config、icons、plans、tests 等；`git diff --stat` => `29 files changed, 424 insertions(+), 1085 deletions(-)`，另有未跟踪 `scripts/verify_neoforge_config.py`、`src/test/java/tfar/classicbar/config/NeoForgeConfigBehaviorTest.java` 等 TASK-03 新产物。
- 外部资料确认（NeoForge 1.21.1）：
  - 资料链：`Documentation/versioned_docs/version-1.21.1/misc/config.md`、`net.neoforged.neoforge.common.ModConfigSpec.Builder#translation(String)`、`net.neoforged.neoforge.client.gui.ConfigurationScreen$ConfigurationSectionScreen#getTooltipComponent(...)`、`net.neoforged.neoforge.common.TranslatableEnum#getTranslatedName()`、`net.neoforged.neoforge.client.gui.IConfigScreenFactory`、`tests/.../ConfigUITest.java`。
  - 结论：配置项/section 的官方本地化入口为 `.translation("key")`；tooltip 使用 `<translationKey>.tooltip` 落到 lang JSON；enum 若要在内置 config screen 本地化，应实现 `TranslatableEnum` 并覆写 `getTranslatedName()`；Mods 页 Config 按钮入口为 `ModContainer#registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new)`。
- RED 证明：
  - `python .\scripts\verify_neoforge_config.py` 首轮 RED，命中缺失 `BarMode` / `BarColorOverlay` / lang JSON / `mod_support` / `.translation(...)` / reload hook / mode-driven vanilla cancel 等缺口。
  - `./gradlew.bat test --tests tfar.classicbar.config.NeoForgeConfigBehaviorTest` 首轮 RED；最初因 TASK-03 新类型与 lang 文件缺失导致编译失败，补入测试所需结构后再次命中“未注册 config screen extension / 未切换到 ClassicBarsConfig reload hook / 缺少 per-bar mode & color overlay / 缺少 reserved mod_support 开关 / 错误 schema key 未收敛”等断言失败，证明验证可拦住临时破坏。
- 关键实现：
  - `ClassicBarsConfig.java`
    - 保持 active source 使用 `net.neoforged.neoforge.common.ModConfigSpec`；client config 仍由 `ClassicBar` 通过 `registerConfig(ModConfig.Type.CLIENT, ...)` 注册。
    - 用 `.translation(...)` 为 `general` / `layout` / `bars` / `mod_support` section 与新增配置项建立稳定 lang key。
    - 为每个 active bar 注册 `show_text`、`icon`、`mode`、`color_overlay`；`mode` 使用 `defineEnum("mode", BarMode.OVERRIDE)`，`color_overlay` 接受 `#RRGGBB` 或 `#AARRGGBB`。
    - `layout.left_order` / `layout.right_order` 继续只接受 active bar id；未知 id 会在应用阶段被忽略，重复项由 `LinkedHashSet` 去重。
    - 新增 `mod_support.<mod_id>.enabled`（`toughasnails` / `vampirism` / `parcool` / `feathers`）作为 quarantine 阶段预留开关，默认 `false`，注释与 lang 明确声明“不代表 compat 已恢复”。
    - `onConfigLoading` / `onConfigReloading` 统一在 client config 事件后调用 `EventHandler.cacheConfigs()`，避免 eager 读取未初始化值。
    - `thirst_bar_color` 作为新 schema 正键；旧 typo `thirstr_bar_color` 不再作为 runtime config key。
  - `BarMode.java`
    - 新增 `OVERRIDE` / `COMPAT` / `DISABLED` 三值稳定 enum，并实现 `TranslatableEnum`，覆写 `getTranslatedName()` 使用 `Component.translatable(...)`。
  - `BarColorOverlay.java` / `ColorUtils.java`
    - 新增 per-bar 颜色覆盖表达：`#RRGGBB` = 全量覆盖，`#AARRGGBB` = alpha 混色；非法值 fallback 到 no-op `#00FFFFFF`。
    - `ColorUtils.parseHexColor(...)` 新增 RGB / ARGB 解析与 alpha 暴露；`ConfigCache` 继续负责 general colors 的 baked cache。
  - `BarSettings.java` / `BarOverlay.java` / `BarOverlayImpl.java`
    - `BarSettings` 扩为 `show_text` + `icon` + `mode` + `color_overlay`。
    - `BarOverlayImpl.render(...)` 会先按 `mode` 判定是否渲染 ClassicBar；新增 `applyConfiguredBarColor(...)` / `getConfiguredTextColor(...)` 统一应用 per-bar 颜色覆盖层。
  - `client/EventHandler.java`
    - `all` 列表改为只装入 `mode != DISABLED` 的 active bars。
    - `disableVanillaLayers(...)` 改为按 bar mode + 运行时 `shouldRender(player)` 条件取消对应 vanilla layer：
      - `health` / `absorption` 共用 `PLAYER_HEALTH`，仅当仍有 override 需求时才取消；
      - `armor` -> `ARMOR_LEVEL`；`food` -> `FOOD_LEVEL`；`health_mount` -> `VEHICLE_HEALTH`；`air` -> `AIR_LEVEL`；
      - `armor_toughness` 无 vanilla 对应层，因此只参与 ClassicBar 渲染，不取消任何 vanilla layer。
  - `ClassicBar.java` / `ClassicBarClient.java`
    - client-only 入口新增 `registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new)`，让 Mods 页 Config 按钮具备官方扩展点。
    - config load / reload listener 收敛到 `ClassicBarsConfig::onConfigLoading` / `::onConfigReloading`。
  - `assets/classicbar/lang/en_us.json` / `zh_cn.json`
    - 新增 section title、tooltip、per-bar mode/color_overlay 文案、reserved mod support 文案与 `BarMode` 枚举文本。
  - `scripts/verify_neoforge_config.py` / `src/test/java/tfar/classicbar/config/NeoForgeConfigBehaviorTest.java`
    - 新增/补强静态验证，覆盖 `.translation(...)`、lang key / tooltip、`BarMode` 三值与 `TranslatableEnum`、per-bar `mode` / `color_overlay`、client reload hook、reserved `mod_support`、`thirst_bar_color` 决策、旧 JSON runtime persistence 移除、compat quarantine 不恢复。
    - JUnit 中加入 `langCoverageVerifierWouldFailIfTooltipKeyDisappeared`，证明 tooltip key 缺失时测试会真实失败，避免伪 TDD。
  - `scripts/verify_independent_icons.py`
    - 因 TASK-03 fallback 配置模型变更，放宽 fallback 检查为“ClassicBarsConfig 仍存在 ClassicBar-owned fallback default”，避免把 mode/color_overlay 架构调整误判为 TASK-02 回归。
- 生成配置事实：`run\config\classicbar-client.toml` 已实际生成并含 `[general]`、`[layout]`、`[bars.<id>]` 的 `mode` / `color_overlay` / `icon` / `show_text`，以及 `[mod_support.<mod_id>].enabled`；日志中出现 `Configuration file ... classicbar-client.toml is not correct. Correcting`，说明 NeoForge 已按新 schema 执行自动修正。
- 旧 JSON 策略：继续按破坏性迁移处理；TASK-03 不实现导入器。旧 `config/classicbar/*.json`、`Gson`、`JsonReader`、`JsonWriter`、`FileReader`、`FileWriter` 不再是 active runtime persistence 主线；迁移说明保留在计划文件，README/changelog 收口延后至 TASK-05。
- `thirst_bar_color` 决策：采用正确 key `thirst_bar_color` 作为新 schema 主键；旧 typo `thirstr_bar_color` 不保留为 runtime alias，不做导入器兼容，仅在注释/lang/计划中明确其为破坏性迁移说明。`verify_neoforge_config.py` 与 `NeoForgeConfigBehaviorTest` 都会拦截把 `define("thirstr_bar_color"` 重新带回 schema 的回归。
- 验证命令与结果：
  - `python .\scripts\verify_neoforge_config.py`：首轮 RED，修正后复跑 => `NeoForge config verification: GREEN`。
  - `./gradlew.bat test --tests tfar.classicbar.config.NeoForgeConfigBehaviorTest`：首轮 RED，修正后复跑 => `BUILD SUCCESSFUL`。
  - `python .\scripts\verify_independent_icons.py`：因 fallback 检查脚本仍假设旧默认构造首轮 RED；修正脚本后复跑 => `Independent icon verification: GREEN`，同时继续声明 compat placeholder 仍只是 reserved replacement targets。
  - `./gradlew.bat clean compileJava` => `BUILD SUCCESSFUL`；仍有 `ClassicBarsConfig.java` deprecated API note，但未阻断任务。
  - `./gradlew.bat test`：并行首轮曾因 `processResources` stale outputs 清理失败而中断；顺序重跑 => `BUILD SUCCESSFUL` / `UP-TO-DATE`。
  - `./gradlew.bat build`：顺序执行 fresh 通过，后续复跑 `UP-TO-DATE`。
  - `./gradlew.bat runClient --stacktrace`：成功进入 client dev run；日志确认 `Classic Bar 1.21.1-4 (classicbar)` 已加载，并出现 `Registering Vanilla Overlays`、`Syncing Classic Bar Configs`、`Configuration file ... classicbar-client.toml is not correct. Correcting`；命令因 GUI 常驻被 timeout 终止，但已到达 config load/reload 与 HUD 初始化关键路径。
- 静态 grep / 边界结论：
  - 对 `src/main/java` 检索 `ClassicBarNetwork`、`VanillaFoodDataPayload`、`RegisterPayloadHandlersEvent`、`PlayerTickEvent`、`PlayerLoggedOutEvent`、`PacketDistributor`、`SimpleChannel`、`NetworkRegistry`、`SyncHandler` 均无 active 命中，未恢复 network/server sync。
  - `build.gradle` 仍排除 `tfar/classicbar/compat/**` 与 `tfar/classicbar/impl/overlays/mod/**`；reserved `mod_support` 开关未解除 quarantine。
- 未验证项：
  - 未手动在 Mods 列表界面点击 Config 按钮，只验证了官方 extension point 注册与客户端启动链。
  - 未逐个切换 `mode = COMPAT / DISABLED` 做人工 HUD 视觉 smoke；当前仅有静态/自动化验证与启动链证据。
  - 未做人工 lang/UI 文案视觉验收；仅验证 key 存在与 loader/config screen 所需 translation 结构。
- 残留风险：
  - `ModConfigSpec` / `ConfigurationScreen` 相关 API 在未来 NeoForge 小版本仍可能调整，升级时需复核 `.translation(...)`、`TranslatableEnum` 与 `IConfigScreenFactory` 路径。
  - `processResources` 在 Windows 上出现过一次 stale outputs 清理失败；当前顺序重跑已通过，判断为构建产物清理噪音而非源码缺陷，但后续若再次出现需留意本地文件占用。
  - `runClient` 仅证明加载与配置修正路径；尚未证明所有 mode 组合下的实际 HUD 视觉完全符合预期。
- 建议：建议进入代码审查。TASK-03 已具备自动化约束、外部资料对齐、运行验证证据与计划记录，可由审查阶段重点复核 mode 语义、UI 本地化文案与后续 compat 开关表达是否足够清晰。

#### TASK-03 审查反馈修复（2026-04-29）
- 状态：已落实 TASK-03 审查阻断与重要反馈，维持纯客户端、network/server sync 不恢复、compat quarantine 不解除。
- 触发原因：审查指出 `health` / `absorption` 共用 `VanillaGuiLayers.PLAYER_HEALTH` 时，原先 `health || absorption` 级别的整层取消会破坏 per-bar `OVERRIDE / COMPAT / DISABLED` 独立语义；同时原版层取消未感知 layout 实际启用状态，存在“ClassicBar 不渲染但 vanilla 被取消”的误杀风险。
- RED 证明：
  - 新增 `src/test/java/tfar/classicbar/client/EventHandlerCancellationBehaviorTest.java`，先约束以下行为：
    - `health=OVERRIDE + absorption=COMPAT` 不取消 `PLAYER_HEALTH`；
    - `health=DISABLED + absorption=OVERRIDE` 不取消 `PLAYER_HEALTH`；
    - 仅当 `health/absorption` 都是 `OVERRIDE` 且两者都在当前 layout 中实际启用并 `shouldRender(player)` 时，才允许取消 `PLAYER_HEALTH`；
    - `food` 被移出 layout 即使 mode=OVERRIDE 也不得取消 `FOOD_LEVEL`；
    - `food/armor/air` 的 `COMPAT` 仍渲染 ClassicBar 但不取消原版，`DISABLED` 既不渲染也不取消原版。
  - 扩充 `NeoForgeConfigBehaviorTest` 与 `verify_neoforge_config.py`，先要求 section `.button` lang key 存在、layout-aware cancel helper 存在、共享 `PLAYER_HEALTH` 文案说明存在、`color_overlay` 语义文案存在。
  - 新增 `BarColorOverlayBehaviorTest`，先要求：非法值 no-op fallback、`#RRGGBB` 全量替换、`#AARRGGBB` alpha 混色。
  - 首轮定向执行 `./gradlew.bat test --tests tfar.classicbar.config.NeoForgeConfigBehaviorTest --tests tfar.classicbar.client.EventHandlerCancellationBehaviorTest --tests tfar.classicbar.api.BarColorOverlayBehaviorTest` 失败；失败点覆盖缺失 layout-aware cancel helper、缺失 `.button` lang key、tooltip 未说明共享 `PLAYER_HEALTH`/颜色覆盖语义，以及尚未引入可直接回归验证的取消策略 helper。
- 关键修复：
  - `src/main/java/tfar/classicbar/client/VanillaLayerCancellationPolicy.java`
    - 新增可单测的保守取消策略 helper，把“是否渲染 ClassicBar”“是否取消独立 vanilla layer”“是否允许取消共享 PLAYER_HEALTH”从事件总线 glue 中抽出。
    - 共享 `PLAYER_HEALTH` 取消规则收敛为：只有 `health` 与 `absorption` 两者都处于 `OVERRIDE`、都在当前 layout 实际启用、且两者当前都 `shouldRender(player)` 时才取消；任一 bar 为 `COMPAT` / `DISABLED` / 不在 layout / 当前不渲染，都保留原版层，避免误杀。
  - `src/main/java/tfar/classicbar/client/EventHandler.java`
    - `cacheConfigs()` 改为记录当前 layout 实际启用的 overlay id 集，并在剔除已 errored overlay 后刷新 `ConfigCache` 中的 active layout set。
    - `disableVanillaLayers(...)` 改为先解析每个 overlay 的运行时状态（mode、layout active、`shouldRender(player)`），再走：
      - `shouldCancelSharedPlayerHealthLayer(...)` 处理 `PLAYER_HEALTH`；
      - `shouldCancelIndependentVanillaLayer(...)` 处理 `ARMOR_LEVEL` / `FOOD_LEVEL` / `VEHICLE_HEALTH` / `AIR_LEVEL`。
    - 这样即便某个 bar 仍是 `OVERRIDE`，只要它不在 layout 中、当前没参与渲染、或共享层另一半不是安全覆盖状态，就不会取消原版层。
  - `src/main/java/tfar/classicbar/config/ConfigCache.java`
    - 新增 `setActiveLayoutOverlays(...)` 与 `isOverlayActiveInLayout(...)`，为 layout-aware cancel 提供 baked 状态来源。
  - `src/main/java/tfar/classicbar/util/Color.java`
    - 将 `colorBlend(...)` 的线性插值改为本地实现，避免 `BarColorOverlay` 纯行为测试依赖 `net.minecraft.util.Mth` 才能在 JUnit 侧运行。
  - `src/main/resources/assets/classicbar/lang/en_us.json` / `zh_cn.json`
    - 补齐 `classicbar.config.section.{general,layout,bars,mod_support}.button`。
    - `health` / `absorption` mode tooltip 明确写出共享 `PLAYER_HEALTH` 限制：ClassicBar 采用保守策略，只有两条共享 bar 都处于实际覆盖条件时才取消；否则保留原版红心/吸收表现。
    - `color_overlay` tooltip 明确写出：
      - `#RRGGBB` 会覆盖该状态栏的所有填充 pass，可能隐藏渐变/分层；
      - `#AARRGGBB` 是与原颜色做 alpha 混色，而不是渲染透明度开关；
      - health 的 effect overlay（如 poison）仍可能继续叠加在上层。
  - `src/test/java/tfar/classicbar/config/NeoForgeConfigBehaviorTest.java` / `src/test/java/tfar/classicbar/client/EventHandlerCancellationBehaviorTest.java` / `src/test/java/tfar/classicbar/api/BarColorOverlayBehaviorTest.java` / `scripts/verify_neoforge_config.py`
    - 把上述共享层策略、layout-aware cancel、`.button` lang 覆盖与颜色覆盖语义都纳入自动回归。
- 共享 `PLAYER_HEALTH` 策略（最终事实）：
  - `health` 与 `absorption` 仍受原版共享 GUI layer 限制，ClassicBar 在 TASK-03 不尝试伪造真正“按原版层分离取消”的能力。
  - 采取保守可验证策略：任一共享 bar 不是 `OVERRIDE`、不在当前 layout、或当前不渲染，就不取消 `PLAYER_HEALTH`；这样至少保证不会出现“一个 bar 设为 `OVERRIDE`，把另一个 `COMPAT / DISABLED` 的 vanilla 表现顺带隐藏”的回归。
  - 该限制已同步写入 tooltip/lang 与本计划记录，供后续复审与未来任务参考。
- layout-aware cancel 策略（最终事实）：
  - Vanilla 取消不再只看 mode；现在必须同时满足：
    1. 当前 bar 的 mode 请求取消 vanilla（`OVERRIDE`）；
    2. 该 bar 当前仍在 layout 实际启用；
    3. 该 bar 对当前玩家 `shouldRender(player)` 为真；
    4. 若是共享 `PLAYER_HEALTH`，health 与 absorption 两侧都满足上述条件。
  - 因此“layout 中删除某 bar，但 mode 仍保留 OVERRIDE”不会再取消对应 vanilla layer。
- 审查反馈修复验证：
  - `python .\scripts\verify_neoforge_config.py` => `NeoForge config verification: GREEN`。
  - `python .\scripts\verify_independent_icons.py` => `Independent icon verification: GREEN`；继续声明 compat placeholder 只是 reserved replacement targets。
  - `./gradlew.bat test --tests tfar.classicbar.config.NeoForgeConfigBehaviorTest --tests tfar.classicbar.client.EventHandlerCancellationBehaviorTest --tests tfar.classicbar.api.BarColorOverlayBehaviorTest` => `BUILD SUCCESSFUL`。
  - `./gradlew.bat test --tests tfar.classicbar.config.NeoForgeConfigBehaviorTest` => `BUILD SUCCESSFUL`。
  - 并行分发执行单测类时，两次 `./gradlew.bat test --tests ...` 曾因 Windows 无法删除 `build\test-results\test\binary\output.bin` 失败；顺序重跑 `EventHandlerCancellationBehaviorTest` => `BUILD SUCCESSFUL`。判断为测试输出目录文件占用噪音，而非源码回归。
- 剩余说明：
  - 本修复不进入 TASK-04，不恢复 compat 编译，不恢复 network/server sync。
  - 由于原版 `PLAYER_HEALTH` 物理上仍是共享层，真正“health/absorption 各自独立取消原版层”的能力仍受 vanilla 结构限制；TASK-03 的目标是保守避免误杀 vanilla，而不是伪造不存在的拆层能力。

#### TASK-03 复审反馈二次修复（2026-04-29）
- 状态：已落实 TASK-03 复审反馈中的共享 `PLAYER_HEALTH` 过保守问题；当前待再次复审。
- 触发原因：上一轮“health 与 absorption 都必须 `OVERRIDE + activeInLayout + shouldRender` 才取消 `PLAYER_HEALTH`”的保守策略，会让默认无吸收心场景下的 `health=OVERRIDE` 退化成与 `COMPAT` 等效，原版红心不会消失。
- 根因：`PLAYER_HEALTH` 是生命与吸收共用的原版 layer。上一轮把“是否存在需要保留的 vanilla absorption 信息”和“absorption ClassicBar 自己当前是否渲染”混成同一个条件，导致即使 `player.getAbsorptionAmount() <= 0`、根本没有原版吸收心可保留时，也仍要求 absorption 侧满足共享取消条件。
- RED 证明：
  - 先扩充 `src/test/java/tfar/classicbar/client/EventHandlerCancellationBehaviorTest.java`，新增并命中以下失败场景：
    - `health=OVERRIDE, absorption=OVERRIDE, absorption absent` 应取消 `PLAYER_HEALTH`；
    - `health=OVERRIDE, absorption=COMPAT, absorption absent` 应取消 `PLAYER_HEALTH`；
    - `health=OVERRIDE, absorption=COMPAT, absorption present` 不取消；
    - `health=OVERRIDE, absorption=DISABLED/removed, absorption present` 不取消；
    - `health=COMPAT/DISABLED, absorption=OVERRIDE, absorption present` 不取消。
  - 执行 `./gradlew.bat test --tests tfar.classicbar.client.EventHandlerCancellationBehaviorTest` 首轮 RED，失败原因为 `VanillaLayerCancellationPolicy.shouldCancelSharedPlayerHealth(...)` 仍要求 absorption 侧无条件满足 `OVERRIDE + activeInLayout + shouldRender`。
- 关键修复：
  - `src/main/java/tfar/classicbar/client/VanillaLayerCancellationPolicy.java`
    - 共享层策略改为“优先保留仍存在的原版 absorption 信息，但让普通 health override 在无吸收心时生效”：
      1. `health` 自身若不满足 `OVERRIDE + activeInLayout + shouldRender`，永不取消 `PLAYER_HEALTH`；
      2. 若 absorption 当前 `shouldRender == false`（典型场景：`player.getAbsorptionAmount() <= 0`），视为没有原版吸收心需要保留，此时直接允许 `health` 单侧触发共享层取消；
      3. 若 absorption 当前 `shouldRender == true`，说明仍有原版吸收心信息需要保留，此时只有 absorption 侧也满足 `OVERRIDE + activeInLayout + shouldRender` 才取消共享层；否则保留整层 vanilla player health。
  - `src/main/java/tfar/classicbar/client/EventHandler.java`
    - `resolveOverlayRenderState(...)` 的 `shouldRender` 改为只反映 overlay 对玩家事实上的可见性（直接取 `overlay.shouldRender(player)`），不再提前混入 layout/mode gate；这样 shared-layer 决策才能区分“absorption 本身当前不存在”与“absorption 当前存在但因 `COMPAT`/`DISABLED`/layout 移除而必须保留 vanilla 信息”。
    - 独立 layer 取消仍继续走 `requestsVanillaCancel + activeInLayout + shouldRender`，保持 layout-aware，不让未进 layout 的 overlay 单独取消原版层。
  - `src/main/resources/assets/classicbar/lang/en_us.json` / `zh_cn.json`
    - `health` / `absorption` mode tooltip 更新为用户可见的共享层语义：
      - 无原版吸收心时，`Health OVERRIDE` 会隐藏原版红心；
      - 只要仍有可见原版吸收心，而 absorption 不是可安全覆盖的 `OVERRIDE`，就会保留整层 vanilla `PLAYER_HEALTH`，避免把生命/吸收信息一起误隐藏。
  - `src/test/java/tfar/classicbar/config/NeoForgeConfigBehaviorTest.java` / `scripts/verify_neoforge_config.py`
    - 将上述 tooltip/lang 关键词纳入自动回归，避免共享层语义再次回退到“必须双方都渲染才取消”的旧文案。
  - `src/test/java/tfar/classicbar/config/ConfigCacheLayoutTrackingBehaviorTest.java`
    - 补一个低成本 reload-like 行为测试，确认 `ConfigCache.setActiveLayoutOverlays(...)` 会用新 snapshot 替换旧 active layout set，避免 layout 变更后继续沿用旧 overlay 活跃态。
- `PLAYER_HEALTH` 共享层策略（当前事实）：
  - `PLAYER_HEALTH` 仍是 health 与 absorption 的共享 vanilla layer，ClassicBar 不能物理上只取消其中一半。
  - 当前实现采用“保留非覆盖信息优先，同时让普通 health override 生效”的折中策略：
    - **无原版 absorption 信息需要保留**（例如 absorption amount `<= 0`，overlay 当前不应渲染）时，只要 `health=OVERRIDE` 且 health 在 layout 中实际启用并 `shouldRender`，就允许取消 `PLAYER_HEALTH`；
    - **有原版 absorption 信息需要保留**（例如 absorption hearts 当前可见）时，只有 health 与 absorption 两侧都满足独立取消条件才取消共享层；任一侧为 `COMPAT` / `DISABLED` / 不在 layout，都保留整层 vanilla player health。
- 二次修复验证：
  - `./gradlew.bat test --tests tfar.classicbar.client.EventHandlerCancellationBehaviorTest --tests tfar.classicbar.config.NeoForgeConfigBehaviorTest --tests tfar.classicbar.config.ConfigCacheLayoutTrackingBehaviorTest` => `BUILD SUCCESSFUL`。
- 剩余说明：
  - 本轮仍不进入 TASK-04，不恢复 compat 编译，不恢复 network/server sync。
  - 该策略已显式承认 vanilla 共享层限制：一旦需要保留原版吸收心，就只能整体保留 `PLAYER_HEALTH`；后续若要进一步优化，只能靠视觉/集成 smoke 继续验证当前折中语义是否足够清晰，而不是伪造不存在的原版拆层能力。

### TASK-04：compat quarantine 与后续恢复策略

**范围**
- 明确第三方 compat 在首轮迁移中的状态。
- Tough As Nails、Vampirism、ParCool 有 1.21.1 NeoForge 构件，但不阻塞 vanilla 主线。
- Feathers 暂无 1.21.1 NeoForge 可用构件，不在首轮恢复。
- 若源码中保留未迁移 compat，必须确保不参与 active compile 或不会加载失败。

**文件/目录**
- `build.gradle`
- `scripts/verify_phase4_overlays.py`
- `scripts/verify_phase4_network.py`
- `src/test/java/tfar/classicbar/CompatQuarantineBehaviorTest.java`
- `src/main/java/tfar/classicbar/client/EventHandler.java`
- `src/main/java/tfar/classicbar/compat/**`
- `src/main/java/tfar/classicbar/impl/overlays/mod/**`
- `src/main/resources/META-INF/neoforge.mods.toml`
- `src/main/resources/assets/classicbar/lang/*.json`
- `plans/plan_classicbar_neoforge_1_21_1_migration.md`

**依赖**
- TASK-01 至 TASK-03 主线能独立编译。

**DoD**
- `build.gradle` 继续显式排除 `tfar/classicbar/compat/**` 与 `tfar/classicbar/impl/overlays/mod/**`，并用注释说明这是 TASK-04 quarantine 边界，不得被误当成已恢复支持。
- active `EventHandler` 只注册 vanilla overlays；`Blood` / `Thirst` / `StaminaB` / `Feathers` 不得回到 active registry。
- active source、`build/classes/java/main` 与已生成 jar 都不得包含 quarantine compat / mod overlay class。
- `neoforge.mods.toml` 不得声明 Tough As Nails / Vampirism / ParCool / Feathers 的任何 dependency block；首轮 quarantine 策略保持“完全不声明 third-party deps”。
- `mod_support` 与相关 lang/tooltip 必须明确写 reserved/quarantined，不得误导为“兼容已恢复”。
- pure client 路线仍保持成立：不恢复 network/server sync、payload 注册、server tick sync 或 dedicated server 支持承诺。
- Tough As Nails / Vampirism / ParCool 只记录为后续独立恢复任务；Feathers 首轮不恢复的原因写明为缺少 1.21.1 NeoForge 可用构件。

**验证命令**
```powershell
python .\scripts\verify_neoforge_config.py
python .\scripts\verify_independent_icons.py
python .\scripts\verify_phase4_overlays.py
python .\scripts\verify_phase4_network.py
gradlew.bat test --tests tfar.classicbar.CompatQuarantineBehaviorTest
gradlew.bat clean compileJava
gradlew.bat test
gradlew.bat build
```

静态 grep：确认 pure client 入口仍为 `@Mod(... dist = Dist.CLIENT)`，且 active source 未恢复 payload / server tick / third-party compat 注册。

**风险与回退**
- sourceSets 排除掩盖长期债务：文档明确 quarantine，不伪装为完整兼容。
- optional dependency 元数据误写 required：统一写 optional 或暂不声明。
- 第三方 API 编译通过但运行异常：每个 compat 独立任务恢复。
- 任务约束禁止 README 收口：当前支持矩阵仅记录在计划、lang 与 verifier，不提前写 README。

**执行记录 / TASK-04（2026-04-29）**
- 状态：GREEN；首轮迁移继续保持 third-party compat quarantine，不恢复 Tough As Nails / Vampirism / ParCool / Feathers，不恢复 network/server sync，不新增 third-party compile/runtime dependencies。
- 接管快照：执行前记录 `git status --short --branch --untracked-files=all` => `## migration/neoforge-1.21.1`，dirty worktree 覆盖 build.gradle、ClassicBarsConfig、lang、此前 TASK-01~03 staged 文件，以及未跟踪 `scripts/verify_phase4_overlays.py` / `scripts/verify_phase4_network.py`；`git diff --stat` => `15 files changed, 99 insertions(+), 656 deletions(-)`（不含未跟踪文件）。
- RED 证明：
  - `python .\scripts\verify_phase4_overlays.py` 首轮 RED，命中 `build.gradle` 缺少 TASK-04 quarantine 注释，以及 `mod_support` lang/tooltip 仍沿用 TASK-03 话术、未写明 TASK-04 与 Feathers 构件缺口。
  - `./gradlew.bat test --tests tfar.classicbar.CompatQuarantineBehaviorTest` 首轮 RED，2 个断言失败，同样卡在 quarantine 注释与 TASK-04 文案缺口，证明 verifier / JUnit 能拦住“只是排除目录但没有声明边界”的伪完成。
  - `python .\scripts\verify_phase4_network.py` 首轮已是 GREEN，说明 pure client 禁恢复 network 的路线在 TASK-04 开始前就没有回退。
- 关键改动：
  - `build.gradle`：保留 `compat/**` 与 `impl/overlays/mod/**` 排除，并补上 TASK-04 quarantine 注释，明确 third-party compat 只能通过后续独立恢复任务回归 active compile。
  - `scripts/verify_phase4_overlays.py`：从旧“恢复 overlay”路线改为 compat quarantine verifier；校验 sourceSet excludes、active `EventHandler` 仅注册 vanilla overlays、active source 不 import quarantined compat、third-party metadata 不误写 required，并在 build 产物存在时额外检查 `build/classes/java/main` 与 `build/libs/*.jar` 中没有 quarantined class。
  - `scripts/verify_phase4_network.py`：改为 pure client 禁恢复 network verifier，要求 `ClassicBar` 保持 `@Mod(... dist = Dist.CLIENT)`、不回注册 payload/server tick，且 legacy network 源文件继续缺席。
  - `src/test/java/tfar/classicbar/CompatQuarantineBehaviorTest.java`：新增 TASK-04 定向静态回归，覆盖 quarantine excludes、active overlay registry、active source 禁 compat/network 恢复、metadata 不误承诺 third-party support、`mod_support` 保持 reserved/quarantined 文案，并带简单 mutation 验证（移除 exclude / 回填 `new Blood()` / 把 optional 改 required / 把 TASK-04 文案替换成 TASK-03 都会失败）。
  - `src/main/java/tfar/classicbar/config/ClassicBarsConfig.java`、`src/main/resources/assets/classicbar/lang/en_us.json`、`zh_cn.json`：把 `mod_support` 与 reserved toggle 注释/tooltip 统一切到 TASK-04 语义；Tough As Nails / Vampirism / ParCool 明确“后续作为迁移后的独立任务恢复”，Feathers 明确“当前缺少可用于 NeoForge 1.21.1 的构件，因此首轮不恢复”。
- GREEN 验证：
  - `python .\scripts\verify_neoforge_config.py` => `NeoForge config verification: GREEN`
  - `python .\scripts\verify_independent_icons.py` => `Independent icon verification: GREEN`，同时继续声明 `blood/thirst/stamina/feathers` placeholder 只是 reserved replacement targets。
  - `python .\scripts\verify_phase4_overlays.py` => `Phase 4 compat quarantine verification: GREEN`
  - `python .\scripts\verify_phase4_network.py` => `Phase 4 pure-client network verification: GREEN`
  - `./gradlew.bat test --tests tfar.classicbar.CompatQuarantineBehaviorTest --tests tfar.classicbar.PureClientConvergenceTest --tests tfar.classicbar.config.NeoForgeConfigBehaviorTest` => `BUILD SUCCESSFUL`
  - `./gradlew.bat clean compileJava` => `BUILD SUCCESSFUL`；仍有 `ClassicBarsConfig.java` deprecated API note，但不阻塞任务。
  - `./gradlew.bat test`：首次与 `./gradlew.bat build` 并行触发时，`processResources` 一次性报 `Failed to clean up stale outputs`；顺序重跑 `./gradlew.bat test` 后 `BUILD SUCCESSFUL`，判断为并行验证噪音而非源码回归。
  - `./gradlew.bat build` => `BUILD SUCCESSFUL`
  - 未单独运行 `jar tf build/libs/*.jar`；替代检查是 `verify_phase4_overlays.py` 在 build 后直接扫描 `build/classes/java/main` 与 `build/libs/*.jar`，确认没有 `tfar/classicbar/compat/**` / `tfar/classicbar/impl/overlays/mod/**` class 泄漏到 active output。
- 静态 grep 结果：
  - pure client 入口 grep 仅命中 `src/main/java/tfar/classicbar/ClassicBar.java` 中的 `@Mod(value = ClassicBar.MODID, dist = Dist.CLIENT)`。
  - network/server sync 相关 token grep 在 `src/main/java` 中唯一命中是 quarantined `impl/overlays/mod/Thirst.java` 的 `Message.presentOnServer`；active compiled source 未恢复 payload / server tick 路径。
  - `neoforge.mods.toml` 未命中 `side="BOTH"`，也未声明 `toughasnails` / `vampirism` / `parcool` / `feathers` 依赖块。
- compat quarantine 状态：
  - Tough As Nails：继续 reserved/quarantined；后续可作为独立恢复任务。
  - Vampirism：继续 reserved/quarantined；后续可作为独立恢复任务。
  - ParCool：继续 reserved/quarantined；后续可作为独立恢复任务。
  - Feathers：继续 reserved/quarantined；首轮不恢复，因为当前缺少可用于 NeoForge 1.21.1 的构件。
- 后续恢复策略：
  - 每个 compat 单独立 TASK，至少补齐：NeoForge API 适配、独立 icon 资源、NeoForge config / lang、active source 不依赖缺模组环境、optional metadata（若声明）、带模组 smoke 与缺模组回退 smoke。
  - Tough As Nails / Vampirism / ParCool 虽然具备 1.21.1 NeoForge 构件线索，但仍不得借本次 TASK-04 文案或 `mod_support` 开关被视为“已支持”；恢复前必须逐项实测。
  - Feathers 在出现可用于 NeoForge 1.21.1 的构件前不进入恢复实现；即便 future task 启动，也应先解决依赖来源，再谈 overlay 迁移。
- 未验证项：
  - 未运行任何 third-party mod 组合 smoke；TASK-04 目标是 quarantine 守卫，不是 compat 恢复。
  - 未进入 README/NOTICE/UNLICENSE 收口；按用户约束留到 TASK-05 再统一处理。
- 残留风险：
  - sourceSet quarantine 能防止 active compile 阻塞，但也意味着 compat 代码债务仍被隔离保存，后续恢复任务需要重新对照 NeoForge API 与第三方版本现状清债。
- `mod_support` 开关已在 tooltip/plan 中明确 reserved 语义，但真正面对最终用户的支持矩阵文档仍待 TASK-05 收口。
- 建议：建议进入代码审查。TASK-04 已具备 RED→GREEN 证据、静态守卫、build/jar 隔离检查与后续恢复策略记录。

**执行记录 / TASK-04 审查反馈修复（2026-04-29）**
- 状态：GREEN；按审查/挑刺反馈完成最小修复，不恢复 compat，不恢复 network/server sync，也不进入 README/NOTICE/UNLICENSE 正式改动。
- RED 证明（fail-fast verifier）：
  - 在保留源码不变的前提下，临时将 `build/libs/classicbar-1.21.1-4.jar` 移走后运行 `python .\scripts\verify_phase4_overlays.py`，修复前脚本仍错误返回 `Phase 4 compat quarantine verification: GREEN`，证明“无 jar 也能 GREEN”的阻断问题真实存在。
  - 修复后复跑同一 missing-jar 分支，脚本改为 RED 并明确报出 `build/libs: no target ClassicBar mod jar was found`，满足 fail-fast 预期。
- 关键改动：
  - `scripts/verify_phase4_overlays.py`：改为强制扫描 `build/libs/classicbar-*.jar` 目标 mod jar；若缺 jar 直接 RED；GREEN 时显式输出 `Scanned target mod jar(s)`；继续检查 jar 内无 quarantine class 泄漏。
  - `scripts/verify_phase4_overlays.py`、`src/test/java/tfar/classicbar/CompatQuarantineBehaviorTest.java`：third-party dependency 守卫升级为 strict 策略，`toughasnails` / `vampirism` / `parcool` / `feathers` 任何 dependency block 都会失败，而不是仅禁止 `required`。
  - `src/main/resources/assets/classicbar/lang/en_us.json`、`zh_cn.json`：移除所有用户可见 `TASK-03` / `TASK-04` 字样，统一替换为稳定 reserved 文案；Feathers 仍保留“当前没有兼容 NeoForge 1.21.1 构件”的用户向说明。
  - `scripts/verify_phase4_network.py`：GREEN 输出改为明确区分“active source 未恢复 network”与“build exclude 的 quarantined source 仍残留 token 但仅因隔离而被接受”，避免误写成全仓无 network。
  - `src/main/java/tfar/classicbar/config/ClassicBarsConfig.java`：同步移除 reserved toggle 注释里的内部 TASK 编号，保持和 lang 一致的稳定用户表述。
  - `plans/plan_classicbar_neoforge_1_21_1_migration.md`：补记本次审查反馈修复，并把 TASK-05 README 支持矩阵明确为硬阻塞。
- GREEN 验证：
  - `python .\scripts\verify_phase4_overlays.py` => `Phase 4 compat quarantine verification: GREEN`，并输出 `Scanned target mod jar(s): build/libs/classicbar-1.21.1-4.jar`
  - `python .\scripts\verify_phase4_network.py` => `Phase 4 pure-client network verification: GREEN`，并额外注明 active source 无 network token、quarantined `src/main/java/tfar/classicbar/impl/overlays/mod/Thirst.java -> presentOnServer` 仅因 build exclude 被接受。
  - `python .\scripts\verify_neoforge_config.py`、`python .\scripts\verify_independent_icons.py`、`./gradlew.bat test --tests tfar.classicbar.CompatQuarantineBehaviorTest`、`./gradlew.bat test`、`./gradlew.bat build` 均需在本次修复后顺序复验；证据见本轮执行回执。
- 边界确认：
  - 本轮未修改 README/NOTICE/UNLICENSE；README 支持矩阵仍是 TASK-05 硬阻塞，未完成前不得宣称整体迁移文档收口或最终交付 GREEN。

### TASK-05：收口验证、文档与交付判定

**范围**
- 汇总验证证据。
- 更新 README/NOTICE/配置说明。
- 明确已支持、已隔离、未验证项目。
- 完成代码审查、收口验证与分支收官前置材料。

**文件/目录**
- `README.md`
- `NOTICE.md` / `UNLICENSE`（若本轮补协议说明）
- `plans/plan_classicbar_neoforge_1_21_1_migration.md`
- 构建与源码改动涉及文件

**依赖**
- TASK-01 至 TASK-04 执行完毕或明确降级。
- README 支持矩阵收口是 TASK-05 硬阻塞；未更新 README 前，不得宣称迁移整体收口、可发布或进入最终交付 GREEN。

**DoD**
- 记录 `gradlew.bat --version`、`clean compileJava`、`test`、`build` 的实际输出摘要与结果。
- 若未执行 `runClient` / `runServer`，不得声明游戏内 HUD 已验证或 dedicated server 已验证。
- README 支持矩阵必须与计划中的 active / quarantined / unverified 事实一致；该矩阵未落到 README 前，TASK-05 不得标记 GREEN。
- README 明确 Minecraft 1.21.1、NeoForge 21.1.x、Java 21、NeoForge config TOML、旧 JSON 处理、独立 icon、compat 支持矩阵。
- 计划文件更新任务状态与验证证据。
- 代码审查与收口验证通过后再进入分支收官，不直接 commit/push/PR。

**验证命令**
```powershell
gradlew.bat --version
gradlew.bat clean compileJava
gradlew.bat test
gradlew.bat build
gradlew.bat runClient
gradlew.bat runServer
```

**风险与回退**
- 构建通过但运行 smoke 未做：只能标记为静态通过/待运行验证。
- README 过度承诺 compat：按实际验证矩阵写，不通过则列为后续。
- 未纳入未跟踪文件：收口前必须检查 `git status --short --branch`。

## 计划审查入口

本计划写入后进入 `计划审查`。审查通过范围应至少覆盖：
- standard 计划格式；
- 接管 dirty worktree 策略；
- TASK 顺序是否符合用户指定；
- NeoForge 1.21.1 / 独立 icon / NeoForge Config 的 DoD；
- compat quarantine 是否足以防止误承诺；
- 验证命令与禁止完成声明是否明确。
