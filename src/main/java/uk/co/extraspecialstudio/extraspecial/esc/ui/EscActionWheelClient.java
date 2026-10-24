package uk.co.extraspecialstudio.extraspecial.esc.ui;

import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ScreenOpenEvent;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;
import uk.co.extraspecialstudio.extraspecial.Extraspecialcore;

/**
 * ESC-owned tick + overlay for all registered {@link EscActionWheel}s.
 * Feature mods only {@link EscActionWheel#register} — they must not re-register overlays.
 */
public final class EscActionWheelClient {
    private EscActionWheelClient() {}

    @Mod.EventBusSubscriber(modid = Extraspecialcore.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static final class ForgeBus {
        private ForgeBus() {}

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase == TickEvent.Phase.START) {
                // handleKeybinds runs after START — keep slot pinned before and after.
                EscActionWheel.relockHotbarIfOpen();
                return;
            }
            if (event.phase != TickEvent.Phase.END) return;
            EscActionWheel.tickAll();
        }

        @SubscribeEvent
        public static void onLogout(ClientPlayerNetworkEvent.LoggedOutEvent event) {
            EscActionWheel.forceResetAll();
        }

        /**
         * Esc must not cancel the wheel — release hold-key / deadzone / RMB do.
         * Opening pause would set {@code mc.screen} and force-reset the wheel; block that.
         */
        @SubscribeEvent
        public static void onScreenOpening(ScreenOpenEvent event) {
            if (!EscActionWheel.anyOpen()) return;
            if (event.getScreen() instanceof PauseScreen) {
                event.setCanceled(true);
            }
        }

        /**
         * Swallow attack/use/scroll while the wheel owns the cursor.
         * Digit hotkeys still reach GLFW (for wedge shortcuts); hotbar is locked in tickAll.
         * RMB cancel is polled via GLFW inside {@link EscActionWheel}.
         */
        @SubscribeEvent
        public static void onMouseButton(InputEvent.MouseInputEvent event) {
            if (!EscActionWheel.anyOpen()) return;
            if (event.getAction() == GLFW.GLFW_PRESS) {
                event.setCanceled(true);
            }
        }

        @SubscribeEvent
        public static void onMouseScroll(InputEvent.MouseScrollEvent event) {
            if (EscActionWheel.anyOpen()) {
                event.setCanceled(true);
            }
        }

        @SubscribeEvent
        public static void onInteraction(InputEvent.ClickInputEvent event) {
            if (!EscActionWheel.anyOpen()) return;
            event.setCanceled(true);
            event.setSwingHand(false);
        }
    }

    @Mod.EventBusSubscriber(modid = Extraspecialcore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static final class ModBus {
        private ModBus() {}

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            OverlayRegistry.registerOverlayTop("esc_action_wheel", (gui, pose, partialTick, screenWidth, screenHeight) ->
                EscActionWheel.renderAll(new EscGraphics(pose), screenWidth, screenHeight));
        }
    }
}
