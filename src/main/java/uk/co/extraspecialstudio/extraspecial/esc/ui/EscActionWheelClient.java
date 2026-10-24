package uk.co.extraspecialstudio.extraspecial.esc.ui;

import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.lwjgl.glfw.GLFW;
import uk.co.extraspecialstudio.extraspecial.Extraspecialcore;

/**
 * ESC-owned tick + overlay for all registered {@link EscActionWheel}s.
 * Feature mods only {@link EscActionWheel#register} — they must not re-register overlays.
 */
public final class EscActionWheelClient {
    private EscActionWheelClient() {}

    @EventBusSubscriber(modid = Extraspecialcore.MODID, value = Dist.CLIENT)
    public static final class GameBus {
        private GameBus() {}

        @SubscribeEvent
        public static void onClientTickPre(ClientTickEvent.Pre event) {
            // Keybinds run after Pre — keep slot pinned before and after.
            EscActionWheel.relockHotbarIfOpen();
        }

        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Post event) {
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

    @EventBusSubscriber(modid = Extraspecialcore.MODID, value = Dist.CLIENT)
    public static final class ModBus {
        private ModBus() {}

        @SubscribeEvent
        public static void onRegisterGuiLayers(RegisterGuiLayersEvent event) {
            event.registerAboveAll(
                ResourceLocation.fromNamespaceAndPath(Extraspecialcore.MODID, "esc_action_wheel"),
                (guiGraphics, deltaTracker) ->
                    EscActionWheel.renderAll(guiGraphics, guiGraphics.guiWidth(), guiGraphics.guiHeight()));
        }
    }
}
