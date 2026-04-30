package tfar.classicbar.api;

import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.TranslatableEnum;

public enum BarPlacement implements TranslatableEnum {
    LEFT("classicbar.config.enum.barplacement.left"),
    RIGHT("classicbar.config.enum.barplacement.right"),
    HIDDEN("classicbar.config.enum.barplacement.hidden");

    private final String translationKey;

    BarPlacement(String translationKey) {
        this.translationKey = translationKey;
    }

    public String translationKey() {
        return translationKey;
    }

    @Override
    public Component getTranslatedName() {
        return Component.translatable(translationKey);
    }
}
