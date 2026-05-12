package tfar.classicbar.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import tfar.classicbar.ClassicBar;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.resources.BarIcons;
import java.util.List;
import java.util.function.BooleanSupplier;

public class ModUtils {

    public record EffectIcon(
        BooleanSupplier condition,
        ResourceLocation normal,
        ResourceLocation blinking
    ) {}
    public static final Minecraft mc = Minecraft.getInstance();
    private static final Font fontRenderer = mc.font;
    public static ResourceLocation CURRENT_TEXTURE = BarIcons.FALLBACK;

    public static void drawTexturedModalRect(GuiGraphics stack, double x, int y, int textureX, int textureY, double width, int height) {
        stack.blit(CURRENT_TEXTURE, (int) x, y, textureX, textureY, (int) width, height);
    }

    public static void drawStandaloneIcon(GuiGraphics stack, int x, int y, int size) {
        stack.blit(CURRENT_TEXTURE, x, y, 0, 0, size, size, size, size);
    }

    public static void drawIconWithTexture(GuiGraphics stack, int x, int y, int size, ResourceLocation texture) {
        ResourceLocation prev = CURRENT_TEXTURE;
        CURRENT_TEXTURE = texture;
        drawStandaloneIcon(stack, x, y, size);
        CURRENT_TEXTURE = prev;
    }

    public static void drawIconWithFlash(GuiGraphics stack, int x, int y, int size,
                                        ResourceLocation normal, ResourceLocation blinking,
                                        boolean flashing, int guiTicks) {
        ResourceLocation prev = CURRENT_TEXTURE;
        if (flashing && (guiTicks / 2) % 2 == 0) {
            CURRENT_TEXTURE = blinking;
        } else {
            CURRENT_TEXTURE = normal;
        }
        drawStandaloneIcon(stack, x, y, size);
        CURRENT_TEXTURE = prev;
    }

    public static int getGuiTicks() {
        return Minecraft.getInstance().gui.getGuiTicks();
    }

    public static float getBreathingAlpha(int guiTicks) {
        return 0.65f + 0.35f * (float) Math.sin(guiTicks * Math.PI / 20);
    }

    public static double getWidth(double d1, double d2) {
        double ratio = BarOverlayImpl.WIDTH * d1 / d2;
        return Math.ceil(ratio);
    }

    public static int getStringLength(String s) {
        return fontRenderer.width(s);
    }

    public static void drawStringOnHUD(GuiGraphics stack, String string, int xOffset, int yOffset, int color) {
        xOffset += 2;
        yOffset += 2;
        net.minecraft.network.chat.Component component = net.minecraft.network.chat.Component.literal(string)
        .withStyle(net.minecraft.network.chat.Style.EMPTY.withFont(ClassicBar.FONT_3X5_TINY));
        stack.drawString(Minecraft.getInstance().font, component, xOffset, yOffset, color, true);
    }

    public static void renderEffectIcons(
        GuiGraphics graphics,
        int baseX,
        int yStart,
        boolean rightHandSide,
        boolean baseFlashing,
        int guiTicks,
        ResourceLocation baseNormal,
        ResourceLocation baseBlinking,
        List<EffectIcon> effects
    ) {
        final int OVERLAP = 4;
        int effectCount = 0;
        for (EffectIcon e : effects) {
            if (e.condition.getAsBoolean()) effectCount++;
        }
        if (rightHandSide) {
            int currentX = baseX + effectCount * OVERLAP;
            for (EffectIcon e : effects) {
                if (e.condition.getAsBoolean()) {
                    ResourceLocation blink = e.blinking != null ? e.blinking : e.normal;
                    ModUtils.drawIconWithFlash(graphics, currentX, yStart, 9,
                        e.normal, blink, baseFlashing, guiTicks);
                    currentX -= OVERLAP;
                }
            }
            if (effectCount == 0) {
                ModUtils.drawIconWithFlash(graphics, baseX, yStart, 9,
                    baseNormal, baseBlinking, baseFlashing, guiTicks);
            }
        } else {
            int currentX = baseX - effectCount * OVERLAP;
            for (EffectIcon e : effects) {
                if (e.condition.getAsBoolean()) {
                    ResourceLocation blink = e.blinking != null ? e.blinking : e.normal;
                    ModUtils.drawIconWithFlash(graphics, currentX, yStart, 9,
                        e.normal, blink, baseFlashing, guiTicks);
                    currentX += OVERLAP;
                }
            }
            if (effectCount == 0) {
                ModUtils.drawIconWithFlash(graphics, baseX, yStart, 9,
                    baseNormal, baseBlinking, baseFlashing, guiTicks);
            }
        }
    }
}
