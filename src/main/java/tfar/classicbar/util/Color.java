package tfar.classicbar.util;

/**
 * RGB color record for ClassicBar.
 * <p>
 * In MC 26.1.2, global shader color (RenderSystem.setShaderColor) has been removed.
 * Color tinting is instead applied through the per-blit color parameter (ARGB int).
 * <p>
 * Instead of setting a global shader uniform, color2Gl() and color2Gla() now store
 * the color in a static {@link #currentBlitColor} field. {@link ModUtils#drawTexturedModalRect}
 * reads this field and passes it to the 11-parameter blit() overload.
 */
public record Color(int r,int g,int b) {
    public static final Color WHITE = Color.from(0xff,0xff,0xff);
    public static final Color BLACK = Color.from(0,0,0);
    public static final Color RED = Color.from(0xff,0,0);
    public static final Color YELLOW = Color.from(0xff,0xff,0);

    /**
     * Holds the current ARGB tint color for the next blit call.
     * Default {@code -1} = {@code 0xFFFFFFFF} = opaque white (no tint).
     * Set by {@link #color2Gl()} / {@link #color2Gla(float)}.
     * Reset by {@link #reset()}.
     */
    private static int currentBlitColor = -1;

    public static Color from(int red, int green, int blue) {
        return new Color(red, green, blue);
    }

    /**
     * Stores this RGB color with full opacity as the current blit tint.
      * Legacy shader-color replacement — stores ARGB into {@link #currentBlitColor}.
     */
    public void color2Gl() {
        currentBlitColor = toBlitARGB(1.0f);
    }

    /**
     * Stores this RGB color with the given alpha as the current blit tint.
     */
    public void color2Gla(float a) {
        currentBlitColor = toBlitARGB(a);
    }

    /**
     * Resets the current blit tint to opaque white (no tint).
     */
    public static void reset() {
        currentBlitColor = -1; // 0xFFFFFFFF = opaque white
    }

    /**
     * Returns the ARGB color currently stored for the next blit call.
     */
    public static int getCurrentBlitColor() {
        return currentBlitColor;
    }

    /**
     * Converts this RGB color + alpha to an ARGB int suitable for
     * {@code GuiGraphicsExtractor.blit(..., int color)}.
     * <p>
     * Format: {@code (alpha << 24) | (red << 16) | (green << 8) | blue}
     */
    private int toBlitARGB(float alpha) {
        int a = Math.clamp((int) (alpha * 255), 0, 255);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public Color colorBlend(Color c2, float d) {
        int r = lerpInt(d, this.r, c2.r);
        int g = lerpInt(d, this.g, c2.g);
        int b = lerpInt(d, this.b, c2.b);
        return Color.from(r, g, b);
    }
    public int colorToText(){
        return this.r << 16 | this.g << 8 | this.b;
    }

    private static int lerpInt(float delta, int start, int end) {
        return start + (int) Math.floor(delta * (end - start));
    }
}
