package tfar.classicbar.impl.overlays.mod;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Player;
import tfar.classicbar.client.HudRenderContext;
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.util.Color;

/**
 * ClassicBar overlay for third-party mod thirst/hydration systems.
 * <p>
 * Supports <b>Tough As Nails</b> ({@code toughasnails}) and
 * <b>Thirst Was Taken</b> ({@code thirst}) mods.
 *
 * NOTE: Neither mod is available for MC 26.1.2 in this build.
 * This overlay will always return false from shouldRender().
 */
public class Thirst extends BarOverlayImpl {

    public Thirst() {
        super("thirst_level");
    }

    /** Consumption flash tracking */
    private double lastThirstLevel = 0;
    private long thirstUpdateCounter = 0;

    /** Thirst data internal record */
    private record ThirstData(int thirst, int hydration, float exhaustion) {}

    // ========================================================================
    //  API Routing — TAN priority (both unavailable in this build)
    // ========================================================================

    /**
     * Fetch thirst data.
     * Both TAN and TWT are unavailable for MC 26.1.2 — returns empty data.
     */
    private static ThirstData getThirstData(Player player) {
        return new ThirstData(0, 0, 0);
    }

    /**
     * Runtime check: is player in active thirst state.
     * Always returns false — no thirst mod available for MC 26.1.2.
     */
    public static boolean isThirstActive(Player player) {
        return false;
    }

    // ========================================================================
    //  BarOverlayImpl implementation
    // ========================================================================

    @Override
    public boolean shouldRender(Player player) {
        return false;
    }

    @Override
    public void renderBar(HudRenderContext context, GuiGraphicsExtractor graphics, Player player, int screenWidth, int screenHeight, int vOffset) {
        // Thirst compat not available in this build
    }

    @Override
    public void renderText(GuiGraphicsExtractor graphics, Player player, int width, int height, int vOffset) {
        // Thirst compat not available in this build
    }

    @Override
    public double getBarWidth(Player player) {
        return 0;
    }

    @Override
    public void renderIcon(GuiGraphicsExtractor graphics, Player player, int width, int height, int vOffset) {
        // Thirst compat not available in this build
    }

    // ========================================================================
    //  Color routing
    // ========================================================================

    @Override
    public Color getPrimaryBarColor(int index, Player player) {
        return ConfigCache.hydration;
    }

    @Override
    public Color getSecondaryBarColor(int index, Player player) {
        return ConfigCache.thirst;
    }
}
