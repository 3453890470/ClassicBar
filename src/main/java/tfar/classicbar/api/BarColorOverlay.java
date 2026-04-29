package tfar.classicbar.api;

import tfar.classicbar.util.Color;
import tfar.classicbar.util.ColorUtils;

import java.util.Locale;

public record BarColorOverlay(String rawValue, Color overlayColor, float blendFactor) {

    public static final String DEFAULT_CONFIG_VALUE = "#00FFFFFF";
    private static final BarColorOverlay NONE = new BarColorOverlay(DEFAULT_CONFIG_VALUE, Color.from(0xFF, 0xFF, 0xFF), 0.0F);

    public static BarColorOverlay none() {
        return NONE;
    }

    public static BarColorOverlay fromConfigValue(String rawValue) {
        ColorUtils.ParsedHexColor parsedHexColor = ColorUtils.parseHexColor(rawValue);
        if (parsedHexColor == null) {
            return none();
        }

        float blendFactor = isSixDigitColor(rawValue) ? 1.0F : parsedHexColor.alphaAsFloat();
        return new BarColorOverlay(normalize(rawValue), parsedHexColor.color(), blendFactor);
    }

    public Color applyTo(Color baseColor) {
        if (blendFactor <= 0.0F) {
            return baseColor;
        }
        if (blendFactor >= 1.0F) {
            return overlayColor;
        }
        return baseColor.colorBlend(overlayColor, blendFactor);
    }

    public void color2Gl(Color baseColor) {
        applyTo(baseColor).color2Gl();
    }

    public void color2Gla(Color baseColor, float alpha) {
        applyTo(baseColor).color2Gla(alpha);
    }

    private static boolean isSixDigitColor(String rawValue) {
        String normalizedHex = normalizeHex(rawValue);
        return normalizedHex != null && normalizedHex.length() == 6;
    }

    private static String normalize(String rawValue) {
        String normalizedHex = normalizeHex(rawValue);
        if (normalizedHex == null) {
            return DEFAULT_CONFIG_VALUE;
        }
        return "#" + normalizedHex.toUpperCase(Locale.ROOT);
    }

    private static String normalizeHex(String rawValue) {
        if (rawValue == null) {
            return null;
        }

        String normalized = rawValue.trim();
        if (normalized.startsWith("#")) {
            normalized = normalized.substring(1);
        }

        if (normalized.length() != 6 && normalized.length() != 8) {
            return null;
        }

        return normalized;
    }
}
