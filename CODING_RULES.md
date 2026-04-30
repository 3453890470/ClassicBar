# ClassicBar 代码规范与架构规则

> 适用项目：ClassicBar — NeoForge 1.21.1 客户端 HUD 模组  
> 模组 ID：`classicbar`  
> 包根：`tfar.classicbar`  
> 本文档结合项目实际代码结构、Vampirism 血液栏集成经验及代码风格统一审查结论编写。

---

## 1. 项目概览

### 1.1 项目定位

ClassicBar 是一个 **NeoForge 1.21.1 纯客户端 HUD 模组**，以经典 RPG 风格进度条替代 Minecraft 原版 HUD 状态栏（生命、盔甲、饥饿值等），并提供第三方模组兼容支持。

- **模组主类**：`tfar.classicbar.ClassicBar`
- **入口点**：`@Mod(value = ClassicBar.MODID, dist = Dist.CLIENT)`
- **配置注册**：构造函数中调用 `modContainer.registerConfig(ModConfig.Type.CLIENT, ClassicBarsConfig.CLIENT_SPEC)`
- **客户端初始化委托**：`ClassicBarClient.init(modBus)`

```java
// ✅ 正确：主类结构
@Mod(value = ClassicBar.MODID, dist = Dist.CLIENT)
public class ClassicBar {
    public static final String MODID = "classicbar";
    public static final Logger logger = LogManager.getLogger();

    public ClassicBar(IEventBus modBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, ClassicBarsConfig.CLIENT_SPEC);
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        ClassicBarClient.init(modBus);
    }
}
```

### 1.2 构建配置要点

- **模组依赖**：第三方兼容模组（如 Vampirism）使用 `compileOnly`，**不打包**第三方 API
- **运行环境**：需搭配对应模组方可测试兼容功能

---

## 2. 架构总览

### 2.1 包结构

```
tfar.classicbar
├── api/               # 公共接口与枚举
│   ├── BarOverlay.java         # overlay 核心接口
│   ├── BarSettings.java        # 配置快照（show_text, icon, mode, color_overlay, textFormat）
│   ├── BarMode.java            # OVERRIDE / DISABLED
│   ├── BarPlacement.java       # LEFT / RIGHT / HIDDEN
│   ├── BarColorOverlay.java    # 颜色叠加层 record
│   └── TextFormat.java         # CURRENT_ONLY / CURRENT_MAX / PERCENT_MAX
├── client/            # 客户端渲染与事件
│   ├── EventHandler.java               # 生命周期管理，渲染入口
│   ├── HudRenderContext.java           # 渲染上下文（Y 偏移管理）
│   ├── VanillaLayerCancellationPolicy.java  # 原版层取消决策
│   └── ClassicBarClient.java           # 客户端初始化（事件总线注册）
├── compat/            # 跨模组兼容工具
│   └── ModCompat.java
├── config/            # NeoForge 配置系统
│   ├── ClassicBarsConfig.java   # 配置定义 + 查询逻辑
│   └── ConfigCache.java         # 运行时缓存，减少配置访问开销
├── impl/              # overlay 实现
│   ├── BarOverlayImpl.java      # 抽象基类，封装公共渲染管线
│   └── overlays/
│       ├── mod/                 # 第三方模组 overlay（例：Blood）
│       └── vanilla/             # 原版状态栏 overlay
│           ├── Health.java
│           ├── Armor.java
│           ├── Absorption.java
│           ├── Hunger.java
│           ├── ArmorToughness.java
│           ├── MountHealth.java
│           └── Air.java
├── resources/         # 图标资源
│   └── BarIcons.java
└── util/              # 工具类
    ├── Color.java
    ├── ColorUtils.java
    ├── ModUtils.java
    └── HealthEffect.java
```

### 2.2 核心类职责

| 类 | 职责 |
|---|---|
| `ClassicBarsConfig` | 所有配置项定义、配置项查询（`getBarSettings`）、布局排序（`getLeftOrder`/`getRightOrder`） |
| `EventHandler` | overlay 生命周期（`bootstrap`/`cacheConfigs`）、渲染循环（`render`）、原版层取消（`disableVanillaLayers`） |
| `BarOverlayImpl` | 抽象基类，封装 `render()` 主流程、纹理绑定、颜色应用、Y 偏移推进 |
| `HudRenderContext` | 跟踪左右两侧当前 Y 偏移（初始 39，每渲染一个同侧条 +10） |
| `ConfigCache` | 将 `ModConfigSpec` 值烘焙为运行时友好的缓存对象（`Color` 实例等） |

### 2.3 核心数据流

```
配置（ModConfigSpec）
  ↓ ClassicBarsConfig.getBarSettings(name)
  ↓ 返回 BarSettings（含 mode / icon / show_text / color_overlay）
  ↓ EventHandler.cacheConfigs()
  ↓ 遍历 registry，对每个 overlay 注入 BarSettings
  ↓ 按 ACTIVE_BAR_IDS + 追加通道 构建 all 列表
  ↓ all 列表传递至 render() 循环
  ↓ 逐 overlay 调用 render(context, graphics, player, sw, sh, vOffset)
  ↓ context.increment(rightHandSide, 10) 推进同侧 Y 偏移
```

```java
// ✅ 正确：cacheConfigs 构建 all 列表
public static void cacheConfigs() {
    bootstrap();
    all.clear();
    registry.values().forEach(barOverlay ->
        barOverlay.setBarSettings(ClassicBarsConfig.getBarSettings(barOverlay.name())));
    Set<String> appliedOverlays = new LinkedHashSet<>();
    applyConfiguredBars(ClassicBarsConfig.getLeftOrder(), false, appliedOverlays);
    applyConfiguredBars(ClassicBarsConfig.getRightOrder(), true, appliedOverlays);
    // 追加通道：非 ACTIVE_BAR 的额外 overlay
    for (BarOverlay overlay : registry.values()) {
        String name = overlay.name();
        if (!ClassicBarsConfig.isActiveBarId(name) && !appliedOverlays.contains(name)) {
            // ...
        }
    }
    // ...
}
```

---

## 3. 代码风格规则

### 3.1 缩进

**规则**：全项目统一 **4-space** 缩进，禁止混用 2-space 或 Tab。

```java
// ✅ 正确：4-space 缩进
public void renderBar(HudRenderContext context, GuiGraphics graphics, Player player,
        int screenWidth, int screenHeight, int vOffset) {
    double barWidth = getBarWidth(player);
    int xStart = screenWidth / 2 + getHOffset();
    int yStart = screenHeight - vOffset;
    if (isFitted() && rightHandSide()) {
        xStart += BarOverlayImpl.WIDTH - barWidth;
    }
    // ...
}

// ❌ 错误：2-space 缩进
public void renderBar(HudRenderContext context, GuiGraphics graphics, Player player,
    int screenWidth, int screenHeight, int vOffset) {
  double barWidth = getBarWidth(player);
  int xStart = screenWidth / 2 + getHOffset();
  // ...
}
```

### 3.2 花括号（Egyptian Braces）

**规则**：左花括号同行（非 K&R 变体），右花括号独占一行。`if`/`else` 即使单行也必须加花括号。

```java
// ✅ 正确：Egyptian braces
public Blood() {
    super("blood");
}

@Override
public boolean shouldRender(Player player) {
    if (!ModList.get().isLoaded("vampirism")) {
        return false;
    }
    return getVampirePlayer(player) != null;
}

// ❌ 错误：左花括号换行
public Blood()
{
    super("blood");
}

// ❌ 错误：单行省略花括号
if (!ModList.get().isLoaded("vampirism")) return false;
```

### 3.3 注释

**规则**：
- 类级别：必须有 `/** */` Javadoc，描述职责 + 颜色方案（若有）
- 方法级别：关键逻辑用 `//` 行注释，英文
- **禁止中文注释**（允许中英双语类 Javadoc，但行内逻辑注释必须英文）

```java
// ✅ 正确：类 Javadoc
/**
 * ClassicBar overlay for {@link Player} health.
 * <p>
 * Renders the player's health as a classic-style progress bar
 * with gradient colors for normal/poisoned/withered/frozen states.
 */
public class Health extends BarOverlayImpl { ... }

// ✅ 正确：关键逻辑行注释，英文
private static IVampirePlayer getVampirePlayer(Player player) {
    try {
        IVampirePlayer vampire = VampirismAPI.vampirePlayer(player);
        // maxBlood == 0 distinguishes non-vampire players
        if (vampire != null && vampire.getBloodStats().getMaxBlood() > 0) {
            return vampire;
        }
        return null;
    } catch (Exception e) {
        return null;
    }
}

// ❌ 错误：中文逻辑注释
// 获取吸血鬼玩家实例
private static IVampirePlayer getVampirePlayer(Player player) { ... }

// ❌ 错误：无意义的评论
// default constructor
public Blood() {
    super("blood");
}
```

### 3.4 `@Override`

**规则**：覆写方法必须加 `@Override`。

```java
// ✅ 正确：所有覆写都有 @Override
@Override
public boolean shouldRender(Player player) {
    return true;
}

@Override
public double getBarWidth(Player player) {
    double hunger = player.getFoodData().getFoodLevel();
    return WIDTH * hunger / 20;
}

// ❌ 错误：缺少 @Override
public boolean shouldRender(Player player) {
    return true;
}
```

### 3.5 import

**规则**：显式 import，禁止通配符 `*`。

```java
// ✅ 正确：显式 import
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.util.Color;

// ❌ 错误：通配符 import
import net.minecraft.client.gui.*;
import tfar.classicbar.impl.*;
import tfar.classicbar.util.*;
```

### 3.6 参数命名

**规则**：`renderBar` 及其同类方法中，`GuiGraphics` 参数统一命名为 `graphics`，不得使用 `matrices`。

```java
// ✅ 正确：graphics
public void renderBar(HudRenderContext context, GuiGraphics graphics, Player player,
        int screenWidth, int screenHeight, int vOffset) {
    // ...
}

// ❌ 错误：matrices（过时命名）
public void renderBar(HudRenderContext context, GuiGraphics matrices, Player player, ...) {
    // ...
}
```

---

## 4. 状态栏实现规范（BarOverlay）

### 4.1 必须履行的契约

每个 overlay 类必须满足以下条件：

| 要求 | 说明 |
|---|---|
| 继承 `BarOverlayImpl` | 所有 overlay 的基类 |
| 构造器调用 `super("id")` | `id` 必须唯一，与配置键名一致 |
| 实现 `shouldRender(Player)` | 控制此 overlay 是否应被渲染 |
| 实现 `getBarWidth(Player)` | 返回当前进度条宽度（0 ~ `WIDTH`） |
| 实现 `getPrimaryBarColor(int, Player)` | 主色（前景/填充） |
| 实现 `getSecondaryBarColor(int, Player)` | 辅色（背景/装饰） |
| 实现 `renderBar(context, graphics, player, sw, sh, vOffset)` | 完整的 bar 渲染逻辑 |
| 实现 `renderText(graphics, player, w, h, vOffset)` | 数值文本 |
| 实现 `renderIcon(graphics, player, w, h, vOffset)` | 图标 |

```java
// ✅ 正确：完整的 overlay 结构
public class Blood extends BarOverlayImpl {

    public Blood() {
        super("blood");
    }

    @Override
    public boolean shouldRender(Player player) {
        if (!ModList.get().isLoaded("vampirism")) return false;
        return getVampirePlayer(player) != null;
    }

    @Override
    public double getBarWidth(Player player) {
        IVampirePlayer vampire = getVampirePlayer(player);
        if (vampire == null) return 0;
        int bloodLevel = vampire.getBloodLevel();
        int maxBlood = Math.max(1, vampire.getBloodStats().getMaxBlood());
        return Math.min(BarOverlayImpl.WIDTH, Math.ceil(BarOverlayImpl.WIDTH * (double) bloodLevel / maxBlood));
    }

    @Override
    public Color getPrimaryBarColor(int index, Player player) {
        return ColorUtils.hex2Color("#FF4444");  // accent / saturation
    }

    @Override
    public Color getSecondaryBarColor(int index, Player player) {
        return ColorUtils.hex2Color("#AA0000");  // main blood bar
    }

    @Override
    public void renderBar(HudRenderContext context, GuiGraphics graphics, Player player,
            int screenWidth, int screenHeight, int vOffset) {
        double barWidth = getBarWidth(player);
        int xStart = screenWidth / 2 + getHOffset();
        int yStart = screenHeight - vOffset;

        Color.reset();
        renderFullBarBackground(graphics, xStart, yStart);

        double barX = xStart + (rightHandSide() ? BarOverlayImpl.WIDTH - barWidth : 0);
        applyConfiguredBarColor(getPrimaryBarColor(0, player));
        renderPartialBar(graphics, barX + 2, yStart + 2, barWidth);
    }

    @Override
    public void renderText(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
        // ...
    }

    @Override
    public void renderIcon(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
        // ...
    }
}
```

### 4.2 颜色语义

- **`getPrimaryBarColor`**：主色（前景填充）。对于有两个填充层的栏（如 Hunger — 饱和度层），这是上层/叠加层颜色。
- **`getSecondaryBarColor`**：辅色（背景/下层填充）。对于 Hunger，这是基础饥饿条颜色。
- 所有颜色值通过 `applyConfiguredBarColor(color)` 或 `getConfiguredTextColor(color)` 应用，以便 `BarColorOverlay` 叠加生效。

```java
// Hunger.java 中的正确用法
@Override
public Color getPrimaryBarColor(int index, Player player) {
    // 主色 = 饱和度层
    boolean hunger = player.hasEffect(MobEffects.HUNGER);
    return hunger ? ConfigCache.saturationDebuff : ConfigCache.saturation;
}

@Override
public Color getSecondaryBarColor(int index, Player player) {
    // 辅色 = 基础饥饿层
    boolean hunger = player.hasEffect(MobEffects.HUNGER);
    return hunger ? ConfigCache.hungerDebuff : ConfigCache.hunger;
}
```

### 4.3 背景渲染

**规则**：根据 `isFitted()` 决定背景渲染方法。

```java
// ✅ 正确：renderBarBackground 中的分派
public void renderBarBackground(GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset) {
    double barWidth = getBarWidth(player);
    int xStart = screenWidth / 2 + getHOffset();
    if (isFitted() && rightHandSide()) {
        xStart += WIDTH - barWidth;
    }
    int yStart = screenHeight - vOffset;

    if (isFitted()) {
        drawScaledBarBackground(graphics, barWidth, xStart, yStart + 1);
    } else {
        renderFullBarBackground(graphics, xStart, yStart);
    }
}
```

- `isFitted()` 返回 `true` → 使用 `renderBarBackground()` 自动选择缩放背景
- `isFitted()` 返回 `false`（或默认）→ 使用 `renderFullBarBackground()`

---

## 5. 渲染管线规则

### 5.1 `all` 列表构建

`all` 列表由 `cacheConfigs()` 方法在配置加载/重载时构建。顺序决定屏幕位置（上下顺序）。

```java
// ✅ 正确：cacheConfigs 流程
public static void cacheConfigs() {
    bootstrap();                          // 确保 registry 已填充
    all.clear();                          // 清空旧列表
    // 1. 注入所有 registry 的 BarSettings
    registry.values().forEach(barOverlay ->
        barOverlay.setBarSettings(ClassicBarsConfig.getBarSettings(barOverlay.name())));

    // 2. ACTIVE_BAR 通过 applyConfiguredBars 统一排序
    Set<String> appliedOverlays = new LinkedHashSet<>();
    applyConfiguredBars(ClassicBarsConfig.getLeftOrder(), false, appliedOverlays);
    applyConfiguredBars(ClassicBarsConfig.getRightOrder(), true, appliedOverlays);

    // 3. 追加通道：非 ACTIVE_BAR 默认右侧
    for (BarOverlay overlay : registry.values()) {
        String name = overlay.name();
        if (!ClassicBarsConfig.isActiveBarId(name) && !appliedOverlays.contains(name)) {
            BarSettings settings = ClassicBarsConfig.getBarSettings(name);
            overlay.setBarSettings(settings);
            if (settings.rendersClassicBar() && appliedOverlays.add(name)) {
                all.add(overlay.setSide(true)); // 默认右侧
            }
        }
    }
    // ...
}
```

### 5.2 Y 坐标计算

Y 坐标 = `screenHeight - vOffset`，从屏幕底部向上累加。

```java
// HudRenderContext 管理两侧独立的偏移
public class HudRenderContext {
    private int leftHeight = 39;   // 左侧从底部向上 39px 开始
    private int rightHeight = 39;  // 右侧从底部向上 39px 开始

    public void increment(boolean right, int amount) {
        if (right) rightHeight += amount;
        else leftHeight += amount;
    }

    public int getOffset(boolean right) {
        return right ? rightHeight : leftHeight;
    }
}
```

### 5.3 偏移管理

- 初始偏移：左右侧均为 **39**
- 每渲染一个同侧 overlay，通过 `context.increment(rightHandSide(), 10)` 推进 **+10**
- 左右侧 offset **独立管理**

### 5.4 排序规则

- 所有 `ACTIVE_BAR` 通过 `applyConfiguredBars()` 统一排序，**无特判**
- 排序依据：`barId` 在左侧/右侧列表中的 `sort_priority` 字段
- 非 `ACTIVE_BAR`（追加通道）**默认右侧渲染**

---

## 6. 配置系统规则

### 6.1 ACTIVE_BAR_IDS

声明所有参与排序的栏，顺序决定默认优先级（索引 + 1）。

```java
// ✅ 正确：声明 ACTIVE_BAR_IDS
private static final List<String> ACTIVE_BAR_IDS = List.of(
    "health", "armor", "absorption", "food",
    "armor_toughness", "health_mount", "air", "blood"
);
```

**规则**：新加 ACTIVE_BAR 必须在列表末尾追加，除非明确需要改变默认顺序。

### 6.2 registerBarConfig

为每个 ACTIVE_BAR 在 `bars.*` 配置段注册完整的配置项（show_text, icon, mode, color_overlay, textFormat）。

```java
// ✅ 正确：在构造器中按 ACTIVE_BAR_IDS 顺序注册
builder.translation(sectionKey("bars")).push("bars");
registerBarConfig(builder, "health", BarIcons.HEALTH, true);
registerBarConfig(builder, "armor", BarIcons.ARMOR, true);
registerBarConfig(builder, "absorption", BarIcons.ABSORPTION, true);
registerBarConfig(builder, "food", BarIcons.FOOD, true);
registerBarConfig(builder, "armor_toughness", BarIcons.ARMOR_TOUGHNESS, true);
registerBarConfig(builder, "health_mount", BarIcons.MOUNT_HEALTH, true);
registerBarConfig(builder, "air", BarIcons.AIR, true);
registerBarConfig(builder, "blood", BarIcons.BLOOD, true);
builder.pop();
```

### 6.3 DEFAULT_PRIORITIES

**互斥栏共用同一优先级序号**，确保它们占据相同屏幕位置。

```java
// ✅ 正确：blood 和 food 互斥，共用优先级 4
private static final Map<String, Integer> DEFAULT_PRIORITIES = Map.of(
    "blood", 4  // blood 和 food 互斥，共用同一位置
);
```

### 6.4 FALLBACK_SETTINGS

非 ACTIVE_BAR 的备用配置。当 `getBarSettings` 在 `BAR_CONFIGS` 中找不到时，回退到此映射。

```java
// ✅ 正确：注册备用配置
private static void registerFallbackBarSettings() {
    FALLBACK_SETTINGS.put("blood",
        new BarSettings(true, BarIcons.BLOOD, BarMode.OVERRIDE, BarColorOverlay.none()));
    FALLBACK_SETTINGS.put("feathers",
        new BarSettings(false, BarIcons.FEATHERS, BarMode.DISABLED, BarColorOverlay.none()));
    FALLBACK_SETTINGS.put("stamina",
        new BarSettings(false, BarIcons.STAMINA, BarMode.DISABLED, BarColorOverlay.none()));
    FALLBACK_SETTINGS.put("thirst_level",
        new BarSettings(false, BarIcons.THIRST, BarMode.DISABLED, BarColorOverlay.none()));
}
```

### 6.5 getBarSettings 查找链

```
BAR_CONFIGS（bars.* 段配置）
  ↓ 存在 → 返回 ConfiguredBarSettings.toBarSettings()
  ↓ 不存在
FALLBACK_SETTINGS（备用配置）
  ↓ 存在 → 返回 copy()
  ↓ 不存在
NULL_SETTINGS（完全禁用）
  → new BarSettings(false, BarIcons.FALLBACK, BarMode.DISABLED, BarColorOverlay.none())
```

```java
// ✅ 正确：查找链实现
public static BarSettings getBarSettings(String overlayName) {
    ConfiguredBarSettings configuredBar = BAR_CONFIGS.get(overlayName);
    if (configuredBar != null) {
        BarSettings settings = configuredBar.toBarSettings();
        // 互斥检测在这里实现
        if ("food".equals(overlayName)) {
            BarSettings bloodSettings = getBarSettings("blood");
            if (bloodSettings.rendersClassicBar()) {
                return new BarSettings(false, BarIcons.FOOD, BarMode.DISABLED, BarColorOverlay.none());
            }
        }
        return settings;
    }
    return FALLBACK_SETTINGS.getOrDefault(overlayName, NULL_SETTINGS).copy();
}
```

---

## 7. 模组兼容规则（从 Vampirism 集成提炼）

### 7.1 依赖策略

- 使用 `compileOnly` 依赖第三方模组 API，**不打包**第三方类到 jar 中
- 运行时通过 `ModList.get().isLoaded("modid")` 守卫所有第三方 API 访问

```java
// ✅ 正确：ModList.isLoaded 作为第一道守卫
@Override
public boolean shouldRender(Player player) {
    if (!ModList.get().isLoaded("vampirism")) return false;
    return getVampirePlayer(player) != null;
}

// ❌ 错误：未守卫直接调用第三方 API
private static IVampirePlayer getVampirePlayer(Player player) {
    return VampirismAPI.vampirePlayer(player);  // 模组未安装时 ClassNotFoundException
}
```

### 7.2 配置层互斥

通过 `getBarSettings()` 实现互斥，不在 `shouldRender()` 中检测。

```java
// ✅ 正确：在 getBarSettings 中实现 blood↔food 互斥
public static BarSettings getBarSettings(String overlayName) {
    ConfiguredBarSettings configuredBar = BAR_CONFIGS.get(overlayName);
    if (configuredBar != null) {
        BarSettings settings = configuredBar.toBarSettings();
        if ("food".equals(overlayName)) {
            BarSettings bloodSettings = getBarSettings("blood");
            if (bloodSettings.rendersClassicBar()) {
                return new BarSettings(false, BarIcons.FOOD, BarMode.DISABLED, BarColorOverlay.none());
            }
        }
        return settings;
    }
    // ...
}
```

### 7.3 注册条件

在 `bootstrap()` 中用 `ModList.isLoaded` 条件注册，不在无条件注册列表中硬编码。

```java
// ✅ 正确：条件注册
public static void bootstrap() {
    if (!registry.isEmpty()) return;

    ClassicBar.logger.info("Registering Vanilla Overlays");
    registerAll(new Health(), new Armor(), new Absorption(), new Hunger(),
        new ArmorToughness(), new MountHealth(), new Air());

    if (ModList.get().isLoaded("vampirism")) {
        register(new Blood());
    }
}
```

### 7.4 原生 HUD 取消

在 `disableVanillaLayers()` 中添加对应层的取消逻辑。

```java
// ✅ 正确：取消 Vampirism 原生 blood_bar 层
ResourceLocation vampirismBloodBar = ResourceLocation.parse("vampirism:blood_bar");
if (vampirismBloodBar.equals(layerName)
    && shouldCancelIndependentVanillaLayer(resolveOverlayRenderState("blood", player))) {
    event.setCanceled(true);
    return;
}
```

### 7.5 配置项

为每个兼容模组在 `mod_support` 段注册单独的开关。

```java
// ✅ 正确：注册模组支持配置
registerReservedModSupport(builder, "toughasnails");           // 默认 false
registerReservedModSupport(builder, "vampirism", true);        // 默认 true
registerReservedModSupport(builder, "farmersdelight", true);   // 默认 true
registerReservedModSupport(builder, "kaleidoscope_cookery", true); // 默认 true
```

### 7.6 位置锚定

通过 `DEFAULT_PRIORITIES` 指定兼容模组 overlay 的默认优先级序号，**不**在渲染层硬编码位置。

```java
private static final Map<String, Integer> DEFAULT_PRIORITIES = Map.of(
    "blood", 4  // 与 food 共用同一位置序号
);
```

---

## 8. 互斥策略

### 8.1 核心原则

> 互斥在配置层（`getBarSettings()`）实现，**不在** `shouldRender()` 中做互斥检测。

### 8.2 原理

- `shouldRender()` 中的互斥检测会使 overlay 进入 `all` 列表但返回 false → 浪费一次迭代
- 配置层互斥使 B 的 `BarSettings.mode == DISABLED` → `rendersClassicBar()` 返回 `false` → B **根本不会进入** `all` 列表 → 渲染层零开销

```java
// ✅ 正确：配置层互斥
public static BarSettings getBarSettings(String overlayName) {
    // ...
    if ("food".equals(overlayName)) {
        BarSettings bloodSettings = getBarSettings("blood");
        if (bloodSettings.rendersClassicBar()) {
            // food 被完全禁用，不会进入 all 列表
            return new BarSettings(false, BarIcons.FOOD, BarMode.DISABLED, BarColorOverlay.none());
        }
    }
    return settings;
}

// ❌ 错误：在 shouldRender 中做互斥
// 这样 food 仍会进入 all 列表，render() 中每次调用 shouldRender() 都有开销
public class Hunger extends BarOverlayImpl {
    @Override
    public boolean shouldRender(Player player) {
        BarSettings bloodSettings = ClassicBarsConfig.getBarSettings("blood");
        return !bloodSettings.rendersClassicBar();  // ❌ 错误！
    }
}
```

### 8.3 互斥对照表

| 激活栏 | 被禁用栏 | 实现位置 |
|---|---|---|
| blood（Vampirism） | food | `getBarSettings("food")` 中检测 |
| 未来：thirst（TAN） | hydration | `getBarSettings("hydration")` 中检测 |

---

## 9. 本地化规则

### 9.1 双语言维护

所有新增功能必须**同时**维护 `en_us.json` 和 `zh_cn.json`。

```json
// ✅ 正确：en_us.json 和 zh_cn.json 同步
// en_us.json
"classicbar.config.general.show_saturation_bar": "Show saturation overlay",
"classicbar.config.general.show_saturation_bar.tooltip": "Render the saturation fill on top of the food bar.",

// zh_cn.json
"classicbar.config.general.show_saturation_bar": "显示饱和度叠加层",
"classicbar.config.general.show_saturation_bar.tooltip": "在饥饿条上方绘制饱和度填充。",
```

### 9.2 禁止"预留/无实际效果"文字

本地化描述必须反映功能的**实际状态**。如果功能未实现，要么不添加配置项，要么如实说明当前行为。

```json
// ✅ 正确（如有保留配置）：说明当前无效果的原因是无可用的兼容版本
"classicbar.config.mod_support.feathers.enabled.tooltip": "Enabling this toggle does not restore Feathers support in this build. No compatible NeoForge 1.21.1 build is currently available."

// ❌ 错误：模糊的"预留/无实际效果"
"classicbar.config.general.show_hydration_bar.tooltip": "Reserved for future compatibility work. No active effect in this build."
```

> **注意**：当前 `en_us.json` 中仍然存在多处 "Reserved for future compatibility work. No active effect in this build." 的表述。新功能不得沿用此类描述，需如实说明功能状态。遗留条目应在功能实现时同步清理。

### 9.3 颜色方案描述

Javadoc 中必须标注 overlay 的颜色方案，方便维护者快速理解视觉效果。

```java
// ✅ 正确：Javadoc 中包含颜色信息
/**
 * Blood overlay for Vampirism mod compatibility.
 * <p>
 * Renders the vampire blood level as a classic-style progress bar
 * with deep red (#AA0000) primary and bright red (#FF4444) accent.
 */
public class Blood extends BarOverlayImpl { ... }
```

---

## 附录：审查清单（新增功能对照用）

| 检查项 | 要求 |
|---|---|
| 包结构合规 | 新 overlay 放入 `impl/overlays/mod/` 或 `impl/overlays/vanilla/` |
| 继承 BarOverlayImpl | 调用 `super("id")` |
| 实现所有抽象方法 | `shouldRender`, `getBarWidth`, `getPrimaryBarColor`, `getSecondaryBarColor`, `renderBar`, `renderText`, `renderIcon` |
| 构造器 id 与注册名一致 | `Blood` → `super("blood")` |
| getBarSettings 注册 | ACTIVE_BAR 在构造器中调用 `registerBarConfig`；非 ACTIVE_BAR 在 `registerFallbackBarSettings` 中注册 |
| 模组兼容守卫 | `ModList.get().isLoaded()` 守卫第三方 API |
| 原版层取消 | 在 `disableVanillaLayers` 中添加对应层 |
| 互斥在配置层 | 在 `getBarSettings()` 实现，不在 `shouldRender()` |
| 注释英文 | 类 Javadoc、行注释均为英文 |
| @Override 覆盖 | 所有覆写方法标注 |
| import 显式 | 无通配符 |
| graphics 命名 | 不使用 `matrices` |
| 本地化同步 | en_us + zh_cn 同时更新 |
| 无"预留"文字 | 描述反映实际功能状态 |
