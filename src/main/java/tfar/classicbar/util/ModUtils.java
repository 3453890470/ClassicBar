package tfar.classicbar.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.resources.BarIcons;

public class ModUtils {
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

  public static double getWidth(double d1, double d2) {
    double ratio = BarOverlayImpl.WIDTH * d1 / d2;
    return Math.ceil(ratio);
  }

  public static int getStringLength(String s) {
    return fontRenderer.width(s);
  }

  public static void drawStringOnHUD(GuiGraphics stack, String string, int xOffset, int yOffset, int color) {
   /* double scale = numbers.numberScale;
    GlStateManager.pushMatrix();
    GlStateManager.scale(scale, scale, 1);
    xOffset /= scale;
    yOffset /= scale;
    int l = fontRenderer.getStringWidth(string);
    xOffset += (left) ? .4*l * (1 - scale) / scale : 0;
    GlStateManager.translate(16 * (1 - scale) / scale, 14 * (1 - scale) / scale, 0);*/

    xOffset += 2;
    yOffset += 2;

    stack.drawString(Minecraft.getInstance().font,string, xOffset, yOffset, color,true);
  }
}
