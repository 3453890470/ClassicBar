package tfar.classicbar.resources;

import net.minecraft.resources.ResourceLocation;
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

    public static final ResourceLocation FALLBACK = ModIcons.FALLBACK;
    public static final ResourceLocation HEALTH = ModIcons.HEALTH;
    public static final ResourceLocation HEALTH_POISON = ModIcons.HEALTH_POISON;
    public static final ResourceLocation HEALTH_WITHER = ModIcons.HEALTH_WITHER;
    public static final ResourceLocation HEALTH_FROZEN = ModIcons.HEALTH_FROZEN;
    public static final ResourceLocation ARMOR = ModIcons.ARMOR;
    public static final ResourceLocation FOOD = ModIcons.FOOD;
    public static final ResourceLocation FOOD_BLINKING = ModIcons.FOOD_BLINKING;
    public static final ResourceLocation FOOD_HUNGER = ModIcons.FOOD_HUNGER;
    public static final ResourceLocation HEALTH_BLINKING = ModIcons.HEALTH_BLINKING;
    public static final ResourceLocation HEALTH_POISON_BLINKING = ModIcons.HEALTH_POISON_BLINKING;
    public static final ResourceLocation HEALTH_WITHER_BLINKING = ModIcons.HEALTH_WITHER_BLINKING;
    public static final ResourceLocation HEALTH_FROZEN_BLINKING = ModIcons.HEALTH_FROZEN_BLINKING;
    public static final ResourceLocation FOOD_HUNGER_BLINKING = ModIcons.FOOD_HUNGER_BLINKING;
    public static final ResourceLocation AIR_BLINKING = ModIcons.AIR_BLINKING;
    public static final ResourceLocation AIR = ModIcons.AIR;
    public static final ResourceLocation ABSORPTION = ModIcons.ABSORPTION;
    public static final ResourceLocation ARMOR_TOUGHNESS = ModIcons.ARMOR_TOUGHNESS;
    public static final ResourceLocation MOUNT_HEALTH = ModIcons.MOUNT_HEALTH;
    public static final ResourceLocation BLOOD = ModIcons.BLOOD;

    //#region Forbidden Curse (EnigmaticLegacy+ compatibility)
    /** Forbidden curse hunger bar icon (EnigmaticLegacy+ compat) */
    public static final ResourceLocation FORBIDDEN_HUNGER = ModIcons.FORBIDDEN_HUNGER;
    //#endregion

    public static final ResourceLocation THIRST = ModIcons.THIRST;
    public static final ResourceLocation FOOD_NOURISHMENT = ModIcons.FOOD_NOURISHMENT;
    public static final ResourceLocation FOOD_SATIATED_SHIELD = ModIcons.FOOD_SATIATED_SHIELD;

    private BarIcons() {
    }

    /**
     * Delegates to {@link ModIcons#forOverlay(String)}.
     */
    public static ResourceLocation forOverlay(String overlayName) {
        return ModIcons.forOverlay(overlayName);
    }
}
