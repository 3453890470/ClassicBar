package tfar.classicbar.impl.overlays.mod;

import auviotre.enigmatic.legacy.contents.item.food.ForbiddenFruit;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;
import tfar.classicbar.client.HudRenderContext;
import tfar.classicbar.config.ClassicBarsConfig;
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.impl.overlays.mod.Blood;
import tfar.classicbar.resources.BarIcons;
import tfar.classicbar.util.Color;
import tfar.classicbar.util.ModUtils;

/**
 * Dedicated Forbidden Curse overlay (EnigmaticLegacy+ compat).
 * <p>
 * Renders a full hunger bar as an independent overlay when the player
 * is under the Forbidden Curse. Mutually exclusive with the food overlay
 * — hides when food is active, takes over rendering.
 * Architecture mirrors {@link Blood}, extends {@link BarOverlayImpl}.
 */
public class ForbiddenHunger extends BarOverlayImpl {

    public ForbiddenHunger() {
        super("forbidden_hunger");
    }

    @Override
    public boolean shouldRender(Player player) {
        if (!ModList.get().isLoaded("enigmaticlegacyplus")) return false;
        if (!ClassicBarsConfig.isReservedModSupportEnabled("enigmaticlegacyplus")) return false;
        try {
            // vampire priority: blood overlay handles, forbidden hunger skips
            if (Blood.isVampireBloodActive(player)) return false;
            return ForbiddenFruit.isForbiddenCursed(player);
        } catch (Throwable t) {
            return false;
        }
    }

    @Override
    public void renderBar(HudRenderContext context, GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset) {
        int xStart = screenWidth / 2 + getHOffset();
        int yStart = screenHeight - vOffset;

        Color.reset();
        renderFullBarBackground(graphics, xStart, yStart);

        // hunger locked at 20, render full bar
        double barWidth = BarOverlayImpl.WIDTH;
        double barX = xStart + (rightHandSide() ? BarOverlayImpl.WIDTH - barWidth : 0);
        applyConfiguredBarColor(ConfigCache.forbiddenCurseBarColor);
        renderPartialBar(graphics, barX + 2, yStart + 2, barWidth);
    }

    @Override
    public void renderText(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
        int baseX = width / 2 + getIconOffset();
        int yStart = height - vOffset;
        textHelper(graphics, baseX, yStart, 20, 20,
                getConfiguredTextColor(ConfigCache.forbiddenCurseBarColor), barSettings.textFormat);
    }

    @Override
    public double getBarWidth(Player player) {
        return BarOverlayImpl.WIDTH;
    }

    @Override
    public void renderIcon(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
        int baseX = width / 2 + getIconOffset();
        int yStart = height - vOffset;
        ModUtils.drawIconWithTexture(graphics, baseX, yStart, 9, BarIcons.FORBIDDEN_HUNGER);
    }
}
