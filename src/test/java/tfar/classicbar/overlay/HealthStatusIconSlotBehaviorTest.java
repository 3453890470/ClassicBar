package tfar.classicbar.overlay;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tfar.classicbar.util.BarPositionMath;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Verifies that status effect icons (poison/wither/frozen) that replace
 * the base red heart icon are drawn at the CORRECT slot position.
 * <p>
 * Root cause (2026-06, third pass): {@code ModUtils.renderEffectIcons()}
 * offsets the first effect icon by {@code effectCount * OVERLAP} from
 * {@code baseX} (e.g., baseX+4 for 1 effect on RHS), instead of drawing
 * it at {@code baseX} where the base heart icon would be. This causes the
 * replacement icon to appear at an offset position (described by user as
 * "the second heart position").
 * <p>
 * The previous two passes incorrectly focused on
 * {@code Absorption.renderBar()} bar coordinates — that fix addressed bar
 * xStart alignment, not icon slot positioning. This test directly verifies
 * the icon coordinate calculation.
 * <p>
 * The fix changes {@code renderEffectIcons()} to start the first effect
 * at {@code baseX}, with additional effects stacked inward.
 */
class HealthStatusIconSlotBehaviorTest {

    private static final int BASE_X = 1000; // arbitrary base value

    // ============ Effect icon positions: replacement must be at baseX ============

    @Test
    @DisplayName("RHS with 1 effect: effect icon must be at baseX (same as base heart)")
    void rhsSingleEffectIconAtBaseX() {
        // Current (buggy) behavior: effect at baseX + 4
        // Expected (fixed):       effect at baseX
        int[] expected = {BASE_X};
        assertArrayEquals(expected,
            BarPositionMath.effectIconPositions(BASE_X, true, 1));
    }

    @Test
    @DisplayName("LHS with 1 effect: effect icon must be at baseX (same as base heart)")
    void lhsSingleEffectIconAtBaseX() {
        int[] expected = {BASE_X};
        assertArrayEquals(expected,
            BarPositionMath.effectIconPositions(BASE_X, false, 1));
    }

    @Test
    @DisplayName("RHS with 2 effects: first at baseX, second at baseX - OVERLAP")
    void rhsTwoEffectsStackLeftFromBaseX() {
        int[] expected = {BASE_X, BASE_X - BarPositionMath.EFFECT_ICON_OVERLAP};
        assertArrayEquals(expected,
            BarPositionMath.effectIconPositions(BASE_X, true, 2));
    }

    @Test
    @DisplayName("LHS with 2 effects: first at baseX, second at baseX + OVERLAP")
    void lhsTwoEffectsStackRightFromBaseX() {
        int[] expected = {BASE_X, BASE_X + BarPositionMath.EFFECT_ICON_OVERLAP};
        assertArrayEquals(expected,
            BarPositionMath.effectIconPositions(BASE_X, false, 2));
    }

    @Test
    @DisplayName("RHS with 3 effects: correct stacking order")
    void rhsThreeEffectsStackLeft() {
        int[] expected = {BASE_X, BASE_X - 4, BASE_X - 8};
        assertArrayEquals(expected,
            BarPositionMath.effectIconPositions(BASE_X, true, 3));
    }

    @Test
    @DisplayName("LHS with 3 effects: correct stacking order")
    void lhsThreeEffectsStackRight() {
        int[] expected = {BASE_X, BASE_X + 4, BASE_X + 8};
        assertArrayEquals(expected,
            BarPositionMath.effectIconPositions(BASE_X, false, 3));
    }

    @Test
    @DisplayName("0 effects returns empty array")
    void zeroEffectsReturnsEmpty() {
        assertArrayEquals(new int[0],
            BarPositionMath.effectIconPositions(BASE_X, true, 0));
        assertArrayEquals(new int[0],
            BarPositionMath.effectIconPositions(BASE_X, false, 0));
    }

    // ============ Text position must anchor to baseX, not shift with effects ============

    @Test
    @DisplayName("Health text X must be baseX regardless of effect count (RHS)")
    void healthTextXIsBaseXForRhs() {
        assertEquals(BASE_X, BarPositionMath.healthTextX(BASE_X, true, 0));
        assertEquals(BASE_X, BarPositionMath.healthTextX(BASE_X, true, 1));
        assertEquals(BASE_X, BarPositionMath.healthTextX(BASE_X, true, 2));
        assertEquals(BASE_X, BarPositionMath.healthTextX(BASE_X, true, 3));
    }

    @Test
    @DisplayName("Health text X must be baseX regardless of effect count (LHS)")
    void healthTextXIsBaseXForLhs() {
        assertEquals(BASE_X, BarPositionMath.healthTextX(BASE_X, false, 0));
        assertEquals(BASE_X, BarPositionMath.healthTextX(BASE_X, false, 1));
        assertEquals(BASE_X, BarPositionMath.healthTextX(BASE_X, false, 2));
        assertEquals(BASE_X, BarPositionMath.healthTextX(BASE_X, false, 3));
    }
}
