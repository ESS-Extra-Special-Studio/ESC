package uk.co.extraspecialstudio.extraspecial;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;
import uk.co.extraspecialstudio.extraspecial.esc.sound.EscSounds;
import uk.co.extraspecialstudio.extraspecial.esc.theme.EscThemeConfigs;

@Mod(Extraspecialcore.MODID)
public class Extraspecialcore {

    public static final String MODID = "extraspecialcore";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Extraspecialcore(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.register(this);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        modContainer.registerConfig(ModConfig.Type.COMMON, EscThemeConfigs.COMMON_SPEC, "extraspecialcore-theme-common.toml");
        modContainer.registerConfig(ModConfig.Type.CLIENT, EscThemeConfigs.CLIENT_SPEC, "extraspecialcore-theme-client.toml");
        EscSounds.REGISTER.register(modEventBus);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        if (Config.debugLogging) {
            LOGGER.debug("ExtraSpecialCore common setup (debug logging enabled).");
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        if (Config.debugLogging) {
            LOGGER.debug("ExtraSpecialCore: server starting.");
        }
    }
}
