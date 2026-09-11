package uk.co.extraspecialstudio.extraspecial;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Common config for Extra Special Core ({@code config/extraspecialcore-common.toml}).
 * <p>
 * Section banners and push/pop match the RadioTowers / Dead Letters style.
 * Theme / UI prefs live in {@code EscThemeConfigs}.
 */
@EventBusSubscriber(modid = Extraspecialcore.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue DEBUG_LOGGING;

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
