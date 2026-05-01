package tfar.classicbar.client;

import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import tfar.classicbar.config.ClassicBarsConfig;

public final class ClassicBarClient {

  private ClassicBarClient() {
  }

  public static void init(IEventBus modBus) {
    EventHandler.bootstrap();
    modBus.addListener(ClassicBarsConfig::onConfigLoading);
    modBus.addListener(ClassicBarsConfig::onConfigReloading);
    modBus.addListener(EventHandler::registerGuiLayers);
    NeoForge.EVENT_BUS.addListener(EventHandler::disableVanillaLayers);
    NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, EventHandler::finalizeFoodLevelCancellation);
  }
}
