package tfar.classicbar.impl.overlays.mod;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;
import tfar.classicbar.client.HudRenderContext;
import tfar.classicbar.impl.BarOverlayImpl;

/**
 * Dedicated Forbidden Curse overlay (EnigmaticLegacy+ compat).
 * <p>
 * NOTE: EnigmaticLegacy+ is not available for MC 26.1.2 in this build.
 * This overlay always returns false from shouldRender() and all render
 * methods are no-ops. The class structure is preserved for future restoration.
 */
public class ForbiddenHunger extends BarOverlayImpl {

    public ForbiddenHunger() {
        super("forbidden_hunger");
    }

    @Override
    public boolean shouldRender(Player player) {
        if (!ModList.get().isLoaded("enigmaticlegacyplus")) return false;
        // EnigmaticLegacy+ API not available in this build
        return false;
    }

    @Override
    public void renderBar(HudRenderContext context, GuiGraphicsExtractor graphics, Player player, int screenWidth, int screenHeight, int vOffset) {
        // EnigmaticLegacy+ compat not available in this build
    }

    @Override
    public void renderText(GuiGraphicsExtractor graphics, Player player, int width, int height, int vOffset) {
        // EnigmaticLegacy+ compat not available in this build
    }

    @Override
    public double getBarWidth(Player player) {
        return 0;
    }

    @Override
    public void renderIcon(GuiGraphicsExtractor graphics, Player player, int width, int height, int vOffset) {
        // EnigmaticLegacy+ compat not available in this build
    }
}
