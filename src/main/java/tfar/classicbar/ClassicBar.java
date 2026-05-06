package tfar.classicbar;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.resources.ResourceLocation;
import tfar.classicbar.client.ClassicBarClient;
import tfar.classicbar.data.DataGen;
import tfar.classicbar.register.ModConfigs;
import tfar.classicbar.register.ModIcons;
import tfar.classicbar.register.ModOverlays;

@Mod(value = ClassicBar.MODID, dist = Dist.CLIENT)
public class ClassicBar {

    public static final String MODID = "classicbar";

    public static final ResourceLocation FONT_3X5 = ResourceLocation.fromNamespaceAndPath(MODID, "3x5");
    public static final ResourceLocation FONT_3X5_TINY = ResourceLocation.fromNamespaceAndPath(MODID, "3x5_tiny");

    public static final Logger logger = LogManager.getLogger();

    public ClassicBar(IEventBus modBus, ModContainer modContainer) {
        ModConfigs.register(modBus, modContainer);
        ModIcons.register();
        ModOverlays.register();
        ClassicBarClient.init(modBus);
        modBus.addListener(DataGen::gatherData);
    }
}
