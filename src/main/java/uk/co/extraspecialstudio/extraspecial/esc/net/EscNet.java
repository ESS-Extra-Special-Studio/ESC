package uk.co.extraspecialstudio.extraspecial.esc.net;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import uk.co.extraspecialstudio.extraspecial.Extraspecialcore;

public final class EscNet {
    private static final String PROTOCOL = "2";
    private static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
        .named(new ResourceLocation(Extraspecialcore.MODID, "theme"))
        .networkProtocolVersion(() -> PROTOCOL)
        .clientAcceptedVersions(v -> PROTOCOL.equals(v) || "1".equals(v))
        .serverAcceptedVersions(v -> PROTOCOL.equals(v) || "1".equals(v))
        .simpleChannel();

    private static int nextId;

    private EscNet() {
    }

    public static void register() {
        CHANNEL.messageBuilder(EscThemeSyncPacket.class, nextId++, NetworkDirection.PLAY_TO_CLIENT)
            .encoder(EscThemeSyncPacket::encode)
            .decoder(EscThemeSyncPacket::decode)
            .consumerMainThread(EscThemeSyncPacket::handle)
            .add();
    }

    public static void sendThemeTo(ServerPlayer player) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), EscThemeSyncPacket.fromCommonConfig());
    }
}
