package uk.co.extraspecialstudio.extraspecial.esc.net;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import uk.co.extraspecialstudio.extraspecial.Extraspecialcore;
import uk.co.extraspecialstudio.extraspecial.esc.theme.EscThemeManager;

@Mod.EventBusSubscriber(modid = Extraspecialcore.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class EscThemeSyncEvents {
    private EscThemeSyncEvents() {
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            EscNet.sendThemeTo(player);
        }
    }

    @Mod.EventBusSubscriber(modid = Extraspecialcore.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static final class Client {
        private Client() {
        }

        @SubscribeEvent
        public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
            EscThemeManager.clearServerPackTheme();
        }
    }
}
