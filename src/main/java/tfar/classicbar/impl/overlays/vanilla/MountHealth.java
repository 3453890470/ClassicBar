package tfar.classicbar.impl.overlays.vanilla;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import tfar.classicbar.client.HudRenderContext;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.util.Color;
import tfar.classicbar.util.ColorUtils;
import tfar.classicbar.util.HealthEffect;
import tfar.classicbar.util.ModUtils;
import net.minecraft.resources.ResourceLocation;
import tfar.classicbar.resources.BarIcons;

public class MountHealth extends BarOverlayImpl {

  private double mountHealth = 0;

  public MountHealth() {
    super("health_mount");
  }

  @Override
  public boolean shouldRender(Player player) {
    return getMount(player) != null;
  }

  @Override
  public void renderBar(HudRenderContext context, GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset) {
    LivingEntity mount = getMount(player);
    if (mount == null) {
      return;
    }

    double currentMountHealth = mount.getHealth();
    double barWidth = getBarWidth(player);
    this.mountHealth = currentMountHealth;
    int backgroundX = screenWidth / 2 + getHOffset();
    double filledX = backgroundX + (rightHandSide() ? BarOverlayImpl.WIDTH - barWidth : 0);
    int yStart = screenHeight - vOffset;
    double maxHealth = mount.getMaxHealth();

    Color.reset();
    renderFullBarBackground(graphics, backgroundX, yStart);
    applyConfiguredBarColor(ColorUtils.calculateScaledColor(currentMountHealth, maxHealth, HealthEffect.NONE));
    renderPartialBar(graphics, filledX + 2, yStart + 2, barWidth);
  }

  @Override
  public double getBarWidth(Player player) {
    LivingEntity mount = getMount(player);
    if (mount == null) {
      return 0;
    }
    double mounthHealth = mount.getHealth();
    double maxHealth = mount.getMaxHealth();
    return Math.ceil(BarOverlayImpl.WIDTH * Math.min(maxHealth, mounthHealth) / maxHealth);
  }

  @Override
  public void renderText(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
    if (player.getVehicle() instanceof LivingEntity mount) {
      double mountHealth = mount.getHealth();
      int xStart = width / 2 + getIconOffset();
      int yStart = height - vOffset;
      textHelper(graphics, xStart, yStart, mountHealth, mount.getMaxHealth(),
                 getConfiguredTextColor(getPrimaryBarColor(0, player)), barSettings.textFormat);
    }
  }

    @Override
    public void renderIcon(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
        int xStart = width / 2 + getIconOffset();
        int yStart = height - vOffset;
        ModUtils.drawIconWithTexture(graphics, xStart, yStart, 9, BarIcons.MOUNT_HEALTH);
    }

  private static LivingEntity getMount(Player player) {
    if (player.getVehicle() instanceof LivingEntity livingEntity && livingEntity.isAlive()) {
      return livingEntity;
    }
    return null;
  }
}
