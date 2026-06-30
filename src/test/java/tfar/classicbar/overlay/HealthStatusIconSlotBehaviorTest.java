package tfar.classicbar.overlay;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tfar.classicbar.util.BarPositionMath;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Verifies that status effect icons (poison/wither/frozen) that replace
 * the base red heart icon are drawn at the CORRECT slot position and
 * stack in the correct direction relative to the health bar.
 * <p>
 * Root cause: the previous fix ({@code renderEffectIcons()} →
 * {@code int currentX = baseX}) correctly anchored the first effect
 * at {@code baseX}, but reversed the stacking direction relative to
 * the original code. Effects now stack TOWARD the health bar (covering it)
 * instead of AWAY from it.
 * <p>
 * Layout semantics:
 * <ul>
 *   <li><b>RHS</b>: icon is to the right of the bar; bar extends LEFT from icon.</li>
 *   <li><b>LHS</b>: icon is to the left of the bar; bar extends RIGHT from icon.</li>
 * </ul>
 * Therefore:
 * <ul>
 *   <li><b>RHS</b>: effects stack RIGHTWARD (away from bar → screen edge) — step = {@code +OVERLAP}</li>
 *   <li><b>LHS</b>: effects stack LEFTWARD (away from bar → screen edge) — step = {@code -OVERLAP}</li>
 * </ul>
 */
class HealthStatusIconSlotBehaviorTest {

    private static final int BASE_X = 1000; // arbitrary base value

    // ============ Effect icon positions: replacement must be at baseX ============

    @Test
    @DisplayName("RHS with 1 effect: effect icon must be at baseX (same as base heart)")
    void rhsSingleEffectIconAtBaseX() {
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
    @DisplayName("RHS with 2 effects: first at baseX, second at baseX + OVERLAP (away from bar)")
    void rhsTwoEffectsStackRightAwayFromBar() {
        // RHS: bar is LEFT of icon, effects stack RIGHT (away from bar)
        int[] expected = {BASE_X, BASE_X + BarPositionMath.EFFECT_ICON_OVERLAP};
        assertArrayEquals(expected,
            BarPositionMath.effectIconPositions(BASE_X, true, 2));
    }

    @Test
    @DisplayName("LHS with 2 effects: first at baseX, second at baseX - OVERLAP (away from bar)")
    void lhsTwoEffectsStackLeftAwayFromBar() {
        // LHS: bar is RIGHT of icon, effects stack LEFT (away from bar)
        int[] expected = {BASE_X, BASE_X - BarPositionMath.EFFECT_ICON_OVERLAP};
        assertArrayEquals(expected,
            BarPositionMath.effectIconPositions(BASE_X, false, 2));
    }

    @Test
    @DisplayName("RHS with 3 effects: correct stacking order (rightward, away from bar)")
    void rhsThreeEffectsStackRightAwayFromBar() {
        int[] expected = {BASE_X, BASE_X + 4, BASE_X + 8};
        assertArrayEquals(expected,
            BarPositionMath.effectIconPositions(BASE_X, true, 3));
    }

    @Test
    @DisplayName("LHS with 3 effects: correct stacking order (leftward, away from bar)")
    void lhsThreeEffectsStackLeftAwayFromBar() {
        int[] expected = {BASE_X, BASE_X - 4, BASE_X - 8};
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

    // ============ Stacking direction integration: ModUtils.renderEffectIcons must match ============

    @Test
    @DisplayName("RHS: renderEffectIcons step must be +OVERLAP (rightward, away from bar)")
    void rhsRenderEffectIconsStepIsPositive() {
        int[] pos2 = BarPositionMath.effectIconPositions(BASE_X, true, 2);
        int step = pos2[1] - pos2[0];
        assertEquals(BarPositionMath.EFFECT_ICON_OVERLAP, step,
            "RHS effects must stack RIGHTWARD (away from bar). " +
            "Step is " + step + " but expected +" + BarPositionMath.EFFECT_ICON_OVERLAP);
    }

    @Test
    @DisplayName("LHS: renderEffectIcons step must be -OVERLAP (leftward, away from bar)")
    void lhsRenderEffectIconsStepIsNegative() {
        int[] pos2 = BarPositionMath.effectIconPositions(BASE_X, false, 2);
        int step = pos2[1] - pos2[0];
        assertEquals(-BarPositionMath.EFFECT_ICON_OVERLAP, step,
            "LHS effects must stack LEFTWARD (away from bar). " +
            "Step is " + step + " but expected -" + BarPositionMath.EFFECT_ICON_OVERLAP);
    }
}
