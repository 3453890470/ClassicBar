# plan_config_overlay_refactor

**状态**：已完成

## 父计划关系

本计划是 `plan_health_variant_textures.md` 的回审改造续作。请在父计划中标记关联。

---

## 目标

1. 将 health 覆盖层颜色（POISON / WITHER / FROZEN）从硬编码改为通过 `ModConfigSpec` 配置（general section）
2. 删除 `ClassicBarsConfig.java` 中全部的冗余 `.comment()` 行（49 行源码，运行期约 85 处调用）
3. 所有新配置使用 `.translation()` + 语言文件 tooltip 模式

---

## 前置现状（勘探结论）

### 配置本地化现状
- `ClassicBarsConfig.java` 中所有配置项**已有** `.translation()` 方法调用（49 行源码，运行期约 85 处调用）
- `en_us.json` 和 `zh_cn.json` 中所有 `{translationKey}.tooltip` 已 **100% 覆盖**
- 当前 `.comment("...")` 中英文注释是冗余后备

### Health 覆盖层现状
- `Health.java.renderBar()` 中 POISON / WITHER / FROZEN 使用硬编码 `RenderSystem.setShaderColor(r,g,b,a)`
- 这 3 个值**不是**填充颜色（gradient），而是**额外绘制的半透明覆盖层纹理**
- 已有的 `ConfiguredBarSettings.color_overlay` 是针对**填充颜色**的混合，不适用于此

### 颜色格式问题
- `Color.java` 是 record `Color(int r, int g, int b)`，**不含 alpha**
- 覆盖层需要 RGBA 四通道配置
- 技术上使用 `#AARRGGBB` 格式（与现有 `BarColorOverlay` 一致），在 `ConfigCache` 中缓存为 `Color` + `float alpha` 两个字段
- `ColorUtils.java` 已有 `parseHexColor(String)` 方法，返回 `ParsedHexColor(Color color, int alpha)` record，直接复用 `ColorUtils.parseHexColor` 即可（无需新增辅助方法）

---

## 范围

### 做什么

#### Phase A: 新增 3 个覆盖层颜色配置项

**ClassicBarsConfig.java (general section)**

在 `frozen_health_color` 配置项之后，新增 3 个配置项字段：

```java
public static ConfigValue<String> healthPoisonOverlayColor;
public static ConfigValue<String> healthWitherOverlayColor;
public static ConfigValue<String> healthFrozenOverlayColor;
```

在 `specGeneral()` 或 `general` builder 中注册（紧接 `frozen_health_color` 之后）：

```java
healthPoisonOverlayColor = builder
    .translation("classicbar.config.general.health_poison_overlay_color")
    .define("health_poison_overlay_color", "#80800080", ClassicBarsConfig::isValidHexColor);
healthWitherOverlayColor = builder
    .translation("classicbar.config.general.health_wither_overlay_color")
    .define("health_wither_overlay_color", "#80595959", ClassicBarsConfig::isValidHexColor);
healthFrozenOverlayColor = builder
    .translation("classicbar.config.general.health_frozen_overlay_color")
    .define("health_frozen_overlay_color", "#804D80FF", ClassicBarsConfig::isValidHexColor);
```

**注意：不加 `.comment()`**（与全量清理一致）

---

**ConfigCache.java**

新增字段和 bake 逻辑：

```java
public static Color healthPoisonOverlay;
public static float healthPoisonOverlayAlpha;
public static Color healthWitherOverlay;
public static float healthWitherOverlayAlpha;
public static Color healthFrozenOverlay;
public static float healthFrozenOverlayAlpha;
```

在 `bake()` 中添加读取逻辑（复用 `ColorUtils.parseHexColor`）：

```java
// 使用 ColorUtils.parseHexColor 解析 #RRGGBB / #AARRGGBB
ColorUtils.ParsedHexColor parsed = ColorUtils.parseHexColor(ClassicBarsConfig.healthPoisonOverlayColor.get());
if (parsed != null) {
    healthPoisonOverlay = parsed.color();
    healthPoisonOverlayAlpha = parsed.alphaAsFloat();
} else {
    healthPoisonOverlay = Color.BLACK;
    healthPoisonOverlayAlpha = 1.0f;
}
// 同理 for wither / frozen
```

**注：** 当 `parseHexColor` 返回 null 时（非法配置值），使用 `Color.BLACK` + `alpha=1.0f` 作为安全 fallback，避免静默失败。

---

**Health.java renderBar()**

在 `Health.java` 顶部新增 import：
```java
import tfar.classicbar.config.ConfigCache;
```

将 3 个覆盖层分支中的硬编码 `setShaderColor` 替换为从 ConfigCache 读取：

```java
if (effect == HealthEffect.POISON) {
    Color c = ConfigCache.healthPoisonOverlay;
    RenderSystem.setShaderColor(c.r()/255f, c.g()/255f, c.b()/255f, ConfigCache.healthPoisonOverlayAlpha);
    ModUtils.drawTexturedModalRect(graphics, f + 1, yStart + 1, 1, 36, barWidth, 7);
    Color.reset();
}
// 同理 for WITHER / FROZEN
```

删除现有的 `// TODO: tune color` 注释（因为现在可在配置文件调优）。

---

**语言文件 en_us.json**

新增 6 条：

```json
"classicbar.config.general.health_poison_overlay_color": "Poison overlay color",
"classicbar.config.general.health_poison_overlay_color.tooltip": "Overlay tint applied to the health bar while poisoned. #RRGGBB for opaque, #AARRGGBB for alpha.",
"classicbar.config.general.health_wither_overlay_color": "Wither overlay color",
"classicbar.config.general.health_wither_overlay_color.tooltip": "Overlay tint applied to the health bar while withered. #RRGGBB for opaque, #AARRGGBB for alpha.",
"classicbar.config.general.health_frozen_overlay_color": "Frozen overlay color",
"classicbar.config.general.health_frozen_overlay_color.tooltip": "Overlay tint applied to the health bar while frozen. #RRGGBB for opaque, #AARRGGBB for alpha.",
```

---

**语言文件 zh_cn.json**

对应新增 6 条中文翻译：

```json
"classicbar.config.general.health_poison_overlay_color": "中毒覆盖层颜色",
"classicbar.config.general.health_poison_overlay_color.tooltip": "中毒时生命条上的覆盖层色调。#RRGGBB 为不透明，#AARRGGBB 可指定透明度。",
"classicbar.config.general.health_wither_overlay_color": "凋零覆盖层颜色",
"classicbar.config.general.health_wither_overlay_color.tooltip": "凋零时生命条上的覆盖层色调。#RRGGBB 为不透明，#AARRGGBB 可指定透明度。",
"classicbar.config.general.health_frozen_overlay_color": "冰冻覆盖层颜色",
"classicbar.config.general.health_frozen_overlay_color.tooltip": "冰冻时生命条上的覆盖层色调。#RRGGBB 为不透明，#AARRGGBB 可指定透明度。",
```

---

#### Phase B: 全量清理冗余 .comment()

**ClassicBarsConfig.java**

删除文件中所有 `.comment("...")` 调用（49 行源码，运行期约 85 处调用）。模式示例：

```java
// 修改前:
.translation("classicbar.config.general.display_icons")
.comment("Whether to show ClassicBar's independent icon textures...")
.define("display_icons", true);

// 修改后:
.translation("classicbar.config.general.display_icons")
.define("display_icons", true);
```

对于空的 `.comment("")` 或仅有占位符的注释也要清理。

---

### 不做什么

- 不修改除 health 之外的其他状态栏的覆盖层逻辑
- 不修改 `BarColorOverlay` 机制
- 不修改 `Color.java` record 结构（用两个字段 Color + alpha 替代）
- 不做 mod 兼容性恢复
- 不修改测试文件（如后续测试因删除 .comment 而失效才修复）

---

## 执行顺序

1. **Phase B 先行**：删除 `ClassicBarsConfig.java` 中全部 `.comment()` 行（噪音大但机械，先做完减少后续 merge 冲突）
2. **Phase A - Config**：`ClassicBarsConfig.java` 新增 3 个配置字段
3. **Phase A - Cache**：`ConfigCache.java` 新增字段 + bake() 逻辑（复用 ColorUtils.parseHexColor）
4. **Phase A - Render**：`Health.java` 替换硬编码为 ConfigCache 读取
5. **Phase A - Lang**：`en_us.json` + `zh_cn.json` 新增各 6 条翻译
6. **编译验证**：`gradlew.bat clean compileJava`
7. **测试验证**：`gradlew.bat test`

---

## DoD（完成判据）

- [ ] `ClassicBarsConfig.java` 中所有 `.comment("...")` 调用已删除
- [ ] `ClassicBarsConfig.java` 新增 3 个配置字段（`health_poison_overlay_color`、`health_wither_overlay_color`、`health_frozen_overlay_color`）
- [ ] 新配置使用 `.translation()` 且不包含 `.comment()`
- [ ] `ConfigCache.java` 新增 3 组 (Color + alpha) 字段 + bake() 逻辑
- [ ] `Health.java.renderBar()` 中 3 个覆盖层从 ConfigCache 读取颜色，无硬编码 RGBA
- [ ] 已删除硬编码颜色的 `// TODO: tune color` 注释
- [ ] `en_us.json` 新增 6 条翻译
- [ ] `zh_cn.json` 新增 6 条翻译
- [ ] `gradlew.bat clean compileJava` BUILD SUCCESSFUL
- [ ] `gradlew.bat test` 全部通过

---

## 风险与回退

| 风险 | 影响 | 回退方案 |
|------|------|----------|
| 删除 `.comment()` 后语言文件未加载时配置无注释 | 配置界面显示空白 | 用户确认的清理行为。NeoForge 运行时环境下语言文件正常加载 |
| `parseHexColor` 解析格式不匹配 | 覆盖层颜色不正确 | 默认值使用 `#AARRGGBB` 格式，与现有 `BarColorOverlay` 的 `#RRGGBB` 兼容。日志输出解析值辅助调试 |
| 编译失败 | 无法通过编译 | 优先检查 import 和 `ColorUtils.parseHexColor` 返回值处理 |
| ConfigCache 新增字段导致其他类编译失败 | 间接编译错误 | 补充缺失的 import 后重新编译 |

---

## 执行记录

- **执行日期**：2026-04-30
- **执行人**：猫娘编写官-米娅
- **审查人**：猫娘审查官-艾琳
- **编译结果**：✅ BUILD SUCCESSFUL（gradlew.bat clean compileJava）
- **测试结果**：✅ BUILD SUCCESSFUL（gradlew.bat test）
- **Phase B**：ClassicBarsConfig.java — 删除全部 49 行冗余 .comment() 调用
- **Phase A - Config**：ClassicBarsConfig.java — 新增 3 个配置字段（health_poison/wither/frozen_overlay_color），使用 .translation() 无 .comment()
- **Phase A - Cache**：ConfigCache.java — 新增 3 组 (Color + alpha) 缓存字段 + bake() 复用 ColorUtils.parseHexColor
- **Phase A - Render**：Health.java — 覆盖层颜色从硬编码改为 ConfigCache 读取；删除 TODO 注释
- **Phase A - Lang**：en_us.json + zh_cn.json 各新增 6 条翻译
