package uk.co.extraspecialstudio.extraspecial.esc.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import uk.co.extraspecialstudio.extraspecial.esc.theme.EscFrameStyle;
import uk.co.extraspecialstudio.extraspecial.esc.theme.EscTheme;
import uk.co.extraspecialstudio.extraspecial.esc.theme.EscThemeConfigs;
import uk.co.extraspecialstudio.extraspecial.esc.theme.EscThemeManager;
import uk.co.extraspecialstudio.extraspecial.esc.theme.EscThemeValidator;

import java.util.function.Supplier;

/**
 * Server → client pack theme (protocol 2: colours + presentation tokens).
 */
public final class EscThemeSyncPacket {
    private static final int VERSION = 2;

    private final boolean override;
    private final boolean lockPlayers;
    private final EscTheme theme;

    public EscThemeSyncPacket(boolean override, boolean lockPlayers, EscTheme theme) {
        this.override = override;
        this.lockPlayers = lockPlayers;
        this.theme = theme == null ? EscTheme.vanilla() : theme;
    }

    public static EscThemeSyncPacket fromCommonConfig() {
        EscThemeManager.PackTheme pack = EscThemeConfigs.readPackTheme();
        if (pack == null) {
            return new EscThemeSyncPacket(false, false, null);
        }
        return new EscThemeSyncPacket(true, pack.lockPlayers(), pack.theme());
    }

    public static void encode(EscThemeSyncPacket msg, FriendlyByteBuf buf) {
        buf.writeVarInt(VERSION);
        buf.writeBoolean(msg.override);
        buf.writeBoolean(msg.lockPlayers);
        EscTheme t = msg.theme;
        buf.writeUtf(t.name() == null ? "" : t.name(), 64);
        buf.writeInt(t.borderArgb());
        buf.writeInt(t.textTitleRgb());
        buf.writeInt(t.textBodyRgb());
        buf.writeInt(t.accentArgb());
        buf.writeInt(t.panelFillArgb());
        buf.writeInt(t.focusBorderArgb());
        buf.writeInt(t.secondaryArgbResolved());
        buf.writeInt(t.mutedRgb());
        buf.writeFloat(t.panelOpacity());
        buf.writeUtf(t.frameStyle().name(), 32);
        buf.writeFloat(t.shadowStrength());
        buf.writeFloat(t.glowStrength());
        buf.writeFloat(t.crtIntensity());
        buf.writeFloat(t.motionScale());
    }

    public static EscThemeSyncPacket decode(FriendlyByteBuf buf) {
        int ver = buf.readVarInt();
        boolean override = buf.readBoolean();
        boolean lock = buf.readBoolean();
        String name = buf.readUtf(64);
        int border = buf.readInt();
        int title = buf.readInt();
        int body = buf.readInt();
        int accent = buf.readInt();
        int panel = buf.readInt();
        int focus = buf.readInt();
        if (ver < 2) {
            EscTheme theme = new EscTheme(name, border, title, body, accent, panel, focus);
            return new EscThemeSyncPacket(override, lock, theme);
        }
        int secondary = buf.readInt();
        int muted = buf.readInt();
        float opacity = buf.readFloat();
        EscFrameStyle frame = EscFrameStyle.byName(buf.readUtf(32));
        float shadow = buf.readFloat();
        float glow = buf.readFloat();
        float crt = buf.readFloat();
        float motion = buf.readFloat();
        EscTheme theme = new EscTheme(
            name, border, title, body, accent, panel, focus, secondary, muted,
            opacity, frame, shadow, glow, crt, motion
        );
        return new EscThemeSyncPacket(override, lock, EscThemeValidator.clamp(theme));
    }

    public static void handle(EscThemeSyncPacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            if (!msg.override) {
                EscThemeManager.clearServerPackTheme();
            } else {
                EscThemeManager.setServerPackTheme(
                    new EscThemeManager.PackTheme(true, msg.lockPlayers, EscThemeValidator.clamp(msg.theme))
                );
            }
        }));
        ctx.setPacketHandled(true);
    }
}
