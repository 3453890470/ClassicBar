package tfar.classicbar.impl.overlays.vanilla;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import tfar.classicbar.client.HudRenderContext;
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.util.Color;
import tfar.classicbar.util.ModUtils;
import net.minecraft.resources.ResourceLocation;
import tfar.classicbar.resources.BarIcons;

/**
 * ClassicBar overlay for {@link net.minecraft.world.entity.player.Player} air supply.
 * <p>
 * Renders the player's remaining air (underwater breathing) as a classic-style
 * progress bar. Color is user-configurable via {@link ConfigCache#air}.
 * Displays air values in seconds (tick / 20).
 */
public class Air extends BarOverlayImpl {

    public Air() {
        super("air");
    }

    @Override
    public boolean shouldRender(Player player) {
        return player.getAirSupply() < player.getMaxAirSupply();
    }

    @Override
    public void renderBar(HudRenderContext context, GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset) {
        int xStart = screenWidth / 2 + getHOffset();
        int yStart = screenHeight - vOffset;
        double barWidth = getBarWidth(player);
        Color.reset();
        // Bar background
        renderFullBarBackground(graphics, xStart, yStart);
        // Draw portion of bar based on air amount
        double f = getBarStartX(xStart, barWidth);
        Color color = getPrimaryBarColor(0, player);
        applyConfiguredBarColor(color);
        renderPartialBar(graphics, f + 2, yStart + 2, barWidth);
    }

    @Override
    public double getBarWidth(Player player) {
        int air = player.getAirSupply();
        int maxAir = player.getMaxAirSupply();
        return Math.ceil((double) BarOverlayImpl.WIDTH * air / maxAir);
    }

    @Override
    public Color getPrimaryBarColor(int index, Player player) {
        return ConfigCache.air;
    }

    @Override
    public void renderText(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
        double air = player.getAirSupply();
        double maxAir = player.getMaxAirSupply();
        int xStart = width / 2 + getIconOffset();
        int yStart = height - vOffset;
        // Display in seconds (tick / 20)
        textHelper(graphics, xStart, yStart, air / 20, maxAir / 20,
                   getConfiguredTextColor(getPrimaryBarColor(0, player)), barSettings.textFormat);
    }

    @Override
    public void renderIcon(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
        int xStart = width / 2 + getIconOffset();
        int yStart = height - vOffset;
        boolean flashing = player.getAirSupply() < player.getMaxAirSupply();
        int guiTicks = ModUtils.getGuiTicks();
        ModUtils.drawIconWithFlash(graphics, xStart, yStart, 9, BarIcons.AIR, BarIcons.AIR_BLINKING, flashing, guiTicks);
    }
}
