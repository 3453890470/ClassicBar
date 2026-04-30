package tfar.classicbar.config;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NeoForgeConfigBehaviorTest {

  private static final Map<String, String> ACTIVE_BAR_CONSTANTS = new LinkedHashMap<>();
  private static final List<String> REQUIRED_LANG_KEYS = List.of(
          "classicbar.config.section.general",
          "classicbar.config.section.general.tooltip",
          "classicbar.config.section.general.button",
          "classicbar.config.section.layout",
          "classicbar.config.section.layout.tooltip",
          "classicbar.config.section.layout.button",
          "classicbar.config.section.bars",
          "classicbar.config.section.bars.tooltip",
          "classicbar.config.section.bars.button",
          "classicbar.config.section.mod_support",
          "classicbar.config.section.mod_support.tooltip",
          "classicbar.config.section.mod_support.button",
          "classicbar.config.layout.left_order",
          "classicbar.config.layout.left_order.tooltip",
          "classicbar.config.layout.right_order",
          "classicbar.config.layout.right_order.tooltip",
          "classicbar.config.mod_support.toughasnails.enabled",
          "classicbar.config.mod_support.toughasnails.enabled.tooltip",
          "classicbar.config.mod_support.vampirism.enabled",
          "classicbar.config.mod_support.vampirism.enabled.tooltip",
          "classicbar.config.mod_support.parcool.enabled",
          "classicbar.config.mod_support.parcool.enabled.tooltip",
          "classicbar.config.mod_support.feathers.enabled",
          "classicbar.config.mod_support.feathers.enabled.tooltip",
          "classicbar.config.enum.bar_mode.override",
          "classicbar.config.enum.bar_mode.disabled"
  );

  static {
    ACTIVE_BAR_CONSTANTS.put("health", "HEALTH");
    ACTIVE_BAR_CONSTANTS.put("armor", "ARMOR");
    ACTIVE_BAR_CONSTANTS.put("absorption", "ABSORPTION");
    ACTIVE_BAR_CONSTANTS.put("food", "FOOD");
    ACTIVE_BAR_CONSTANTS.put("armor_toughness", "ARMOR_TOUGHNESS");
    ACTIVE_BAR_CONSTANTS.put("health_mount", "MOUNT_HEALTH");
    ACTIVE_BAR_CONSTANTS.put("air", "AIR");
  }

  @Test
  void sourceDefinesClientConfigRegistrationSectionsAndConfigScreenExtension() throws IOException {
    String classicBar = readProjectFile("src/main/java/tfar/classicbar/ClassicBar.java");
    String classicBarClient = readProjectFile("src/main/java/tfar/classicbar/client/ClassicBarClient.java");
    String classicBarsConfig = readProjectFile("src/main/java/tfar/classicbar/config/ClassicBarsConfig.java");

    assertTrue(classicBar.contains("registerConfig(ModConfig.Type.CLIENT, ClassicBarsConfig.CLIENT_SPEC);"), "ClassicBar should register a client config spec");
    assertTrue(classicBar.contains("registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);"), "ClassicBar should expose the NeoForge config screen extension point");
    assertTrue(classicBarClient.contains("modBus.addListener(ClassicBarsConfig::onConfigLoading);"), "ClassicBarClient should register the ClassicBarsConfig load hook");
    assertTrue(classicBarClient.contains("modBus.addListener(ClassicBarsConfig::onConfigReloading);"), "ClassicBarClient should register the ClassicBarsConfig reload hook");
    assertTrue(classicBarsConfig.contains("public static final ModConfigSpec CLIENT_SPEC;"), "ClassicBarsConfig should own the NeoForge client spec");
    assertTrue(classicBarsConfig.contains("sectionKey(\"general\")"), "ClassicBarsConfig should define the general section translation key");
    assertTrue(classicBarsConfig.contains("sectionKey(\"layout\")"), "ClassicBarsConfig should define the layout section translation key");
    assertTrue(classicBarsConfig.contains("sectionKey(\"bars\")"), "ClassicBarsConfig should define the bars section translation key");
    assertTrue(classicBarsConfig.contains("sectionKey(\"mod_support\")"), "ClassicBarsConfig should define the reserved mod_support section translation key");
    assertTrue(classicBarsConfig.contains(".push(\"general\")"), "ClassicBarsConfig should define the general section");
    assertTrue(classicBarsConfig.contains(".push(\"layout\")"), "ClassicBarsConfig should define the layout section");
    assertTrue(classicBarsConfig.contains(".push(\"bars\")"), "ClassicBarsConfig should define the bars section");
    assertTrue(classicBarsConfig.contains(".push(\"mod_support\")"), "ClassicBarsConfig should define the reserved mod_support section");
  }

  @Test
  void sourceDefinesPerBarShowTextIconModeAndColorOverlay() throws IOException {
    String classicBarsConfig = readProjectFile("src/main/java/tfar/classicbar/config/ClassicBarsConfig.java");
    String barSettings = readProjectFile("src/main/java/tfar/classicbar/api/BarSettings.java");

    assertTrue(barSettings.contains("public BarMode mode;"), "BarSettings should store per-bar mode");
    assertTrue(barSettings.contains("public BarColorOverlay color_overlay;"), "BarSettings should store per-bar color overlays");
    assertTrue(barSettings.contains("rendersClassicBar()"), "BarSettings should expose a ClassicBar render gate");
    assertTrue(barSettings.contains("cancelsVanillaLayer()"), "BarSettings should expose a vanilla cancellation gate");

    for (Map.Entry<String, String> entry : ACTIVE_BAR_CONSTANTS.entrySet()) {
      String barId = entry.getKey();
      String iconConstant = entry.getValue();
      assertTrue(classicBarsConfig.contains("registerBarConfig(builder, \"" + barId + "\", BarIcons." + iconConstant), barId + " should be registered in ClassicBarsConfig");
    }

    assertTrue(classicBarsConfig.contains("barKey(name, \"show_text\")"), "ClassicBarsConfig should translate per-bar show_text entries");
    assertTrue(classicBarsConfig.contains("barKey(name, \"icon\")"), "ClassicBarsConfig should translate per-bar icon entries");
    assertTrue(classicBarsConfig.contains("barKey(name, \"mode\")"), "ClassicBarsConfig should translate per-bar mode entries");
    assertTrue(classicBarsConfig.contains("barKey(name, \"color_overlay\")"), "ClassicBarsConfig should translate per-bar color overlay entries");
    assertTrue(classicBarsConfig.contains("define(\"show_text\""), "ClassicBarsConfig should define per-bar show_text entries");
    assertTrue(classicBarsConfig.contains("define(\"icon\""), "ClassicBarsConfig should define per-bar icon entries");
    assertTrue(classicBarsConfig.contains("defineEnum(\"mode\""), "ClassicBarsConfig should define per-bar mode entries");
    assertTrue(classicBarsConfig.contains("define(\"color_overlay\""), "ClassicBarsConfig should define per-bar color overlay entries");
  }

  @Test
  void sourceDefinesBarModeAndColorOverlayParsers() throws IOException {
    String barMode = readProjectFile("src/main/java/tfar/classicbar/api/BarMode.java");
    String barColorOverlay = readProjectFile("src/main/java/tfar/classicbar/api/BarColorOverlay.java");
    String colorUtils = readProjectFile("src/main/java/tfar/classicbar/util/ColorUtils.java");

    assertTrue(barMode.contains("implements TranslatableEnum"), "BarMode should implement TranslatableEnum for config screen localization");
    assertTrue(barMode.contains("OVERRIDE"), "BarMode should include OVERRIDE");
    assertFalse(barMode.contains("COMPAT"), "BarMode should no longer include COMPAT");
    assertTrue(barMode.contains("DISABLED"), "BarMode should include DISABLED");
    assertTrue(barMode.contains("Component.translatable"), "BarMode should override getTranslatedName with translatable components");
    assertTrue(barColorOverlay.contains("DEFAULT_CONFIG_VALUE = \"#00FFFFFF\""), "BarColorOverlay should define a no-op default overlay");
    assertTrue(barColorOverlay.contains("ColorUtils.parseHexColor"), "BarColorOverlay should reuse the shared hex parser");
    assertTrue(barColorOverlay.contains("applyTo"), "BarColorOverlay should expose tint application logic");
    assertTrue(colorUtils.contains("parseHexColor"), "ColorUtils should parse RGB and ARGB hex strings");
    assertTrue(colorUtils.contains("normalized.length() != 6 && normalized.length() != 8"), "ColorUtils should accept only RGB or ARGB hex strings");
    assertTrue(colorUtils.contains("public record ParsedHexColor"), "ColorUtils should expose parsed alpha information");
  }

  @Test
  void sourceKeepsLayoutSafetyReloadHooksAndModeDrivenVanillaCancellation() throws IOException {
    String clientEventHandler = readProjectFile("src/main/java/tfar/classicbar/client/EventHandler.java");
    String classicBarsConfig = readProjectFile("src/main/java/tfar/classicbar/config/ClassicBarsConfig.java");
    String configCache = readProjectFile("src/main/java/tfar/classicbar/config/ConfigCache.java");

    assertTrue(clientEventHandler.contains("Set<String> appliedOverlays = new LinkedHashSet<>()"), "layout application should track already-applied overlay ids");
    assertTrue(clientEventHandler.contains("overlay == null"), "layout application should ignore unknown ids without crashing");
    assertTrue(clientEventHandler.contains("settings.rendersClassicBar() && appliedOverlays.add(overlayId)"), "layout application should only stage overlays that still render through ClassicBar");
    assertTrue(clientEventHandler.contains("ConfigCache.setActiveLayoutOverlays(appliedOverlays);"), "layout application should cache the active layout overlay ids");
    assertTrue(configCache.contains("setActiveLayoutOverlays"), "ConfigCache should expose the active layout overlay cache writer");
    assertTrue(configCache.contains("isOverlayActiveInLayout"), "ConfigCache should expose the active layout overlay cache reader");
    assertTrue(clientEventHandler.contains("shouldCancelSharedPlayerHealthLayer"), "PLAYER_HEALTH should use a dedicated shared-layer cancellation policy");
    assertTrue(clientEventHandler.contains("shouldCancelIndependentVanillaLayer"), "single-layer vanilla overlays should use the layout-aware cancellation helper");
    assertTrue(clientEventHandler.contains("resolveOverlayRenderState"), "vanilla cancellation should resolve per-overlay runtime state");
    assertTrue(clientEventHandler.contains("ConfigCache.isOverlayActiveInLayout(overlayId)"), "vanilla cancellation should respect whether a bar is active in the current layout");
    assertTrue(clientEventHandler.contains("settings.cancelsVanillaLayer()"), "Only OVERRIDE mode should request vanilla cancellation");
    assertTrue(classicBarsConfig.contains("public static void onConfigLoading"), "ClassicBarsConfig should own the client config load hook");
    assertTrue(classicBarsConfig.contains("public static void onConfigReloading"), "ClassicBarsConfig should own the client config reload hook");
    assertTrue(classicBarsConfig.contains("event.getConfig().getType() == ModConfig.Type.CLIENT"), "Config reload should guard on client config type");
    assertTrue(classicBarsConfig.contains("EventHandler.cacheConfigs();"), "Config load and reload should rebuild client caches");
  }

  @Test
  void sourceDefinesReservedModSupportWithoutRestoringQuarantine() throws IOException {
    String classicBarsConfig = readProjectFile("src/main/java/tfar/classicbar/config/ClassicBarsConfig.java");
    String buildGradle = readProjectFile("build.gradle");

    assertTrue(classicBarsConfig.contains("registerReservedModSupport(builder, \"toughasnails\")"), "Reserved Tough As Nails toggle should exist");
    assertTrue(classicBarsConfig.contains("registerReservedModSupport(builder, \"vampirism\")"), "Reserved Vampirism toggle should exist");
    assertTrue(classicBarsConfig.contains("registerReservedModSupport(builder, \"parcool\")"), "Reserved ParCool toggle should exist");
    assertTrue(classicBarsConfig.contains("registerReservedModSupport(builder, \"feathers\")"), "Reserved Feathers toggle should exist");
    assertTrue(classicBarsConfig.contains("modSupportKey(modId, \"enabled\")"), "Reserved mod support entries should be localized through the helper key builder");
    assertTrue(buildGradle.contains("exclude 'tfar/classicbar/compat/**'"), "compat quarantine should remain excluded from active compile");
    assertTrue(buildGradle.contains("exclude 'tfar/classicbar/impl/overlays/mod/**'"), "mod overlay quarantine should remain excluded from active compile");
  }

  @Test
  void sourceUsesCorrectThirstKeyAndAvoidsLegacyJsonPersistence() throws IOException {
    String classicBarsConfig = readProjectFile("src/main/java/tfar/classicbar/config/ClassicBarsConfig.java");

    assertTrue(classicBarsConfig.contains("define(\"thirst_bar_color\""), "NeoForge config should use the corrected thirst_bar_color key");
    assertFalse(classicBarsConfig.contains("define(\"thirstr_bar_color\""), "NeoForge config should not keep the misspelled thirstr_bar_color key");

    for (String forbidden : List.of("ForgeConfigSpec", "Gson", "JsonReader", "JsonWriter", "FileReader", "FileWriter", "config/classicbar")) {
      assertFalse(readMainJavaTree().contains(forbidden), "Active source should not reintroduce legacy JSON persistence token: " + forbidden);
    }
  }

  @Test
  void sourceLocalizesConfigEntriesAndProvidesLangKeys() throws IOException {
    String classicBarsConfig = readProjectFile("src/main/java/tfar/classicbar/config/ClassicBarsConfig.java");
    String enUs = readProjectFile("src/main/resources/assets/classicbar/lang/en_us.json");
    String zhCn = readProjectFile("src/main/resources/assets/classicbar/lang/zh_cn.json");

    assertTrue(classicBarsConfig.contains(".translation("), "ClassicBarsConfig should use NeoForge translation keys for config sections and entries");
    assertNoMissingLangKeys(enUs, "en_us.json");
    assertNoMissingLangKeys(zhCn, "zh_cn.json");
  }

  @Test
  void langExplainsSharedPlayerHealthLimitAndColorOverlaySemantics() throws IOException {
    String enUs = readProjectFile("src/main/resources/assets/classicbar/lang/en_us.json");
    String zhCn = readProjectFile("src/main/resources/assets/classicbar/lang/zh_cn.json");

    String healthModeTooltip = extractLangValue(enUs, "classicbar.config.bars.health.mode.tooltip");
    String absorptionModeTooltip = extractLangValue(enUs, "classicbar.config.bars.absorption.mode.tooltip");
    String healthColorTooltip = extractLangValue(enUs, "classicbar.config.bars.health.color_overlay.tooltip");
    String foodColorTooltip = extractLangValue(enUs, "classicbar.config.bars.food.color_overlay.tooltip");
    String zhHealthModeTooltip = extractLangValue(zhCn, "classicbar.config.bars.health.mode.tooltip");
    String zhAbsorptionModeTooltip = extractLangValue(zhCn, "classicbar.config.bars.absorption.mode.tooltip");
    String zhHealthColorTooltip = extractLangValue(zhCn, "classicbar.config.bars.health.color_overlay.tooltip");

    assertTrue(healthModeTooltip.contains("PLAYER_HEALTH"), "health mode tooltip should document the shared PLAYER_HEALTH vanilla layer");
    assertTrue(healthModeTooltip.contains("no absorption hearts"), "health mode tooltip should explain that normal health override still hides vanilla hearts when no vanilla absorption needs preserving");
    assertTrue(healthModeTooltip.contains("visible absorption hearts"), "health mode tooltip should explain that visible vanilla absorption hearts keep the shared layer unless absorption also overrides");
    assertTrue(absorptionModeTooltip.contains("PLAYER_HEALTH"), "absorption mode tooltip should document the shared PLAYER_HEALTH vanilla layer");
    assertTrue(absorptionModeTooltip.contains("visible absorption hearts"), "absorption mode tooltip should explain when shared-layer cancellation stays conservative");
    assertTrue(healthColorTooltip.contains("all fill passes"), "health color tooltip should explain that #RRGGBB replaces every fill pass");
    assertTrue(healthColorTooltip.contains("alpha-blends"), "health color tooltip should explain that #AARRGGBB alpha-blends instead of changing render transparency");
    assertTrue(healthColorTooltip.contains("effect overlays can still render on top"), "health color tooltip should mention effect overlays can still stack");
    assertTrue(foodColorTooltip.contains("all fill passes"), "food color tooltip should explain that multi-pass fills are all recolored");
    assertTrue(zhHealthModeTooltip.contains("PLAYER_HEALTH"), "zh_cn health mode tooltip should also document the shared PLAYER_HEALTH layer");
    assertTrue(zhHealthModeTooltip.contains("没有原版吸收心"), "zh_cn health mode tooltip should explain that health override can still hide vanilla hearts when no absorption hearts are present");
    assertTrue(zhAbsorptionModeTooltip.contains("原版吸收心"), "zh_cn absorption mode tooltip should explain the visible absorption preservation rule");
    assertTrue(zhHealthColorTooltip.contains("所有填充层"), "zh_cn health color tooltip should explain that all fill passes are recolored");
  }

  @Test
  void langCoverageVerifierWouldFailIfTooltipKeyDisappeared() throws IOException {
    String enUs = readProjectFile("src/main/resources/assets/classicbar/lang/en_us.json");
    String brokenLang = enUs.replace("\"classicbar.config.bars.health.mode.tooltip\"", "\"classicbar.config.bars.health.mode.tooltip.missing\"");
    List<String> missingKeys = findMissingLangKeys(brokenLang);
    assertTrue(missingKeys.contains("classicbar.config.bars.health.mode.tooltip"), "Lang verifier should fail when a required tooltip key disappears");
  }

  @Test
  void langCoverageVerifierWouldFailIfSectionButtonKeyDisappeared() throws IOException {
    String enUs = readProjectFile("src/main/resources/assets/classicbar/lang/en_us.json");
    String brokenLang = enUs.replace("\"classicbar.config.section.general.button\"", "\"classicbar.config.section.general.button.missing\"");
    List<String> missingKeys = findMissingLangKeys(brokenLang);
    assertTrue(missingKeys.contains("classicbar.config.section.general.button"), "Lang verifier should fail when a required section button key disappears");
  }

  private static void assertNoMissingLangKeys(String langJson, String fileName) {
    List<String> missingKeys = findMissingLangKeys(langJson);
    assertTrue(missingKeys.isEmpty(), fileName + " is missing lang keys: " + missingKeys);
  }

  private static List<String> findMissingLangKeys(String langJson) {
    List<String> missingKeys = new ArrayList<>();
    for (String key : REQUIRED_LANG_KEYS) {
      if (!langJson.contains("\"" + key + "\"")) {
        missingKeys.add(key);
      }
    }
    for (String barId : ACTIVE_BAR_CONSTANTS.keySet()) {
      for (String suffix : List.of("show_text", "show_text.tooltip", "icon", "icon.tooltip", "mode", "mode.tooltip", "color_overlay", "color_overlay.tooltip", "text_format", "text_format.tooltip")) {
        String key = "classicbar.config.bars." + barId + "." + suffix;
        if (!langJson.contains("\"" + key + "\"")) {
          missingKeys.add(key);
        }
      }
    }
    return missingKeys;
  }

  private static String readMainJavaTree() throws IOException {
    StringBuilder builder = new StringBuilder();
    Path root = projectFile("src/main/java");
    try (var files = Files.walk(root)) {
      for (Path file : files.filter(path -> path.toString().endsWith(".java")).toList()) {
        builder.append(Files.readString(file));
      }
    }
    return builder.toString();
  }

  private static String readProjectFile(String relativePath) throws IOException {
    return Files.readString(projectFile(relativePath));
  }

  private static String extractLangValue(String langJson, String key) {
    Pattern pattern = Pattern.compile("\\\"" + Pattern.quote(key) + "\\\"\\s*:\\s*\\\"([^\\\"]*)\\\"");
    Matcher matcher = pattern.matcher(langJson);
    assertTrue(matcher.find(), "Missing lang value for key: " + key);
    return matcher.group(1);
  }

  private static Path projectFile(String relativePath) {
    String projectDir = System.getProperty("classicbar.projectDir");
    if (projectDir == null || projectDir.isBlank()) {
      throw new IllegalStateException("Missing classicbar.projectDir test property");
    }
    return Path.of(projectDir).resolve(relativePath);
  }
}
