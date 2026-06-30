package tfar.classicbar.client;

import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;

/**
 * Client-side initialization for ClassicBar.
 * <p>
 * Config registration and overlay bootstrap are handled by
 * {@link tfar.classicbar.register.ModConfigs} and {@link tfar.classicbar.register.ModOverlays}
 * respectively, called from {@link tfar.classicbar.ClassicBar} before this class.
 * This class wires remaining event-bus listeners for GUI rendering and debug features.
 */
public final class ClassicBarClient {

    private ClassicBarClient() {
    }

    public static void init(IEventBus modBus) {
        modBus.addListener(EventHandler::registerGuiLayers);
        NeoForge.EVENT_BUS.addListener(EventHandler::disableVanillaLayers);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, EventHandler::finalizeFoodLevelCancellation);
        NeoForge.EVENT_BUS.addListener(EventHandler::onDebugPlayerTick);
    }
}
