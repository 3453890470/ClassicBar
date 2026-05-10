package tfar.classicbar.impl.overlays.vanilla;


import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import tfar.classicbar.client.HudRenderContext;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.util.Color;
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.util.ColorUtils;
import tfar.classicbar.impl.overlays.vanilla.HarmType;
import tfar.classicbar.util.ModUtils;
import tfar.classicbar.resources.BarIcons;
import java.util.List;

/**
 * ClassicBar overlay for {@link Player} health.
 * <p>
 * Renders the player's health as a classic-style progress bar
 * with gradient colors for normal/poisoned/withered/frozen states.
 */
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

        // Detect health decrease (always)
        if (health < playerHealth) {
            healthUpdateCounter = updateCounter + 2;
            lastPlayerHealth = playerHealth;
        } else if (health > playerHealth && player.invulnerableTime > 0) {
            healthUpdateCounter = updateCounter + 2;
        }
        playerHealth = health;
        double displayHealth = health + (lastPlayerHealth - health) * ((double) player.invulnerableTime / player.invulnerableDuration);

        int xStart = screenWidth / 2 + getHOffset();
        int yStart = screenHeight - vOffset;
        double maxHealth = player.getMaxHealth();

        HarmType effect = getHarmType(player);

        int i4 = (highlight) ? 18 : 0;

        Color.reset();
        // Bar background
        ModUtils.drawTexturedModalRect(graphics, xStart, yStart, 0, i4, WIDTH + 4, 9);

        double f = xStart + (rightHandSide() ? WIDTH - barWidth : 0);

        // Is the bar changing
        // Pass 1, draw bar portion
        // Interpolate the bar
        if (displayHealth != health) {
            // Reset to white
            if (displayHealth > health) {
                Color.reset();
                // Draw interpolation
                double w = ModUtils.getWidth(displayHealth, maxHealth);
                double off = rightHandSide() ? w - barWidth : 0;
                // Draw interpolation
                renderPartialBar(graphics, f + 2 - off, yStart + 2, w);
                // Health is increasing, IDK what to do here
            } else {/*
                      f = xStart + getWidth(health, maxHealth);
                      drawTexturedModalRect(f, yStart + 1, 1, 10, getWidth(health - displayHealth, maxHealth), 7, general.style, true, true);*/
            }
        }
        // Calculate bar color
        Color primary = getPrimaryBarColor(0, player);
        applyConfiguredBarColor(primary);
        // Draw portion of bar based on health remaining
        renderPartialBar(graphics, f + 2, yStart + 2, barWidth);
        boolean hasPoison = player.hasEffect(MobEffects.POISON);
        boolean hasWither = player.hasEffect(MobEffects.WITHER);
        boolean hasFrozen = player.getTicksFrozen() > 0;

        if (hasPoison) {
            Color c = ConfigCache.healthPoisonOverlay;
            if (c != null) {
                RenderSystem.setShaderColor(c.r() / 255f, c.g() / 255f, c.b() / 255f, ConfigCache.healthPoisonOverlayAlpha);
                ModUtils.drawTexturedModalRect(graphics, f + 1, yStart + 1, 1, 36, barWidth, 7);
                Color.reset();
            }
        }
        if (hasWither) {
            Color c = ConfigCache.healthWitherOverlay;
            if (c != null) {
                RenderSystem.setShaderColor(c.r() / 255f, c.g() / 255f, c.b() / 255f, ConfigCache.healthWitherOverlayAlpha);
                ModUtils.drawTexturedModalRect(graphics, f + 1, yStart + 1, 1, 36, barWidth, 7);
                Color.reset();
            }
        }
        if (hasFrozen) {
            Color c = ConfigCache.healthFrozenOverlay;
            if (c != null) {
                RenderSystem.setShaderColor(c.r() / 255f, c.g() / 255f, c.b() / 255f, ConfigCache.healthFrozenOverlayAlpha);
                ModUtils.drawTexturedModalRect(graphics, f + 1, yStart + 1, 1, 36, barWidth, 7);
                Color.reset();
            }
        }
    }

    @Override
    public Color getPrimaryBarColor(int index, Player player) {
        double health = player.getHealth();
        double maxHealth = player.getMaxHealth();
        HarmType effect = getHarmType(player);
        return ColorUtils.calculateScaledColor(health, maxHealth, effect);
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
        int baseX = width / 2 + getIconOffset();
        int yStart = height - vOffset;

        boolean hasPoison = player.hasEffect(MobEffects.POISON);
        boolean hasWither = player.hasEffect(MobEffects.WITHER);
        boolean hasFrozen = player.getTicksFrozen() > 0;
        int effectCount = 0;
        if (hasPoison) effectCount++;
        if (hasWither) effectCount++;
        if (hasFrozen) effectCount++;

        int textX;
        if (rightHandSide()) {
            textX = baseX + effectCount * 4;   // textHelper internally handles offset based on icon
        } else {
            textX = baseX - effectCount * 4;   // textHelper internally handles offset based on icon
        }

        textHelper(graphics, textX, yStart, health, player.getMaxHealth(),
            getConfiguredTextColor(getPrimaryBarColor(0, player)), barSettings.textFormat);
    }

    @Override
    public void renderIcon(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
        int baseX = width / 2 + getIconOffset();
        int yStart = height - vOffset;
        int guiTicks = ModUtils.getGuiTicks();

        boolean hasPoison = player.hasEffect(MobEffects.POISON);
        boolean hasWither = player.hasEffect(MobEffects.WITHER);
        boolean hasFrozen = player.getTicksFrozen() > 0;

        boolean baseFlashing = healthUpdateCounter > (long) guiTicks;

        ModUtils.renderEffectIcons(graphics, baseX, yStart, rightHandSide(), baseFlashing, guiTicks,
            BarIcons.HEALTH, BarIcons.HEALTH_BLINKING,
            List.of(
                new ModUtils.EffectIcon(() -> hasFrozen, BarIcons.HEALTH_FROZEN, BarIcons.HEALTH_FROZEN_BLINKING),
                new ModUtils.EffectIcon(() -> hasWither, BarIcons.HEALTH_WITHER, BarIcons.HEALTH_WITHER_BLINKING),
                new ModUtils.EffectIcon(() -> hasPoison, BarIcons.HEALTH_POISON, BarIcons.HEALTH_POISON_BLINKING)
            )
        );
    }
}
