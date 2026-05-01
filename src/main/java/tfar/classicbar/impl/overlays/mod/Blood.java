package tfar.classicbar.impl.overlays.mod;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;
import tfar.classicbar.client.HudRenderContext;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.resources.BarIcons;
import tfar.classicbar.util.Color;
import tfar.classicbar.util.ColorUtils;
import tfar.classicbar.util.ModUtils;

public class Blood extends BarOverlayImpl {

    public Blood() {
        super("blood");
    }

    /**
     * 运行时检测：玩家是否为吸血鬼（血族等级 > 0）。
     * 供 {@link ForbiddenHunger} 等互斥 overlay 调用。
     */
    public static boolean isVampireBloodActive(Player player) {
        if (!ModList.get().isLoaded("vampirism")) return false;
        try {
            IVampirePlayer vp = VampirismAPI.vampirePlayer(player);
            return vp.getLevel() > 0;
        } catch (Throwable t) {
            return false;
        }
    }

    @Override
    public boolean shouldRender(Player player) {
        return isVampireBloodActive(player);
    }

    @Override
    public void renderBar(HudRenderContext context, GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset) {
        int xStart = screenWidth / 2 + getHOffset();
        int yStart = screenHeight - vOffset;

        Color.reset();
        renderFullBarBackground(graphics, xStart, yStart);

        try {
            IVampirePlayer vp = VampirismAPI.vampirePlayer(player);
            int blood = vp.getBloodLevel();
            int maxBlood = vp.getBloodStats().getMaxBlood();
            if (blood > 0 && maxBlood > 0) {
                double barWidth = blood * (double) BarOverlayImpl.WIDTH / maxBlood;
                double barX = xStart + (rightHandSide() ? BarOverlayImpl.WIDTH - barWidth : 0);
                applyConfiguredBarColor(ColorUtils.hex2Color("#AA0000"));
                renderPartialBar(graphics, barX + 2, yStart + 2, barWidth);
            }
        } catch (Throwable t) {
            // ignore
        }
    }

    @Override
    public void renderText(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
        try {
            IVampirePlayer vp = VampirismAPI.vampirePlayer(player);
            int blood = vp.getBloodLevel();
            int maxBlood = vp.getBloodStats().getMaxBlood();
            if (maxBlood <= 0) return;
            int baseX = width / 2 + getIconOffset();
            int yStart = height - vOffset;
            textHelper(graphics, baseX, yStart, blood, maxBlood,
                    getConfiguredTextColor(ColorUtils.hex2Color("#AA0000")), barSettings.textFormat);
        } catch (Throwable t) {
            // ignore
        }
    }

    @Override
    public double getBarWidth(Player player) {
        try {
            IVampirePlayer vp = VampirismAPI.vampirePlayer(player);
            int blood = vp.getBloodLevel();
            int maxBlood = vp.getBloodStats().getMaxBlood();
            if (maxBlood <= 0) return 0;
            return Math.min(BarOverlayImpl.WIDTH, Math.ceil(BarOverlayImpl.WIDTH * (double) blood / maxBlood));
        } catch (Throwable t) {
            return 0;
        }
    }

    @Override
    public void renderIcon(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
        int baseX = width / 2 + getIconOffset();
        int yStart = height - vOffset;
        ModUtils.drawIconWithTexture(graphics, baseX, yStart, 9, BarIcons.BLOOD);
    }
}
