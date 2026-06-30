package tfar.classicbar.impl.overlays.vanilla;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Player;
import tfar.classicbar.client.HudRenderContext;
import tfar.classicbar.config.ClassicBarsConfig;
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.util.Color;
import tfar.classicbar.util.ModUtils;
import tfar.classicbar.resources.BarIcons;

public class Absorption extends BarOverlayImpl {

    public Absorption() {
        super("absorption");
    }

    @Override
    public boolean shouldRender(Player player) {
      return player.getAbsorptionAmount() > 0;
    }

    @Override
    public void renderBar(HudRenderContext context, GuiGraphicsExtractor graphics, Player player, int screenWidth, int screenHeight, int vOffset) {
        double absorb = player.getAbsorptionAmount();
        double barWidth = getBarWidth(player);

        int xStart = screenWidth / 2 + getHOffset();
        int yStart = screenHeight - vOffset;

        if (rightHandSide()) {
            xStart += BarOverlayImpl.WIDTH - barWidth;
        }

        int index = computeWrapIndex(absorb, 20, ConfigCache.absorption.size());
        Color primary = getPrimaryBarColor(index, player);

        renderBarBackground(graphics, player, screenWidth, screenHeight, vOffset);

        if (index == 0) {
            applyConfiguredBarColor(primary);
            renderPartialBar(graphics, xStart + 2, yStart + 2, barWidth);
        } else {
            Color secondary = getSecondaryBarColor(index - 1, player);
            applyConfiguredBarColor(secondary);
            renderFullBar(graphics, xStart + 2, yStart + 2);
            if (absorb % 20 != 0 && index < ConfigCache.absorption.size() - 1) {
                applyConfiguredBarColor(primary);
                renderPartialBar(graphics, xStart + 2, yStart + 2, ModUtils.getWidth(absorb % 20, 20));
            }
        }
    }

    @Override
    public double getBarWidth(Player player) {
        return Math.ceil(WIDTH * Math.min(20, player.getAbsorptionAmount()) / 20d);
    }

    @Override
    public Color getPrimaryBarColor(int index, Player player) {
        return ConfigCache.absorption.get(index);
    }

    @Override
    public boolean isFitted() {
        return !ClassicBarsConfig.fullAbsorptionBar.get();
    }

    @Override
    public void renderText(GuiGraphicsExtractor graphics, Player player, int width, int height, int vOffset) {
        renderSimpleText(graphics, width, height, vOffset, player.getAbsorptionAmount(), 20, player);
    }

    @Override
    public void renderIcon(GuiGraphicsExtractor graphics, Player player, int width, int height, int vOffset) {
        int xStart = width / 2 + getIconOffset();
        int yStart = height - vOffset;
        ModUtils.drawIconWithTexture(graphics, xStart, yStart, 9, BarIcons.ABSORPTION);
    }
}
