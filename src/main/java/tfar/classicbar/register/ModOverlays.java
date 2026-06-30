package tfar.classicbar.register;

import net.neoforged.fml.ModList;
import tfar.classicbar.ClassicBar;
import tfar.classicbar.api.BarOverlay;
import tfar.classicbar.client.EventHandler;
import tfar.classicbar.impl.overlays.vanilla.Absorption;
import tfar.classicbar.impl.overlays.vanilla.Air;
import tfar.classicbar.impl.overlays.vanilla.Armor;
import tfar.classicbar.impl.overlays.vanilla.ArmorToughness;
import tfar.classicbar.impl.overlays.vanilla.Health;
import tfar.classicbar.impl.overlays.vanilla.Hunger;
import tfar.classicbar.impl.overlays.vanilla.MountHealth;
import tfar.classicbar.impl.overlays.mod.Blood;
import tfar.classicbar.impl.overlays.mod.ForbiddenHunger;
import tfar.classicbar.impl.overlays.mod.Thirst;

/**
 * Centralized registry for all ClassicBar overlay instances.
 * <p>
 * Follows the pattern from ExtraHostileNeuralNetworks' {@code register/} package:
 * all overlay definitions are collected here instead of being scattered across
 * event handlers and initialization code.
 * <p>
 * Vanilla overlays are always registered; mod-compat overlays are registered
 * conditionally based on the presence of their target mod.
 */
public final class ModOverlays {

    private ModOverlays() {
    }

    /**
     * Creates and registers all overlay instances.
     * <p>
     * Safe to call multiple times — overlays are only registered once via
     * {@link EventHandler#registerAll(BarOverlay...)} and
     * {@link EventHandler#register(BarOverlay)}.
     */
    public static void register() {
        ClassicBar.logger.info("Registering Vanilla Overlays");
        EventHandler.registerAll(
                new Health(),
                new Armor(),
                new Absorption(),
                new Hunger(),
                new ArmorToughness(),
                new MountHealth(),
                new Air()
        );

        // Mod-compat overlays (conditional on mod presence)
        if (ModList.get().isLoaded("vampirism")) {
            ClassicBar.logger.info("Registering vampirism compat overlay (Blood)");
            EventHandler.register(new Blood());
        }
        if (ModList.get().isLoaded("enigmaticlegacyplus")) {
            ClassicBar.logger.info("Registering enigmaticlegacyplus compat overlay (ForbiddenHunger)");
            EventHandler.register(new ForbiddenHunger());
        }
        if (ModList.get().isLoaded("toughasnails") || ModList.get().isLoaded("thirst")) {
            ClassicBar.logger.info("Registering thirst compat overlay (Thirst)");
            EventHandler.register(new Thirst());
        }
    }
}
