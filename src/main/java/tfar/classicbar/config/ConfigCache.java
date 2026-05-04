package tfar.classicbar.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import tfar.classicbar.util.Color;
import tfar.classicbar.util.ColorUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ConfigCache {

    public static boolean icons;
    public static boolean showFoodPreview;
    public static List<Color> armor = new ArrayList<>();
    public static List<Color> armor_toughness = new ArrayList<>();
    public static Color hunger;
    public static Color hungerDebuff;
    public static Color saturation;
    public static Color saturationDebuff;
    public static Color thirst;
    public static Color thirstDebuff;
    public static boolean showHydrationBar;
    public static boolean showHeldDrinkOverlay;
    public static boolean showThirstExhaustionOverlay;
    public static Color nourishment;
    public static Color satiatedShield;
    public static boolean disableFdNourishmentOverlay;
    public static boolean enableNourishmentCompat;
    public static boolean enableSatiatedShieldCompat;
    public static Color forbiddenCurseBarColor;  // forbidden curse hunger bar color
    public static boolean enableEnigmaticLegacyPlus; // EnigmaticLegacyPlus compat toggle
    public static Color hydration;
    public static Color hydrationDebuff;
    public static Color air;
    public static List<Color> normal = new ArrayList<>();
    public static List<Color> poison = new ArrayList<>();
    public static List<Color> wither = new ArrayList<>();
    public static List<Color> absorption = new ArrayList<>();
    public static List<Color> absorptionPoison = new ArrayList<>();
    public static List<Color> absorptionWither = new ArrayList<>();
    public static Color frozenHealth;
    public static Color healthPoisonOverlay;
    public static float healthPoisonOverlayAlpha;
    public static Color healthWitherOverlay;
    public static float healthWitherOverlayAlpha;
    public static Color healthFrozenOverlay;
    public static float healthFrozenOverlayAlpha;
    private static Set<String> activeLayoutOverlays = Set.of();

    private static void clear() {
        armor.clear();
        armor_toughness.clear();
        normal.clear();
        poison.clear();
        wither.clear();
        absorption.clear();
        absorptionPoison.clear();
        absorptionWither.clear();
    }

    public static void setActiveLayoutOverlays(Set<String> overlayIds) {
        activeLayoutOverlays = Set.copyOf(new LinkedHashSet<>(overlayIds));
    }

    public static boolean isOverlayActiveInLayout(String overlayId) {
        return activeLayoutOverlays.contains(overlayId);
    }

    public static void bake() {
        clear();
        icons = ClassicBarsConfig.displayIcons.get();
        showFoodPreview = ClassicBarsConfig.showFoodPreview.get();

        cacheList(ClassicBarsConfig.armorColors, armor);
        cacheList(ClassicBarsConfig.armorToughnessColors, armor_toughness);
        cacheList(ClassicBarsConfig.normalColors, normal);
        cacheList(ClassicBarsConfig.poisonedColors, poison);
        cacheList(ClassicBarsConfig.witheredColors, wither);
        cacheList(ClassicBarsConfig.absorptionColors, absorption);
        cacheList(ClassicBarsConfig.absorptionPoisonColors, absorptionPoison);
        cacheList(ClassicBarsConfig.absorptionWitherColors, absorptionWither);
        hunger = ColorUtils.hex2Color(ClassicBarsConfig.hungerBarColor.get());
        hungerDebuff = ColorUtils.hex2Color(ClassicBarsConfig.hungerBarDebuffColor.get());
        saturation = ColorUtils.hex2Color(ClassicBarsConfig.saturationBarColor.get());
        saturationDebuff = ColorUtils.hex2Color(ClassicBarsConfig.saturationBarDebuffColor.get());
        nourishment = ColorUtils.hex2Color(ClassicBarsConfig.nourishmentBarColor.get());
        satiatedShield = ColorUtils.hex2Color(ClassicBarsConfig.satiatedShieldBarColor.get());
        disableFdNourishmentOverlay = ClassicBarsConfig.disableFdNourishmentOverlay.get();
        enableNourishmentCompat = ClassicBarsConfig.isReservedModSupportEnabled("farmersdelight");
        enableSatiatedShieldCompat = ClassicBarsConfig.isReservedModSupportEnabled("kaleidoscope_cookery");
        forbiddenCurseBarColor = ColorUtils.hex2Color(ClassicBarsConfig.forbiddenCurseBarColor.get());
        enableEnigmaticLegacyPlus = ClassicBarsConfig.isReservedModSupportEnabled("enigmaticlegacyplus");
        thirst = ColorUtils.hex2Color(ClassicBarsConfig.thirstBarColor.get());
        thirstDebuff = ColorUtils.hex2Color(ClassicBarsConfig.thirstBarDebuffColor.get());
        hydration = ColorUtils.hex2Color(ClassicBarsConfig.hydrationBarColor.get());
        hydrationDebuff = ColorUtils.hex2Color(ClassicBarsConfig.hydrationBarDebuffColor.get());
        showHydrationBar = ClassicBarsConfig.showHydrationBar.get();
        showHeldDrinkOverlay = ClassicBarsConfig.showHeldDrinkOverlay.get();
        showThirstExhaustionOverlay = ClassicBarsConfig.showThirstExhaustionOverlay.get();
        air = ColorUtils.hex2Color(ClassicBarsConfig.airBarColor.get());
        frozenHealth = ColorUtils.hex2Color(ClassicBarsConfig.frozenHealthColor.get());

        // health overlay color (parsed from #AARRGGBB config)
        ColorUtils.ParsedHexColor parsed;

        parsed = ColorUtils.parseHexColor(ClassicBarsConfig.healthPoisonOverlayColor.get());
        if (parsed != null) {
            healthPoisonOverlay = parsed.color();
            healthPoisonOverlayAlpha = parsed.alphaAsFloat();
        } else {
            healthPoisonOverlay = Color.BLACK;
            healthPoisonOverlayAlpha = 1.0f;
        }

        parsed = ColorUtils.parseHexColor(ClassicBarsConfig.healthWitherOverlayColor.get());
        if (parsed != null) {
            healthWitherOverlay = parsed.color();
            healthWitherOverlayAlpha = parsed.alphaAsFloat();
        } else {
            healthWitherOverlay = Color.BLACK;
            healthWitherOverlayAlpha = 1.0f;
        }

        parsed = ColorUtils.parseHexColor(ClassicBarsConfig.healthFrozenOverlayColor.get());
        if (parsed != null) {
            healthFrozenOverlay = parsed.color();
            healthFrozenOverlayAlpha = parsed.alphaAsFloat();
        } else {
            healthFrozenOverlay = Color.BLACK;
            healthFrozenOverlayAlpha = 1.0f;
        }
    }

    private static void cacheList(ModConfigSpec.ConfigValue<List<? extends String>> config, List<Color> cache) {
        for (String s : config.get()) {
            cache.add(ColorUtils.hex2Color(s));
        }
    }
}
