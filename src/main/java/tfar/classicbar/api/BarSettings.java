package tfar.classicbar.api;

import net.minecraft.resources.ResourceLocation;
import tfar.classicbar.api.TextFormat;

public class BarSettings {
    public boolean show_text;
    public ResourceLocation icon;
    public BarMode mode;
    public BarColorOverlay color_overlay;
    public TextFormat textFormat;

    public BarSettings() {
        this(false, null, BarMode.DISABLED, BarColorOverlay.none(), TextFormat.CURRENT_ONLY);
    }

    public BarSettings(boolean showText, ResourceLocation icon) {
        this(showText, icon, BarMode.OVERRIDE, BarColorOverlay.none(), TextFormat.CURRENT_ONLY);
    }

    public BarSettings(boolean showText, ResourceLocation icon, BarMode mode, BarColorOverlay colorOverlay) {
        this(showText, icon, mode, colorOverlay, TextFormat.CURRENT_ONLY);
    }

    public BarSettings(boolean showText, ResourceLocation icon, BarMode mode, BarColorOverlay colorOverlay, TextFormat textFormat) {
        this.show_text = showText;
        this.icon = icon;
        this.mode = mode;
        this.color_overlay = colorOverlay;
        this.textFormat = textFormat;
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
        copy.textFormat = textFormat;
        return copy;
    }

}
