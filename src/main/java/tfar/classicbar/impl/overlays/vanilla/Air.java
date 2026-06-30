package tfar.classicbar.impl.overlays.vanilla;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Player;
import tfar.classicbar.client.HudRenderContext;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.util.Color;
import tfar.classicbar.util.ModUtils;
import tfar.classicbar.resources.BarIcons;

public class Air extends BarOverlayImpl {

    public Air() {
        super("air");
    }

    @Override
    public boolean shouldRender(Player player) {
        return player.getAirSupply() < player.getMaxAirSupply();
    }

    @Override
    public void renderBar(HudRenderContext context, GuiGraphicsExtractor graphics, Player player, int screenWidth, int screenHeight, int vOffset) {
        double air = player.getAirSupply();
        double maxAir = player.getMaxAirSupply();
        double barWidth = getBarWidth(player);

        int xStart = screenWidth / 2 + getHOffset();
        int yStart = screenHeight - vOffset;

        renderBarBackground(graphics,player,screenWidth,screenHeight,vOffset);
        Color color = getPrimaryBarColor(0, player);
        applyConfiguredBarColor(color);
        renderPartialBar(graphics,xStart + 2,yStart + 2,barWidth);
    }

    @Override
    public double getBarWidth(Player player) {
        double air = player.getAirSupply();
        double maxAir = player.getMaxAirSupply();
        return Math.ceil(WIDTH * (air / maxAir));
    }

    @Override
    public Color getPrimaryBarColor(int index, Player player) {
        return ConfigCache.air;
    }

    @Override
    public Color getSecondaryBarColor(int index, Player player) {
        return ConfigCache.air;
    }

    @Override
    public boolean isFitted() {
        return false;
    }

    @Override
    public void renderText(GuiGraphicsExtractor graphics, Player player, int width, int height, int vOffset) {
        renderSimpleText(graphics, width, height, vOffset, player.getAirSupply(), player.getMaxAirSupply(), player);
    }

    @Override
    public void renderIcon(GuiGraphicsExtractor graphics, Player player, int width, int height, int vOffset) {
        int xStart = width / 2 + getIconOffset();
        int yStart = height - vOffset;
        ModUtils.drawIconWithTexture(graphics, xStart, yStart, 9, BarIcons.AIR);
    }
}
