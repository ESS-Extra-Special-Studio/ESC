package uk.co.extraspecialstudio.extraspecial.esc.gui;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import uk.co.extraspecialstudio.extraspecial.Extraspecialcore;

/**
 * Client bootstrap for ESC GUI runtime assets and demo profiles.
 */
@EventBusSubscriber(modid = Extraspecialcore.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class EscGuiClient {
    private EscGuiClient() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(EscGuiRuntime::registerBuiltins);
    }
}
