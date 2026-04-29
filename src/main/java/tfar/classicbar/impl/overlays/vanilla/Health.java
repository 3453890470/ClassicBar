package tfar.classicbar.impl.overlays.vanilla;


import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import tfar.classicbar.client.HudRenderContext;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.util.Color;
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.util.ColorUtils;
import tfar.classicbar.util.HealthEffect;
import tfar.classicbar.util.ModUtils;
import tfar.classicbar.resources.BarIcons;
import net.minecraft.resources.ResourceLocation;

public class Health extends BarOverlayImpl {

  private double playerHealth = 0;
  private long healthUpdateCounter = 0;
  private double lastPlayerHealth = 0;

  public Health() {
    super("health");
  }

  @Override
  public boolean shouldRender(Player player) {
    return true;
  }

  @Override
  public void renderBar(HudRenderContext context, GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset) {
    int updateCounter = context.getGuiTicks();

    double health = player.getHealth();
    double barWidth = getBarWidth(player);
    boolean highlight = healthUpdateCounter > (long) updateCounter && (healthUpdateCounter - (long) updateCounter) / 3 % 2 == 1;

    //player is damaged and resistant
    if (health < playerHealth && player.invulnerableTime > 0) {
      healthUpdateCounter = updateCounter + 20;
      lastPlayerHealth = playerHealth;
    } else if (health > playerHealth && player.invulnerableTime > 0) {
      healthUpdateCounter = updateCounter + 10;
      /* lastPlayerHealth = playerHealth;*/
    }
    playerHealth = health;
    double displayHealth = health + (lastPlayerHealth - health) * ((double) player.invulnerableTime / player.invulnerableDuration);

    int xStart = screenWidth / 2 + getHOffset();
    int yStart = screenHeight - vOffset;
    double maxHealth = player.getMaxHealth();

    HealthEffect effect = getHealthEffect(player);

    int i4 = (highlight) ? 18 : 0;

    Color.reset();
    //Bar background
    ModUtils.drawTexturedModalRect(graphics,xStart, yStart, 0, i4, WIDTH + 4, 9);

    double f = xStart + (rightHandSide() ? WIDTH - barWidth : 0);

    //is the bar changing
    //Pass 1, draw bar portion
    //interpolate the bar
    if (displayHealth != health) {
      //reset to white
      if (displayHealth > health) {
        Color.reset();
        //draw interpolation
        double w = ModUtils.getWidth(displayHealth, maxHealth);
        double off = rightHandSide() ? w - barWidth : 0;
        //draw interpolation
        renderPartialBar(graphics,f + 2 - off, yStart + 2,w);
        //Health is increasing, IDK what to do here
      } else {/*
                  f = xStart + getWidth(health, maxHealth);
                  drawTexturedModalRect(f, yStart + 1, 1, 10, getWidth(health - displayHealth, maxHealth), 7, general.style, true, true);*/
      }
    }
    //calculate bar color
    Color primary = getPrimaryBarColor(0,player);
    applyConfiguredBarColor(primary);
    //draw portion of bar based on health remaining
    renderPartialBar(graphics,f + 2, yStart + 2, barWidth);
    if (effect == HealthEffect.POISON) {
      Color c = ConfigCache.healthPoisonOverlay;
      RenderSystem.setShaderColor(c.r()/255f, c.g()/255f, c.b()/255f, ConfigCache.healthPoisonOverlayAlpha);
      ModUtils.drawTexturedModalRect(graphics, f + 1, yStart + 1, 1, 36, barWidth, 7);
      Color.reset();
    } else if (effect == HealthEffect.WITHER) {
      Color c = ConfigCache.healthWitherOverlay;
      RenderSystem.setShaderColor(c.r()/255f, c.g()/255f, c.b()/255f, ConfigCache.healthWitherOverlayAlpha);
      ModUtils.drawTexturedModalRect(graphics, f + 1, yStart + 1, 1, 36, barWidth, 7);
      Color.reset();
    } else if (effect == HealthEffect.FROZEN) {
      Color c = ConfigCache.healthFrozenOverlay;
      RenderSystem.setShaderColor(c.r()/255f, c.g()/255f, c.b()/255f, ConfigCache.healthFrozenOverlayAlpha);
      ModUtils.drawTexturedModalRect(graphics, f + 1, yStart + 1, 1, 36, barWidth, 7);
      Color.reset();
    }
  }

  @Override
  public Color getPrimaryBarColor(int index, Player player) {
    double health = player.getHealth();
    double maxHealth = player.getMaxHealth();
    HealthEffect effect = getHealthEffect(player);
    return ColorUtils.calculateScaledColor(health,maxHealth,effect);
  }

  @Override
  public double getBarWidth(Player player) {
    double health = player.getHealth();
    double maxHealth = player.getMaxHealth();
    return WIDTH * health / maxHealth;
  }

  @Override
  public void renderText(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
    double health = player.getHealth();
    int xStart = width / 2 + getIconOffset();
    int yStart = height - vOffset;
    textHelper(graphics,xStart,yStart,health,getConfiguredTextColor(getPrimaryBarColor(0,player)));
  }

  @Override
  public void renderIcon(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
    int xStart = width / 2 + getIconOffset();
    int yStart = height - vOffset;
    HealthEffect effect = getHealthEffect(player);
    ResourceLocation saved = ModUtils.CURRENT_TEXTURE;
    try {
      if (effect == HealthEffect.POISON) {
        ModUtils.CURRENT_TEXTURE = BarIcons.HEALTH_POISON;
      } else if (effect == HealthEffect.WITHER) {
        ModUtils.CURRENT_TEXTURE = BarIcons.HEALTH_WITHER;
      } else if (effect == HealthEffect.FROZEN) {
        ModUtils.CURRENT_TEXTURE = BarIcons.HEALTH_FROZEN;
      }
      ModUtils.drawStandaloneIcon(graphics, xStart, yStart, 9);
    } finally {
      ModUtils.CURRENT_TEXTURE = saved;
    }
  }
}
