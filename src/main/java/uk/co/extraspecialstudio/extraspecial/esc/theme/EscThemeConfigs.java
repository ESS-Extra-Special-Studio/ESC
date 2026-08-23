package uk.co.extraspecialstudio.extraspecial.esc.theme;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import uk.co.extraspecialstudio.extraspecial.Extraspecialcore;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * COMMON pack/server theme + CLIENT personal prefs.
 * <p>
 * <b>Who writes what</b>
 * <ul>
 *   <li>{@link #COMMON_SPEC} ({@code extraspecialcore-theme-common.toml}) — modpack / server
 *       theme override. Synced to joining clients. Hub UI never writes these fields.</li>
 *   <li>{@link #CLIENT_SPEC} ({@code extraspecialcore-theme-client.toml}) — this player's machine
 *       only: Border/Text/Accent, Q / Motion / BG, colour scope. Never uploaded to the server.</li>
 * </ul>
 * Hierarchy: ESC default → pack/server (if override) → application → player (if unlocked).
 */
public final class EscThemeConfigs {
    public static final ModConfigSpec COMMON_SPEC;
    public static final ModConfigSpec CLIENT_SPEC;

    public static final ModConfigSpec.ConfigValue<String> DEFAULT_PRESET;
    public static final ModConfigSpec.BooleanValue PACK_OVERRIDE;
    public static final ModConfigSpec.BooleanValue LOCK_PLAYERS;
    public static final ModConfigSpec.ConfigValue<String> PACK_PRESET;
    public static final ModConfigSpec.ConfigValue<String> PACK_EFFECT;
    public static final ModConfigSpec.ConfigValue<String> PACK_FRAME;
    public static final ModConfigSpec.ConfigValue<String> PACK_BORDER;
    public static final ModConfigSpec.ConfigValue<String> PACK_TEXT;
    public static final ModConfigSpec.ConfigValue<String> PACK_BODY;
    public static final ModConfigSpec.ConfigValue<String> PACK_ACCENT;
    public static final ModConfigSpec.ConfigValue<String> PACK_PANEL;
    public static final ModConfigSpec.DoubleValue PACK_OPACITY;

    public static final ModConfigSpec.BooleanValue PLAYER_CUSTOM;
    public static final ModConfigSpec.BooleanValue PLAYER_APPLY_GLOBAL;
    public static final ModConfigSpec.IntValue PLAYER_BORDER;
    public static final ModConfigSpec.IntValue PLAYER_TEXT;
    public static final ModConfigSpec.IntValue PLAYER_ACCENT;
    /** Optional custom ARGB/hex override; blank = use swatch index. */
    public static final ModConfigSpec.ConfigValue<String> PLAYER_BORDER_COLOR;
    public static final ModConfigSpec.ConfigValue<String> PLAYER_TEXT_COLOR;
    public static final ModConfigSpec.ConfigValue<String> PLAYER_ACCENT_COLOR;
    public static final ModConfigSpec.ConfigValue<String> RECENT_COLORS;
    public static final ModConfigSpec.ConfigValue<String> SAVED_COLORS;
    public static final ModConfigSpec.ConfigValue<String> QUALITY_MODE;
    public static final ModConfigSpec.BooleanValue REDUCED_MOTION;
    public static final ModConfigSpec.ConfigValue<String> BACKDROP_STYLE;
    public static final ModConfigSpec.DoubleValue UI_SCALE;
    public static final ModConfigSpec.BooleanValue UI_SOUNDS;
    public static final ModConfigSpec.DoubleValue UI_SOUND_VOLUME;
    public static final ModConfigSpec.BooleanValue LAYOUT_DEBUG;

    static {
        ModConfigSpec.Builder common = new ModConfigSpec.Builder();
        common.comment("ESC theme — defaults and modpack/server override.",
                "Hierarchy: ESC default → pack/server → application → player (if unlocked).")
            .push("theme");
        DEFAULT_PRESET = common
            .comment("VANILLA, FALLOUT, MAGIC, APOCALYPSE, CYBER, CLEAN, INDUSTRIAL, HIGH_CONTRAST, MATRIX, OCEAN, CLOUDS, GROWTH, EMBER",
                "Fresh install / ESH first-run uses GROWTH (ESH green chrome over the forest backdrop).")
            .define("defaultPreset", "GROWTH");
        PACK_OVERRIDE = common.define("packOverride", false);
        LOCK_PLAYERS = common.define("lockPlayers", true);
        PACK_PRESET = common.define("packPreset", "FALLOUT");
        PACK_EFFECT = common
            .comment("Optional effect package: NONE, TERMINAL, FALLOUT, CLEAN, HUD, CINEMATIC")
            .define("effectPreset", "");
        PACK_FRAME = common
            .comment("Optional frame override: SQUARE, ROUNDED_SOFT, CHAMFER, CUT_CORNER, BRACKET")
            .define("frameStyle", "");
        PACK_BORDER = common.define("border", "");
        PACK_TEXT = common.define("text", "");
        PACK_BODY = common.define("body", "");
        PACK_ACCENT = common.define("accent", "");
        PACK_PANEL = common.define("panel", "");
        PACK_OPACITY = common.defineInRange("panelOpacity", -1.0, -1.0, 1.0);
        common.pop();
        COMMON_SPEC = common.build();

        ModConfigSpec.Builder client = new ModConfigSpec.Builder();
        client.push("player");
        PLAYER_CUSTOM = client.define("customEnabled", false);
        PLAYER_APPLY_GLOBAL = client
            .comment(
                "PERSONAL CLIENT PREF — never synced to the server.",
                "When false (default / Apply to: ESH), Border/Text/Accent only tint Extra Special Hub.",
                "When true (Apply to: ALL), those colours also tint every ESC window (Dead Air, Pantheon, etc.).",
                "A pack/server theme with lockPlayers=true ignores both."
            )
            .define("applyPlayerToAllEsc", false);
        PLAYER_BORDER = client.defineInRange("borderSwatch", 0, 0, 64);
        PLAYER_TEXT = client.defineInRange("textSwatch", 0, 0, 64);
        PLAYER_ACCENT = client.defineInRange("accentSwatch", 0, 0, 64);
        PLAYER_BORDER_COLOR = client
            .comment("Custom border colour (#RRGGBB / #AARRGGBB). Blank = use borderSwatch.")
            .define("borderColor", "");
        PLAYER_TEXT_COLOR = client
            .comment("Custom text colour (#RRGGBB / #AARRGGBB). Blank = use textSwatch.")
            .define("textColor", "");
        PLAYER_ACCENT_COLOR = client
            .comment("Custom accent colour (#RRGGBB / #AARRGGBB). Blank = use accentSwatch.")
            .define("accentColor", "");
        RECENT_COLORS = client
            .comment("Comma-separated recent colours from the ESC colour picker.")
            .define("recentColors", "");
        SAVED_COLORS = client
            .comment("Comma-separated saved custom colours from the ESC colour picker.")
            .define("savedColors", "");
        client.pop();
        client.push("visual");
        QUALITY_MODE = client
            .comment(
                "OFF, LOW, MEDIUM, HIGH — visual budget for ESC effects.",
                "OFF: none. LOW: light scale. MEDIUM: shadows/glow/CRT/border sweeps.",
                "HIGH: also enables the animated backdrop (needs Motion ON).",
                "Fresh install is HIGH so the default Growth backdrop actually draws."
            )
            .define("quality", "HIGH");
        REDUCED_MOTION = client
            .comment("When true (Motion OFF in hub), freezes backdrop/border animation; backdrop stays visible at Q=HIGH.")
            .define("reducedMotion", false);
        BACKDROP_STYLE = client
            .comment("Animated backdrop when Q=HIGH and Motion ON: GRID, SCAN, RAIN, PULSE, SPARKS, MATRIX, OCEAN, CLOUDS, AURORA, EMBER, STARFIELD, LIGHTNING, GROWTH")
            .define("backdropStyle", "GROWTH");
        UI_SCALE = client.defineInRange("uiScale", 1.0, 0.75, 1.5);
        UI_SOUNDS = client
            .comment("Play ESC UI sound cues (hub nav, buttons, expand/collapse, etc.)")
            .define("uiSounds", true);
        UI_SOUND_VOLUME = client
            .comment("Master multiplier for ESC UI sounds (0–1)")
            .defineInRange("uiSoundVolume", 0.85, 0.0, 1.0);
        LAYOUT_DEBUG = client
            .comment("Draw EscScreen content/footer bounds (dev aid). Off by default.")
            .define("layoutDebug", false);
        client.pop();
        CLIENT_SPEC = client.build();
    }

    private EscThemeConfigs() {
    }

    public static String defaultPreset() {
        return DEFAULT_PRESET.get();
    }

    public static boolean playerCustomEnabled() {
        return PLAYER_CUSTOM.get();
    }

    /**
     * Hub-only by default on this PC: player Border/Text/Accent apply to {@code extraspecialhub}.
     * Enable {@link #PLAYER_APPLY_GLOBAL} to tint every ESC screen on this same client.
     * Never affects other players or the server — pack override + lock does that.
     */
    public static boolean playerAppliesTo(String applicationId) {
        if (!playerCustomEnabled()) {
            return false;
        }
        if (PLAYER_APPLY_GLOBAL.get()) {
            return true;
        }
        return applicationId != null && applicationId.equals("extraspecialhub");
    }

    public static boolean playerApplyGlobal() {
        return PLAYER_APPLY_GLOBAL.get();
    }

    /** Footer label — ESH = hub only, ALL = every ESC screen on this machine. */
    public static String playerScopeLabel() {
        return playerApplyGlobal() ? "Apply to: ALL" : "Apply to: ESH";
    }

    public static int playerBorderIndex() {
        return PLAYER_BORDER.get();
    }

    public static int playerTextIndex() {
        return PLAYER_TEXT.get();
    }

    public static int playerAccentIndex() {
        return PLAYER_ACCENT.get();
    }

    public static String playerBorderColorRaw() {
        return PLAYER_BORDER_COLOR.get();
    }

    public static String playerTextColorRaw() {
        return PLAYER_TEXT_COLOR.get();
    }

    public static String playerAccentColorRaw() {
        return PLAYER_ACCENT_COLOR.get();
    }

    public static int playerColorArgb(EscColorRole role, int fallbackArgb) {
        String raw = switch (role) {
            case BORDER -> playerBorderColorRaw();
            case TEXT -> playerTextColorRaw();
            case ACCENT -> playerAccentColorRaw();
        };
        Integer custom = EscColor.parseHex(raw);
        if (custom != null) {
            return custom;
        }
        EscThemeSwatch sw = switch (role) {
            case BORDER -> EscThemeSwatch.byIndex(playerBorderIndex());
            case TEXT -> EscThemeSwatch.byIndex(playerTextIndex());
            case ACCENT -> EscThemeSwatch.byIndex(playerAccentIndex());
        };
        return switch (role) {
            case BORDER -> sw.borderArgb();
            case TEXT -> EscColor.opaque(sw.titleRgb());
            case ACCENT -> sw.accentArgb();
        };
    }

    public static void setPlayerCustomColor(EscColorRole role, int argb) {
        PLAYER_CUSTOM.set(true);
        String hex = "#" + EscColor.toHexRgb(argb);
        EscThemeSwatch nearest = EscColor.nearestSwatch(argb);
        switch (role) {
            case BORDER -> {
                PLAYER_BORDER_COLOR.set(hex);
                PLAYER_BORDER.set(nearest.ordinal());
            }
            case TEXT -> {
                PLAYER_TEXT_COLOR.set(hex);
                PLAYER_TEXT.set(nearest.ordinal());
            }
            case ACCENT -> {
                PLAYER_ACCENT_COLOR.set(hex);
                PLAYER_ACCENT.set(nearest.ordinal());
            }
        }
    }

    public static void clearPlayerCustomColor(EscColorRole role) {
        switch (role) {
            case BORDER -> PLAYER_BORDER_COLOR.set("");
            case TEXT -> PLAYER_TEXT_COLOR.set("");
            case ACCENT -> PLAYER_ACCENT_COLOR.set("");
        }
    }

    public static void cyclePlayerSwatch(EscColorRole role) {
        PLAYER_CUSTOM.set(true);
        clearPlayerCustomColor(role);
        switch (role) {
            case BORDER -> PLAYER_BORDER.set(EscThemeSwatch.byIndex(PLAYER_BORDER.get()).next().ordinal());
            case TEXT -> PLAYER_TEXT.set(EscThemeSwatch.byIndex(PLAYER_TEXT.get()).next().ordinal());
            case ACCENT -> PLAYER_ACCENT.set(EscThemeSwatch.byIndex(PLAYER_ACCENT.get()).next().ordinal());
        }
    }

    public static List<Integer> recentColors() {
        return parseColorList(RECENT_COLORS.get());
    }

    public static List<Integer> savedColors() {
        return parseColorList(SAVED_COLORS.get());
    }

    public static void pushRecentColor(int argb) {
        RECENT_COLORS.set(serializeColorList(prependUnique(recentColors(), EscColor.opaque(argb), 12)));
    }

    public static void addSavedColor(int argb) {
        SAVED_COLORS.set(serializeColorList(prependUnique(savedColors(), EscColor.opaque(argb), 16)));
    }

    private static List<Integer> prependUnique(List<Integer> existing, int argb, int max) {
        List<Integer> out = new ArrayList<>(max);
        out.add(argb);
        for (int c : existing) {
            if ((c & 0xFFFFFF) == (argb & 0xFFFFFF)) {
                continue;
            }
            out.add(c);
            if (out.size() >= max) {
                break;
            }
        }
        return out;
    }

    private static List<Integer> parseColorList(String raw) {
        List<Integer> out = new ArrayList<>();
        if (raw == null || raw.isBlank()) {
            return out;
        }
        for (String part : raw.split(",")) {
            Integer c = EscColor.parseHex(part.trim());
            if (c != null) {
                out.add(c);
            }
        }
        return out;
    }

    private static String serializeColorList(List<Integer> colors) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < colors.size(); i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append('#').append(EscColor.toHexRgb(colors.get(i)).toUpperCase(Locale.ROOT));
        }
        return sb.toString();
    }

    public static EscQualityMode qualityMode() {
        return EscQualityMode.byName(QUALITY_MODE.get());
    }

    public static boolean reducedMotion() {
        return REDUCED_MOTION.get();
    }

    public static EscBackdropStyle backdropStyle() {
        return EscBackdropStyle.byName(BACKDROP_STYLE.get());
    }

    public static float uiScale() {
        return UI_SCALE.get().floatValue();
    }

    public static boolean uiSoundsEnabled() {
        return UI_SOUNDS.get();
    }

    public static float uiSoundVolume() {
        return UI_SOUND_VOLUME.get().floatValue();
    }

    /** When true, EscScreen draws content/footer rect outlines (dev layout aid). */
    public static boolean layoutDebugEnabled() {
        return LAYOUT_DEBUG.get();
    }

    /** Stable stamp of pack-theme config values for {@link EscThemeManager} caching. */
    public static long packConfigStamp() {
        long s = PACK_OVERRIDE.get() ? 1 : 0;
        s = s * 31 + Objects.hashCode(PACK_PRESET.get());
        s = s * 31 + Objects.hashCode(PACK_EFFECT.get());
        s = s * 31 + Objects.hashCode(PACK_FRAME.get());
        s = s * 31 + Objects.hashCode(PACK_BORDER.get());
        s = s * 31 + Objects.hashCode(PACK_TEXT.get());
        s = s * 31 + Objects.hashCode(PACK_BODY.get());
        s = s * 31 + Objects.hashCode(PACK_ACCENT.get());
        s = s * 31 + Objects.hashCode(PACK_PANEL.get());
        s = s * 31 + PACK_OPACITY.get().hashCode();
        s = s * 31 + (LOCK_PLAYERS.get() ? 1 : 0);
        return s;
    }

    public static EscThemeManager.PackTheme readPackTheme() {
        if (!PACK_OVERRIDE.get()) {
            return null;
        }
        EscTheme base = EscThemePreset.byName(PACK_PRESET.get()).theme();
        EscEffectPreset effect = EscEffectPreset.byName(PACK_EFFECT.get());
        if (effect != EscEffectPreset.NONE) {
            base = effect.apply(base);
        }
        if (PACK_FRAME.get() != null && !PACK_FRAME.get().isBlank()) {
            base = base.withPresentation(
                base.panelOpacity(), EscFrameStyle.byName(PACK_FRAME.get()),
                base.shadowStrength(), base.glowStrength(), base.crtIntensity(), base.motionScale()
            );
        }
        if (PACK_OPACITY.get() >= 0) {
            base = base.withPresentation(
                PACK_OPACITY.get().floatValue(), base.frameStyle(),
                base.shadowStrength(), base.glowStrength(), base.crtIntensity(), base.motionScale()
            );
        }
        base = applyToken(base, PACK_BORDER.get(), Token.BORDER);
        base = applyToken(base, PACK_TEXT.get(), Token.TEXT);
        base = applyToken(base, PACK_BODY.get(), Token.BODY);
        base = applyToken(base, PACK_ACCENT.get(), Token.ACCENT);
        base = applyToken(base, PACK_PANEL.get(), Token.PANEL);
        base = EscThemeValidator.clamp(base);
        return new EscThemeManager.PackTheme(true, LOCK_PLAYERS.get(), base.withName("Pack:" + PACK_PRESET.get()));
    }

    private enum Token { BORDER, TEXT, BODY, ACCENT, PANEL }

    private static EscTheme applyToken(EscTheme theme, String raw, Token token) {
        if (raw == null || raw.isBlank()) {
            return theme;
        }
        EscThemeSwatch sw = EscThemeSwatch.byName(raw);
        Integer hex = parseHex(raw);
        return switch (token) {
            case BORDER -> theme.withBorder(sw != null ? sw.borderArgb() : (hex != null ? asArgb(hex) : theme.borderArgb()));
            case TEXT -> {
                if (sw != null) yield theme.withText(sw.titleRgb(), sw.bodyRgb());
                if (hex != null) {
                    int rgb = hex & 0xFFFFFF;
                    yield theme.withText(rgb, dim(rgb));
                }
                yield theme;
            }
            case BODY -> {
                if (sw != null) yield theme.withText(theme.textTitleRgb(), sw.bodyRgb());
                if (hex != null) yield theme.withText(theme.textTitleRgb(), hex & 0xFFFFFF);
                yield theme;
            }
            case ACCENT -> theme.withAccent(sw != null ? sw.accentArgb() : (hex != null ? asArgb(hex) : theme.accentArgb()));
            case PANEL -> hex == null ? theme : theme.withPanelFill(asArgb(hex));
        };
    }

    private static int asArgb(int hex) {
        return (hex > 0xFFFFFF) ? hex : (0xFF000000 | (hex & 0xFFFFFF));
    }

    private static int dim(int rgb) {
        int r = ((rgb >> 16) & 0xFF) * 5 / 6;
        int g = ((rgb >> 8) & 0xFF) * 5 / 6;
        int b = (rgb & 0xFF) * 5 / 6;
        return (r << 16) | (g << 8) | b;
    }

    private static Integer parseHex(String raw) {
        String s = raw.trim();
        if (s.startsWith("#")) s = s.substring(1);
        if (s.regionMatches(true, 0, "0x", 0, 2)) s = s.substring(2);
        try {
            if (s.length() == 6 || s.length() == 8) {
                return (int) Long.parseLong(s, 16);
            }
        } catch (NumberFormatException ignored) {
        }
        return null;
    }

    @EventBusSubscriber(modid = Extraspecialcore.MODID, bus = EventBusSubscriber.Bus.MOD)
    public static final class Events {
        private Events() {
        }

        @SubscribeEvent
        public static void onLoad(ModConfigEvent event) {
            Object spec = event.getConfig().getSpec();
            if (spec != COMMON_SPEC && spec != CLIENT_SPEC) {
                return;
            }
            EscThemeManager.invalidateCache();
        }
    }
}
