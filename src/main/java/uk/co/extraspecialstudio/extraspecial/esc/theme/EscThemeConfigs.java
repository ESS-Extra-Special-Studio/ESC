package uk.co.extraspecialstudio.extraspecial.esc.theme;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
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
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final ForgeConfigSpec CLIENT_SPEC;

    public static final ForgeConfigSpec.ConfigValue<String> DEFAULT_PRESET;
    public static final ForgeConfigSpec.BooleanValue PACK_OVERRIDE;
    public static final ForgeConfigSpec.BooleanValue LOCK_PLAYERS;
    public static final ForgeConfigSpec.ConfigValue<String> PACK_PRESET;
    public static final ForgeConfigSpec.ConfigValue<String> PACK_EFFECT;
    public static final ForgeConfigSpec.ConfigValue<String> PACK_FRAME;
    public static final ForgeConfigSpec.ConfigValue<String> PACK_BORDER;
    public static final ForgeConfigSpec.ConfigValue<String> PACK_TEXT;
    public static final ForgeConfigSpec.ConfigValue<String> PACK_BODY;
    public static final ForgeConfigSpec.ConfigValue<String> PACK_ACCENT;
    public static final ForgeConfigSpec.ConfigValue<String> PACK_PANEL;
    public static final ForgeConfigSpec.DoubleValue PACK_OPACITY;

    public static final ForgeConfigSpec.BooleanValue PLAYER_CUSTOM;
    public static final ForgeConfigSpec.BooleanValue PLAYER_APPLY_GLOBAL;
    public static final ForgeConfigSpec.IntValue PLAYER_BORDER;
    public static final ForgeConfigSpec.IntValue PLAYER_TEXT;
    public static final ForgeConfigSpec.IntValue PLAYER_ACCENT;
    /** Optional custom ARGB/hex override; blank = use swatch index. */
    public static final ForgeConfigSpec.ConfigValue<String> PLAYER_BORDER_COLOR;
    public static final ForgeConfigSpec.ConfigValue<String> PLAYER_TEXT_COLOR;
    public static final ForgeConfigSpec.ConfigValue<String> PLAYER_ACCENT_COLOR;
    public static final ForgeConfigSpec.ConfigValue<String> RECENT_COLORS;
    public static final ForgeConfigSpec.ConfigValue<String> SAVED_COLORS;
    public static final ForgeConfigSpec.ConfigValue<String> QUALITY_MODE;
    public static final ForgeConfigSpec.BooleanValue REDUCED_MOTION;
    public static final ForgeConfigSpec.ConfigValue<String> BACKDROP_STYLE;
    public static final ForgeConfigSpec.DoubleValue UI_SCALE;
    public static final ForgeConfigSpec.BooleanValue UI_SOUNDS;
    public static final ForgeConfigSpec.DoubleValue UI_SOUND_VOLUME;
    public static final ForgeConfigSpec.BooleanValue LAYOUT_DEBUG;

    static {
        ForgeConfigSpec.Builder common = new ForgeConfigSpec.Builder();

        // ========== COMMON: PACK / SERVER THEME ==========
        common.comment(
                "============================================================",
                "PACK / SERVER THEME (COMMON)",
                "Synced to joining clients. Hub UI never writes these fields.",
                "Hierarchy: ESC default → pack/server → application → player.",
                "============================================================"
            )
            .push("theme");
        DEFAULT_PRESET = common
            .comment(
                "----- START HERE: DEFAULT LOOK -----",
                "Preset when no pack override is active.",
                "VANILLA, FALLOUT, MAGIC, APOCALYPSE, CYBER, CLEAN, INDUSTRIAL,",
                "HIGH_CONTRAST, MATRIX, OCEAN, CLOUDS, GROWTH, EMBER.",
                "Fresh install / ESH first-run uses GROWTH. Default: GROWTH."
            )
            .define("defaultPreset", "GROWTH");
        PACK_OVERRIDE = common
            .comment(
                "----- PACK OVERRIDE -----",
                "true: use packPreset / colours / frame below instead of defaultPreset.",
                "Default: false."
            )
            .define("packOverride", false);
        LOCK_PLAYERS = common
            .comment(
                "When pack override is on: true blocks player Border/Text/Accent.",
                "Default: true."
            )
            .define("lockPlayers", true);
        PACK_PRESET = common
            .comment("Preset name used while packOverride is true. Default: FALLOUT.")
            .define("packPreset", "FALLOUT");
        PACK_EFFECT = common
            .comment(
                "Optional effect package: NONE, TERMINAL, FALLOUT, CLEAN, HUD, CINEMATIC.",
                "Empty = none. Default: empty."
            )
            .define("effectPreset", "");
        PACK_FRAME = common
            .comment(
                "Optional frame override: SQUARE, ROUNDED_SOFT, CHAMFER, CUT_CORNER, BRACKET.",
                "Empty = preset default. Default: empty."
            )
            .define("frameStyle", "");
        PACK_BORDER = common
            .comment("Optional border colour token or #hex. Empty = preset. Default: empty.")
            .define("border", "");
        PACK_TEXT = common
            .comment("Optional title text colour token or #hex. Empty = preset. Default: empty.")
            .define("text", "");
        PACK_BODY = common
            .comment("Optional body text colour token or #hex. Empty = preset. Default: empty.")
            .define("body", "");
        PACK_ACCENT = common
            .comment("Optional accent colour token or #hex. Empty = preset. Default: empty.")
            .define("accent", "");
        PACK_PANEL = common
            .comment("Optional panel fill #hex. Empty = preset. Default: empty.")
            .define("panel", "");
        PACK_OPACITY = common
            .comment(
                "Panel opacity override. -1 = use preset. Range 0.0–1.0 when set.",
                "Default: -1.0."
            )
            .defineInRange("panelOpacity", -1.0, -1.0, 1.0);
        common.pop();
        COMMON_SPEC = common.build();

        ForgeConfigSpec.Builder client = new ForgeConfigSpec.Builder();

        // ========== CLIENT: PLAYER COLOURS ==========
        client.comment(
                "============================================================",
                "PLAYER COLOURS (CLIENT ONLY)",
                "This machine only — never uploaded to the server.",
                "Ignored when pack override + lockPlayers is on.",
                "============================================================"
            )
            .push("player");
        PLAYER_CUSTOM = client
            .comment(
                "----- START HERE: PERSONAL TINT -----",
                "true: apply Border/Text/Accent prefs below. Default: false."
            )
            .define("customEnabled", false);
        PLAYER_APPLY_GLOBAL = client
            .comment(
                "When false (Apply to: ESH), colours only tint Extra Special Hub.",
                "When true (Apply to: ALL), also tint every ESC window (Dead Air, Pantheon, …).",
                "Default: false."
            )
            .define("applyPlayerToAllEsc", false);
        PLAYER_BORDER = client
            .comment("Border swatch index (0–64). Default: 0.")
            .defineInRange("borderSwatch", 0, 0, 64);
        PLAYER_TEXT = client
            .comment("Text swatch index (0–64). Default: 0.")
            .defineInRange("textSwatch", 0, 0, 64);
        PLAYER_ACCENT = client
            .comment("Accent swatch index (0–64). Default: 0.")
            .defineInRange("accentSwatch", 0, 0, 64);
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
            .comment("Comma-separated recent colours from the ESC colour picker. Default: empty.")
            .define("recentColors", "");
        SAVED_COLORS = client
            .comment("Comma-separated saved custom colours from the ESC colour picker. Default: empty.")
            .define("savedColors", "");
        client.pop();

        // ========== CLIENT: VISUAL / MOTION ==========
        client.comment(
                "============================================================",
                "VISUAL QUALITY AND MOTION (CLIENT ONLY)",
                "Q / Motion / backdrop / UI scale and sounds on this machine.",
                "============================================================"
            )
            .push("visual");
        QUALITY_MODE = client
            .comment(
                "----- START HERE: QUALITY -----",
                "OFF, LOW, MEDIUM, HIGH — visual budget for ESC effects.",
                "OFF: none. LOW: light scale. MEDIUM: shadows/glow/CRT/border sweeps.",
                "HIGH: also enables the animated backdrop (needs Motion ON).",
                "Default: HIGH (so Growth backdrop draws on fresh install)."
            )
            .define("quality", "HIGH");
        REDUCED_MOTION = client
            .comment(
                "true (Motion OFF in hub): freeze backdrop/border animation;",
                "backdrop still visible at Q=HIGH. Default: false."
            )
            .define("reducedMotion", false);
        BACKDROP_STYLE = client
            .comment(
                "Animated backdrop when Q=HIGH and Motion ON:",
                "GRID, SCAN, RAIN, PULSE, SPARKS, MATRIX, OCEAN, CLOUDS,",
                "AURORA, EMBER, STARFIELD, LIGHTNING, GROWTH. Default: GROWTH."
            )
            .define("backdropStyle", "GROWTH");
        UI_SCALE = client
            .comment("ESC UI scale multiplier. Default: 1.0.")
            .defineInRange("uiScale", 1.0, 0.75, 1.5);
        UI_SOUNDS = client
            .comment("Play ESC UI sound cues (hub nav, buttons, expand/collapse). Default: true.")
            .define("uiSounds", true);
        UI_SOUND_VOLUME = client
            .comment("Master multiplier for ESC UI sounds (0–1). Default: 0.85.")
            .defineInRange("uiSoundVolume", 0.85, 0.0, 1.0);
        LAYOUT_DEBUG = client
            .comment("Draw EscScreen content/footer bounds (dev aid). Default: false.")
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

    @Mod.EventBusSubscriber(modid = Extraspecialcore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
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
