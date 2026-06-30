package tfar.classicbar.impl.overlays.mod;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;
import tfar.classicbar.client.HudRenderContext;
import tfar.classicbar.impl.BarOverlayImpl;

/**
 * ClassicBar overlay for Vampirism blood level.
 * <p>
 * Renders the player's vampire blood level as a classic-style progress bar.
 * Only renders when Vampirism is loaded and the player is a vampire.
 *
 * NOTE: Vampirism is not available for MC 26.1.2 in this build.
 * This overlay always returns false from shouldRender() and all render
 * methods are no-ops. The class structure is preserved for future restoration.
 */
public class Blood extends BarOverlayImpl {

    public Blood() {
        super("blood");
    }

    /**
     * runtime check: is player a vampire (blood level > 0)
     * Currently always returns false — Vampirism not available for MC 26.1.2.
     */
    public static boolean isVampireBloodActive(Player player) {
        if (!ModList.get().isLoaded("vampirism")) return false;
        // Vampirism API not available in this build — requires MC 26.1.2 compatible version
        return false;
    }

    @Override
    public boolean shouldRender(Player player) {
        // Vampirism not available for MC 26.1.2 — always disabled
        return false;
    }

    @Override
    public void renderBar(HudRenderContext context, GuiGraphicsExtractor graphics, Player player, int screenWidth, int screenHeight, int vOffset) {
        // Vampirism compat not available in this build
    }

    @Override
    public void renderText(GuiGraphicsExtractor graphics, Player player, int width, int height, int vOffset) {
        // Vampirism compat not available in this build
    }

    @Override
    public double getBarWidth(Player player) {
        return 0;
    }

    @Override
    public void renderIcon(GuiGraphicsExtractor graphics, Player player, int width, int height, int vOffset) {
        // Vampirism compat not available in this build
    }
}
