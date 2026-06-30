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
     * with each subsequent effect offset by {@link #EFFECT_ICON_OVERLAP} AWAY from
     * the health bar (to avoid covering it).
     * <p>
     * Layout semantics:
     * <ul>
     *   <li><b>RHS</b> ({@code rightSide = true}): bar extends LEFT from the icon;
     *       icons stack RIGHTWARD (step = {@code +OVERLAP}, away from bar).</li>
     *   <li><b>LHS</b> ({@code rightSide = false}): bar extends RIGHT from the icon;
     *       icons stack LEFTWARD (step = {@code -OVERLAP}, away from bar).</li>
     * </ul>
     *
     * @param baseX       the base x position of the icon
     * @param rightSide   true for RHS (bar left of icon, icons extend right),
     *                    false for LHS (bar right of icon, icons extend left)
     * @param effectCount number of active effects (0 returns empty array)
     * @return array of x-positions for each effect, in draw order
     */
    public static int[] effectIconPositions(int baseX, boolean rightSide, int effectCount) {
        if (effectCount <= 0) return new int[0];
        int[] positions = new int[effectCount];
        int currentX = baseX;
        // RHS: step +OVERLAP (rightward, away from bar)
        // LHS: step -OVERLAP (leftward, away from bar)
        int step = rightSide ? EFFECT_ICON_OVERLAP : -EFFECT_ICON_OVERLAP;
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
