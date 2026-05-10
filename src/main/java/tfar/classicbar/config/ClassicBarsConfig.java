package tfar.classicbar.config;

import com.google.common.collect.Lists;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import tfar.classicbar.ClassicBar;
import tfar.classicbar.api.BarColorOverlay;
import tfar.classicbar.api.BarMode;
import tfar.classicbar.api.BarPlacement;
import tfar.classicbar.api.BarSettings;
import tfar.classicbar.api.TextFormat;
import tfar.classicbar.client.EventHandler;
import tfar.classicbar.resources.BarIcons;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Configuration definitions for all ClassicBar overlays.
 * 
 * <h3>Layout Rules</h3>
 * <ul>
 *   <li>Left: health, absorption, armor, armor_toughness (top→bottom)</li>
 *   <li>Right: food, blood, forbidden_hunger, mount_health, air (top→bottom)</li>
 * </ul>
 * 
 * <h3>Mutual Exclusion</h3>
 * <ul>
 *   <li>blood active → food DISABLED, forbidden_hunger DISABLED</li>
 *   <li>forbidden_hunger active → food DISABLED</li>
 * </ul>
 */
public class ClassicBarsConfig {

    public static final ClassicBarsConfig CLIENT;
    public static final ModConfigSpec CLIENT_SPEC;

    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("#(?:[0-9a-fA-F]{6}|[0-9a-fA-F]{8})");
    private static final List<String> ACTIVE_BAR_IDS = List.of(
        // LEFT side
        "health", "armor", "absorption", "armor_toughness",
        // RIGHT side — health_mount at position 1
        "health_mount", "food", "blood", "forbidden_hunger", "thirst_level", "air"
    );

    /** Default left-side bar ID set */
    private static final Set<String> LEFT_BAR_IDS = Set.of("health", "armor", "absorption", "armor_toughness");

    /** 
   * Default sort priority (lower = higher/top).
   * LEFT and RIGHT sides sort independently.
   */
    private static final Map<String, Integer> DEFAULT_PRIORITIES = Map.ofEntries(
        // LEFT side (top→bottom)
        Map.entry("health", 1),
        Map.entry("absorption", 2),
        Map.entry("armor", 3),
        Map.entry("armor_toughness", 4),
        // RIGHT side (top→bottom)
        Map.entry("health_mount", 1),
        Map.entry("food", 2),
        Map.entry("thirst_level", 3),
        Map.entry("blood", 4),
        Map.entry("forbidden_hunger", 5),
        Map.entry("air", 6)
    );

    public static boolean isActiveBarId(String id) {
        return ACTIVE_BAR_IDS.contains(id);
    }

    private static final Map<String, ConfiguredBarSettings> BAR_CONFIGS = new LinkedHashMap<>();
    private static final Map<String, ModConfigSpec.BooleanValue> MOD_SUPPORT_ENABLED = new LinkedHashMap<>();
    // layout placement (3-state buttons replace list)
    public static final Map<String, ModConfigSpec.EnumValue<BarPlacement>> PLACEMENTS = new LinkedHashMap<>();
    public static final Map<String, ModConfigSpec.IntValue> SORT_PRIORITIES = new LinkedHashMap<>();
    private static final Map<String, BarSettings> FALLBACK_SETTINGS = new HashMap<>();
    private static final BarSettings NULL_SETTINGS = new BarSettings(false, BarIcons.FALLBACK, BarMode.DISABLED, BarColorOverlay.none());

    static {
        Pair<ClassicBarsConfig, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(ClassicBarsConfig::new);
        CLIENT_SPEC = specPair.getRight();
        CLIENT = specPair.getLeft();
    }

    static ModConfigSpec.BooleanValue displayIcons;
    public static ModConfigSpec.BooleanValue displayToughnessBar;
    public static ModConfigSpec.BooleanValue fullAbsorptionBar;
    public static ModConfigSpec.BooleanValue fullArmorBar;
    public static ModConfigSpec.BooleanValue fullToughnessBar;
    public static ModConfigSpec.BooleanValue lowArmorWarning;
    public static ModConfigSpec.BooleanValue showSaturationBar;
    public static ModConfigSpec.BooleanValue showHydrationBar;
    public static ModConfigSpec.BooleanValue showHeldFoodOverlay;
    public static ModConfigSpec.BooleanValue showHeldDrinkOverlay;
    public static ModConfigSpec.BooleanValue showExhaustionOverlay;
    public static ModConfigSpec.BooleanValue showThirstExhaustionOverlay;
    public static ModConfigSpec.BooleanValue showFoodPreview;

    public static ModConfigSpec.DoubleValue transitionSpeed;
    static ModConfigSpec.ConfigValue<String> hungerBarColor;
    static ModConfigSpec.ConfigValue<String> hungerBarDebuffColor;
    static ModConfigSpec.ConfigValue<String> saturationBarColor;
    static ModConfigSpec.ConfigValue<String> saturationBarDebuffColor;
    static ModConfigSpec.ConfigValue<String> thirstBarColor;
    static ModConfigSpec.ConfigValue<String> thirstBarDebuffColor;
    static ModConfigSpec.ConfigValue<String> hydrationBarColor;
    static ModConfigSpec.ConfigValue<String> hydrationBarDebuffColor;
    static ModConfigSpec.ConfigValue<String> airBarColor;
    static ModConfigSpec.ConfigValue<List<? extends String>> armorColors;
    static ModConfigSpec.ConfigValue<List<? extends String>> armorToughnessColors;
    static ModConfigSpec.ConfigValue<List<? extends String>> absorptionColors;
    static ModConfigSpec.ConfigValue<List<? extends String>> absorptionPoisonColors;
    static ModConfigSpec.ConfigValue<List<? extends String>> absorptionWitherColors;
    public static ModConfigSpec.ConfigValue<List<? extends Double>> normalFractions;
    static ModConfigSpec.ConfigValue<List<? extends String>> normalColors;
    public static ModConfigSpec.ConfigValue<List<? extends Double>> poisonedFractions;
    static ModConfigSpec.ConfigValue<List<? extends String>> poisonedColors;
    public static ModConfigSpec.ConfigValue<List<? extends Double>> witheredFractions;
    static ModConfigSpec.ConfigValue<List<? extends String>> witheredColors;
    public static ModConfigSpec.ConfigValue<String> frozenHealthColor;
    public static ModConfigSpec.ConfigValue<String> healthPoisonOverlayColor;
    public static ModConfigSpec.ConfigValue<String> healthWitherOverlayColor;
    public static ModConfigSpec.ConfigValue<String> healthFrozenOverlayColor;
    public static ModConfigSpec.ConfigValue<String> lavaBarColor;
    public static ModConfigSpec.ConfigValue<String> flightBarColor;
    public static ModConfigSpec.ConfigValue<String> nourishmentBarColor;
    public static ModConfigSpec.ConfigValue<String> satiatedShieldBarColor;
    static ModConfigSpec.ConfigValue<String> forbiddenCurseBarColor;
    public static ModConfigSpec.BooleanValue disableFdNourishmentOverlay;

    // === Debug section fields (for development testing) ===
    public static ModConfigSpec.BooleanValue debugWitherEnabled;
    public static ModConfigSpec.BooleanValue debugPoisonEnabled;
    public static ModConfigSpec.BooleanValue debugFrozenEnabled;
    public static ModConfigSpec.BooleanValue debugHungerEnabled;
    public static ModConfigSpec.BooleanValue debugNourishmentEnabled;
    public static ModConfigSpec.BooleanValue debugSatiatedShieldEnabled;
    public static ModConfigSpec.BooleanValue debugVampireEnabled;
    public static ModConfigSpec.BooleanValue debugForbiddenCurseEnabled;

    // === Config builder helpers ===

    private static ModConfigSpec.BooleanValue generalBool(ModConfigSpec.Builder builder, String key, boolean defaultValue) {
        return builder.translation(generalKey(key)).define(key, defaultValue);
    }

    private static ModConfigSpec.ConfigValue<String> generalColor(ModConfigSpec.Builder builder, String key, String defaultHex) {
        return builder.translation(generalKey(key)).define(key, defaultHex, ClassicBarsConfig::isValidHexColor);
    }

    private static ModConfigSpec.BooleanValue debugBool(ModConfigSpec.Builder builder, String key) {
        return builder.translation(debugKey(key)).define(key, false);
    }

    private static <T> ModConfigSpec.ConfigValue<List<? extends T>> generalList(ModConfigSpec.Builder builder, String key, List<T> defaults, Predicate<Object> validator) {
        return builder.translation(generalKey(key)).defineList(key, defaults, validator);
    }

    public ClassicBarsConfig(ModConfigSpec.Builder builder) {
        BAR_CONFIGS.clear();
        MOD_SUPPORT_ENABLED.clear();
        FALLBACK_SETTINGS.clear();

        builder.translation(sectionKey("general"))

                        .push("general");
        displayIcons = generalBool(builder, "display_icons", true);
        displayToughnessBar = generalBool(builder, "display_toughness_bar", true);
        fullAbsorptionBar = generalBool(builder, "full_absorption_bar", false);
        fullArmorBar = generalBool(builder, "full_armor_bar", false);
        fullToughnessBar = generalBool(builder, "full_toughness_bar", false);
        lowArmorWarning = generalBool(builder, "display_low_armor_warning", true);
        showSaturationBar = generalBool(builder, "show_saturation_bar", true);
        showHydrationBar = generalBool(builder, "show_hydration_bar", true);
        showHeldFoodOverlay = generalBool(builder, "show_held_food_overlay", true);
        showHeldDrinkOverlay = generalBool(builder, "show_held_drink_overlay", true);
        showExhaustionOverlay = generalBool(builder, "show_exhaustion_overlay", true);
        showThirstExhaustionOverlay = generalBool(builder, "show_thirst_exhaustion_overlay", true);
        showFoodPreview = generalBool(builder, "show_food_preview", true);
        transitionSpeed = builder.translation(generalKey("transition_speed"))

                        .defineInRange("transition_speed", 3.0D, 0.0D, Double.MAX_VALUE);

        hungerBarColor = generalColor(builder, "hunger_bar_color", "#B34D00");
        hungerBarDebuffColor = generalColor(builder, "hunger_bar_debuff_color", "#249016");
        thirstBarColor = generalColor(builder, "thirst_bar_color", "#1C5EE4");
        thirstBarDebuffColor = generalColor(builder, "thirst_bar_debuff_color", "#5A891C");
        airBarColor = generalColor(builder, "air_bar_color", "#00E6E6");
        saturationBarColor = generalColor(builder, "saturation_bar_color", "#FFCC00");
        saturationBarDebuffColor = generalColor(builder, "saturation_bar_debuff_color", "#87BC00");
        hydrationBarColor = generalColor(builder, "hydration_bar_color", "#00A3E2");
        hydrationBarDebuffColor = generalColor(builder, "hydration_bar_debuff_color", "#85CF25");
        lavaBarColor = generalColor(builder, "lava_bar_color", "#FF8000");
        flightBarColor = generalColor(builder, "flight_bar_color", "#FFFFFF");

        armorColors = generalList(builder, "armor_color_values",
                        Lists.newArrayList("#AAAAAA", "#FF5500", "#FFC747", "#27FFE3", "#00FF00", "#7F00FF"),
                        ClassicBarsConfig::isValidHexColor);
        armorToughnessColors = generalList(builder, "armor_toughness_color_values",
                        Lists.newArrayList("#AAAAAA", "#FF5500", "#FFC747", "#27FFE3", "#00FF00", "#7F00FF"),
                        ClassicBarsConfig::isValidHexColor);
        absorptionColors = generalList(builder, "absorption_color_values",
                        Lists.newArrayList("#D4AF37", "#C2C73B", "#8DC337", "#36BA77", "#4A5BC4", "#D89AE2", "#DF9DC7", "#DFA99D", "#D4DF9D", "#3E84C6", "#B8C1E8", "#DFDFDF"),
                        ClassicBarsConfig::isValidHexColor);
        absorptionPoisonColors = generalList(builder, "absorption_poison_color_values",
                        Lists.newArrayList("#D4AF37", "#C2C73B", "#8DC337", "#36BA77", "#4A5BC4", "#D89AE2", "#DF9DC7", "#DFA99D", "#D4DF9D", "#3E84C6", "#B8C1E8", "#DFDFDF"),
                        ClassicBarsConfig::isValidHexColor);
        absorptionWitherColors = generalList(builder, "absorption_wither_color_values",
                        Lists.newArrayList("#D4AF37", "#C2C73B", "#8DC337", "#36BA77", "#4A5BC4", "#D89AE2", "#DF9DC7", "#DFA99D", "#D4DF9D", "#3E84C6", "#B8C1E8", "#DFDFDF"),
                        ClassicBarsConfig::isValidHexColor);
        normalColors = generalList(builder, "normal_colors",
                        Lists.newArrayList("#FF0000", "#FFFF00", "#00FF00"),
                        ClassicBarsConfig::isValidHexColor);
        normalFractions = generalList(builder, "normal_fractions",
                        Lists.newArrayList(.25D, .5D, .75D),
                        ClassicBarsConfig::isValidUnitFraction);
        poisonedColors = generalList(builder, "poisoned_colors",
                        Lists.newArrayList("#00FF00", "#55FF55", "#00FF00"),
                        ClassicBarsConfig::isValidHexColor);
        poisonedFractions = generalList(builder, "poisoned_fractions",
                        Lists.newArrayList(.25D, .5D, .75D),
                        ClassicBarsConfig::isValidUnitFraction);
        witheredColors = generalList(builder, "withered_colors",
                        Lists.newArrayList("#555555", "#AAAAAA", "#555555"),
                        ClassicBarsConfig::isValidHexColor);
        witheredFractions = generalList(builder, "withered_fractions",
                        Lists.newArrayList(.25D, .5D, .75D),
                        ClassicBarsConfig::isValidUnitFraction);
        frozenHealthColor = generalColor(builder, "frozen_health_color", "#7FAFFF");
        healthPoisonOverlayColor = builder
                        .translation("classicbar.config.general.health_poison_overlay_color")
                        .define("health_poison_overlay_color", "#80800080", ClassicBarsConfig::isValidHexColor);
        healthWitherOverlayColor = builder
                        .translation("classicbar.config.general.health_wither_overlay_color")
                        .define("health_wither_overlay_color", "#80595959", ClassicBarsConfig::isValidHexColor);
        healthFrozenOverlayColor = builder
                        .translation("classicbar.config.general.health_frozen_overlay_color")
                        .define("health_frozen_overlay_color", "#804D80FF", ClassicBarsConfig::isValidHexColor);

        // === Nourishment (FarmersDelight) compat ===
        nourishmentBarColor = generalColor(builder, "nourishment_bar_color", "#F3B300");
        satiatedShieldBarColor = generalColor(builder, "satiated_shield_bar_color", "#FF1313");
        // forbidden fruit curse hunger bar color (EnigmaticLegacy+)
        forbiddenCurseBarColor = builder.translation(generalKey("forbidden_curse_bar_color"))
                        .comment("Color of the hunger bar when under the Forbidden Curse (EnigmaticLegacy+).",
                                        "Format: #RRGGBB or #AARRGGBB")
                        .define("forbidden_curse_bar_color", "#9932CC", ClassicBarsConfig::isValidHexColor);
        disableFdNourishmentOverlay = generalBool(builder, "disable_fd_nourishment_overlay", true);
        builder.pop();

        builder.translation(sectionKey("layout"))

                        .push("layout");
        for (String barId : ACTIVE_BAR_IDS) {
            BarPlacement defaultPlacement = getDefaultPlacement(barId);
            int defaultPriority = getDefaultPriority(barId);
            builder.translation(layoutKey("placement_" + barId))
                            .push(barId);
            PLACEMENTS.put(barId, builder.translation(layoutKey("placement_value"))
                            .defineEnum("placement", defaultPlacement));
            SORT_PRIORITIES.put(barId, builder.translation(layoutKey("priority_value"))
                            .defineInRange("sort_priority", defaultPriority, 1, ACTIVE_BAR_IDS.size()));
            builder.pop();
        }
        builder.pop();

        builder.translation(sectionKey("bars"))

                        .push("bars");
        // === LEFT side ===
        registerBarConfig(builder, "health", BarIcons.HEALTH, true);
        registerBarConfig(builder, "armor", BarIcons.ARMOR, true);
        registerBarConfig(builder, "absorption", BarIcons.ABSORPTION, true);
        registerBarConfig(builder, "armor_toughness", BarIcons.ARMOR_TOUGHNESS, true);

        // === RIGHT side — health_mount at position 1 ===
        registerBarConfig(builder, "health_mount", BarIcons.MOUNT_HEALTH, true);
        registerBarConfig(builder, "food", BarIcons.FOOD, true);
        registerBarConfig(builder, "blood", BarIcons.BLOOD, true);
        registerBarConfig(builder, "forbidden_hunger", BarIcons.FORBIDDEN_HUNGER, true);
        registerBarConfig(builder, "thirst_level", BarIcons.THIRST, true);
        registerBarConfig(builder, "air", BarIcons.AIR, true);
        builder.pop();

        builder.translation(sectionKey("mod_support"))

                        .push("mod_support");
        registerReservedModSupport(builder, "toughasnails");
        registerReservedModSupport(builder, "thirst");
        registerReservedModSupport(builder, "vampirism", true);
        registerReservedModSupport(builder, "parcool");

        registerReservedModSupport(builder, "farmersdelight", true);
        registerReservedModSupport(builder, "kaleidoscope_cookery", true);
        registerReservedModSupport(builder, "enigmaticlegacyplus", true);
        builder.pop();

        // === Debug section (for development testing) ===
        builder.translation(sectionKey("debug"))
                        .push("debug");
        debugWitherEnabled = debugBool(builder, "wither");
        debugPoisonEnabled = debugBool(builder, "poison");
        debugFrozenEnabled = debugBool(builder, "frozen");
        debugHungerEnabled = debugBool(builder, "hunger");
        debugNourishmentEnabled = debugBool(builder, "nourishment");
        debugSatiatedShieldEnabled = debugBool(builder, "satiated_shield");
        debugVampireEnabled = debugBool(builder, "vampire");
        debugForbiddenCurseEnabled = debugBool(builder, "forbidden_curse");
        builder.pop();

        registerFallbackBarSettings();
    }

    public static void onConfigLoading(ModConfigEvent.Loading event) {
        refreshClientConfig(event);
    }

    public static void onConfigReloading(ModConfigEvent.Reloading event) {
        refreshClientConfig(event);
    }

    public static boolean isReservedModSupportEnabled(String modId) {
        ModConfigSpec.BooleanValue configValue = MOD_SUPPORT_ENABLED.get(modId);
        return configValue != null && configValue.get();
    }

    public static BarSettings getBarSettings(String overlayName) {
        ConfiguredBarSettings configuredBar = BAR_CONFIGS.get(overlayName);
        if (configuredBar != null) {
            return configuredBar.toBarSettings();
        }
        // food ↔ blood mutual exclusion: blood active → food disabled
        if ("food".equals(overlayName)) {
            BarSettings bloodSettings = getBarSettings("blood");
            if (bloodSettings.rendersClassicBar()) {
                return new BarSettings(false, BarIcons.FOOD, BarMode.DISABLED, BarColorOverlay.none());
            }
        }
        return FALLBACK_SETTINGS.getOrDefault(overlayName, NULL_SETTINGS).copy();
    }

    private static void refreshClientConfig(ModConfigEvent event) {
        if (event.getConfig().getType() == ModConfig.Type.CLIENT && event.getConfig().getModId().equals(ClassicBar.MODID)) {
            EventHandler.cacheConfigs();
            ClassicBar.logger.info("Syncing Classic Bar Configs");
        }
    }

    private static void registerBarConfig(ModConfigSpec.Builder builder, String name, ResourceLocation defaultIcon, boolean defaultShowText) {
        BAR_CONFIGS.put(name, ConfiguredBarSettings.create(builder, name, defaultIcon, defaultShowText));
    }

    private static void registerReservedModSupport(ModConfigSpec.Builder builder, String modId) {
        registerReservedModSupport(builder, modId, false);
    }

    private static void registerReservedModSupport(ModConfigSpec.Builder builder, String modId, boolean defaultValue) {
        builder.translation(modSupportSectionKey(modId))
                        .push(modId);
        MOD_SUPPORT_ENABLED.put(modId, builder.translation(modSupportKey(modId, "enabled"))
                        .define("enabled", defaultValue));
        builder.pop();
    }

    private static void registerFallbackBarSettings() {

        FALLBACK_SETTINGS.put("thirst_level", new BarSettings(false, BarIcons.THIRST, BarMode.DISABLED, BarColorOverlay.none()));
    }

    private static boolean isValidIconValue(Object value) {
        if (!(value instanceof String raw)) {
            return false;
        }
        if (raw.isBlank()) {
            return true;
        }
        ResourceLocation resourceLocation = ResourceLocation.tryParse(raw);
        return isValidClassicBarIcon(resourceLocation);
    }

    private static ResourceLocation resolveConfiguredIcon(String raw, ResourceLocation fallback) {
        if (raw == null || raw.isBlank()) {
            return fallback;
        }
        ResourceLocation resourceLocation = ResourceLocation.tryParse(raw);
        if (isValidClassicBarIcon(resourceLocation)) {
            return resourceLocation;
        }
        ClassicBar.logger.warn("Invalid icon resource '{}' for ClassicBar config, falling back to {}", raw, fallback);
        return fallback;
    }

    private static BarColorOverlay resolveColorOverlay(String raw, String barName) {
        if (isValidHexColor(raw)) {
            return BarColorOverlay.fromConfigValue(raw);
        }
        ClassicBar.logger.warn("Invalid color overlay '{}' for ClassicBar bar '{}', falling back to {}", raw, barName, BarColorOverlay.DEFAULT_CONFIG_VALUE);
        return BarColorOverlay.none();
    }

    private static boolean isValidClassicBarIcon(ResourceLocation resourceLocation) {
        return resourceLocation != null
                        && ClassicBar.MODID.equals(resourceLocation.getNamespace())
                        && resourceLocation.getPath().startsWith("textures/gui/icons/")
                        && resourceLocation.getPath().endsWith(".png");
    }

    private static boolean isValidActiveBarId(Object value) {
        return value instanceof String string && ACTIVE_BAR_IDS.contains(string);
    }

    private static boolean isValidHexColor(Object value) {
        return value instanceof String string && HEX_COLOR_PATTERN.matcher(string).matches();
    }

    private static boolean isValidUnitFraction(Object value) {
        return value instanceof Number number && number.doubleValue() >= 0.0D && number.doubleValue() <= 1.0D;
    }

    private static String sectionKey(String name) {
        return "classicbar.config.section." + name;
    }

    private static String generalKey(String name) {
        return "classicbar.config.general." + name;
    }

    private static String debugKey(String key) {
        return "classicbar.config.debug." + key;
    }

    private static String layoutKey(String name) {
        return "classicbar.config.layout." + name;
    }

    /** Assign default placement from original layout */
    private static BarPlacement getDefaultPlacement(String barId) {
        return LEFT_BAR_IDS.contains(barId) ? BarPlacement.LEFT : BarPlacement.RIGHT;
    }

    /**
   * Default render side for non-ACTIVE_BAR extra overlays.
   * Used by the fallback channel to decide where extra overlays render.
   */
    public static BarPlacement getDefaultPlacementForExtra(String id) {
        // non-ACTIVE_BAR overlays default to right side
        return BarPlacement.RIGHT;
    }

    /** Return default sort priority (1-N) */
    private static int getDefaultPriority(String barId) {
        // manually assign priority
        Integer manualPri = DEFAULT_PRIORITIES.get(barId);
        if (manualPri != null) return manualPri;
        // default: based on ACTIVE_BAR_IDS index order
        int index = ACTIVE_BAR_IDS.indexOf(barId);
        return index >= 0 ? index + 1 : ACTIVE_BAR_IDS.size();
    }

    /** Get left-side bars sorted by priority */
    public static List<String> getLeftOrder() {
        List<String> left = new ArrayList<>();
        for (String barId : ACTIVE_BAR_IDS) {
            if (PLACEMENTS.containsKey(barId) && PLACEMENTS.get(barId).get() == BarPlacement.LEFT) {
                left.add(barId);
            }
        }
        left.sort(Comparator.comparingInt(id -> SORT_PRIORITIES.get(id).get()));
        return left;
    }

    /** Get right-side bars sorted by priority */
    public static List<String> getRightOrder() {
        List<String> right = new ArrayList<>();
        for (String barId : ACTIVE_BAR_IDS) {
            if (PLACEMENTS.containsKey(barId) && PLACEMENTS.get(barId).get() == BarPlacement.RIGHT) {
                right.add(barId);
            }
        }
        right.sort(Comparator.comparingInt(id -> SORT_PRIORITIES.get(id).get()));
        return right;
    }

    private static String barSectionKey(String barName) {
        return "classicbar.config.bars." + barName;
    }

    private static String barKey(String barName, String name) {
        return barSectionKey(barName) + "." + name;
    }

    private static String modSupportSectionKey(String modId) {
        return "classicbar.config.mod_support." + modId;
    }

    private static String modSupportKey(String modId, String name) {
        return modSupportSectionKey(modId) + "." + name;
    }

        private static final class ConfiguredBarSettings {
                private final String name;
                private final ResourceLocation fallbackIcon;
                private final ModConfigSpec.BooleanValue showText;
                private final ModConfigSpec.ConfigValue<String> icon;
                private final ModConfigSpec.EnumValue<BarMode> mode;
                private final ModConfigSpec.ConfigValue<String> colorOverlay;
                private final ModConfigSpec.EnumValue<TextFormat> textFormat;

                private ConfiguredBarSettings(String name, ResourceLocation fallbackIcon, ModConfigSpec.BooleanValue showText,
                                      ModConfigSpec.ConfigValue<String> icon, ModConfigSpec.EnumValue<BarMode> mode,
                                      ModConfigSpec.ConfigValue<String> colorOverlay, ModConfigSpec.EnumValue<TextFormat> textFormat) {
                        this.name = name;
                        this.fallbackIcon = fallbackIcon;
                        this.showText = showText;
                        this.icon = icon;
                        this.mode = mode;
                        this.colorOverlay = colorOverlay;
                        this.textFormat = textFormat;
                }

                private static ConfiguredBarSettings create(ModConfigSpec.Builder builder, String name, ResourceLocation defaultIcon, boolean defaultShowText) {
                        builder.translation(barSectionKey(name))
                                        .push(name);
                        ModConfigSpec.BooleanValue showText = builder.translation(barKey(name, "show_text"))
                                        .define("show_text", defaultShowText);
                        ModConfigSpec.ConfigValue<String> icon = builder.translation(barKey(name, "icon"))
                                        .define("icon", defaultIcon.toString(), ClassicBarsConfig::isValidIconValue);
                        ModConfigSpec.EnumValue<BarMode> mode = builder.translation(barKey(name, "mode"))
                                        .defineEnum("mode", BarMode.OVERRIDE);
                        ModConfigSpec.ConfigValue<String> colorOverlay = builder.translation(barKey(name, "color_overlay"))
                                        .define("color_overlay", BarColorOverlay.DEFAULT_CONFIG_VALUE, ClassicBarsConfig::isValidHexColor);
                        ModConfigSpec.EnumValue<TextFormat> textFormat = builder.translation(barKey(name, "text_format"))
                                        .defineEnum("text_format", TextFormat.CURRENT_ONLY);
                        builder.pop();
                        return new ConfiguredBarSettings(name, defaultIcon, showText, icon, mode, colorOverlay, textFormat);
                }

                private BarSettings toBarSettings() {
                        BarMode resolvedMode = mode.get() == null ? BarMode.OVERRIDE : mode.get();
                        return new BarSettings(showText.get(), resolveConfiguredIcon(icon.get(), fallbackIcon), resolvedMode, resolveColorOverlay(colorOverlay.get(), name), textFormat.get());
                }
        }
}
