package uk.co.extraspecialstudio.extraspecial.esc.gui;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import uk.co.extraspecialstudio.extraspecial.Extraspecialcore;

/**
 * Client bootstrap for ESC GUI runtime assets and demo profiles.
 */
@Mod.EventBusSubscriber(modid = Extraspecialcore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class EscGuiClient {
    private EscGuiClient() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(EscGuiRuntime::registerBuiltins);
    }
}
