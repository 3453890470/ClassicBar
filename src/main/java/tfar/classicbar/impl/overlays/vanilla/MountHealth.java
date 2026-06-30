package tfar.classicbar.impl.overlays.vanilla;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import tfar.classicbar.client.HudRenderContext;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.util.Color;
import tfar.classicbar.util.ModUtils;
import tfar.classicbar.resources.BarIcons;

public class MountHealth extends BarOverlayImpl {

    public MountHealth() {
        super("health_mount");
    }

    @Override
    public boolean shouldRender(Player player) {
        return player.getVehicle() instanceof LivingEntity;
    }

    @Override
    public void renderBar(HudRenderContext context, GuiGraphicsExtractor graphics, Player player, int screenWidth, int screenHeight, int vOffset) {
        double health = getVehicleHealth(player);
        double maxHealth = getVehicleMaxHealth(player);
        double barWidth = getBarWidth(player);

        int xStart = screenWidth / 2 + getHOffset();
        int yStart = screenHeight - vOffset;

        renderBarBackground(graphics,player,screenWidth,screenHeight,vOffset);

        Color primary = getPrimaryBarColor(0, player);
        applyConfiguredBarColor(primary);
        renderPartialBar(graphics,xStart + 2,yStart + 2,barWidth);
    }

    @Override
    public double getBarWidth(Player player) {
        double health = getVehicleHealth(player);
        double maxHealth = getVehicleMaxHealth(player);
        return WIDTH * health / maxHealth;
    }

    @Override
    public Color getPrimaryBarColor(int index, Player player) {
        return Color.WHITE;
    }

    @Override
    public Color getSecondaryBarColor(int index, Player player) {
        return Color.WHITE;
    }

    @Override
    public void renderText(GuiGraphicsExtractor graphics, Player player, int width, int height, int vOffset) {
        renderSimpleText(graphics, width, height, vOffset, getVehicleHealth(player), getVehicleMaxHealth(player), player);
    }

    @Override
    public void renderIcon(GuiGraphicsExtractor graphics, Player player, int width, int height, int vOffset) {
        int xStart = width / 2 + getIconOffset();
        int yStart = height - vOffset;
        ModUtils.drawIconWithTexture(graphics, xStart, yStart, 9, BarIcons.MOUNT_HEALTH);
    }

    private static double getVehicleHealth(Player player) {
        if (player.getVehicle() instanceof LivingEntity living) {
            return living.getHealth();
        }
        return 0;
    }

    private static double getVehicleMaxHealth(Player player) {
        if (player.getVehicle() instanceof LivingEntity living) {
            return living.getMaxHealth();
        }
        return 1;
    }
}
