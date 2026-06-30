package tfar.classicbar.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.client.renderer.RenderPipelines;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.resources.BarIcons;
import java.util.List;
import java.util.function.BooleanSupplier;

public class ModUtils {

    public record EffectIcon(
        BooleanSupplier condition,
        Identifier normal,
        Identifier blinking
    ) {}
    public static final Minecraft mc = Minecraft.getInstance();
    private static final Font fontRenderer = mc.font;
    public static Identifier CURRENT_TEXTURE = BarIcons.FALLBACK;

    public static void drawTexturedModalRect(GuiGraphicsExtractor stack, double x, int y, int textureX, int textureY, double width, int height) {
        stack.blit(RenderPipelines.GUI_TEXTURED, CURRENT_TEXTURE, (int) x, y, textureX, textureY, (int) width, height, 256, 256, Color.getCurrentBlitColor());
    }

    public static void drawStandaloneIcon(GuiGraphicsExtractor stack, int x, int y, int size) {
        stack.blit(RenderPipelines.GUI_TEXTURED, CURRENT_TEXTURE, x, y, 0, 0, size, size, size, size, -1);
    }

    public static void drawIconWithTexture(GuiGraphicsExtractor stack, int x, int y, int size, Identifier texture) {
        Identifier prev = CURRENT_TEXTURE;
        CURRENT_TEXTURE = texture;
        drawStandaloneIcon(stack, x, y, size);
        CURRENT_TEXTURE = prev;
    }

    public static void drawIconWithFlash(GuiGraphicsExtractor stack, int x, int y, int size,
                                        Identifier normal, Identifier blinking,
                                        boolean flashing, int guiTicks) {
        Identifier prev = CURRENT_TEXTURE;
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

    public static void drawStringOnHUD(GuiGraphicsExtractor stack, String string, int xOffset, int yOffset, int color) {
        xOffset += 2;
        yOffset += 2;
        // FontDescription API changed in MC 26.1.2; using default font for now
        stack.text(Minecraft.getInstance().font, string, xOffset, yOffset, color, true);
    }

    public static void renderEffectIcons(
        GuiGraphicsExtractor graphics,
        int baseX,
        int yStart,
        boolean rightHandSide,
        boolean baseFlashing,
        int guiTicks,
        Identifier baseNormal,
        Identifier baseBlinking,
        List<EffectIcon> effects
    ) {
        final int OVERLAP = 4;
        int effectCount = 0;
        for (EffectIcon e : effects) {
            if (e.condition.getAsBoolean()) effectCount++;
        }
        if (rightHandSide) {
            // RHS: bar extends LEFT from icon. Stack RIGHTWARD (away from bar).
            int currentX = baseX;
            for (EffectIcon e : effects) {
                if (e.condition.getAsBoolean()) {
                    Identifier blink = e.blinking != null ? e.blinking : e.normal;
                    ModUtils.drawIconWithFlash(graphics, currentX, yStart, 9,
                        e.normal, blink, baseFlashing, guiTicks);
                    currentX += OVERLAP;
                }
            }
            if (effectCount == 0) {
                ModUtils.drawIconWithFlash(graphics, baseX, yStart, 9,
                    baseNormal, baseBlinking, baseFlashing, guiTicks);
            }
        } else {
            // LHS: bar extends RIGHT from icon. Stack LEFTWARD (away from bar).
            int currentX = baseX;
            for (EffectIcon e : effects) {
                if (e.condition.getAsBoolean()) {
                    Identifier blink = e.blinking != null ? e.blinking : e.normal;
                    ModUtils.drawIconWithFlash(graphics, currentX, yStart, 9,
                        e.normal, blink, baseFlashing, guiTicks);
                    currentX -= OVERLAP;
                }
            }
            if (effectCount == 0) {
                ModUtils.drawIconWithFlash(graphics, baseX, yStart, 9,
                    baseNormal, baseBlinking, baseFlashing, guiTicks);
            }
        }
    }
}
