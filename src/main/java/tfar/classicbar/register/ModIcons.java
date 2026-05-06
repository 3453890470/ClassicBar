package tfar.classicbar.register;

import net.minecraft.resources.ResourceLocation;
import tfar.classicbar.ClassicBar;

/**
 * Centralized registry for all ClassicBar icon ResourceLocations.
 * <p>
 * This is the canonical source for icon constants.
 * {@link tfar.classicbar.resources.BarIcons} delegates here for backward compatibility.
 * New code should prefer referencing this class directly.
 */
public final class ModIcons {

    public static final ResourceLocation FALLBACK = icon("fallback");
    public static final ResourceLocation HEALTH = icon("health");
    public static final ResourceLocation HEALTH_POISON = icon("health_poison");
    public static final ResourceLocation HEALTH_WITHER = icon("health_wither");
    public static final ResourceLocation HEALTH_FROZEN = icon("health_frozen");
    public static final ResourceLocation ARMOR = icon("armor");
    public static final ResourceLocation FOOD = icon("food");
    public static final ResourceLocation FOOD_BLINKING = icon("food_blinking");
    public static final ResourceLocation FOOD_HUNGER = icon("food_hunger");
    public static final ResourceLocation HEALTH_BLINKING = icon("health_blinking");
    public static final ResourceLocation HEALTH_POISON_BLINKING = icon("health_poison_blinking");
    public static final ResourceLocation HEALTH_WITHER_BLINKING = icon("health_wither_blinking");
    public static final ResourceLocation HEALTH_FROZEN_BLINKING = icon("health_frozen_blinking");
    public static final ResourceLocation FOOD_HUNGER_BLINKING = icon("food_hunger_blinking");
    public static final ResourceLocation AIR_BLINKING = icon("air_blinking");
    public static final ResourceLocation AIR = icon("air");
    public static final ResourceLocation ABSORPTION = icon("absorption");
    public static final ResourceLocation ARMOR_TOUGHNESS = icon("armor_toughness");
    public static final ResourceLocation MOUNT_HEALTH = icon("mount_health");
    public static final ResourceLocation BLOOD = icon("blood");

    //#region Forbidden Curse (EnigmaticLegacy+ compatibility)
    /** Forbidden curse hunger bar icon (EnigmaticLegacy+ compat) */
    public static final ResourceLocation FORBIDDEN_HUNGER = icon("forbidden_hunger");
    //#endregion

    public static final ResourceLocation THIRST = icon("thirst");
    public static final ResourceLocation FOOD_NOURISHMENT = icon("food_nourishment");
    public static final ResourceLocation FOOD_SATIATED_SHIELD = icon("food_satiated_shield");

    private ModIcons() {
    }

    /**
     * Resolves an overlay name to its corresponding icon ResourceLocation.
     */
    public static ResourceLocation forOverlay(String overlayName) {
        return switch (overlayName) {
            case "health" -> HEALTH;
            case "armor" -> ARMOR;
            case "food" -> FOOD;
            case "air" -> AIR;
            case "absorption" -> ABSORPTION;
            case "armor_toughness" -> ARMOR_TOUGHNESS;
            case "health_mount", "mount_health" -> MOUNT_HEALTH;
            case "blood" -> BLOOD;
            case "thirst", "thirst_level" -> THIRST;
            default -> FALLBACK;
        };
    }

    /**
     * Register icon constants.
     * <p>
     * Icons are static constants and need no runtime registration.
     * This method exists for consistency with the centralized registration pattern.
     */
    public static void register() {
        // All icons are static final constants; nothing to register at runtime.
    }

    private static ResourceLocation icon(String name) {
        return ResourceLocation.fromNamespaceAndPath(ClassicBar.MODID, "textures/gui/icons/" + name + ".png");
    }
}
