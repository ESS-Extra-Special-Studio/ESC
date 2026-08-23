package uk.co.extraspecialstudio.extraspecial;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import uk.co.extraspecialstudio.extraspecial.esc.net.EscNet;
import uk.co.extraspecialstudio.extraspecial.esc.theme.EscThemeConfigs;

@Mod(Extraspecialcore.MODID)
public class Extraspecialcore {

    public static final String MODID = "extraspecialcore";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Extraspecialcore() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, EscThemeConfigs.COMMON_SPEC, "extraspecialcore-theme-common.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, EscThemeConfigs.CLIENT_SPEC, "extraspecialcore-theme-client.toml");
        uk.co.extraspecialstudio.extraspecial.esc.sound.EscSounds.REGISTER.register(modEventBus);
        EscNet.register();
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
