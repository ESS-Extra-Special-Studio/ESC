package uk.co.extraspecialstudio.extraspecial;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

/**
 * Common config for Extra Special Core ({@code config/extraspecialcore-common.toml}).
 * <p>
 * Section banners and push/pop match the RadioTowers / Dead Letters style.
 * Theme / UI prefs live in {@code EscThemeConfigs}.
 */
@Mod.EventBusSubscriber(modid = Extraspecialcore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue DEBUG_LOGGING;

    static {
        // ========== LIBRARY ==========
        BUILDER.comment(
                "============================================================",
                "EXTRA SPECIAL CORE",
                "Library diagnostics only. Theme / colour / motion settings",
                "are in extraspecialcore-theme-common.toml and -theme-client.toml.",
                "============================================================"
        ).push("general");

        DEBUG_LOGGING = BUILDER
                .comment(
                        "----- START HERE -----",
                        "When true, ESC may log extra diagnostics for troubleshooting.",
                        "Default: false."
                )
                .define("debugLogging", false);
        BUILDER.pop();
    }

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
