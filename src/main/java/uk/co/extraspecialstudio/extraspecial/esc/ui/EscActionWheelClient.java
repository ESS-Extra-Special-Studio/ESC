package uk.co.extraspecialstudio.extraspecial.esc.ui;

import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
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
        public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
            EscActionWheel.forceResetAll();
        }

        /**
         * Esc must not cancel the wheel — release hold-key / deadzone / RMB do.
         * Opening pause would set {@code mc.screen} and force-reset the wheel; block that.
         */
        @SubscribeEvent
        public static void onScreenOpening(ScreenEvent.Opening event) {
            if (!EscActionWheel.anyOpen()) return;
            if (event.getNewScreen() instanceof PauseScreen) {
                event.setCanceled(true);
            }
        }

        /**
         * Swallow attack/use/scroll while the wheel owns the cursor.
         * Digit hotkeys still reach GLFW (for wedge shortcuts); hotbar is locked in tickAll.
         * RMB cancel is polled via GLFW inside {@link EscActionWheel}.
         */
        @SubscribeEvent
        public static void onMouseButton(InputEvent.MouseButton.Pre event) {
            if (!EscActionWheel.anyOpen()) return;
            if (event.getAction() == GLFW.GLFW_PRESS) {
                event.setCanceled(true);
            }
        }

        @SubscribeEvent
        public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
            if (EscActionWheel.anyOpen()) {
                event.setCanceled(true);
            }
        }

        @SubscribeEvent
        public static void onInteraction(InputEvent.InteractionKeyMappingTriggered event) {
            if (!EscActionWheel.anyOpen()) return;
            event.setCanceled(true);
            event.setSwingHand(false);
        }
    }

    @Mod.EventBusSubscriber(modid = Extraspecialcore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static final class ModBus {
        private ModBus() {}

        @SubscribeEvent
        public static void onRegisterOverlays(RegisterGuiOverlaysEvent event) {
            event.registerAboveAll("esc_action_wheel",
                (gui, guiGraphics, partialTick, screenWidth, screenHeight) ->
                    EscActionWheel.renderAll(guiGraphics, screenWidth, screenHeight));
        }
    }
}
