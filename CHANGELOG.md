# Changelog

## [1.0.2] - 2026-04-30

### 新增
- **食物动态预览**：手持食物时在饱食度/饱和度条上以半透明色块预览吃下后恢复量（类 AppleSkin 风格），可通过 `show_food_preview` 配置开关

### 修复
- **饱食度/饱和度渲染条溢出容器**：当其他 Mod 将饱食度/饱和度上限突破 20 后，条宽度不再超过容器背景边界
- **饱和度预览上限锁定为 20**：防止动态值异常导致预览越界

---

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

### 提交
- `8770e8e` — 8 files changed, +15/-89

---

## [1.0.0] - 2026-04-30

### 综合改进

- **数字偏移调整**：状态栏数值上移 2 像素（`yStart - 1` → `yStart - 3`）
- **数值显示格式**：新增 `TextFormat` 枚举，支持 `仅当前值` / `当前值/最大值` / `百分比` 三种模式，通过配置注册与 `textHelper` 改造实现
- **图标命名审查**：统一图标资源命名规范
- **聚合化配置评估**：配置系统聚合度评估与优化

### 配置重构

- **覆盖层颜色可配置**：Health POISON/WITHER/FROZEN 覆盖层颜色从硬编码迁移至 `ConfigCache` 动态配置
- **清理冗余注释**：移除 `ClassicBarsConfig.java` 中 49 行多余的 `.comment()` 调用

### 变种纹理

- **特效图标切换**：`Health.renderIcon()` 根据中毒/凋灵/冻伤状态自动切换对应图标纹理
- **特效覆盖层渲染**：`Health.renderBar()` 补充 WITHER / FROZEN 覆盖层颜色叠加
- **纹理资源注册**：`BarIcons.java` 新增 `HEALTH_POISON` / `HEALTH_WITHER` / `HEALTH_FROZEN` 常量
- **纹理文件**：需用户自行放置对应 PNG 文件至 `textures/gui/icons/`

### 提交
- `ec82286` — 综合改进与重构基线的统一提交
