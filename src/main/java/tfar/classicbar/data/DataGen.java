package tfar.classicbar.data;

import net.neoforged.neoforge.data.event.GatherDataEvent;
import tfar.classicbar.ClassicBar;

public class DataGen {

    public static void gatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        var output = generator.getPackOutput();

        generator.addProvider(
                event.includeClient(),
                new ClassicBarLangProvider(output, ClassicBar.MODID, "en_us")
        );
    }
}
