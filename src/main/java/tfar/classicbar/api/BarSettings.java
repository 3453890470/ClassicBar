package tfar.classicbar.api;

import net.minecraft.resources.ResourceLocation;

public class BarSettings {
    public boolean show_text;
    public ResourceLocation icon;
    public BarMode mode;
    public BarColorOverlay color_overlay;

    public BarSettings() {
        this(false, null, BarMode.DISABLED, BarColorOverlay.none());
    }

    public BarSettings(boolean showText, ResourceLocation icon) {
        this(showText, icon, BarMode.OVERRIDE, BarColorOverlay.none());
    }

    public BarSettings(boolean showText, ResourceLocation icon, BarMode mode, BarColorOverlay colorOverlay) {
        this.show_text = showText;
        this.icon = icon;
        this.mode = mode;
        this.color_overlay = colorOverlay;
    }

    public boolean rendersClassicBar() {
        return mode.rendersClassicBar();
    }

    public boolean cancelsVanillaLayer() {
        return mode.cancelsVanillaLayer();
    }

    public BarSettings copy() {
        BarSettings copy = new BarSettings();
        copy.show_text = show_text;
        copy.icon = icon;
        copy.mode = mode;
        copy.color_overlay = color_overlay;
        return copy;
    }

}
