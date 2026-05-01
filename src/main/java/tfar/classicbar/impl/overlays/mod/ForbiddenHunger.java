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
 * 独立禁忌诅咒状态栏 (EnigmaticLegacy+ 兼容)。
 * <p>
 * 当玩家处于禁忌诅咒状态时，以独立 overlay 渲染满格饥饿条。
 * 与 food overlay 互斥——food 活跃时隐藏，由本 overlay 接管。
 * 架构与 {@link Blood} 一致，继承 {@link BarOverlayImpl}。
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
            // 血族优先：如果玩家是吸血鬼，由 blood 处理，禁忌栏不渲染
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

        // 饥饿值锁定为 20，渲染满条
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
