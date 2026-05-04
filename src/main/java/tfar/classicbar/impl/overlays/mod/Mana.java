package tfar.classicbar.impl.overlays.mod;

import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import com.hollingsworth.arsnouveau.common.capability.ManaCap;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;
import tfar.classicbar.client.HudRenderContext;
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.resources.BarIcons;
import tfar.classicbar.util.Color;
import tfar.classicbar.util.ModUtils;

/**
 * ClassicBar overlay for Ars Nouveau mana.
 * <p>
 * Renders the player's mana as a classic-style progress bar.
 * Color comes from the {@link ConfigCache#mana} color config.
 * Only renders when Ars Nouveau is loaded and the player has mana capacity.
 */
public class Mana extends BarOverlayImpl {

    public Mana() {
        super("mana");
    }

    @Override
    public boolean shouldRender(Player player) {
        if (!ModList.get().isLoaded("ars_nouveau")) return false;
        try {
            ManaCap mana = CapabilityRegistry.getMana(player);
            return mana != null && mana.getMaxMana() > 0;
        } catch (Throwable t) {
            return false;
        }
    }

    @Override
    public void renderBar(HudRenderContext context, GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset) {
        try {
            ManaCap mana = CapabilityRegistry.getMana(player);
            if (mana == null) return;

            double current = mana.getCurrentMana();
            int max = mana.getMaxMana();
            if (max <= 0) return;

            double barWidth = getBarWidth(player);
            int xStart = screenWidth / 2 + getHOffset();
            int yStart = screenHeight - vOffset;

            Color.reset();
            renderFullBarBackground(graphics, xStart, yStart);

            double f = getBarStartX(xStart, barWidth);
            applyConfiguredBarColor(ConfigCache.mana);
            renderPartialBar(graphics, f + 2, yStart + 2, barWidth);
        } catch (Throwable t) {
            // ignore
        }
    }

    @Override
    public void renderText(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
        try {
            ManaCap mana = CapabilityRegistry.getMana(player);
            if (mana == null) return;

            double current = mana.getCurrentMana();
            int max = mana.getMaxMana();
            if (max <= 0) return;

            int baseX = width / 2 + getIconOffset();
            int yStart = height - vOffset;
            textHelper(graphics, baseX, yStart, current, max,
                    getConfiguredTextColor(ConfigCache.mana), barSettings.textFormat);
        } catch (Throwable t) {
            // ignore
        }
    }

    @Override
    public double getBarWidth(Player player) {
        try {
            ManaCap mana = CapabilityRegistry.getMana(player);
            if (mana == null) return 0;
            int max = mana.getMaxMana();
            if (max <= 0) return 0;
            return Math.min(BarOverlayImpl.WIDTH, Math.ceil(BarOverlayImpl.WIDTH * mana.getCurrentMana() / max));
        } catch (Throwable t) {
            return 0;
        }
    }

    @Override
    public void renderIcon(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
        int xStart = width / 2 + getIconOffset();
        int yStart = height - vOffset;
        ModUtils.drawIconWithTexture(graphics, xStart, yStart, 9, BarIcons.MANA);
    }
}
