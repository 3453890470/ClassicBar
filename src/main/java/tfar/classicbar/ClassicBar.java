package tfar.classicbar;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tfar.classicbar.client.ClassicBarClient;
import tfar.classicbar.config.ClassicBarsConfig;

@Mod(value = ClassicBar.MODID, dist = Dist.CLIENT)
public class ClassicBar {

  public static final String MODID = "classicbar";

  public static final Logger logger = LogManager.getLogger();

  public ClassicBar(IEventBus modBus, ModContainer modContainer) {
    modContainer.registerConfig(ModConfig.Type.CLIENT, ClassicBarsConfig.CLIENT_SPEC);
    modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    ClassicBarClient.init(modBus);
  }
}
