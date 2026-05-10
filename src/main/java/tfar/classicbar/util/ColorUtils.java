package tfar.classicbar.util;

import net.minecraft.util.Mth;
import tfar.classicbar.config.ClassicBarsConfig;
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.impl.overlays.vanilla.HarmType;

import java.util.List;


public class ColorUtils {
    public static Color hex2Color(String s) {
        ParsedHexColor parsedHexColor = parseHexColor(s);
        return parsedHexColor != null ? parsedHexColor.color() : Color.BLACK;
    }

    public static ParsedHexColor parseHexColor(String s) {
        if (s == null) {
            return null;
        }

        String normalized = s.trim();
        if (normalized.startsWith("#")) {
            normalized = normalized.substring(1);
        }

        if (normalized.length() != 6 && normalized.length() != 8) {
            return null;
        }

        try {
            long value = Long.parseLong(normalized, 16);
            int alpha;
            int red;
            int green;
            int blue;

            if (normalized.length() == 6) {
                alpha = 0xFF;
                red = (int) ((value >> 16) & 0xFF);
                green = (int) ((value >> 8) & 0xFF);
                blue = (int) (value & 0xFF);
            } else {
                alpha = (int) ((value >> 24) & 0xFF);
                red = (int) ((value >> 16) & 0xFF);
                green = (int) ((value >> 8) & 0xFF);
                blue = (int) (value & 0xFF);
            }

            return new ParsedHexColor(Color.from(red, green, blue), alpha);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    public static Color calculateScaledColor(double d1, double d2, HarmType harmType) {
        double d3 = (d1 / d2);

        List<Color> colorCodes;
        List<? extends Double> colorFractions;

        switch (harmType) {
            case NONE: colorCodes = ConfigCache.normal;
            colorFractions = ClassicBarsConfig.normalFractions.get(); break;
            case POISON: colorCodes = ConfigCache.poison;
                colorFractions = ClassicBarsConfig.poisonedFractions.get(); break;
            case WITHER: colorCodes = ConfigCache.wither;
                colorFractions = ClassicBarsConfig.witheredFractions.get(); break;
            case FROZEN:return ConfigCache.frozenHealth;
            default: return Color.BLACK;
        }

        if (colorCodes.size() != colorFractions.size()) return Color.BLACK;
        int i1 = colorFractions.size() - 1;
        int i3 = 0;
        for (int i2 = 0; i2 < i1; i2++) {
            if (d3 < colorFractions.get(i2)) break;
            i3++;
        }

        //return first color in the list if health is too low
        if (d3 <= colorFractions.get(0))
            return colorCodes.get(0);
        //return last color in the list if health is too high
        if (d3 >= colorFractions.get(colorFractions.size() - 1))
            return colorCodes.get(colorCodes.size() - 1);

        Color c1 = colorCodes.get(i3 - 1);
        Color c2 = colorCodes.get(i3);

        double d4 = Mth.inverseLerp(d3,colorFractions.get(i3-1),colorFractions.get(i3));
        return c1.colorBlend(c2, (float) d4);
    }

    public record ParsedHexColor(Color color, int alpha) {
        public float alphaAsFloat() {
            return alpha / 255.0F;
        }
    }

}
