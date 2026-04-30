package tfar.classicbar.config;

import com.google.common.collect.Lists;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import tfar.classicbar.ClassicBar;
import tfar.classicbar.api.BarColorOverlay;
import tfar.classicbar.api.BarMode;
import tfar.classicbar.api.BarSettings;
import tfar.classicbar.api.TextFormats;
import tfar.classicbar.client.EventHandler;
import tfar.classicbar.resources.BarIcons;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class ClassicBarsConfig {

  public static final ClassicBarsConfig CLIENT;
  public static final ModConfigSpec CLIENT_SPEC;

  private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("#(?:[0-9a-fA-F]{6}|[0-9a-fA-F]{8})");
  private static final Set<String> ACTIVE_BAR_IDS = Set.of("health", "armor", "absorption", "food", "armor_toughness", "health_mount", "air");

  private static final Map<String, ConfiguredBarSettings> BAR_CONFIGS = new LinkedHashMap<>();
  private static final Map<String, ModConfigSpec.BooleanValue> MOD_SUPPORT_ENABLED = new LinkedHashMap<>();
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

  public static ModConfigSpec.ConfigValue<List<? extends String>> leftorder;
  public static ModConfigSpec.ConfigValue<List<? extends String>> rightorder;

  public ClassicBarsConfig(ModConfigSpec.Builder builder) {
    BAR_CONFIGS.clear();
    MOD_SUPPORT_ENABLED.clear();
    FALLBACK_SETTINGS.clear();

    builder.translation(sectionKey("general"))

            .push("general");
    displayIcons = builder.translation(generalKey("display_icons"))

            .define("display_icons", true);
    displayToughnessBar = builder.translation(generalKey("display_toughness_bar"))

            .define("display_toughness_bar", true);
    fullAbsorptionBar = builder.translation(generalKey("full_absorption_bar"))

            .define("full_absorption_bar", false);
    fullArmorBar = builder.translation(generalKey("full_armor_bar"))

            .define("full_armor_bar", false);
    fullToughnessBar = builder.translation(generalKey("full_toughness_bar"))

            .define("full_toughness_bar", false);
    lowArmorWarning = builder.translation(generalKey("display_low_armor_warning"))

            .define("display_low_armor_warning", true);
    showSaturationBar = builder.translation(generalKey("show_saturation_bar"))

            .define("show_saturation_bar", true);
    showHydrationBar = builder.translation(generalKey("show_hydration_bar"))

            .define("show_hydration_bar", true);
    showHeldFoodOverlay = builder.translation(generalKey("show_held_food_overlay"))

            .define("show_held_food_overlay", true);
    showHeldDrinkOverlay = builder.translation(generalKey("show_held_drink_overlay"))

            .define("show_held_drink_overlay", true);
    showExhaustionOverlay = builder.translation(generalKey("show_exhaustion_overlay"))

            .define("show_exhaustion_overlay", true);
    showThirstExhaustionOverlay = builder.translation(generalKey("show_thirst_exhaustion_overlay"))

            .define("show_thirst_exhaustion_overlay", true);
    transitionSpeed = builder.translation(generalKey("transition_speed"))

            .defineInRange("transition_speed", 3.0D, 0.0D, Double.MAX_VALUE);

    hungerBarColor = builder.translation(generalKey("hunger_bar_color"))

            .define("hunger_bar_color", "#B34D00", ClassicBarsConfig::isValidHexColor);
    hungerBarDebuffColor = builder.translation(generalKey("hunger_bar_debuff_color"))

            .define("hunger_bar_debuff_color", "#249016", ClassicBarsConfig::isValidHexColor);
    thirstBarColor = builder.translation(generalKey("thirst_bar_color"))

            .define("thirst_bar_color", "#1C5EE4", ClassicBarsConfig::isValidHexColor);
    thirstBarDebuffColor = builder.translation(generalKey("thirst_bar_debuff_color"))

            .define("thirst_bar_debuff_color", "#5A891C", ClassicBarsConfig::isValidHexColor);
    airBarColor = builder.translation(generalKey("air_bar_color"))

            .define("air_bar_color", "#00E6E6", ClassicBarsConfig::isValidHexColor);
    saturationBarColor = builder.translation(generalKey("saturation_bar_color"))

            .define("saturation_bar_color", "#FFCC00", ClassicBarsConfig::isValidHexColor);
    saturationBarDebuffColor = builder.translation(generalKey("saturation_bar_debuff_color"))

            .define("saturation_bar_debuff_color", "#87BC00", ClassicBarsConfig::isValidHexColor);
    hydrationBarColor = builder.translation(generalKey("hydration_bar_color"))

            .define("hydration_bar_color", "#00A3E2", ClassicBarsConfig::isValidHexColor);
    hydrationBarDebuffColor = builder.translation(generalKey("hydration_bar_debuff_color"))

            .define("hydration_bar_debuff_color", "#85CF25", ClassicBarsConfig::isValidHexColor);
    lavaBarColor = builder.translation(generalKey("lava_bar_color"))

            .define("lava_bar_color", "#FF8000", ClassicBarsConfig::isValidHexColor);
    flightBarColor = builder.translation(generalKey("flight_bar_color"))

            .define("flight_bar_color", "#FFFFFF", ClassicBarsConfig::isValidHexColor);

    armorColors = builder.translation(generalKey("armor_color_values"))

            .defineList("armor_color_values", Lists.newArrayList("#AAAAAA", "#FF5500", "#FFC747", "#27FFE3", "#00FF00", "#7F00FF"), ClassicBarsConfig::isValidHexColor);
    armorToughnessColors = builder.translation(generalKey("armor_toughness_color_values"))

            .defineList("armor_toughness_color_values", Lists.newArrayList("#AAAAAA", "#FF5500", "#FFC747", "#27FFE3", "#00FF00", "#7F00FF"), ClassicBarsConfig::isValidHexColor);
    absorptionColors = builder.translation(generalKey("absorption_color_values"))

            .defineList("absorption_color_values", Lists.newArrayList("#D4AF37", "#C2C73B", "#8DC337", "#36BA77", "#4A5BC4", "#D89AE2", "#DF9DC7", "#DFA99D", "#D4DF9D", "#3E84C6", "#B8C1E8", "#DFDFDF"), ClassicBarsConfig::isValidHexColor);
    absorptionPoisonColors = builder.translation(generalKey("absorption_poison_color_values"))

            .defineList("absorption_poison_color_values", Lists.newArrayList("#D4AF37", "#C2C73B", "#8DC337", "#36BA77", "#4A5BC4", "#D89AE2", "#DF9DC7", "#DFA99D", "#D4DF9D", "#3E84C6", "#B8C1E8", "#DFDFDF"), ClassicBarsConfig::isValidHexColor);
    absorptionWitherColors = builder.translation(generalKey("absorption_wither_color_values"))

            .defineList("absorption_wither_color_values", Lists.newArrayList("#D4AF37", "#C2C73B", "#8DC337", "#36BA77", "#4A5BC4", "#D89AE2", "#DF9DC7", "#DFA99D", "#D4DF9D", "#3E84C6", "#B8C1E8", "#DFDFDF"), ClassicBarsConfig::isValidHexColor);
    normalColors = builder.translation(generalKey("normal_colors"))

            .defineList("normal_colors", Lists.newArrayList("#FF0000", "#FFFF00", "#00FF00"), ClassicBarsConfig::isValidHexColor);
    normalFractions = builder.translation(generalKey("normal_fractions"))

            .defineList("normal_fractions", Lists.newArrayList(.25D, .5D, .75D), ClassicBarsConfig::isValidUnitFraction);
    poisonedColors = builder.translation(generalKey("poisoned_colors"))

            .defineList("poisoned_colors", Lists.newArrayList("#00FF00", "#55FF55", "#00FF00"), ClassicBarsConfig::isValidHexColor);
    poisonedFractions = builder.translation(generalKey("poisoned_fractions"))

            .defineList("poisoned_fractions", Lists.newArrayList(.25D, .5D, .75D), ClassicBarsConfig::isValidUnitFraction);
    witheredColors = builder.translation(generalKey("withered_colors"))

            .defineList("withered_colors", Lists.newArrayList("#555555", "#AAAAAA", "#555555"), ClassicBarsConfig::isValidHexColor);
    witheredFractions = builder.translation(generalKey("withered_fractions"))

            .defineList("withered_fractions", Lists.newArrayList(.25D, .5D, .75D), ClassicBarsConfig::isValidUnitFraction);
    frozenHealthColor = builder.translation(generalKey("frozen_health_color"))

            .define("frozen_health_color", "#7FAFFF", ClassicBarsConfig::isValidHexColor);
    healthPoisonOverlayColor = builder
            .translation("classicbar.config.general.health_poison_overlay_color")
            .define("health_poison_overlay_color", "#80800080", ClassicBarsConfig::isValidHexColor);
    healthWitherOverlayColor = builder
            .translation("classicbar.config.general.health_wither_overlay_color")
            .define("health_wither_overlay_color", "#80595959", ClassicBarsConfig::isValidHexColor);
    healthFrozenOverlayColor = builder
            .translation("classicbar.config.general.health_frozen_overlay_color")
            .define("health_frozen_overlay_color", "#804D80FF", ClassicBarsConfig::isValidHexColor);
    builder.pop();

    builder.translation(sectionKey("layout"))

            .push("layout");
    leftorder = builder.translation(layoutKey("left_order"))

            .defineList("left_order", Lists.newArrayList("health", "armor", "absorption"), ClassicBarsConfig::isValidActiveBarId);
    rightorder = builder.translation(layoutKey("right_order"))

            .defineList("right_order", Lists.newArrayList("health_mount", "food", "armor_toughness", "air"), ClassicBarsConfig::isValidActiveBarId);
    builder.pop();

    builder.translation(sectionKey("bars"))

            .push("bars");
    registerBarConfig(builder, "health", BarIcons.HEALTH, true);
    registerBarConfig(builder, "armor", BarIcons.ARMOR, true);
    registerBarConfig(builder, "absorption", BarIcons.ABSORPTION, true);
    registerBarConfig(builder, "food", BarIcons.FOOD, true);
    registerBarConfig(builder, "armor_toughness", BarIcons.ARMOR_TOUGHNESS, true);
    registerBarConfig(builder, "health_mount", BarIcons.MOUNT_HEALTH, true);
    registerBarConfig(builder, "air", BarIcons.AIR, true);
    builder.pop();

    builder.translation(sectionKey("mod_support"))

            .push("mod_support");
    registerReservedModSupport(builder, "toughasnails");
    registerReservedModSupport(builder, "vampirism");
    registerReservedModSupport(builder, "parcool");
    registerReservedModSupport(builder, "feathers");
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
    builder.translation(modSupportSectionKey(modId))
            .push(modId);
    MOD_SUPPORT_ENABLED.put(modId, builder.translation(modSupportKey(modId, "enabled"))
            .define("enabled", false));
    builder.pop();
  }

  private static void registerFallbackBarSettings() {
    FALLBACK_SETTINGS.put("blood", new BarSettings(false, BarIcons.BLOOD, BarMode.DISABLED, BarColorOverlay.none()));
    FALLBACK_SETTINGS.put("feathers", new BarSettings(false, BarIcons.FEATHERS, BarMode.DISABLED, BarColorOverlay.none()));
    FALLBACK_SETTINGS.put("stamina", new BarSettings(false, BarIcons.STAMINA, BarMode.DISABLED, BarColorOverlay.none()));
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

  private static String layoutKey(String name) {
    return "classicbar.config.layout." + name;
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
        private final ModConfigSpec.ConfigValue<String> textFormat;

        private ConfiguredBarSettings(String name, ResourceLocation fallbackIcon, ModConfigSpec.BooleanValue showText,
                                      ModConfigSpec.ConfigValue<String> icon, ModConfigSpec.EnumValue<BarMode> mode,
                                      ModConfigSpec.ConfigValue<String> colorOverlay, ModConfigSpec.ConfigValue<String> textFormat) {
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
            ModConfigSpec.ConfigValue<String> textFormat = builder.translation(barKey(name, "text_format"))
                    .define("text_format", "current_only",
                            o -> o instanceof String s && (s.equals("current_only") || s.equals("current_max") || s.equals("percent_max")));
            builder.pop();
            return new ConfiguredBarSettings(name, defaultIcon, showText, icon, mode, colorOverlay, textFormat);
        }

        private BarSettings toBarSettings() {
            BarMode resolvedMode = mode.get() == null ? BarMode.OVERRIDE : mode.get();
            return new BarSettings(showText.get(), resolveConfiguredIcon(icon.get(), fallbackIcon), resolvedMode, resolveColorOverlay(colorOverlay.get(), name), textFormat.get());
        }
    }
}
