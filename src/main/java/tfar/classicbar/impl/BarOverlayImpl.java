package tfar.classicbar.impl;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import tfar.classicbar.ClassicBar;
import tfar.classicbar.api.BarMode;
import tfar.classicbar.api.BarOverlay;
import tfar.classicbar.api.BarSettings;
import tfar.classicbar.api.TextFormat;
import tfar.classicbar.client.HudRenderContext;
import tfar.classicbar.resources.BarIcons;
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.util.Color;
import tfar.classicbar.util.HealthEffect;
import tfar.classicbar.util.ModUtils;

public abstract class BarOverlayImpl implements BarOverlay {

    //maximum width the bar can be
    public static final int WIDTH = 77;
    public static final int HEIGHT = 5;
    public static final int BAR_U = 2;
    public static final int BAR_V = 11;
    public static final ResourceLocation ICON_BAR = ResourceLocation.fromNamespaceAndPath(ClassicBar.MODID, "textures/gui/health.png");

    public static final ResourceLocation GUI_ICONS_LOCATION = BarIcons.FALLBACK;
    protected String name;
    protected boolean side;
    protected BarSettings barSettings;

    public BarOverlayImpl(String name) {
        this.name = name;
    }

    @Override
    public boolean shouldRender(Player player) {
        return true;
    }

    @Override
    public void setBarSettings(BarSettings barSettings) {
        this.barSettings = barSettings;
    }

    @Override
    public final boolean rightHandSide() {
        return side;
    }

    @Override
    public final BarOverlay setSide(boolean right) {
        side = right;
        return this;
    }

    @Override
    public void render(HudRenderContext context, GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset) {
        if (barSettings == null || !barSettings.rendersClassicBar()) {
            return;
        }

        if (shouldRender(player)) {
            context.setupOverlayRenderState();
            try {
                bindBarTexture();
                renderBar(context, graphics, player, screenWidth, screenHeight, vOffset);
                if (shouldRenderText()) {
                    Color.reset();
                    renderText(graphics, player, screenWidth, screenHeight, vOffset);
                }
                if (ConfigCache.icons) {
                    Color.reset();
                    bindIconTexture();
                    renderIcon(graphics, player, screenWidth, screenHeight, vOffset);
                }
            } finally {
                Color.reset();
            }
            context.increment(rightHandSide(), 10);
        }
    }

    public abstract void renderBar(HudRenderContext context, GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset);

    protected boolean shouldFlash(Player player) {
        return false;
    }

    public final boolean shouldRenderText() {
        return barSettings.show_text;
    }

    public abstract void renderText(GuiGraphics graphics, Player player, int width, int height, int vOffset);

    public abstract void renderIcon(GuiGraphics graphics, Player player, int width, int height, int vOffset);

    protected final void applyConfiguredBarColor(Color baseColor) {
        barSettings.color_overlay.color2Gl(baseColor);
    }

    protected final void applyConfiguredBarColor(Color baseColor, float alpha) {
        barSettings.color_overlay.color2Gla(baseColor, alpha);
    }

    protected final int getConfiguredTextColor(Color baseColor) {
        return barSettings.color_overlay.applyTo(baseColor).colorToText();
    }

    public int getHOffset() {
        return rightHandSide() ? 10 : -91;
    }

    public int getIconOffset() {
        return rightHandSide() ? 92 : -101;
    }

    protected HealthEffect getHealthEffect(Player player) {
        HealthEffect effects = HealthEffect.NONE;//16
        if (player.hasEffect(MobEffects.POISON)) effects = HealthEffect.POISON;//evaluates to 52
        else if (player.hasEffect(MobEffects.WITHER)) effects = HealthEffect.WITHER;//evaluates to 88
        else if (player.isFullyFrozen()) effects = HealthEffect.FROZEN;
        return effects;
    }

    public void renderBarBackground(GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset) {
        double barWidth = getBarWidth(player);
        int xStart = screenWidth / 2 + getHOffset();
        if (isFitted() && rightHandSide()) {
            xStart += WIDTH - barWidth;
        }
        int yStart = screenHeight - vOffset;

        if (isFitted()) {
            drawScaledBarBackground(graphics, barWidth, xStart, yStart + 1);
        } else renderFullBarBackground(graphics, xStart, yStart);
    }
    public void drawScaledBarBackground(GuiGraphics stack, double barWidth, int x, int y) {
        if (rightHandSide()) {
            ModUtils.drawTexturedModalRect(stack,x, y - 1, 0, 0, barWidth + 2, 9);
            ModUtils.drawTexturedModalRect(stack,x + barWidth + 2, y-1, WIDTH + 2, 0, 2, 9);
        } else {
            ModUtils.drawTexturedModalRect(stack,x, y - 1, 0, 0, (int) (barWidth + 2), 9);
            ModUtils.drawTexturedModalRect(stack, (int) (x + barWidth + 2), y - 1, WIDTH + 2, 0, 2, 9);
        }
    }
    public void textHelper(GuiGraphics graphics, int xStart, int yStart, double stat, double maxStat, int color, TextFormat format) {
        // prevent division by zero
        if (maxStat <= 0) {
            maxStat = 1;
        }
        String text;
        switch (format) {
            case CURRENT_MAX:
                text = (int) Math.floor(stat) + " / " + (int) Math.floor(maxStat);
                break;
            case PERCENT_MAX:
                int percent = (int) Math.round(stat / maxStat * 100);
                text = percent + "% / " + (int) Math.floor(maxStat);
                break;
            default: // CURRENT_ONLY
                text = (int) Math.floor(stat) + "";
                break;
        }
        int i2 = ConfigCache.icons ? 1 : 0;
        if (rightHandSide()) {
            ModUtils.drawStringOnHUD(graphics, text, xStart + 9 * i2, yStart - 2, color);
        } else {
            int i3 = ModUtils.getStringLength(text);
            ModUtils.drawStringOnHUD(graphics, text, xStart - 9 * i2 - i3 + 5, yStart - 2, color);
        }
    }
    protected int computeWrapIndex(double stat, double maxStat, int colorListSize) {
        return Math.min((int) Math.ceil(stat / maxStat), colorListSize) - 1;
    }

    protected void renderSimpleText(GuiGraphics graphics, int width, int height, int vOffset,
                                    double stat, double maxStat, Player player) {
        int xStart = width / 2 + getIconOffset();
        int yStart = height - vOffset;
        textHelper(graphics, xStart, yStart, stat, maxStat,
                   getConfiguredTextColor(getPrimaryBarColor(0, player)), barSettings.textFormat);
    }

    protected double getBarStartX(int xStart, double barWidth) {
        return xStart + (rightHandSide() ? WIDTH - barWidth : 0);
    }

    public void renderFullBarBackground(GuiGraphics graphics, int xStart, int yStart) {
        ModUtils.drawTexturedModalRect(graphics, xStart, yStart, 0, 0, WIDTH + 4, 9);
    }
    public void renderFullBar(GuiGraphics graphics, int xStart, int yStart) {
        renderPartialBar(graphics,xStart,yStart,WIDTH);
    }
    public void renderPartialBar(GuiGraphics graphics, double xStart, int yStart,double barWidth) {
        ModUtils.drawTexturedModalRect(graphics, xStart, yStart, BAR_U, BAR_V, barWidth, HEIGHT);
    }

    protected void renderPreviewBar(GuiGraphics graphics, double previewValue, double maxValue,
                                    double currentBarWidth, Color color, float breath,
                                    int xStart, int yStart) {
        if (previewValue <= 0) return;
        double previewWidth = Math.ceil(WIDTH * previewValue / maxValue);
        double previewX = rightHandSide()
            ? xStart + WIDTH + 2 - previewWidth
            : xStart + currentBarWidth + 2;
        // clamp to container bounds
        double containerLeft = xStart + 2;
        double containerRight = xStart + WIDTH + 2;
        if (rightHandSide()) {
            if (previewX < containerLeft) {
                previewWidth = Math.max(0, previewWidth - (containerLeft - previewX));
                previewX = containerLeft;
            }
        } else {
            double previewEnd = previewX + previewWidth;
            if (previewEnd > containerRight) {
                previewWidth = Math.max(0, containerRight - previewX);
            }
        }
        if (previewWidth > 0) {
            applyConfiguredBarColor(color, breath);
            renderPartialBar(graphics, previewX, yStart + 2, previewWidth);
        }
    }

    protected void renderWhitePreviewOverlay(GuiGraphics graphics, double previewA, double previewB,
                                              double maxValue, double currentBarWidth,
                                              float breath, int xStart, int yStart) {
        double maxPreviewWidth = 0;
        if (previewA > 0) {
            maxPreviewWidth = Math.max(maxPreviewWidth, Math.ceil(WIDTH * previewA / maxValue));
        }
        if (previewB > 0) {
            maxPreviewWidth = Math.max(maxPreviewWidth, Math.ceil(WIDTH * previewB / maxValue));
        }
        if (maxPreviewWidth <= 0) return;
        double overlayX = rightHandSide()
            ? xStart + WIDTH + 2 - maxPreviewWidth
            : xStart + currentBarWidth + 2;
        double containerLeft = xStart + 2;
        if (overlayX < containerLeft) {
            maxPreviewWidth = Math.max(0, maxPreviewWidth - (containerLeft - overlayX));
            overlayX = containerLeft;
        }
        if (maxPreviewWidth > 0) {
            Color.WHITE.color2Gla(breath);
            ModUtils.drawTexturedModalRect(graphics, overlayX, yStart, 0, 35, maxPreviewWidth, 9);
            Color.reset();
        }
    }

    @Override
    public Color getPrimaryBarColor(int index, Player player) {
        return Color.BLACK;
    }
    @Override
    public Color getSecondaryBarColor(int index, Player player) {
        return Color.BLACK;
    }
    @Override
    public final ResourceLocation getIconRL() {
        return barSettings.icon;
    }
    @Override
    public boolean isFitted() {
        return false;
    }
    @Override
    public final String name() {
        return name;
    }
}
