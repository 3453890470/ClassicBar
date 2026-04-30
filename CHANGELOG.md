# Changelog

## [1.2.0] - 2026-05-01
### Added
- Vampirism 血液状态栏支持
  - 独立的 Blood overlay 实现，深红/亮红双色进度条
  - 吸血鬼状态下自动隐藏饱食度栏，血液栏占据其位置
  - 自动取消 Vampirism 原生血条 HUD 叠加
  - 完整的 `bars.blood.*` 配置项（模式/图标/文字/颜色）
- 可拓展的位置锚点排序系统
  - DEFAULT_PRIORITIES 映射表，互斥栏共用同一优先级序号
  - 新增状态栏通过 `Map.of("id", priority)` 一行指定默认位置
- CODING_RULES.md 项目代码规范文档（9 章 + 自审查清单）

### Changed
- 配置层互斥策略：`getBarSettings()` 中 blood 启用时自动禁用 food
- 所有状态栏代码风格统一：4-space 缩进、英文注释、@Override 补全、类级 Javadoc
- ACTIVE_BAR_IDS 纳入 blood，走统一排序通道（无特判代码）
- Hunger.shouldRender() 简化回归，移除 Vampirism 检测逻辑
- Blood.java 颜色语义对齐 vanilla 组（primary=主色）

### Fixed
- 血液栏出现在右侧顶端而非食物栏位置的问题
- 饱食度栏与血液栏同时渲染的问题
- 血液栏数值文字默认不显示的问题
- MountHealth.java 缩进自相矛盾 + 死字段
- Air.java 缩进自相矛盾 + 缺失空行
- Absorption.java 类型转换错误 + 缺失 @Override

## [1.1.0] — 2026-05-01

### ✨ 新功能
- **农夫乐事兼容**：检测滋养效果，食物栏图标层叠 + 金色条覆盖层
- **森罗物语:厨房兼容**：检测饱腹代偿效果，食物栏图标层叠 + 红色条覆盖层
- **多重效果图标层叠系统**：
  - 食物栏：基础图标 + 饥饿 + 滋养 + 饱腹代偿依次层叠露出 4px
  - 生命栏：基础图标 + 中毒 + 凋零 + 冻伤依次层叠露出 4px
  - 条覆盖层叠加：多个效果可同时叠加半透明色层
  - 侧边自适应：图标向远离条的方向延伸
  - 文字位置自动跟随图标末端

### ⚙️ 配置重构
- 布局排序从文本列表改为三态按钮（LEFT / RIGHT / HIDDEN）+ 排序优先级
- 数值显示格式从文本输入改为按钮选择器
- 新增 FarmersDelight 滋养覆盖层禁用开关（默认开启）
- 新增 `nourishment_bar_color` / `satiated_shield_bar_color` 颜色配置

### 🔧 优化
- 闪烁时机优化：仅在数值变化时闪烁 2 tick 而非持续闪烁
- 效果图标闪烁同步规则精细化
- 修复文字位置「过度偏右」的双重偏移问题
- 补全缺失的 `show_food_preview` 本地化

### 📦 依赖
- FarmersDelight 和 KaleidoscopeCookery 作为可选依赖加入开发环境
- compileOnly + runtimeOnly 双作用域，运行时可选加载

## [1.0.5] — 2026-04-30
- **优化**：图标闪烁切换频率加快（每 2 tick），闪烁持续时间缩短（10 tick）
- **修复**：吸收条颜色不再随中毒/凋零/冰冻效果变化，始终使用统一配色；冻伤时颜色覆盖层消失的 bug 同步修复
- **版本号** 1.0.4 → 1.0.5

## [1.0.4] — 2026-04-30
- **新增**：图标闪烁变种——生命值减少、饱食度减少、缺氧时图标在 normal/blinking 纹理间切换闪烁
- **新增**：7 个闪烁变种贴图（health/poison/wither/frozen/food/food_hunger/air 的 blinking 变种）
- **重构**：闪烁机制从 alpha 透明度改为纹理替换（`ModUtils.drawIconWithFlash`）
- **修复**：生命值减少闪烁不再依赖 invulnerableTime，所有伤害类型均触发
- **版本号** 1.0.3 → 1.0.4

## [1.0.3] — 2026-04-30
- **新增**：食物预览——手持食物时在饱食度/饱和度条上显示呼吸闪烁的预览增量条，叠加预览覆盖层
- **新增**：饥饿状态图标变种——饥饿效果时饱食度图标切换为暗绿色变种 (`food_hunger.png`)
- **重构**：图标变种逻辑聚类到 `ModUtils.drawIconWithTexture`，统一 7 个状态栏图标绘制方法
- **调整**：状态栏数值文字下移 1 像素
- **修复**：颜色覆盖层超出容器边界——预览条/覆盖层均 clamp 在容器有效区域 `[xStart+2, xStart+79]` 内
- **修复**：预览覆盖层 y 坐标与容器顶部对齐，合并为统一覆盖层
- **修复**：饱食度/饱和度渲染条溢出容器——`Math.min(WIDTH, ...)` 溢出保护

---

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
