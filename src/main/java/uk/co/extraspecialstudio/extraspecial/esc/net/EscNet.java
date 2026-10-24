package uk.co.extraspecialstudio.extraspecial.esc.net;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;
import uk.co.extraspecialstudio.extraspecial.Extraspecialcore;

public final class EscNet {
    private static final int PROTOCOL = 2;
    private static final SimpleChannel CHANNEL = ChannelBuilder
        .named(new ResourceLocation(Extraspecialcore.MODID, "theme"))
        .networkProtocolVersion(PROTOCOL)
        .clientAcceptedVersions(EscNet::acceptProtocol)
        .serverAcceptedVersions(EscNet::acceptProtocol)
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
        CHANNEL.send(EscThemeSyncPacket.fromCommonConfig(), PacketDistributor.PLAYER.with(player));
    }

    private static boolean acceptProtocol(Channel.VersionTest.Status status, int version) {
        return status == Channel.VersionTest.Status.PRESENT && (version == PROTOCOL || version == 1);
    }
}
