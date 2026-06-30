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

public class ArmorToughness extends BarOverlayImpl {

    public ArmorToughness() {
        super("armor_toughness");
    }

    @Override
    public boolean shouldRender(Player player) {
        return player.getArmorValue() > 0 && ClassicBarsConfig.displayToughnessBar.get();
    }

    @Override
    public void renderBar(HudRenderContext context, GuiGraphicsExtractor graphics, Player player, int screenWidth, int screenHeight, int vOffset) {
        int armorToughness = getArmorToughness(player);
        double barWidth = getBarWidth(player);

        int xStart = screenWidth / 2 + getHOffset();
        int yStart = screenHeight - vOffset;
        int index = computeWrapIndex(armorToughness, 20, ConfigCache.armor_toughness.size());

        renderBarBackground(graphics,player,screenWidth,screenHeight,vOffset);

        Color primary = getPrimaryBarColor(index, player);

        if (index == 0) {
            applyConfiguredBarColor(primary);
            renderPartialBar(graphics,xStart + 2,yStart + 2,barWidth);
        }
        else {
            renderFullBar(graphics,xStart + 2,yStart + 2);
            if (armorToughness % 20 != 0) {
                Color secondary = getSecondaryBarColor(index - 1, player);
                applyConfiguredBarColor(secondary);
                double w = ModUtils.getWidth(armorToughness % 20, 20);
                double f = xStart + (rightHandSide() ? WIDTH - w : 0);
                renderPartialBar(graphics,f + 2,yStart + 2,w);
            }
        }
    }

    @Override
    public double getBarWidth(Player player) {
        int armorToughness = getArmorToughness(player);
        return WIDTH * Math.min(20, armorToughness) / 20d;
    }

    @Override
    public Color getPrimaryBarColor(int index, Player player) {
        return ConfigCache.armor_toughness.get(index);
    }

    @Override
    public Color getSecondaryBarColor(int index, Player player) {
        return ConfigCache.armor_toughness.get(index);
    }

    @Override
    public boolean isFitted() {
        return !ClassicBarsConfig.fullToughnessBar.get();
    }

    @Override
    public void renderText(GuiGraphicsExtractor graphics, Player player, int width, int height, int vOffset) {
        renderSimpleText(graphics, width, height, vOffset, getArmorToughness(player), 20, player);
    }

    @Override
    public void renderIcon(GuiGraphicsExtractor graphics, Player player, int width, int height, int vOffset) {
        int xStart = width / 2 + getIconOffset();
        int yStart = height - vOffset;
        ModUtils.drawIconWithTexture(graphics, xStart, yStart, 9, BarIcons.ARMOR_TOUGHNESS);
    }

    private static int getArmorToughness(Player player) {
        return (int) player.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.ARMOR_TOUGHNESS);
    }
}
