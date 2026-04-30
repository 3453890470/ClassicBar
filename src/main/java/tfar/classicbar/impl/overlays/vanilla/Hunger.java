package tfar.classicbar.impl.overlays.vanilla;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import tfar.classicbar.client.HudRenderContext;
import tfar.classicbar.compat.ModCompat;
import tfar.classicbar.config.ClassicBarsConfig;
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.util.Color;
import tfar.classicbar.resources.BarIcons;
import tfar.classicbar.util.ModUtils;

public class Hunger extends BarOverlayImpl {

  public Hunger() {
    super("food");
  }

  private double lastFoodLevel = 0;
  private long foodUpdateCounter = 0;

  @Override
  public boolean shouldRender(Player player) {
    return true;
  }

  @Override
  public void renderBar(HudRenderContext context, GuiGraphics matrices, Player player, int screenWidth, int screenHeight, int vOffset) {
    double hunger = player.getFoodData().getFoodLevel();
    double maxHunger = 20;//HungerHelper.getMaxHunger(player);

    // 检测第三方模组 buff 效果
    boolean hasNourishment = ModCompat.hasNourishment(player);
    boolean hasSatiatedShield = ModCompat.hasSatiatedShield(player);

    double barWidthH = getBarWidth(player);

    // Detect hunger decrease
    int updateCounter = context.getGuiTicks();
    if (hunger < lastFoodLevel) {
      foodUpdateCounter = updateCounter + 2;
    }
    lastFoodLevel = hunger;

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

    // Buff 效果颜色（由 getPrimaryBarColor / getSecondaryBarColor 自行处理）
    // 覆盖层在 renderBar 末尾叠加

    applyConfiguredBarColor(hungerColor);
    renderPartialBar(matrices,f + 2, yStart + 2,  barWidthH);
    if (currentSat > 0 && ClassicBarsConfig.showSaturationBar.get()) {
      //draw saturation
      applyConfiguredBarColor(satColor);
      f = xStart + (rightHandSide() ? BarOverlayImpl.WIDTH - barWidthS : 0);
      renderPartialBar(matrices,f + 2, yStart + 2, barWidthS);
    }

    // --- Food preview ---
    if (ConfigCache.showFoodPreview) {
      ItemStack held = player.getMainHandItem();
      FoodProperties food = held.getItem().getFoodProperties(held, player);
      if (food != null) {
        int nutrition = food.nutrition();
        float satMod = food.saturation();

        // Expected hunger after eating
        double newHunger = Math.min(maxHunger, hunger + nutrition);
        double hungerPreview = newHunger - hunger;

        // Expected saturation after eating — capped at 20
        double satFromFood = nutrition * satMod * 2.0f;
        double cappedCurrentSat = Math.min(20, currentSat);
        double cappedNewSaturation = Math.min(20, cappedCurrentSat + satFromFood);
        double satPreview = Math.max(0, cappedNewSaturation - cappedCurrentSat);

        if (hungerPreview > 0 || satPreview > 0) {
          // Breathing alpha: 0.30 ~ 1.00, period 40 ticks (2 seconds)
          float breath = 0.65f + 0.35f * (float) Math.sin(context.getGuiTicks() * Math.PI / 20);

          // p4: Hunger preview bar
          if (hungerPreview > 0) {
            double previewWidth = Math.ceil(BarOverlayImpl.WIDTH * hungerPreview / maxHunger);
            double previewX = rightHandSide()
                ? xStart + BarOverlayImpl.WIDTH + 2 - previewWidth
                : xStart + barWidthH + 2;
            // Clamp to container bounds: [xStart+2, xStart+79]
            double containerLeft = xStart + 2;
            double containerRight = xStart + BarOverlayImpl.WIDTH + 2;
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
              applyConfiguredBarColor(hungerColor, breath);
              renderPartialBar(matrices, previewX, yStart + 2, previewWidth);
            }
          }

          // p5: Saturation preview bar
          if (satPreview > 0) {
            double previewWidth = Math.ceil(BarOverlayImpl.WIDTH * satPreview / maxHunger);
            double previewX = rightHandSide()
                ? xStart + BarOverlayImpl.WIDTH + 2 - previewWidth
                : xStart + barWidthS + 2;
            // Clamp to container bounds: [xStart+2, xStart+79]
            double containerLeft = xStart + 2;
            double containerRight = xStart + BarOverlayImpl.WIDTH + 2;
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
              applyConfiguredBarColor(satColor, breath);
              renderPartialBar(matrices, previewX, yStart + 2, previewWidth);
            }
          }

          // p6: Preview overlay — from rightmost edge, covers max of both preview widths
          double maxPreviewWidth = 0;
          if (hungerPreview > 0) {
            maxPreviewWidth = Math.max(maxPreviewWidth, Math.ceil(BarOverlayImpl.WIDTH * hungerPreview / maxHunger));
          }
          if (satPreview > 0) {
            maxPreviewWidth = Math.max(maxPreviewWidth, Math.ceil(BarOverlayImpl.WIDTH * satPreview / maxHunger));
          }
          if (maxPreviewWidth > 0) {
            double overlayX = rightHandSide()
                ? xStart + BarOverlayImpl.WIDTH + 2 - maxPreviewWidth
                : xStart + barWidthH + 2; // LHS: keep original behavior
            double containerLeft = xStart + 2;
            if (overlayX < containerLeft) {
              maxPreviewWidth = Math.max(0, maxPreviewWidth - (containerLeft - overlayX));
              overlayX = containerLeft;
            }
            if (maxPreviewWidth > 0) {
              Color.WHITE.color2Gla(breath);
              ModUtils.drawTexturedModalRect(matrices, overlayX, yStart, 0, 35, maxPreviewWidth, 9);
              Color.reset();
            }
          }
        }
      }
    }

    // === Buff 效果条覆盖层 ===
    if (ConfigCache.enableNourishmentCompat && ConfigCache.enableSatiatedShieldCompat &&
        (hasNourishment || hasSatiatedShield)) {
      int coverX = xStart + 2;
      int coverWidth = BarOverlayImpl.WIDTH;

      if (hasNourishment && ConfigCache.enableNourishmentCompat) {
        applyConfiguredBarColor(ConfigCache.nourishment, 0.20f);
        renderPartialBar(matrices, coverX, yStart + 2, coverWidth);
      }

      if (hasSatiatedShield && ConfigCache.enableSatiatedShieldCompat) {
        applyConfiguredBarColor(ConfigCache.satiatedShield, 0.20f);
        renderPartialBar(matrices, coverX, yStart + 2, coverWidth);
      }

      Color.reset();
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
    if (ModCompat.hasNourishment(player) && ConfigCache.enableNourishmentCompat) {
      return ConfigCache.nourishment;
    }
    if (ModCompat.hasSatiatedShield(player) && ConfigCache.enableSatiatedShieldCompat) {
      return ConfigCache.satiatedShield;
    }
    boolean hunger = player.hasEffect(MobEffects.HUNGER);
    return hunger ? ConfigCache.saturationDebuff : ConfigCache.saturation;
  }

  //hunger
  @Override
  public Color getSecondaryBarColor(int index, Player player) {
    if (ModCompat.hasNourishment(player) && ConfigCache.enableNourishmentCompat) {
      return ConfigCache.nourishment;
    }
    if (ModCompat.hasSatiatedShield(player) && ConfigCache.enableSatiatedShieldCompat) {
      return ConfigCache.satiatedShield;
    }
    boolean hunger = player.hasEffect(MobEffects.HUNGER);
    return hunger ? ConfigCache.hungerDebuff : ConfigCache.hunger;
  }

  @Override
  public void renderText(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
    double hunger = player.getFoodData().getFoodLevel();
    int baseX = width / 2 + getIconOffset();
    int yStart = height - vOffset;

    // 计算效果图标数量，以调整文字位置
    boolean hasHunger = player.hasEffect(MobEffects.HUNGER);
    boolean hasNourishment = ModCompat.hasNourishment(player) && ConfigCache.enableNourishmentCompat;
    boolean hasSatiatedShield = ModCompat.hasSatiatedShield(player) && ConfigCache.enableSatiatedShieldCompat;
    int effectCount = 0;
    if (hasHunger) effectCount++;
    if (hasNourishment) effectCount++;
    if (hasSatiatedShield) effectCount++;

    // 文字跟随图标堆叠末端
    int textX;
    if (rightHandSide()) {
      // 右侧图标：文字在最右侧效果图标的右边
      textX = baseX + effectCount * 4;   // textHelper 内部根据图标自动处理偏移
    } else {
      // 左侧图标：文字在最左侧效果图标的左边
      textX = baseX - effectCount * 4;   // textHelper 内部根据图标自动处理偏移
    }

    textHelper(graphics, textX, yStart, hunger, 20,
               getConfiguredTextColor(getPrimaryBarColor(0, player)), barSettings.textFormat);
  }

  @Override
  public void renderIcon(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
    int baseX = width / 2 + getIconOffset();
    int yStart = height - vOffset;
    int guiTicks = ModUtils.getGuiTicks();

    boolean hasHunger = player.hasEffect(MobEffects.HUNGER);
    boolean hasNourishment = ModCompat.hasNourishment(player) && ConfigCache.enableNourishmentCompat;
    boolean hasSatiatedShield = ModCompat.hasSatiatedShield(player) && ConfigCache.enableSatiatedShieldCompat;

    // 效果计数（饥饿 + 滋养 + 饱腹代偿）
    int effectCount = 0;
    if (hasHunger) effectCount++;
    if (hasNourishment) effectCount++;
    if (hasSatiatedShield) effectCount++;

    final int OVERLAP = 4;
    boolean baseFlashing = foodUpdateCounter > (long) guiTicks;

    // 基础 FOOD 图标始终在最靠近条的一侧（最上层）
    if (rightHandSide()) {
      // === 右侧：基础在最左，效果向右延伸 ===
      // 绘制顺序从右到左（底层→顶层）：
      // 饱腹代偿 → 滋养 → 饥饿 → 基础
      int startX = baseX + effectCount * OVERLAP;
      int currentX = startX;

      if (hasSatiatedShield) {
        ModUtils.drawIconWithFlash(graphics, currentX, yStart, 9,
            BarIcons.FOOD_SATIATED_SHIELD, BarIcons.FOOD_SATIATED_SHIELD, false, guiTicks);
        currentX -= OVERLAP;
      }
      if (hasNourishment) {
        ModUtils.drawIconWithFlash(graphics, currentX, yStart, 9,
            BarIcons.FOOD_NOURISHMENT, BarIcons.FOOD_NOURISHMENT, false, guiTicks);
        currentX -= OVERLAP;
      }
      if (hasHunger) {
        ModUtils.drawIconWithFlash(graphics, currentX, yStart, 9,
            BarIcons.FOOD_HUNGER, BarIcons.FOOD_HUNGER_BLINKING, baseFlashing, guiTicks);
        currentX -= OVERLAP;
      }
      // 基础 FOOD（最左，顶层）
      ModUtils.drawIconWithFlash(graphics, baseX, yStart, 9,
          BarIcons.FOOD, BarIcons.FOOD_BLINKING, baseFlashing, guiTicks);

    } else {
      // === 左侧：基础在最右，效果向左延伸 ===
      // 绘制顺序从左到右（底层→顶层）：
      // 饱腹代偿 → 滋养 → 饥饿 → 基础
      int startX = baseX - effectCount * OVERLAP;
      int currentX = startX;

      if (hasSatiatedShield) {
        ModUtils.drawIconWithFlash(graphics, currentX, yStart, 9,
            BarIcons.FOOD_SATIATED_SHIELD, BarIcons.FOOD_SATIATED_SHIELD, false, guiTicks);
        currentX += OVERLAP;
      }
      if (hasNourishment) {
        ModUtils.drawIconWithFlash(graphics, currentX, yStart, 9,
            BarIcons.FOOD_NOURISHMENT, BarIcons.FOOD_NOURISHMENT, false, guiTicks);
        currentX += OVERLAP;
      }
      if (hasHunger) {
        ModUtils.drawIconWithFlash(graphics, currentX, yStart, 9,
            BarIcons.FOOD_HUNGER, BarIcons.FOOD_HUNGER_BLINKING, baseFlashing, guiTicks);
        currentX += OVERLAP;
      }
      // 基础 FOOD（最右，顶层）
      ModUtils.drawIconWithFlash(graphics, baseX, yStart, 9,
          BarIcons.FOOD, BarIcons.FOOD_BLINKING, baseFlashing, guiTicks);
    }
  }
}
