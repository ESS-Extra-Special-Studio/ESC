package uk.co.extraspecialstudio.extraspecial;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import uk.co.extraspecialstudio.extraspecial.esc.config.EscConfig;

@EventBusSubscriber(modid = Extraspecialcore.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config {

    private static final ModConfigSpec.Builder BUILDER = EscConfig.begin("ExtraSpecialCore library settings.");

    private static final ModConfigSpec.BooleanValue DEBUG_LOGGING = BUILDER
        .comment("When true, ESC may log extra diagnostics for troubleshooting.")
        .define("debugLogging", false);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean debugLogging;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        if (event.getConfig().getSpec() != SPEC) {
            return;
        }
        debugLogging = DEBUG_LOGGING.get();
    }
}
