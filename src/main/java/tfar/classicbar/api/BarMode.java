package tfar.classicbar.api;

import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.TranslatableEnum;

public enum BarMode implements TranslatableEnum {
    OVERRIDE("classicbar.config.enum.bar_mode.override"),
    DISABLED("classicbar.config.enum.bar_mode.disabled");

    private final String translationKey;

    BarMode(String translationKey) {
        this.translationKey = translationKey;
    }

    public String translationKey() {
        return translationKey;
    }

    @Override
    public Component getTranslatedName() {
        return Component.translatable(translationKey);
    }

    public boolean rendersClassicBar() {
        return this != DISABLED;
    }

    public boolean cancelsVanillaLayer() {
        return this == OVERRIDE;
    }
}
