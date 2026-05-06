package tfar.classicbar.register;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import tfar.classicbar.config.ClassicBarsConfig;

/**
 * Centralized registry for all ClassicBar configuration setup.
 * <p>
 * Handles:
 * <ul>
 *   <li>Config spec registration with the mod container</li>
 *   <li>Configuration screen factory extension point</li>
 *   <li>Config loading and reloading event listeners</li>
 * </ul>
 */
public final class ModConfigs {

    private ModConfigs() {
    }

    /**
     * Registers all config-related components.
     * <p>
     * Must be called from the mod constructor to ensure the config spec
     * is available before any config events fire.
     *
     * @param modBus        the mod event bus
     * @param modContainer  the mod container instance
     */
    public static void register(IEventBus modBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, ClassicBarsConfig.CLIENT_SPEC);
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        modBus.addListener(ClassicBarsConfig::onConfigLoading);
        modBus.addListener(ClassicBarsConfig::onConfigReloading);
    }
}
