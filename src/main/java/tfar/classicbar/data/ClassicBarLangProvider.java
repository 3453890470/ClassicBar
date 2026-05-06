package tfar.classicbar.data;

import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.bus.api.SubscribeEvent;
import tfar.classicbar.ClassicBar;
import net.minecraft.data.PackOutput;

public class ClassicBarLangProvider extends LanguageProvider {

    public ClassicBarLangProvider(PackOutput output, String modid, String locale) {
        super(output, modid, locale);
    }

    @Override
    protected void addTranslations() {
        // === Config sections ===
        add("classicbar.config.section.general", "General");
        add("classicbar.config.section.general.tooltip", "Core ClassicBar client settings.");
        add("classicbar.config.section.general.button", "Open General");
        add("classicbar.config.section.layout", "Layout");
        add("classicbar.config.section.layout.tooltip", "Control the placement of each bar");
        add("classicbar.config.section.layout.button", "Open Layout");
        add("classicbar.config.section.bars", "Bars");
        add("classicbar.config.section.bars.tooltip", "Per-bar ClassicBar settings for mode, icons, text, and color overlays.");
        add("classicbar.config.section.bars.button", "Open Bars");
        add("classicbar.config.section.mod_support", "Mod Support");
        add("classicbar.config.section.mod_support.tooltip", "Compatibility and reserved support for third-party mods.");
        add("classicbar.config.section.mod_support.button", "Open Mod Support");

        // === General config ===
        add("classicbar.config.general.display_icons", "Show icons");
        add("classicbar.config.general.display_icons.tooltip", "Render ClassicBar's standalone icon PNG beside enabled bars.");
        add("classicbar.config.general.display_toughness_bar", "Show armor toughness bar");
        add("classicbar.config.general.display_toughness_bar.tooltip", "Allow the armor toughness overlay to render when the player has armor toughness.");
        add("classicbar.config.general.full_absorption_bar", "Full absorption bar width");
        add("classicbar.config.general.full_absorption_bar.tooltip", "Keep the absorption bar at full width instead of fitting it to the current amount.");
        add("classicbar.config.general.full_armor_bar", "Full armor bar width");
        add("classicbar.config.general.full_armor_bar.tooltip", "Keep the armor bar at full width instead of fitting it to the current amount.");
        add("classicbar.config.general.full_toughness_bar", "Full armor toughness bar width");
        add("classicbar.config.general.full_toughness_bar.tooltip", "Keep the armor toughness bar at full width instead of fitting it to the current amount.");
        add("classicbar.config.general.display_low_armor_warning", "Low armor warning");
        add("classicbar.config.general.display_low_armor_warning.tooltip", "Flash the armor bar when equipped armor pieces are almost broken.");
        add("classicbar.config.general.show_saturation_bar", "Show saturation overlay");
        add("classicbar.config.general.show_saturation_bar.tooltip", "Render the saturation fill on top of the food bar.");
        add("classicbar.config.general.show_hydration_bar", "Reserved hydration overlay");
        add("classicbar.config.general.show_hydration_bar.tooltip", "Reserved for future compatibility work. No active effect in this build.");
        add("classicbar.config.general.nourishment_bar_color", "Nourishment Bar Color");
        add("classicbar.config.general.nourishment_bar_color.tooltip", "Bar color when Nourishment (FarmersDelight) is active");
        add("classicbar.config.general.satiated_shield_bar_color", "Satiated Shield Bar Color");
        add("classicbar.config.general.satiated_shield_bar_color.tooltip", "Bar color when Satiated Shield (Kaleidoscope Cookery) is active");
        add("classicbar.config.general.forbidden_curse_bar_color", "Forbidden curse bar color");
        add("classicbar.config.general.forbidden_curse_bar_color.tooltip", "Color of the hunger bar when under the Forbidden Curse (EnigmaticLegacy+).");
        add("classicbar.config.general.disable_fd_nourishment_overlay", "Disable FD Nourishment Overlay");
        add("classicbar.config.general.disable_fd_nourishment_overlay.tooltip", "Hides Farmer's Delight's golden nourishment overlay on the food bar to prevent overlapping with Classic Bar");
        add("classicbar.config.general.show_held_food_overlay", "Reserved held food overlay");
        add("classicbar.config.general.show_held_food_overlay.tooltip", "Reserved for future compatibility work. No active effect in this build.");
        add("classicbar.config.general.show_held_drink_overlay", "Reserved held drink overlay");
        add("classicbar.config.general.show_held_drink_overlay.tooltip", "Reserved for future compatibility work. No active effect in this build.");
        add("classicbar.config.general.show_exhaustion_overlay", "Reserved exhaustion overlay");
        add("classicbar.config.general.show_exhaustion_overlay.tooltip", "Reserved for future compatibility work. No active effect in this build.");
        add("classicbar.config.general.show_food_preview", "Show Food Preview");
        add("classicbar.config.general.show_food_preview.tooltip", "Display a preview of hunger and saturation restoration when holding food items");
        add("classicbar.config.general.show_thirst_exhaustion_overlay", "Reserved thirst exhaustion overlay");
        add("classicbar.config.general.show_thirst_exhaustion_overlay.tooltip", "Reserved for future compatibility work. No active effect in this build.");
        add("classicbar.config.general.transition_speed", "Reserved transition speed");
        add("classicbar.config.general.transition_speed.tooltip", "Animation speed used by reserved compat overlays when that work is restored later.");
        add("classicbar.config.general.hunger_bar_color", "Hunger bar color");
        add("classicbar.config.general.hunger_bar_color.tooltip", "Base fill color used by the hunger bar.");
        add("classicbar.config.general.hunger_bar_debuff_color", "Hunger debuff color");
        add("classicbar.config.general.hunger_bar_debuff_color.tooltip", "Fill color used by the hunger bar while the Hunger debuff is active.");
        add("classicbar.config.general.thirst_bar_color", "Reserved thirst bar color");
        add("classicbar.config.general.thirst_bar_color.tooltip", "Reserved for future thirst compat. This replaces the legacy misspelled thirstr_bar_color key.");
        add("classicbar.config.general.thirst_bar_debuff_color", "Reserved thirst debuff color");
        add("classicbar.config.general.thirst_bar_debuff_color.tooltip", "Reserved for future thirst compat while a thirst debuff is active.");
        add("classicbar.config.general.air_bar_color", "Air bar color");
        add("classicbar.config.general.air_bar_color.tooltip", "Base fill color used by the air bar.");
        add("classicbar.config.general.saturation_bar_color", "Saturation color");
        add("classicbar.config.general.saturation_bar_color.tooltip", "Fill color used by the saturation overlay.");
        add("classicbar.config.general.saturation_bar_debuff_color", "Saturation debuff color");
        add("classicbar.config.general.saturation_bar_debuff_color.tooltip", "Fill color used by the saturation overlay while the Hunger debuff is active.");
        add("classicbar.config.general.hydration_bar_color", "Reserved hydration color");
        add("classicbar.config.general.hydration_bar_color.tooltip", "Reserved for future compatibility work. No active effect in this build.");
        add("classicbar.config.general.hydration_bar_debuff_color", "Reserved hydration debuff color");
        add("classicbar.config.general.hydration_bar_debuff_color.tooltip", "Reserved for future hydration compat while a debuff is active.");
        add("classicbar.config.general.lava_bar_color", "Reserved lava color");
        add("classicbar.config.general.lava_bar_color.tooltip", "Reserved for future compat overlays.");
        add("classicbar.config.general.flight_bar_color", "Reserved flight color");
        add("classicbar.config.general.flight_bar_color.tooltip", "Reserved for future compat overlays.");
        add("classicbar.config.general.armor_color_values", "Armor gradient colors");
        add("classicbar.config.general.armor_color_values.tooltip", "Color list used by the armor bar gradient.");
        add("classicbar.config.general.armor_toughness_color_values", "Armor toughness gradient colors");
        add("classicbar.config.general.armor_toughness_color_values.tooltip", "Color list used by the armor toughness bar gradient.");
        add("classicbar.config.general.absorption_color_values", "Absorption gradient colors");
        add("classicbar.config.general.absorption_color_values.tooltip", "Color list used by the absorption bar gradient.");
        add("classicbar.config.general.absorption_poison_color_values", "Poisoned absorption colors");
        add("classicbar.config.general.absorption_poison_color_values.tooltip", "Color list used by the absorption bar while poisoned.");
        add("classicbar.config.general.absorption_wither_color_values", "Withered absorption colors");
        add("classicbar.config.general.absorption_wither_color_values.tooltip", "Color list used by the absorption bar while withered.");
        add("classicbar.config.general.normal_colors", "Normal health colors");
        add("classicbar.config.general.normal_colors.tooltip", "Health gradient colors for the normal health state.");
        add("classicbar.config.general.normal_fractions", "Normal health breakpoints");
        add("classicbar.config.general.normal_fractions.tooltip", "Fraction breakpoints used by the normal health gradient. Values must stay between 0 and 1.");
        add("classicbar.config.general.poisoned_colors", "Poisoned health colors");
        add("classicbar.config.general.poisoned_colors.tooltip", "Health gradient colors for the poisoned health state.");
        add("classicbar.config.general.poisoned_fractions", "Poisoned health breakpoints");
        add("classicbar.config.general.poisoned_fractions.tooltip", "Fraction breakpoints used by the poisoned health gradient. Values must stay between 0 and 1.");
        add("classicbar.config.general.withered_colors", "Withered health colors");
        add("classicbar.config.general.withered_colors.tooltip", "Health gradient colors for the withered health state.");
        add("classicbar.config.general.withered_fractions", "Withered health breakpoints");
        add("classicbar.config.general.withered_fractions.tooltip", "Fraction breakpoints used by the withered health gradient. Values must stay between 0 and 1.");
        add("classicbar.config.general.frozen_health_color", "Frozen health color");
        add("classicbar.config.general.frozen_health_color.tooltip", "Health color used when the player is fully frozen.");
        add("classicbar.config.general.health_poison_overlay_color", "Poison overlay color");
        add("classicbar.config.general.health_poison_overlay_color.tooltip", "Overlay tint applied to the health bar while poisoned. #RRGGBB for opaque, #AARRGGBB for alpha.");
        add("classicbar.config.general.health_wither_overlay_color", "Wither overlay color");
        add("classicbar.config.general.health_wither_overlay_color.tooltip", "Overlay tint applied to the health bar while withered. #RRGGBB for opaque, #AARRGGBB for alpha.");
        add("classicbar.config.general.health_frozen_overlay_color", "Frozen overlay color");
        add("classicbar.config.general.health_frozen_overlay_color.tooltip", "Overlay tint applied to the health bar while frozen. #RRGGBB for opaque, #AARRGGBB for alpha.");

        // === Layout config ===
        add("classicbar.config.layout.placement_health", "Health");
        add("classicbar.config.layout.placement_armor", "Armor");
        add("classicbar.config.layout.placement_absorption", "Absorption");
        add("classicbar.config.layout.placement_food", "Food");
        add("classicbar.config.layout.placement_armor_toughness", "Armor Toughness");
        add("classicbar.config.layout.placement_health_mount", "Mount Health");
        add("classicbar.config.layout.placement_air", "Air");
        add("classicbar.config.layout.placement_blood", "Blood");
        add("classicbar.config.layout.placement_forbidden_hunger", "Forbidden Hunger");
        add("classicbar.config.layout.placement_thirst_level", "Thirst");
        add("classicbar.config.layout.placement_thirst_level.tooltip", "Placement for the thirst bar");
        add("classicbar.config.layout.placement_value", "Placement");
        add("classicbar.config.layout.priority_value", "Priority");

        // === Bars: Health ===
        add("classicbar.config.bars.health", "Health bar");
        add("classicbar.config.bars.health.tooltip", "Settings for the health overlay.");
        add("classicbar.config.bars.health.show_text", "Show health text");
        add("classicbar.config.bars.health.show_text.tooltip", "Render the numeric health value next to the health bar.");
        add("classicbar.config.bars.health.icon", "Health icon");
        add("classicbar.config.bars.health.icon.tooltip", "ClassicBar icon resource for the health overlay. Must stay inside classicbar:textures/gui/icons/*.png.");
        add("classicbar.config.bars.health.mode", "Health mode");
        add("classicbar.config.bars.health.mode.tooltip", "Health and absorption share vanilla PLAYER_HEALTH. If there are no absorption hearts to preserve, Health OVERRIDE still hides vanilla hearts. If visible absorption hearts are present, ClassicBar only cancels PLAYER_HEALTH when both shared bars are OVERRIDE, active in layout, and currently rendering; otherwise the whole vanilla player health layer stays visible to preserve health and absorption info.");
        add("classicbar.config.bars.health.color_overlay", "Health color overlay");
        add("classicbar.config.bars.health.color_overlay.tooltip", "#RRGGBB replaces all fill passes for this bar, which can hide gradients and layered fills. #AARRGGBB alpha-blends with the original fill colors instead of changing render transparency, and health effect overlays can still render on top.");
        add("classicbar.config.bars.health.text_format", "Health text format");
        add("classicbar.config.bars.health.text_format.tooltip", "Controls how the health value is displayed. current_only: 12, current_max: 12 / 20, percent_max: 60% / 20.");

        // === Bars: Armor ===
        add("classicbar.config.bars.armor", "Armor bar");
        add("classicbar.config.bars.armor.tooltip", "Settings for the armor overlay.");
        add("classicbar.config.bars.armor.show_text", "Show armor text");
        add("classicbar.config.bars.armor.show_text.tooltip", "Render the numeric armor value next to the armor bar.");
        add("classicbar.config.bars.armor.icon", "Armor icon");
        add("classicbar.config.bars.armor.icon.tooltip", "ClassicBar icon resource for the armor overlay. Must stay inside classicbar:textures/gui/icons/*.png.");
        add("classicbar.config.bars.armor.mode", "Armor mode");
        add("classicbar.config.bars.armor.mode.tooltip", "OVERRIDE hides vanilla armor, DISABLED turns off the ClassicBar armor overlay.");
        add("classicbar.config.bars.armor.color_overlay", "Armor color overlay");
        add("classicbar.config.bars.armor.color_overlay.tooltip", "#RRGGBB replaces all fill passes for this bar, which can hide gradient detail. #AARRGGBB alpha-blends with the original fill colors instead of changing render transparency.");
        add("classicbar.config.bars.armor.text_format", "Armor text format");
        add("classicbar.config.bars.armor.text_format.tooltip", "Controls how the armor value is displayed. current_only: 12, current_max: 12 / 20, percent_max: 60% / 20.");

        // === Bars: Absorption ===
        add("classicbar.config.bars.absorption", "Absorption bar");
        add("classicbar.config.bars.absorption.tooltip", "Settings for the absorption overlay.");
        add("classicbar.config.bars.absorption.show_text", "Show absorption text");
        add("classicbar.config.bars.absorption.show_text.tooltip", "Render the numeric absorption value next to the absorption bar.");
        add("classicbar.config.bars.absorption.icon", "Absorption icon");
        add("classicbar.config.bars.absorption.icon.tooltip", "ClassicBar icon resource for the absorption overlay. Must stay inside classicbar:textures/gui/icons/*.png.");
        add("classicbar.config.bars.absorption.mode", "Absorption mode");
        add("classicbar.config.bars.absorption.mode.tooltip", "Absorption also shares vanilla PLAYER_HEALTH with health. If there are no visible absorption hearts, Health OVERRIDE can still hide vanilla hearts on its own. When visible absorption hearts are present, ClassicBar only cancels PLAYER_HEALTH if both health and absorption are OVERRIDE, active in layout, and currently rendering; DISABLED, or layout removal keeps the whole vanilla player health layer visible.");
        add("classicbar.config.bars.absorption.color_overlay", "Absorption color overlay");
        add("classicbar.config.bars.absorption.color_overlay.tooltip", "#RRGGBB replaces all fill passes for this bar, which can hide gradients and layered absorption fills. #AARRGGBB alpha-blends with the original fill colors instead of changing render transparency.");
        add("classicbar.config.bars.absorption.text_format", "Absorption text format");
        add("classicbar.config.bars.absorption.text_format.tooltip", "Controls how the absorption value is displayed. current_only: 12, current_max: 12 / 20, percent_max: 60% / 20.");

        // === Bars: Food ===
        add("classicbar.config.bars.food", "Food bar");
        add("classicbar.config.bars.food.tooltip", "Settings for the food overlay.");
        add("classicbar.config.bars.food.show_text", "Show food text");
        add("classicbar.config.bars.food.show_text.tooltip", "Render the numeric food value next to the food bar.");
        add("classicbar.config.bars.food.icon", "Food icon");
        add("classicbar.config.bars.food.icon.tooltip", "ClassicBar icon resource for the food overlay. Must stay inside classicbar:textures/gui/icons/*.png.");
        add("classicbar.config.bars.food.mode", "Food mode");
        add("classicbar.config.bars.food.mode.tooltip", "OVERRIDE hides vanilla food, DISABLED turns off the ClassicBar food overlay.");
        add("classicbar.config.bars.food.color_overlay", "Food color overlay");
        add("classicbar.config.bars.food.color_overlay.tooltip", "#RRGGBB replaces all fill passes for this bar, including the saturation pass, which can hide vanilla-style layering. #AARRGGBB alpha-blends with the original fill colors instead of changing render transparency.");
        add("classicbar.config.bars.food.text_format", "Food text format");
        add("classicbar.config.bars.food.text_format.tooltip", "Controls how the food value is displayed. current_only: 12, current_max: 12 / 20, percent_max: 60% / 20.");

        // === Bars: Armor Toughness ===
        add("classicbar.config.bars.armor_toughness", "Armor toughness bar");
        add("classicbar.config.bars.armor_toughness.tooltip", "Settings for the armor toughness overlay.");
        add("classicbar.config.bars.armor_toughness.show_text", "Show armor toughness text");
        add("classicbar.config.bars.armor_toughness.show_text.tooltip", "Render the numeric armor toughness value next to the armor toughness bar.");
        add("classicbar.config.bars.armor_toughness.icon", "Armor toughness icon");
        add("classicbar.config.bars.armor_toughness.icon.tooltip", "ClassicBar icon resource for the armor toughness overlay. Must stay inside classicbar:textures/gui/icons/*.png.");
        add("classicbar.config.bars.armor_toughness.mode", "Armor toughness mode");
        add("classicbar.config.bars.armor_toughness.mode.tooltip", "OVERRIDE renders ClassicBar armor toughness because vanilla has no matching layer. DISABLED turns it off.");
        add("classicbar.config.bars.armor_toughness.color_overlay", "Armor toughness color overlay");
        add("classicbar.config.bars.armor_toughness.color_overlay.tooltip", "#RRGGBB replaces all fill passes for this bar, which can hide gradient detail. #AARRGGBB alpha-blends with the original fill colors instead of changing render transparency.");
        add("classicbar.config.bars.armor_toughness.text_format", "Armor toughness text format");
        add("classicbar.config.bars.armor_toughness.text_format.tooltip", "Controls how the armor toughness value is displayed. current_only: 12, current_max: 12 / 20, percent_max: 60% / 20.");

        // === Bars: Mount Health ===
        add("classicbar.config.bars.health_mount", "Mount health bar");
        add("classicbar.config.bars.health_mount.tooltip", "Settings for the mount health overlay.");
        add("classicbar.config.bars.health_mount.show_text", "Show mount health text");
        add("classicbar.config.bars.health_mount.show_text.tooltip", "Render the numeric mount health value next to the mount health bar.");
        add("classicbar.config.bars.health_mount.icon", "Mount health icon");
        add("classicbar.config.bars.health_mount.icon.tooltip", "ClassicBar icon resource for the mount health overlay. Must stay inside classicbar:textures/gui/icons/*.png.");
        add("classicbar.config.bars.health_mount.mode", "Mount health mode");
        add("classicbar.config.bars.health_mount.mode.tooltip", "OVERRIDE hides vanilla mount health, DISABLED turns off the ClassicBar mount health overlay.");
        add("classicbar.config.bars.health_mount.color_overlay", "Mount health color overlay");
        add("classicbar.config.bars.health_mount.color_overlay.tooltip", "#RRGGBB replaces all fill passes for this bar, which can hide gradients and layered fills. #AARRGGBB alpha-blends with the original fill colors instead of changing render transparency.");
        add("classicbar.config.bars.health_mount.text_format", "Mount health text format");
        add("classicbar.config.bars.health_mount.text_format.tooltip", "Controls how the mount health value is displayed. current_only: 12, current_max: 12 / 20, percent_max: 60% / 20.");

        // === Bars: Air ===
        add("classicbar.config.bars.air", "Air bar");
        add("classicbar.config.bars.air.tooltip", "Settings for the air overlay.");
        add("classicbar.config.bars.air.show_text", "Show air text");
        add("classicbar.config.bars.air.show_text.tooltip", "Render the numeric air value next to the air bar.");
        add("classicbar.config.bars.air.icon", "Air icon");
        add("classicbar.config.bars.air.icon.tooltip", "ClassicBar icon resource for the air overlay. Must stay inside classicbar:textures/gui/icons/*.png.");
        add("classicbar.config.bars.air.mode", "Air mode");
        add("classicbar.config.bars.air.mode.tooltip", "OVERRIDE hides vanilla air, DISABLED turns off the ClassicBar air overlay.");
        add("classicbar.config.bars.air.color_overlay", "Air color overlay");
        add("classicbar.config.bars.air.color_overlay.tooltip", "#RRGGBB replaces all fill passes for this bar, which can hide layered fill detail. #AARRGGBB alpha-blends with the original fill colors instead of changing render transparency.");
        add("classicbar.config.bars.air.text_format", "Air text format");
        add("classicbar.config.bars.air.text_format.tooltip", "Controls how the air value is displayed. current_only: 12, current_max: 12 / 20, percent_max: 60% / 20.");

        // === Bars: Blood ===
        add("classicbar.config.bars.blood", "Blood bar");
        add("classicbar.config.bars.blood.tooltip", "Settings for the blood overlay.");
        add("classicbar.config.bars.blood.show_text", "Show blood text");
        add("classicbar.config.bars.blood.show_text.tooltip", "Render the numeric blood value next to the blood bar.");
        add("classicbar.config.bars.blood.icon", "Blood icon");
        add("classicbar.config.bars.blood.icon.tooltip", "ClassicBar icon resource for the blood overlay. Must stay inside classicbar:textures/gui/icons/*.png.");
        add("classicbar.config.bars.blood.mode", "Blood mode");
        add("classicbar.config.bars.blood.mode.tooltip", "OVERRIDE enables the ClassicBar blood overlay (requires Vampirism), DISABLED turns it off.");
        add("classicbar.config.bars.blood.color_overlay", "Blood color overlay");
        add("classicbar.config.bars.blood.color_overlay.tooltip", "#RRGGBB replaces all fill passes for this bar, which can hide fill detail. #AARRGGBB alpha-blends with the original fill colors instead of changing render transparency.");
        add("classicbar.config.bars.blood.text_format", "Blood text format");
        add("classicbar.config.bars.blood.text_format.tooltip", "Controls how the blood value is displayed. current_only: 12, current_max: 12 / 20, percent_max: 60% / 20.");

        // === Bars: Forbidden Hunger ===
        add("classicbar.config.bars.forbidden_hunger", "Forbidden Hunger bar");
        add("classicbar.config.bars.forbidden_hunger.tooltip", "Settings for the EnigmaticLegacy+ Forbidden Hunger bar.");
        add("classicbar.config.bars.forbidden_hunger.show_text", "Show forbidden hunger text");
        add("classicbar.config.bars.forbidden_hunger.show_text.tooltip", "Render the numeric value for the forbidden hunger bar.");
        add("classicbar.config.bars.forbidden_hunger.icon", "Forbidden hunger icon");
        add("classicbar.config.bars.forbidden_hunger.icon.tooltip", "ClassicBar icon resource path for the forbidden hunger bar.");
        add("classicbar.config.bars.forbidden_hunger.mode", "Forbidden hunger mode");
        add("classicbar.config.bars.forbidden_hunger.mode.tooltip", "OVERRIDE enables the ClassicBar forbidden hunger overlay (requires EnigmaticLegacy+), DISABLED disables it.");
        add("classicbar.config.bars.forbidden_hunger.color_overlay", "Forbidden hunger color overlay");
        add("classicbar.config.bars.forbidden_hunger.color_overlay.tooltip", "Overlay hex color for the forbidden hunger bar.");
        add("classicbar.config.bars.forbidden_hunger.text_format", "Forbidden hunger text format");
        add("classicbar.config.bars.forbidden_hunger.text_format.tooltip", "Controls how the numeric value is displayed for the forbidden hunger bar.");

        // === Bars: Thirst Level ===
        add("classicbar.config.bars.thirst_level", "Thirst");
        add("classicbar.config.bars.thirst_level.tooltip", "Replaces the food bar with a thirst bar when a thirst mod is loaded");
        add("classicbar.config.bars.thirst_level.mode", "thirst_level");
        add("classicbar.config.bars.thirst_level.show_text", "Show thirst value");
        add("classicbar.config.bars.thirst_level.color_overlay", "Thirst bar color");
        add("classicbar.config.bars.thirst_level.icon", "Thirst icon");
        add("classicbar.config.bars.thirst_level.text_format", "Thirst text format");

        // === Mod Support ===
        add("classicbar.config.mod_support.toughasnails", "Tough As Nails");
        add("classicbar.config.mod_support.toughasnails.tooltip", "Reserved compatibility section for Tough As Nails. No active effect in this build.");
        add("classicbar.config.mod_support.toughasnails.enabled", "Reserved Tough As Nails toggle");
        add("classicbar.config.mod_support.toughasnails.enabled.tooltip", "Enabling this toggle does not restore Tough As Nails support in this build.");
        add("classicbar.config.mod_support.vampirism", "Vampirism");
        add("classicbar.config.mod_support.vampirism.tooltip", "Compatibility section for Vampirism blood bar.");
        add("classicbar.config.mod_support.vampirism.enabled", "Vampirism Blood Bar");
        add("classicbar.config.mod_support.vampirism.enabled.tooltip", "Enable Vampirism blood bar support (enabled by default when Vampirism is installed).");
        add("classicbar.config.mod_support.parcool", "ParCool");
        add("classicbar.config.mod_support.parcool.tooltip", "Reserved compatibility section for ParCool. No active effect in this build.");
        add("classicbar.config.mod_support.parcool.enabled", "Reserved ParCool toggle");
        add("classicbar.config.mod_support.parcool.enabled.tooltip", "Enabling this toggle does not restore ParCool support in this build.");

        add("classicbar.config.mod_support.farmersdelight", "FarmersDelight Compat");
        add("classicbar.config.mod_support.farmersdelight.tooltip", "FarmersDelight compatibility for Nourishment effect detection on the food bar.");
        add("classicbar.config.mod_support.farmersdelight.enabled", "FarmersDelight Compat");
        add("classicbar.config.mod_support.farmersdelight.enabled.tooltip", "Enable Nourishment effect detection for food bar rendering");
        add("classicbar.config.mod_support.kaleidoscope_cookery", "Kaleidoscope Cookery Compat");
        add("classicbar.config.mod_support.kaleidoscope_cookery.tooltip", "Kaleidoscope Cookery compatibility for Satiated Shield effect detection on the food bar.");
        add("classicbar.config.mod_support.kaleidoscope_cookery.enabled", "Kaleidoscope Cookery Compat");
        add("classicbar.config.mod_support.kaleidoscope_cookery.enabled.tooltip", "Enable Satiated Shield effect detection for food bar rendering");
        add("classicbar.config.mod_support.enigmaticlegacyplus", "EnigmaticLegacy+");
        add("classicbar.config.mod_support.enigmaticlegacyplus.tooltip", "Compatibility section for EnigmaticLegacy+ Forbidden Hunger bar.");
        add("classicbar.config.mod_support.enigmaticlegacyplus.enabled", "EnigmaticLegacy+ Forbidden Hunger");
        add("classicbar.config.mod_support.enigmaticlegacyplus.enabled.tooltip", "Enable compatibility with EnigmaticLegacy+ Forbidden Hunger bar.");
        add("classicbar.config.mod_support.thirst", "Thirst Was Taken");
        add("classicbar.config.mod_support.thirst.tooltip", "Enable support for Thirst Was Taken mod");
        add("classicbar.config.mod_support.thirst.enabled", "Enable Thirst Was Taken support");

        // === Enum values ===
        add("classicbar.config.enum.bar_mode.override", "Override vanilla");
        add("classicbar.config.enum.bar_mode.disabled", "Disable ClassicBar");
        add("classicbar.config.enum.text_format.current_only", "Current only");
        add("classicbar.config.enum.text_format.current_max", "Current / Max");
        add("classicbar.config.enum.text_format.percent_max", "Percent / Max");
        add("classicbar.config.enum.barplacement.left", "Left");
        add("classicbar.config.enum.barplacement.right", "Right");
        add("classicbar.config.enum.barplacement.hidden", "Hidden");

        // === Top-level config keys (thirst support) ===
        add("classicbar.config.thirst_bar_color", "Thirst bar color");
        add("classicbar.config.thirst_bar_debuff_color", "Thirst bar debuff color");
        add("classicbar.config.hydration_bar_color", "Hydration bar color");
        add("classicbar.config.hydration_bar_debuff_color", "Hydration bar debuff color");
        add("classicbar.config.show_hydration_bar", "Show hydration bar");
        add("classicbar.config.show_hydration_bar.tooltip", "Shows the hydration (quench) sub-bar under the thirst bar");
        add("classicbar.config.show_held_drink_overlay", "Show held drink overlay");
        add("classicbar.config.show_held_drink_overlay.tooltip", "Shows thirst info when holding a drinkable item");
        add("classicbar.config.show_thirst_exhaustion_overlay", "Show thirst exhaustion overlay");
        add("classicbar.config.show_thirst_exhaustion_overlay.tooltip", "Shows thirst exhaustion flash effect");

        // === Debug ===
        add("classicbar.config.section.debug", "Debug");
        add("classicbar.config.debug.wither", "Wither (every 1min, 2min duration)");
        add("classicbar.config.debug.wither.tooltip", "Applies Wither II every minute for testing bar colors");
        add("classicbar.config.debug.poison", "Poison (every 1min, 2min duration)");
        add("classicbar.config.debug.poison.tooltip", "Applies Poison II every minute for testing bar colors");
        add("classicbar.config.debug.frozen", "Frozen (every 1min, 2min duration)");
        add("classicbar.config.debug.frozen.tooltip", "Applies Powder Snow freeze damage every minute");
        add("classicbar.config.debug.hunger", "Hunger (every 1min, 2min duration)");
        add("classicbar.config.debug.hunger.tooltip", "Applies Hunger II every minute for testing bar colors");
        add("classicbar.config.debug.nourishment", "Nourishment (every 1min, 2min duration)");
        add("classicbar.config.debug.nourishment.tooltip", "Applies Farmer's Delight Nourishment every minute");
        add("classicbar.config.debug.satiated_shield", "Satiated Shield (every 1min, 2min duration)");
        add("classicbar.config.debug.satiated_shield.tooltip", "Applies Kaleidoscope Cookery Satiated Shield every minute");
        add("classicbar.config.debug.vampire", "Vampire Level 1");
        add("classicbar.config.debug.vampire.tooltip", "Sets vampire level to 1, disable to revert to human");
        add("classicbar.config.debug.forbidden_curse", "Forbidden Curse");
        add("classicbar.config.debug.forbidden_curse.tooltip", "Applies Enigmatic Legacy+ Forbidden Curse, disable to remove");
    }
}
