package tfar.classicbar.resources;

import net.minecraft.resources.Identifier;
import tfar.classicbar.register.ModIcons;

/**
 * Backward-compatible wrapper around {@link ModIcons}.
 * <p>
 * All constants now delegate to {@link ModIcons}, the canonical icon registry.
 * This class is retained for source compatibility — existing code that imports
 * {@code BarIcons} continues to work unchanged.
 * <p>
 * New code should prefer {@link ModIcons} directly.
 */
public final class BarIcons {

    public static final Identifier FALLBACK = ModIcons.FALLBACK;
    public static final Identifier HEALTH = ModIcons.HEALTH;
    public static final Identifier HEALTH_POISON = ModIcons.HEALTH_POISON;
    public static final Identifier HEALTH_WITHER = ModIcons.HEALTH_WITHER;
    public static final Identifier HEALTH_FROZEN = ModIcons.HEALTH_FROZEN;
    public static final Identifier ARMOR = ModIcons.ARMOR;
    public static final Identifier FOOD = ModIcons.FOOD;
    public static final Identifier FOOD_BLINKING = ModIcons.FOOD_BLINKING;
    public static final Identifier FOOD_HUNGER = ModIcons.FOOD_HUNGER;
    public static final Identifier HEALTH_BLINKING = ModIcons.HEALTH_BLINKING;
    public static final Identifier HEALTH_POISON_BLINKING = ModIcons.HEALTH_POISON_BLINKING;
    public static final Identifier HEALTH_WITHER_BLINKING = ModIcons.HEALTH_WITHER_BLINKING;
    public static final Identifier HEALTH_FROZEN_BLINKING = ModIcons.HEALTH_FROZEN_BLINKING;
    public static final Identifier FOOD_HUNGER_BLINKING = ModIcons.FOOD_HUNGER_BLINKING;
    public static final Identifier AIR_BLINKING = ModIcons.AIR_BLINKING;
    public static final Identifier AIR = ModIcons.AIR;
    public static final Identifier ABSORPTION = ModIcons.ABSORPTION;
    public static final Identifier ARMOR_TOUGHNESS = ModIcons.ARMOR_TOUGHNESS;
    public static final Identifier MOUNT_HEALTH = ModIcons.MOUNT_HEALTH;
    public static final Identifier BLOOD = ModIcons.BLOOD;

    //#region Forbidden Curse (EnigmaticLegacy+ compatibility)
    /** Forbidden curse hunger bar icon (EnigmaticLegacy+ compat) */
    public static final Identifier FORBIDDEN_HUNGER = ModIcons.FORBIDDEN_HUNGER;
    //#endregion

    public static final Identifier THIRST = ModIcons.THIRST;
    public static final Identifier THIRST_BLINKING = ModIcons.THIRST_BLINKING;
    public static final Identifier THIRST_HUNGER = ModIcons.THIRST_HUNGER;
    public static final Identifier THIRST_HUNGER_BLINKING = ModIcons.THIRST_HUNGER_BLINKING;
    public static final Identifier THIRST_NOURISHMENT = ModIcons.THIRST_NOURISHMENT;
    public static final Identifier FOOD_NOURISHMENT = ModIcons.FOOD_NOURISHMENT;
    public static final Identifier FOOD_SATIATED_SHIELD = ModIcons.FOOD_SATIATED_SHIELD;

    private BarIcons() {
    }

    /**
     * Delegates to {@link ModIcons#forOverlay(String)}.
     */
    public static Identifier forOverlay(String overlayName) {
        return ModIcons.forOverlay(overlayName);
    }
}
