package tfar.classicbar.client;

/**
 * Pure helper that encapsulates debug buff scheduling logic,
 * separated from {@link EventHandler} for direct testability.
 * <p>
 * All methods are stateless pure functions; no mocking required.
 */
public final class DebugBuffScheduler {

    /**
     * Default interval between periodic debug effect applications.
     * 20 TPS × 60 seconds = 1200 ticks.
     */
    public static final int DEBUG_INTERVAL_TICKS = 1200;

    /**
     * Minimum allowed interval (20 ticks = 1 second at 20 TPS).
     */
    public static final int DEBUG_INTERVAL_MINIMUM = 20;

    /**
     * Maximum allowed interval (72000 ticks = 1 hour at 20 TPS).
     */
    public static final int DEBUG_INTERVAL_MAXIMUM = 72000;

    /**
     * Duration applied to debug effects so they persist across the interval.
     * 1200 ticks × 2 = 2400 ticks (2 minutes) ensures the effect does not
     * expire before the next interval tick.
     */
    public static final int DEBUG_EFFECT_DURATION_TICKS = 2400;

    private DebugBuffScheduler() {
    }

    /**
     * Convenience overload that uses the default {@link #DEBUG_INTERVAL_TICKS}.
     *
     * @param tickCount    player.tickCount at the time of PlayerTickEvent.Post
     * @param isClientSide player.level().isClientSide()
     * @param isProduction FMLLoader.getCurrent().isProduction()
     * @return true if periodic effects should be applied this tick
     * @see #shouldApplyDebugEffects(int, boolean, boolean, int)
     */
    public static boolean shouldApplyDebugEffects(int tickCount, boolean isClientSide, boolean isProduction) {
        return shouldApplyDebugEffects(tickCount, isClientSide, isProduction, DEBUG_INTERVAL_TICKS);
    }

    /**
     * Determines whether periodic debug effects should be applied this tick.
     * <p>
     * Three guards:
     * <ol>
     *   <li>Skip client-side player entities (effects must apply on server)</li>
     *   <li>Skip production environment (dev-only feature)</li>
     *   <li>Only fire at interval-tick boundaries (default 1200 = 1 minute at 20 TPS)</li>
     * </ol>
     *
     * @param tickCount     player.tickCount at the time of PlayerTickEvent.Post
     * @param isClientSide  player.level().isClientSide()
     * @param isProduction  FMLLoader.getCurrent().isProduction()
     * @param intervalTicks interval in ticks between effect applications
     * @return true if periodic effects should be applied this tick
     */
    public static boolean shouldApplyDebugEffects(int tickCount, boolean isClientSide, boolean isProduction, int intervalTicks) {
        if (isClientSide) return false;
        if (isProduction) return false;
        if (intervalTicks <= 0) return false;
        return tickCount % intervalTicks == 0;
    }
}
