package tfar.classicbar.impl.overlays.mod;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;
import tfar.classicbar.client.HudRenderContext;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.util.Color;
import tfar.classicbar.util.ColorUtils;
import tfar.classicbar.util.ModUtils;

/**
 * Blood overlay for Vampirism mod compatibility.
 * <p>
 * Renders the vampire blood level as a classic-style progress bar
 * with deep red (#AA0000) primary and bright red (#FF4444) accent.
 * <p>
 * Runtime safety (two-layer guard):
 * <ul>
 *   <li>ModList.isLoaded("vampirism") guards class-level Vampirism API access</li>
 *   <li>VampirismAPI.vampirePlayer() + maxBlood > 0 distinguishes vampire players;
 *       returns null for non-vampire players</li>
 * </ul>
 * The config toggle is intentionally not checked — Vampirism presence + vampire
 * status auto-enables the blood bar.
 */
public class Blood extends BarOverlayImpl {

    public Blood() {
        super("blood");
    }

    /**
     * Two-layer guard: mod loaded, player is vampire.
     * Config toggle is intentionally removed — Vampirism presence + vampire status auto-enables the blood bar.
     */
    @Override
    public boolean shouldRender(Player player) {
        if (!ModList.get().isLoaded("vampirism")) return false;
        return getVampirePlayer(player) != null;
    }

    /**
     * Safely retrieve the {@link IVampirePlayer} handle for a vampire player.
     * <p>
     * {@link VampirismAPI#vampirePlayer(Player)} returns an {@link IVampirePlayer}
     * directly (not Optional) for all players via attachment system.
     * Non-vampire players have maxBlood == 0, which we use as the distinguishing check.
     *
     * @return the {@link IVampirePlayer} if the player is a vampire, {@code null} otherwise
     */
    private static IVampirePlayer getVampirePlayer(Player player) {
        try {
            IVampirePlayer vampire = VampirismAPI.vampirePlayer(player);
            if (vampire != null && vampire.getBloodStats().getMaxBlood() > 0) {
                return vampire;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public double getBarWidth(Player player) {
        IVampirePlayer vampire = getVampirePlayer(player);
        if (vampire == null) return 0;
        int bloodLevel = vampire.getBloodLevel();
        int maxBlood = Math.max(1, vampire.getBloodStats().getMaxBlood());
        return Math.min(BarOverlayImpl.WIDTH, Math.ceil(BarOverlayImpl.WIDTH * (double) bloodLevel / maxBlood));
    }

    @Override
    public Color getPrimaryBarColor(int index, Player player) {
        // Deep red — main blood bar color
        return ColorUtils.hex2Color("#AA0000");
    }

    @Override
    public Color getSecondaryBarColor(int index, Player player) {
        // Bright red — used for accent / saturation portion
        return ColorUtils.hex2Color("#FF4444");
    }

    @Override
    public void renderBar(HudRenderContext context, GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset) {
        IVampirePlayer vampire = getVampirePlayer(player);
        if (vampire == null) return;

        double barWidth = getBarWidth(player);
        int xStart = screenWidth / 2 + getHOffset();
        int yStart = screenHeight - vOffset;

        // Background
        Color.reset();
        renderFullBarBackground(graphics, xStart, yStart);

        // Blood bar foreground
        double barX = xStart + (rightHandSide() ? BarOverlayImpl.WIDTH - barWidth : 0);
        applyConfiguredBarColor(getPrimaryBarColor(0, player));
        renderPartialBar(graphics, barX + 2, yStart + 2, barWidth);
    }

    @Override
    public void renderText(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
        IVampirePlayer vampire = getVampirePlayer(player);
        if (vampire == null) return;

        int bloodLevel = vampire.getBloodLevel();
        int maxBlood = Math.max(1, vampire.getBloodStats().getMaxBlood());
        int baseX = width / 2 + getIconOffset();
        int yStart = height - vOffset;

        textHelper(graphics, baseX, yStart, bloodLevel, maxBlood,
                getConfiguredTextColor(getPrimaryBarColor(0, player)), barSettings.textFormat);
    }

    @Override
    public void renderIcon(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
        int baseX = width / 2 + getIconOffset();
        int yStart = height - vOffset;
        // Texture is already bound to BarIcons.BLOOD via bindIconTexture() in the render pipeline
        ModUtils.drawStandaloneIcon(graphics, baseX, yStart, 9);
    }
}
