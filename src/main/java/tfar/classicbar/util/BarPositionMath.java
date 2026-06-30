package tfar.classicbar.util;

/**
 * Pure coordinate computation helpers for health status icon rendering.
 * <p>
 * Contains only static math — no Minecraft runtime dependencies.
 * Safe to use from unit tests and production rendering code.
 */
public final class BarPositionMath {

    /** Maximum width the bar can be. */
    public static final int WIDTH = 77;

    private BarPositionMath() {}

    /**
     * Computes the bar width for health.
     * Pure math: WIDTH * health / maxHealth.
     *
     * @param health    current health
     * @param maxHealth maximum health
     * @return bar width
     */
    public static double healthBarWidth(double health, double maxHealth) {
        return WIDTH * health / maxHealth;
    }

    /** Overlap between stacked effect icons, in pixels. */
    public static final int EFFECT_ICON_OVERLAP = 4;

    /**
     * Computes the x-positions for status effect icons that replace the base icon.
     * <p>
     * The first effect icon occupies the same slot as the base icon ({@code baseX}),
     * with each subsequent effect offset by {@link #EFFECT_ICON_OVERLAP} inward.
     * This ensures the replacement icon renders at the base icon's position
     * rather than being shifted right (RHS) or left (LHS).
     *
     * @param baseX       the base x position of the icon
     * @param rightSide   true for RHS (icons extend left), false for LHS (icons extend right)
     * @param effectCount number of active effects (0 returns empty array)
     * @return array of x-positions for each effect, in draw order
     */
    public static int[] effectIconPositions(int baseX, boolean rightSide, int effectCount) {
        if (effectCount <= 0) return new int[0];
        int[] positions = new int[effectCount];
        int currentX = baseX;
        int step = rightSide ? -EFFECT_ICON_OVERLAP : EFFECT_ICON_OVERLAP;
        for (int i = 0; i < effectCount; i++) {
            positions[i] = currentX;
            currentX += step;
        }
        return positions;
    }

    /**
     * Computes the starting x for the health overlay's text when effects are active.
     * <p>
     * The text position should anchor to {@code baseX} (same as the first icon),
     * not shifted by effect count.
     *
     * @param baseX       the base x position of the icon
     * @param rightSide   true for RHS, false for LHS
     * @param effectCount number of active effects (0 = no effect)
     * @return the text anchor x position
     */
    public static int healthTextX(int baseX, boolean rightSide, int effectCount) {
        return baseX;
    }
}
