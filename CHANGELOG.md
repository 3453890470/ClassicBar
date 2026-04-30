# Changelog

## [1.0.1] - 2026-04-30

### 修复
- **译名修正**：配置界面中 `食物条` → `饱食度条`（`zh_cn.json`）
- **凋灵/中毒/冻伤图标**：等待提供正确的 PNG 文件后替换（搁置中）

### 清理
- **移除 YAML Config 死依赖**：`build.gradle` 中 `compileOnly` + `runtimeOnly` YAML Config 依赖已删除
- **删除 COMPAT 兼容模式**：全链路清理，涉及 7 个文件
  - `BarMode.java`：枚举仅保留 `OVERRIDE` / `DISABLED`
  - `EventHandler.java`：移除 COMPAT 偏移检测块
  - `HudRenderContext.java`：移除 compat 偏移字段与方法
  - `zh_cn.json` / `en_us.json`：移除 `bar_mode.compat` 条目及相关 tooltip 文案
  - `NeoForgeConfigBehaviorTest.java`：移除 COMPAT 语言键校验
  - `EventHandlerCancellationBehaviorTest.java`：移除 COMPAT 相关测试方法

### 提交信息
- **Commit**: `8770e8e`
- **分支**: `1.21.1`
- **变更统计**: 8 个文件修改，+15 / -89 行
