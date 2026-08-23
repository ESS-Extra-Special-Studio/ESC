package uk.co.extraspecialstudio.extraspecial;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import uk.co.extraspecialstudio.extraspecial.esc.config.EscConfig;

@Mod.EventBusSubscriber(modid = Extraspecialcore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {

    private static final ForgeConfigSpec.Builder BUILDER = EscConfig.begin("ExtraSpecialCore library settings.");

    private static final ForgeConfigSpec.BooleanValue DEBUG_LOGGING = BUILDER
        .comment("When true, ESC may log extra diagnostics for troubleshooting.")
        .define("debugLogging", false);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean debugLogging;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        if (event.getConfig().getSpec() != SPEC) {
            return;
        }
        debugLogging = DEBUG_LOGGING.get();
    }
}
