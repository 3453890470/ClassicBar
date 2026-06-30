package tfar.classicbar.impl.overlays.vanilla;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import tfar.classicbar.client.HudRenderContext;
import tfar.classicbar.config.ClassicBarsConfig;
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.util.Color;
import tfar.classicbar.util.ModUtils;
import tfar.classicbar.resources.BarIcons;

/**
 * ClassicBar overlay for {@link net.minecraft.world.entity.player.Player} armor rating.
 * <p>
 * Renders the player's armor protection value as a classic-style progress bar,
 * wrapping at intervals of 20. Supports low armor warning and durability-based
 * flashing. Color comes from the {@link ConfigCache#armor} color list.
 */
public class Armor extends BarOverlayImpl {

    private static final EquipmentSlot[] armorList = new EquipmentSlot[]{EquipmentSlot.HEAD,
            EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    public Armor() {
        super("armor");
    }

    @Override
    public boolean shouldRender(Player player) {
        return calculateArmorValue(player) >= 1;
    }

    @Override
    public void renderBar(HudRenderContext context, GuiGraphicsExtractor graphics, Player player, int screenWidth, int screenHeight, int vOffset) {
        double armor = calculateArmorValue(player);
        double barWidth = getBarWidth(player);

        boolean warn = shouldFlash(player);
        float armorAlpha = warn ? (System.currentTimeMillis() / 500) % 2 : 1;
        int xStart = screenWidth / 2 + getHOffset();

        if (rightHandSide()) {
            xStart += WIDTH - barWidth;
        }

        int yStart = screenHeight - vOffset;
        //bar background
        renderBarBackground(graphics, player, screenWidth, screenHeight, vOffset);
        //how many layers are there? remember to start at 0
        int index = computeWrapIndex(armor, 20, ConfigCache.armor.size());
        Color primary = getPrimaryBarColor(index, player);

        if (index == 0) {
            //calculate bar color
            applyConfiguredBarColor(primary, armorAlpha);
            //draw portion of bar based on armor
            renderPartialBar(graphics, xStart + 2, yStart + 2, barWidth);
        } else {
            //we have wrapped, draw 2 bars
            //draw first bar
            //case 1: bar is not capped and is partially filled
            if (armor % 20 != 0) {
                Color secondary = getSecondaryBarColor(index - 1, player);
                //draw complete first bar
                applyConfiguredBarColor(secondary, armorAlpha);
                renderFullBar(graphics, xStart + 2, yStart + 2);
                //draw partial second bar
                applyConfiguredBarColor(primary);
                double w = ModUtils.getWidth(armor % 20, 20);
                double f = xStart + (rightHandSide() ? WIDTH - w : 0);
                renderPartialBar(graphics, f + 2, yStart + 2, w);
            }
            //case 2, bar is a multiple of 20, or it is capped
            else {
                //draw complete second bar
                applyConfiguredBarColor(primary, armorAlpha);
                renderFullBar(graphics, xStart + 2, yStart + 2);
            }
            // now handle the low armor warning
            if (warn) {
                //draw one bar
                applyConfiguredBarColor(primary, armorAlpha);
                renderPartialBar(graphics, xStart + 2, yStart + 2, ModUtils.getWidth(armor - index * 20, 20));
            }
        }
    }

    @Override
    protected boolean shouldFlash(Player player) {
        return ClassicBarsConfig.lowArmorWarning.get() && getDamagedAmount(player) > 0;
    }

    @Override
    public double getBarWidth(Player player) {
        int armor = calculateArmorValue(player);
        return Math.ceil(WIDTH * Math.min(20, armor) / 20d);//armor can go above 20 in modded contexts!
    }

    @Override
    public Color getPrimaryBarColor(int index, Player player) {
        return ConfigCache.armor.get(index);
    }

    @Override
    public Color getSecondaryBarColor(int index, Player player) {
        return ConfigCache.armor.get(index);
    }

    @Override
    public boolean isFitted() {
        return !ClassicBarsConfig.fullArmorBar.get();
    }


    public static int getDamagedAmount(Player player) {
        int warningAmount = 0;
        for (EquipmentSlot slot : armorList) {
            ItemStack stack = player.getItemBySlot(slot);
            if (stack.isEmpty() || !stack.isDamageableItem()) continue;
            int max = stack.getMaxDamage();
            int current = stack.getDamageValue();
            int percentage = 100;
            if (max != 0) percentage = 100 * (max - current) / (max);
            if (percentage < 5) {
                warningAmount += 1;
            }
        }
        return warningAmount;
    }

    @Override
    public void renderText(GuiGraphicsExtractor graphics, Player player, int width, int height, int vOffset) {
        double armor = player.getArmorValue();
        renderSimpleText(graphics, width, height, vOffset, armor, 20, player);
    }

    @Override
    public void renderIcon(GuiGraphicsExtractor graphics, Player player, int width, int height, int vOffset) {
        int xStart = width / 2 + getIconOffset();
        int yStart = height - vOffset;
        ModUtils.drawIconWithTexture(graphics, xStart, yStart, 9, BarIcons.ARMOR);
    }

    private static int calculateArmorValue(Player player) {
        return player.getArmorValue();
    }
}
