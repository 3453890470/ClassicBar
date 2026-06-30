package tfar.classicbar.client;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD: DebugBuffScheduler shouldApplyDebugEffects interval semantics.
 * <p>
 * RED phase: This test references DebugBuffScheduler which does not exist yet.
 * Expected failure: compilation error "cannot find symbol DebugBuffScheduler".
 * <p>
 * GREEN phase: After implementing DebugBuffScheduler, this test verifies:
 * <ul>
 *   <li>Interval tick (1200 = 20 TPS × 60s) → true</li>
 *   <li>Non-interval tick → false</li>
 *   <li>Client side → false</li>
 *   <li>Production → false</li>
 *   <li>Client side + Production → false (short-circuit)</li>
 * </ul>
 */
class DebugBuffSchedulerBehaviorTest {

    // === Interval correctness (20 TPS × 60s = 1200 ticks) ===

    @Test
    void intervalTickEvery1200Ticks() {
        // tickCount = 1200 → 60 seconds at 20 TPS → should apply
        assertTrue(DebugBuffScheduler.shouldApplyDebugEffects(1200, false, false));
    }

    @Test
    void intervalTickAt2400Ticks() {
        // tickCount = 2400 → 2 minutes at 20 TPS → should apply
        assertTrue(DebugBuffScheduler.shouldApplyDebugEffects(2400, false, false));
    }

    @Test
    void intervalTickAt3600Ticks() {
        // tickCount = 3600 → 3 minutes at 20 TPS → should apply
        assertTrue(DebugBuffScheduler.shouldApplyDebugEffects(3600, false, false));
    }

    @Test
    void nonIntervalTickAtTick1() {
        // tickCount = 1 → first server tick after increment, NOT 1200 boundary
        assertFalse(DebugBuffScheduler.shouldApplyDebugEffects(1, false, false));
    }

    @Test
    void nonIntervalTickAtTick1199() {
        // tickCount = 1199 → one tick before the 60-second mark
        assertFalse(DebugBuffScheduler.shouldApplyDebugEffects(1199, false, false));
    }

    @Test
    void nonIntervalTickAtTick1201() {
        // tickCount = 1201 → one tick after the 60-second mark
        assertFalse(DebugBuffScheduler.shouldApplyDebugEffects(1201, false, false));
    }

    // === Side guard (isClientSide) ===

    @Test
    void clientSideSuppressesEffect() {
        // Even at a 1200-boundary tick, client-side must suppress
        assertFalse(DebugBuffScheduler.shouldApplyDebugEffects(1200, true, false));
    }

    @Test
    void clientSideSuppressesAtAnyTick() {
        assertFalse(DebugBuffScheduler.shouldApplyDebugEffects(1, true, false));
        assertFalse(DebugBuffScheduler.shouldApplyDebugEffects(600, true, false));
        assertFalse(DebugBuffScheduler.shouldApplyDebugEffects(2400, true, false));
    }

    // === Production guard ===

    @Test
    void productionSuppressesEffect() {
        // Even at a 1200-boundary tick, production must suppress
        assertFalse(DebugBuffScheduler.shouldApplyDebugEffects(1200, false, true));
    }

    @Test
    void productionSuppressesAtAnyTick() {
        assertFalse(DebugBuffScheduler.shouldApplyDebugEffects(1, false, true));
        assertFalse(DebugBuffScheduler.shouldApplyDebugEffects(600, false, true));
        assertFalse(DebugBuffScheduler.shouldApplyDebugEffects(2400, false, true));
    }

    // === Combined guards ===

    @Test
    void clientSideAndProductionBothSuppress() {
        assertFalse(DebugBuffScheduler.shouldApplyDebugEffects(1200, true, true));
        assertFalse(DebugBuffScheduler.shouldApplyDebugEffects(1, true, true));
        assertFalse(DebugBuffScheduler.shouldApplyDebugEffects(2400, true, true));
    }

    // === Edge cases ===

    @Test
    void tickCountZeroMatchesOriginalAlgorithm() {
        // 0 % 1200 == 0 in Java (matches original algorithm).
        // In practice, tickCount is NEVER 0 when PlayerTickEvent.Post fires
        // because ServerLevel.tickNonPassenger() increments tickCount
        // before entity.tick() runs, so this path is unreachable at runtime.
        assertTrue(DebugBuffScheduler.shouldApplyDebugEffects(0, false, false));
    }

    // === Interval parameter overload ===

    @Test
    void fourParamDelegationMatchesThreeParam() {
        // The 4-param overload with default interval should match 3-param
        assertEquals(
            DebugBuffScheduler.shouldApplyDebugEffects(1200, false, false),
            DebugBuffScheduler.shouldApplyDebugEffects(1200, false, false, DebugBuffScheduler.DEBUG_INTERVAL_TICKS));
        assertEquals(
            DebugBuffScheduler.shouldApplyDebugEffects(1, false, false),
            DebugBuffScheduler.shouldApplyDebugEffects(1, false, false, DebugBuffScheduler.DEBUG_INTERVAL_TICKS));
        assertEquals(
            DebugBuffScheduler.shouldApplyDebugEffects(600, true, false),
            DebugBuffScheduler.shouldApplyDebugEffects(600, true, false, DebugBuffScheduler.DEBUG_INTERVAL_TICKS));
        assertEquals(
            DebugBuffScheduler.shouldApplyDebugEffects(1200, false, true),
            DebugBuffScheduler.shouldApplyDebugEffects(1200, false, true, DebugBuffScheduler.DEBUG_INTERVAL_TICKS));
    }

    @Test
    void customInterval50Ticks() {
        assertTrue(DebugBuffScheduler.shouldApplyDebugEffects(0, false, false, 50));
        assertTrue(DebugBuffScheduler.shouldApplyDebugEffects(50, false, false, 50));
        assertFalse(DebugBuffScheduler.shouldApplyDebugEffects(25, false, false, 50));
        assertTrue(DebugBuffScheduler.shouldApplyDebugEffects(100, false, false, 50));
        assertFalse(DebugBuffScheduler.shouldApplyDebugEffects(101, false, false, 50));
    }

    @Test
    void shortInterval20Ticks() {
        // 20 ticks = minimum valid interval (1 second at 20 TPS)
        assertTrue(DebugBuffScheduler.shouldApplyDebugEffects(20, false, false, 20));
        assertTrue(DebugBuffScheduler.shouldApplyDebugEffects(40, false, false, 20));
        assertFalse(DebugBuffScheduler.shouldApplyDebugEffects(10, false, false, 20));
    }

    @Test
    void maximumInterval72000Ticks() {
        // 72000 ticks = 1 hour at 20 TPS
        assertTrue(DebugBuffScheduler.shouldApplyDebugEffects(72000, false, false, 72000));
        assertTrue(DebugBuffScheduler.shouldApplyDebugEffects(144000, false, false, 72000));
        assertFalse(DebugBuffScheduler.shouldApplyDebugEffects(1, false, false, 72000));
    }

    // === Defensive guards for intervalTicks ===

    @Test
    void negativeIntervalReturnsFalse() {
        assertFalse(DebugBuffScheduler.shouldApplyDebugEffects(1200, false, false, -1));
        assertFalse(DebugBuffScheduler.shouldApplyDebugEffects(1200, false, false, -100));
    }

    @Test
    void zeroIntervalReturnsFalse() {
        // tickCount % 0 would throw ArithmeticException; must be guarded
        assertFalse(DebugBuffScheduler.shouldApplyDebugEffects(1200, false, false, 0));
    }
}
