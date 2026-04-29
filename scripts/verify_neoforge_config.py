from __future__ import annotations

import re
import sys
from pathlib import Path, PurePosixPath


ROOT = Path(__file__).resolve().parent.parent
ACTIVE_OVERLAYS = {
    "health": "HEALTH",
    "armor": "ARMOR",
    "absorption": "ABSORPTION",
    "food": "FOOD",
    "armor_toughness": "ARMOR_TOUGHNESS",
    "health_mount": "MOUNT_HEALTH",
    "air": "AIR",
}
EXPECTED_QUARANTINE_EXCLUDES = [
    "tfar/classicbar/compat/**",
    "tfar/classicbar/impl/overlays/mod/**",
]
FORBIDDEN_RUNTIME_REFERENCES = [
    "ForgeConfigSpec",
    "com.google.gson",
    "Gson",
    "JsonObject",
    "JsonReader",
    "JsonWriter",
    "FileReader",
    "FileWriter",
    "config/classicbar",
]
REQUIRED_LANG_KEYS = [
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
    "classicbar.config.general.display_icons",
    "classicbar.config.general.display_icons.tooltip",
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
    "classicbar.config.enum.bar_mode.compat",
    "classicbar.config.enum.bar_mode.disabled",
]

for overlay_name in ACTIVE_OVERLAYS:
    REQUIRED_LANG_KEYS.extend(
        [
            f"classicbar.config.bars.{overlay_name}.show_text",
            f"classicbar.config.bars.{overlay_name}.show_text.tooltip",
            f"classicbar.config.bars.{overlay_name}.icon",
            f"classicbar.config.bars.{overlay_name}.icon.tooltip",
            f"classicbar.config.bars.{overlay_name}.mode",
            f"classicbar.config.bars.{overlay_name}.mode.tooltip",
            f"classicbar.config.bars.{overlay_name}.color_overlay",
            f"classicbar.config.bars.{overlay_name}.color_overlay.tooltip",
        ]
    )


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
    if re.search(pattern, text, flags=re.MULTILINE | re.DOTALL) is None:
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


def require_lang_keys(lang_text: str, label: str, ctx: CheckContext) -> None:
    for key in REQUIRED_LANG_KEYS:
        if f'"{key}"' not in lang_text:
            ctx.fail(f"{label}: missing lang key {key!r}")


def main() -> int:
    ctx = CheckContext()

    build_gradle = read_text(ROOT / "build.gradle", ctx)
    classic_bar = read_text(ROOT / "src" / "main" / "java" / "tfar" / "classicbar" / "ClassicBar.java", ctx)
    classic_bar_client = read_text(ROOT / "src" / "main" / "java" / "tfar" / "classicbar" / "client" / "ClassicBarClient.java", ctx)
    client_event_handler = read_text(ROOT / "src" / "main" / "java" / "tfar" / "classicbar" / "client" / "EventHandler.java", ctx)
    classic_bars_config = read_text(ROOT / "src" / "main" / "java" / "tfar" / "classicbar" / "config" / "ClassicBarsConfig.java", ctx)
    bar_settings = read_text(ROOT / "src" / "main" / "java" / "tfar" / "classicbar" / "api" / "BarSettings.java", ctx)
    config_cache = read_text(ROOT / "src" / "main" / "java" / "tfar" / "classicbar" / "config" / "ConfigCache.java", ctx)
    bar_icons = read_text(ROOT / "src" / "main" / "java" / "tfar" / "classicbar" / "resources" / "BarIcons.java", ctx)
    bar_mode = read_text(ROOT / "src" / "main" / "java" / "tfar" / "classicbar" / "api" / "BarMode.java", ctx)
    bar_color_overlay = read_text(ROOT / "src" / "main" / "java" / "tfar" / "classicbar" / "api" / "BarColorOverlay.java", ctx)
    en_us = read_text(ROOT / "src" / "main" / "resources" / "assets" / "classicbar" / "lang" / "en_us.json", ctx)
    zh_cn = read_text(ROOT / "src" / "main" / "resources" / "assets" / "classicbar" / "lang" / "zh_cn.json", ctx)

    quarantine_allowlist = extract_source_set_excludes(build_gradle)
    if not quarantine_allowlist:
        ctx.fail("build.gradle: expected sourceSets quarantine excludes for active-source checks")
    elif sorted(quarantine_allowlist) != sorted(EXPECTED_QUARANTINE_EXCLUDES):
        ctx.fail(
            "build.gradle quarantine allowlist changed unexpectedly: "
            f"expected {EXPECTED_QUARANTINE_EXCLUDES}, got {quarantine_allowlist}"
        )

    require_contains(classic_bar, "modContainer.registerConfig(ModConfig.Type.CLIENT, ClassicBarsConfig.CLIENT_SPEC);", "ClassicBar registerConfig call", ctx)
    require_contains(classic_bar, "registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);", "ClassicBar config screen extension point", ctx)
    require_contains(classic_bars_config, "public static final ModConfigSpec CLIENT_SPEC;", "ClassicBarsConfig CLIENT_SPEC", ctx)
    require_contains(classic_bars_config, "public static final ClassicBarsConfig CLIENT;", "ClassicBarsConfig CLIENT", ctx)
    require_absent(classic_bar, "EventHandler.cacheConfigs();", "ClassicBar constructor eager config read", ctx)

    require_contains(classic_bar_client, "modBus.addListener(ClassicBarsConfig::onConfigLoading);", "ClassicBarClient config loading listener", ctx)
    require_contains(classic_bar_client, "modBus.addListener(ClassicBarsConfig::onConfigReloading);", "ClassicBarClient config reloading listener", ctx)
    require_absent(classic_bar_client, "ClassicBarClient::onConfigLoading", "ClassicBarClient should delegate config loading to ClassicBarsConfig", ctx)
    require_absent(classic_bar_client, "ClassicBarClient::onConfigReloading", "ClassicBarClient should delegate config reloading to ClassicBarsConfig", ctx)

    require_contains(classic_bars_config, "public static void onConfigLoading", "ClassicBarsConfig loading callback", ctx)
    require_contains(classic_bars_config, "public static void onConfigReloading", "ClassicBarsConfig reloading callback", ctx)
    require_contains(classic_bars_config, "event.getConfig().getType() == ModConfig.Type.CLIENT", "ClassicBarsConfig client-type guard", ctx)
    require_contains(classic_bars_config, "EventHandler.cacheConfigs();", "ClassicBarsConfig cache refresh", ctx)

    require_contains(classic_bars_config, 'sectionKey("general")', "ClassicBarsConfig general section translation", ctx)
    require_contains(classic_bars_config, 'sectionKey("layout")', "ClassicBarsConfig layout section translation", ctx)
    require_contains(classic_bars_config, 'sectionKey("bars")', "ClassicBarsConfig bars section translation", ctx)
    require_contains(classic_bars_config, 'sectionKey("mod_support")', "ClassicBarsConfig mod_support section translation", ctx)

    require_contains(classic_bars_config, '.push("general")', "ClassicBarsConfig general section", ctx)
    require_contains(classic_bars_config, '.push("layout")', "ClassicBarsConfig layout section", ctx)
    require_contains(classic_bars_config, '.push("bars")', "ClassicBarsConfig bars section", ctx)
    require_contains(classic_bars_config, '.push("mod_support")', "ClassicBarsConfig mod_support section", ctx)

    require_contains(classic_bars_config, 'define("thirst_bar_color"', "ClassicBarsConfig corrected thirst key", ctx)
    require_absent(classic_bars_config, 'define("thirstr_bar_color"', "ClassicBarsConfig typo key removal", ctx)

    require_contains(bar_icons, '"textures/gui/icons/" + name + ".png"', "BarIcons stable icon path", ctx)
    require_contains(classic_bars_config, "ClassicBarsConfig::isValidIconValue", "ClassicBarsConfig icon validator", ctx)
    require_contains(classic_bars_config, "ResourceLocation.tryParse", "ClassicBarsConfig icon parsing", ctx)
    require_contains(classic_bars_config, "ClassicBar.MODID.equals(resourceLocation.getNamespace())", "ClassicBarsConfig icon namespace gate", ctx)
    require_contains(classic_bars_config, 'resourceLocation.getPath().startsWith("textures/gui/icons/")', "ClassicBarsConfig icon path prefix gate", ctx)
    require_contains(classic_bars_config, 'resourceLocation.getPath().endsWith(".png")', "ClassicBarsConfig icon png suffix gate", ctx)

    for overlay, constant in ACTIVE_OVERLAYS.items():
        require_contains(classic_bars_config, f'registerBarConfig(builder, "{overlay}", BarIcons.{constant}', f"ClassicBarsConfig active bar registration for {overlay}", ctx)

    require_contains(classic_bars_config, 'barKey(name, "show_text")', "ClassicBarsConfig per-bar show_text translation helper", ctx)
    require_contains(classic_bars_config, 'barKey(name, "icon")', "ClassicBarsConfig per-bar icon translation helper", ctx)
    require_contains(classic_bars_config, 'barKey(name, "mode")', "ClassicBarsConfig per-bar mode translation helper", ctx)
    require_contains(classic_bars_config, 'barKey(name, "color_overlay")', "ClassicBarsConfig per-bar color overlay translation helper", ctx)

    require_contains(classic_bars_config, 'define("show_text"', "ClassicBarsConfig bar show_text key", ctx)
    require_contains(classic_bars_config, 'define("icon"', "ClassicBarsConfig bar icon key", ctx)
    require_contains(classic_bars_config, 'defineEnum("mode"', "ClassicBarsConfig bar mode key", ctx)
    require_contains(classic_bars_config, 'define("color_overlay"', "ClassicBarsConfig bar color overlay key", ctx)
    require_contains(classic_bars_config, "ClassicBarsConfig::isValidActiveBarId", "ClassicBarsConfig layout validator", ctx)
    require_contains(classic_bars_config, "ClassicBarsConfig::isValidHexColor", "ClassicBarsConfig color validator", ctx)
    require_contains(classic_bars_config, "ClassicBarsConfig::isValidUnitFraction", "ClassicBarsConfig fraction validator", ctx)
    require_regex(classic_bars_config, r'leftorder\s*=\s*builder\.translation\(layoutKey\("left_order"\)\).*defineList\("left_order".*ClassicBarsConfig::isValidActiveBarId', "ClassicBarsConfig left_order validator", ctx)
    require_regex(classic_bars_config, r'rightorder\s*=\s*builder\.translation\(layoutKey\("right_order"\)\).*defineList\("right_order".*ClassicBarsConfig::isValidActiveBarId', "ClassicBarsConfig right_order validator", ctx)

    require_contains(bar_mode, "implements TranslatableEnum", "BarMode translatable enum", ctx)
    require_contains(bar_mode, "OVERRIDE", "BarMode override constant", ctx)
    require_contains(bar_mode, "COMPAT", "BarMode compat constant", ctx)
    require_contains(bar_mode, "DISABLED", "BarMode disabled constant", ctx)
    require_contains(bar_mode, "Component.translatable", "BarMode translated name override", ctx)
    require_contains(bar_color_overlay, "DEFAULT_CONFIG_VALUE = \"#00FFFFFF\"", "BarColorOverlay default no-op tint", ctx)
    require_contains(bar_color_overlay, "ColorUtils.parseHexColor", "BarColorOverlay color parser", ctx)
    require_contains(bar_color_overlay, "applyTo", "BarColorOverlay tint application", ctx)

    require_contains(bar_settings, "public BarMode mode;", "BarSettings mode field", ctx)
    require_contains(bar_settings, "public BarColorOverlay color_overlay;", "BarSettings color_overlay field", ctx)
    require_contains(bar_settings, "rendersClassicBar()", "BarSettings classicbar mode gate", ctx)
    require_contains(bar_settings, "cancelsVanillaLayer()", "BarSettings vanilla cancellation gate", ctx)

    require_contains(client_event_handler, "Set<String>", "EventHandler duplicate overlay guard type", ctx)
    require_contains(client_event_handler, "new LinkedHashSet<>()", "EventHandler duplicate overlay guard implementation", ctx)
    require_contains(client_event_handler, "overlay == null", "EventHandler unknown overlay null guard", ctx)
    require_contains(client_event_handler, "settings.rendersClassicBar() && appliedOverlays.add(overlayId)", "EventHandler disabled overlay guard", ctx)
    require_contains(client_event_handler, "ConfigCache.setActiveLayoutOverlays(appliedOverlays);", "EventHandler applied layout cache", ctx)
    require_contains(client_event_handler, "VanillaGuiLayers.PLAYER_HEALTH", "EventHandler player health vanilla layer handling", ctx)
    require_contains(client_event_handler, "shouldCancelSharedPlayerHealthLayer", "EventHandler shared player health cancellation rule", ctx)
    require_contains(client_event_handler, "shouldCancelIndependentVanillaLayer", "EventHandler independent layer cancellation rule", ctx)
    require_contains(client_event_handler, "resolveOverlayRenderState", "EventHandler runtime overlay state resolver", ctx)
    require_contains(client_event_handler, "ConfigCache.isOverlayActiveInLayout(overlayId)", "EventHandler layout-aware overlay guard", ctx)
    require_contains(client_event_handler, "settings.cancelsVanillaLayer()", "EventHandler override-only vanilla cancellation", ctx)

    require_contains(config_cache, "setActiveLayoutOverlays", "ConfigCache active layout overlay setter", ctx)
    require_contains(config_cache, "isOverlayActiveInLayout", "ConfigCache active layout overlay lookup", ctx)

    require_contains(config_cache, "icons = ClassicBarsConfig.displayIcons.get();", "ConfigCache uses NeoForge config", ctx)
    require_contains(config_cache, "ColorUtils.hex2Color", "ConfigCache color parsing", ctx)

    require_contains(classic_bars_config, 'registerReservedModSupport(builder, "toughasnails")', "ClassicBarsConfig TAN reserved toggle registration", ctx)
    require_contains(classic_bars_config, 'registerReservedModSupport(builder, "vampirism")', "ClassicBarsConfig Vampirism reserved toggle registration", ctx)
    require_contains(classic_bars_config, 'registerReservedModSupport(builder, "parcool")', "ClassicBarsConfig ParCool reserved toggle registration", ctx)
    require_contains(classic_bars_config, 'registerReservedModSupport(builder, "feathers")', "ClassicBarsConfig Feathers reserved toggle registration", ctx)
    require_contains(classic_bars_config, 'modSupportKey(modId, "enabled")', "ClassicBarsConfig mod support translation helper", ctx)

    require_lang_keys(en_us, "en_us.json", ctx)
    require_lang_keys(zh_cn, "zh_cn.json", ctx)
    require_contains(en_us, "no absorption hearts", "en_us shared player health fallback semantics", ctx)
    require_contains(en_us, "visible absorption hearts", "en_us shared player health preservation semantics", ctx)
    require_contains(zh_cn, "没有原版吸收心", "zh_cn shared player health fallback semantics", ctx)
    require_contains(zh_cn, "原版吸收心", "zh_cn shared player health preservation semantics", ctx)

    for forbidden in FORBIDDEN_RUNTIME_REFERENCES:
        matches = collect_java_matches(ROOT / "src" / "main" / "java", forbidden)
        require_absent_in_active_sources(matches, quarantine_allowlist, f"Forbidden runtime config reference {forbidden}", ctx)

    if ctx.failures:
        print("NeoForge config verification: RED")
        for failure in ctx.failures:
            print(f"- {failure}")
        if ctx.notes:
            print("\nNotes:")
            for note in ctx.notes:
                print(f"- {note}")
        return 1

    print("NeoForge config verification: GREEN")
    for note in ctx.notes:
        print(f"- {note}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
