package tfar.classicbar.impl.overlays.vanilla;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import tfar.classicbar.client.HudRenderContext;
import tfar.classicbar.config.ClassicBarsConfig;
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.util.Color;
import tfar.classicbar.util.ModUtils;

public class Hunger extends BarOverlayImpl {

  public Hunger() {
    super("food");
  }

  @Override
  public boolean shouldRender(Player player) {
    return true;
  }

  @Override
  public void renderBar(HudRenderContext context, GuiGraphics matrices, Player player, int screenWidth, int screenHeight, int vOffset) {
    double hunger = player.getFoodData().getFoodLevel();
    double maxHunger = 20;//HungerHelper.getMaxHunger(player);
    
    double barWidthH = getBarWidth(player);
    
    double currentSat = player.getFoodData().getSaturationLevel();
    double maxSat = maxHunger;
    double barWidthS = getSatBarWidth(player);

    int xStart = screenWidth / 2 + getHOffset();
    int yStart = screenHeight - vOffset;

    //Bar background
    Color.reset();
    renderFullBarBackground(matrices,xStart,yStart);
    //draw portion of bar based on hunger amount
    double f = xStart + (rightHandSide() ? BarOverlayImpl.WIDTH - barWidthH : 0);

    Color hungerColor = getSecondaryBarColor(0,player);
    Color satColor = getPrimaryBarColor(0,player);

    applyConfiguredBarColor(hungerColor);
    renderPartialBar(matrices,f + 2, yStart + 2,  barWidthH);
    if (currentSat > 0 && ClassicBarsConfig.showSaturationBar.get()) {
      //draw saturation
      applyConfiguredBarColor(satColor);
      f = xStart + (rightHandSide() ? BarOverlayImpl.WIDTH - barWidthS : 0);
      renderPartialBar(matrices,f + 2, yStart + 2, barWidthS);
    }

    // --- Food preview (AppleSkin-style) ---
    if (ConfigCache.showFoodPreview) {
      ItemStack held = player.getMainHandItem();
      FoodProperties food = held.getItem().getFoodProperties(held, player);
      if (food != null) {
        int nutrition = food.nutrition();
        float satMod = food.saturation();

        // Expected hunger after eating
        double newHunger = Math.min(maxHunger, hunger + nutrition);
        double hungerPreview = newHunger - hunger;
        if (hungerPreview > 0) {
          double hungerPreviewWidth = Math.ceil(BarOverlayImpl.WIDTH * hungerPreview / maxHunger);
          double previewX = rightHandSide()
              ? xStart + BarOverlayImpl.WIDTH - barWidthH - hungerPreviewWidth + 2
              : xStart + barWidthH + 2;
          applyConfiguredBarColor(hungerColor, 0.39f);
          renderPartialBar(matrices, previewX, yStart + 2, hungerPreviewWidth);
        }

        // Expected saturation after eating — capped at 20 regardless of current values
        double satFromFood = nutrition * satMod * 2.0f;
        double cappedCurrentSat = Math.min(20, currentSat);
        double cappedNewSaturation = Math.min(20, cappedCurrentSat + satFromFood);
        double satPreview = Math.max(0, cappedNewSaturation - cappedCurrentSat);
        if (satPreview > 0) {
          double satPreviewWidth = Math.ceil(BarOverlayImpl.WIDTH * satPreview / maxHunger);
          double satPreviewX = rightHandSide()
              ? xStart + BarOverlayImpl.WIDTH - barWidthS - satPreviewWidth + 2
              : xStart + barWidthS + 2;
          applyConfiguredBarColor(satColor, 0.39f);
          renderPartialBar(matrices, satPreviewX, yStart + 2, satPreviewWidth);
        }
      }
    }
  }

  @Override
  public double getBarWidth(Player player) {
    double hunger = player.getFoodData().getFoodLevel();
    double maxHunger = 20;
    return Math.min(BarOverlayImpl.WIDTH, Math.ceil(BarOverlayImpl.WIDTH * hunger / maxHunger));
  }
  
  public int getSatBarWidth(Player player) {
    double saturation = player.getFoodData().getSaturationLevel();
    double maxSat = 20;
    return Math.min(BarOverlayImpl.WIDTH, (int) Math.ceil(BarOverlayImpl.WIDTH * saturation / maxSat));
  }
  //saturation
  @Override
  public Color getPrimaryBarColor(int index, Player player) {
    boolean hunger = player.hasEffect(MobEffects.HUNGER);
    return hunger ? ConfigCache.saturationDebuff : ConfigCache.saturation;
  }

  //hunger
  @Override
  public Color getSecondaryBarColor(int index, Player player) {
    boolean hunger = player.hasEffect(MobEffects.HUNGER);
    return hunger ? ConfigCache.hungerDebuff : ConfigCache.hunger;
  }

  @Override
  public void renderText(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
    double hunger = player.getFoodData().getFoodLevel();
    int xStart = width / 2 + getIconOffset();
    int yStart = height - vOffset;
    textHelper(graphics, xStart, yStart, hunger, 20,
               getConfiguredTextColor(getPrimaryBarColor(0, player)), barSettings.textFormat);
  }

  @Override
  public void renderIcon(GuiGraphics graphics, Player player, int width, int height, int vOffset) {

    int xStart = width / 2 + getIconOffset();
    int yStart = height - vOffset;
    ModUtils.drawStandaloneIcon(graphics, xStart, yStart, 9);
  }
}
