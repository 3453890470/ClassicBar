package tfar.classicbar.impl.overlays.vanilla;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import tfar.classicbar.client.HudRenderContext;
import tfar.classicbar.config.ClassicBarsConfig;
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.util.Color;
import tfar.classicbar.util.HealthEffect;
import tfar.classicbar.util.ModUtils;

public class Absorption extends BarOverlayImpl {

    public Absorption() {
        super("absorption");
    }

    @Override
    public boolean shouldRender(Player player) {
        return player.getAbsorptionAmount() > 0;
    }

    @Override
    public void renderBar(HudRenderContext context, GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset) {

        double absorb = player.getAbsorptionAmount();
        double barWidth = getBarWidth(player);

        int xStart = screenWidth / 2 + getHOffset();
        int yStart = screenHeight - vOffset;
        double maxHealth = player.getMaxHealth();

        if (rightHandSide()) {
            xStart += BarOverlayImpl.WIDTH - barWidth;
        }

        //draw absorption bar
        int index = Math.min((int) Math.ceil(absorb / maxHealth), ConfigCache.absorption.size()) - 1;
        Color primary = getPrimaryBarColor(index, player);
        Color.reset();
        //draw background bar
        renderBarBackground(graphics, player, screenWidth, screenHeight, vOffset);
        if (index == 0) {//no wrapping
            //background
            applyConfiguredBarColor(primary);
            //bar
            renderPartialBar(graphics, xStart + 2, yStart + 2, barWidth);
        } else {
            //we have wrapped, draw 2 bars
            //draw first full bar
            Color secondary = getSecondaryBarColor(index - 1, player);
            applyConfiguredBarColor(secondary);
            renderFullBar(graphics, xStart + 2, yStart + 2);
            //is it on the edge or capped already?
            if (absorb % maxHealth != 0 && index < ConfigCache.absorption.size() - 1) {
                //draw second partial bar
                applyConfiguredBarColor(primary);
                renderPartialBar(graphics, xStart + 2, yStart + 2, ModUtils.getWidth(absorb % maxHealth, maxHealth));
            }
        }
    }

    public double getBarWidth(Player player) {
        double absorb = player.getAbsorptionAmount();
        double maxHealth = player.getMaxHealth();
        return (int) Math.ceil(BarOverlayImpl.WIDTH * Math.min(maxHealth, absorb) / maxHealth);
    }

    @Override
    public Color getPrimaryBarColor(int index, Player player) {
        HealthEffect effect = getHealthEffect(player);
        switch (effect) {
            case NONE -> {
                return ConfigCache.absorption.get(index);
            }
            case POISON -> {
                return ConfigCache.absorptionPoison.get(index);
            }
            case WITHER -> {
                return ConfigCache.absorptionWither.get(index);
            }
        }
        return super.getPrimaryBarColor(index, player);
    }

    @Override
    public boolean isFitted() {
        return !ClassicBarsConfig.fullAbsorptionBar.get();
    }

    @Override
    public void renderText(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
        double absorb = player.getAbsorptionAmount();
        int xStart = width / 2 + getIconOffset();
        int yStart = height - vOffset;
        textHelper(graphics, xStart, yStart, absorb, player.getMaxHealth(),
                   getConfiguredTextColor(getPrimaryBarColor(0, player)), barSettings.textFormat);
    }

    @Override
    public void renderIcon(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
        int xStart = width / 2 + getIconOffset();
        int yStart = height - vOffset;
        ModUtils.drawStandaloneIcon(graphics, xStart, yStart, 9);
    }
}
