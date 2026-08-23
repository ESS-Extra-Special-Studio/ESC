package uk.co.extraspecialstudio.extraspecial.esc.theme;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ESC theme stack:
 * <pre>
 *   ESC built-in defaults
 *          ↓
 *   Modpack / server theme
 *          ↓
 *   Application preferences (per modId)
 *          ↓
 *   Player personal override (if unlocked; hub-only by default, optional global)
 * </pre>
 * Mods call {@link #resolve()} / {@link #style()} — they do not need to know about packs.
 */
public final class EscThemeManager {
    private static volatile PackTheme packFromServer;
    private static final Map<String, EscTheme> APPLICATION = new ConcurrentHashMap<>();
    private static volatile int appGeneration;
    private static volatile int packGeneration;
    private static final ConcurrentHashMap<String, CachedResolve> CACHE = new ConcurrentHashMap<>();

    private EscThemeManager() {
    }

    public record PackTheme(boolean override, boolean lockPlayers, EscTheme theme) {
    }

    private record CachedResolve(long stamp, EscThemeResolved resolved, uk.co.extraspecialstudio.extraspecial.esc.ui.EscUiStyle style) {
    }

    /** Dedicated-server push (cleared on client logout). */
    public static void setServerPackTheme(PackTheme pack) {
        packFromServer = pack;
        packGeneration++;
        invalidateCache();
    }

    public static void clearServerPackTheme() {
        packFromServer = null;
        packGeneration++;
        invalidateCache();
    }

    /**
     * Optional per-application theme (e.g. a Pip-Boy screen wanting Fallout metrics).
     * Applied after pack theme when the pack is not overriding, or always as a soft layer
     * when pack override is off.
     */
    public static void registerApplicationTheme(String applicationId, EscTheme theme) {
        if (applicationId == null || applicationId.isBlank() || theme == null) {
            return;
        }
        APPLICATION.put(applicationId, EscThemeValidator.clamp(theme));
        appGeneration++;
        invalidateCache();
    }

    /** Soft application overlay — only set fields on the partial replace underlying tokens. */
    public static void registerApplicationPartial(String applicationId, EscThemePartial partial) {
        if (applicationId == null || applicationId.isBlank() || partial == null) {
            return;
        }
        EscTheme base = APPLICATION.getOrDefault(applicationId, EscThemePreset.byName(EscThemeConfigs.defaultPreset()).theme());
        APPLICATION.put(applicationId, EscThemeValidator.clamp(base.overlay(partial)));
        appGeneration++;
        invalidateCache();
    }

    public static void clearApplicationTheme(String applicationId) {
        if (applicationId != null) {
            APPLICATION.remove(applicationId);
            appGeneration++;
            invalidateCache();
        }
    }

    /** Drop cached resolve/style entries (player knobs, quality, etc.). */
    public static void invalidateCache() {
        CACHE.clear();
    }

    public static EscThemeResolved resolve() {
        return resolve(null);
    }

    public static EscThemeResolved resolve(String applicationId) {
        return cached(applicationId).resolved();
    }

    /** Resolved theme without the player layer (for seeding personal swatches). */
    public static EscTheme resolveBase(String applicationId) {
        EscTheme theme = EscThemePreset.byName(EscThemeConfigs.defaultPreset()).theme();
        boolean locked = false;
        PackTheme pack = effectivePack();
        if (pack != null && pack.override()) {
            theme = pack.theme();
            locked = pack.lockPlayers();
        }
        if (!locked && applicationId != null && !applicationId.isBlank()) {
            EscTheme app = APPLICATION.get(applicationId);
            if (app != null) {
                theme = app;
            }
        }
        return theme;
    }

    public static uk.co.extraspecialstudio.extraspecial.esc.ui.EscUiStyle style() {
        return style(null);
    }

    public static uk.co.extraspecialstudio.extraspecial.esc.ui.EscUiStyle style(String applicationId) {
        return cached(applicationId).style();
    }

    public static boolean canPlayerCustomize() {
        PackTheme pack = effectivePack();
        return pack == null || !pack.override() || !pack.lockPlayers();
    }

    /** True when a modpack/server pack theme is currently winning the stack. */
    public static boolean isPackOverrideActive() {
        PackTheme pack = effectivePack();
        return pack != null && pack.override();
    }

    /**
     * True when pack/server has locked player colour knobs.
     * Quality / Motion / BG remain personal client prefs either way.
     */
    public static boolean isPlayerThemeLocked() {
        return !canPlayerCustomize();
    }

    private static CachedResolve cached(String applicationId) {
        String key = applicationId == null || applicationId.isBlank() ? "" : applicationId;
        long stamp = configStamp(key);
        CachedResolve hit = CACHE.get(key);
        if (hit != null && hit.stamp() == stamp) {
            return hit;
        }
        EscThemeResolved resolved = resolveFresh(applicationId);
        uk.co.extraspecialstudio.extraspecial.esc.ui.EscUiStyle style =
            uk.co.extraspecialstudio.extraspecial.esc.ui.EscUiStyle.fromTheme(resolved.theme());
        CachedResolve built = new CachedResolve(stamp, resolved, style);
        CACHE.put(key, built);
        return built;
    }

    private static long configStamp(String applicationIdKey) {
        long s = appGeneration * 1_000_003L + packGeneration;
        s = s * 31 + Objects.hashCode(EscThemeConfigs.defaultPreset());
        s = s * 31 + (EscThemeConfigs.playerCustomEnabled() ? 1 : 0);
        s = s * 31 + (EscThemeConfigs.playerApplyGlobal() ? 1 : 0);
        s = s * 31 + EscThemeConfigs.playerBorderIndex();
        s = s * 31 + EscThemeConfigs.playerTextIndex();
        s = s * 31 + EscThemeConfigs.playerAccentIndex();
        s = s * 31 + Objects.hashCode(EscThemeConfigs.playerBorderColorRaw());
        s = s * 31 + Objects.hashCode(EscThemeConfigs.playerTextColorRaw());
        s = s * 31 + Objects.hashCode(EscThemeConfigs.playerAccentColorRaw());
        s = s * 31 + Objects.hashCode(EscThemeConfigs.qualityMode().name());
        s = s * 31 + EscThemeConfigs.packConfigStamp();
        s = s * 31 + (EscThemeConfigs.playerAppliesTo(applicationIdKey.isEmpty() ? null : applicationIdKey) ? 1 : 0);
        EscTheme app = APPLICATION.get(applicationIdKey);
        if (app != null) {
            s = s * 31 + System.identityHashCode(app);
        }
        return s;
    }

    private static EscThemeResolved resolveFresh(String applicationId) {
        boolean eshScope = !EscThemeConfigs.playerApplyGlobal()
            && (applicationId == null || !applicationId.equals("extraspecialhub"));
        EscTheme theme = eshScope
            ? EscThemePreset.byName("VANILLA").theme()
            : EscThemePreset.byName(EscThemeConfigs.defaultPreset()).theme();
        boolean packActive = false;
        boolean locked = false;

        PackTheme pack = effectivePack();
        if (pack != null && pack.override()) {
            theme = pack.theme();
            packActive = true;
            locked = pack.lockPlayers();
        }

        if (!locked && applicationId != null && !applicationId.isBlank()) {
            EscTheme app = APPLICATION.get(applicationId);
            if (app != null) {
                theme = app;
            }
        }

        boolean playerActive = false;
        if (!locked && EscThemeConfigs.playerAppliesTo(applicationId)) {
            theme = applyPlayerSwatches(theme);
            playerActive = true;
        }

        return new EscThemeResolved(theme, packActive, locked, playerActive);
    }

    private static PackTheme effectivePack() {
        if (packFromServer != null && packFromServer.override()) {
            return packFromServer;
        }
        return EscThemeConfigs.readPackTheme();
    }

    private static EscTheme applyPlayerSwatches(EscTheme base) {
        EscThemeSwatch borderSw = EscThemeSwatch.byIndex(EscThemeConfigs.playerBorderIndex());
        EscThemeSwatch textSw = EscThemeSwatch.byIndex(EscThemeConfigs.playerTextIndex());
        EscThemeSwatch accentSw = EscThemeSwatch.byIndex(EscThemeConfigs.playerAccentIndex());

        int border = resolvePlayerArgb(EscThemeConfigs.playerBorderColorRaw(), borderSw.borderArgb());
        Integer textCustom = EscColor.parseHex(EscThemeConfigs.playerTextColorRaw());
        int title = textCustom != null ? EscColor.rgb(textCustom) : (textSw.titleRgb() & 0xFFFFFF);
        int body = textCustom != null ? EscColor.dim(title) : (textSw.bodyRgb() & 0xFFFFFF);
        int accent = resolvePlayerArgb(EscThemeConfigs.playerAccentColorRaw(), accentSw.accentArgb());
        int muted = darkenRgb(body, 0.55f);
        // Focus / secondary follow accent so recolour knobs hit selection chrome too.
        return new EscTheme(
            base.name() + "+Player",
            border,
            title,
            body,
            accent,
            base.panelFillArgb(),
            accent,
            accent,
            muted,
            base.panelOpacity(),
            base.frameStyle(),
            base.shadowStrength(),
            base.glowStrength(),
            base.crtIntensity(),
            base.motionScale()
        );
    }

    private static int resolvePlayerArgb(String raw, int fallbackArgb) {
        Integer custom = EscColor.parseHex(raw);
        return custom != null ? custom : fallbackArgb;
    }

    private static int darkenRgb(int rgb, float factor) {
        int r = Math.round(((rgb >> 16) & 0xFF) * factor);
        int g = Math.round(((rgb >> 8) & 0xFF) * factor);
        int b = Math.round((rgb & 0xFF) * factor);
        return (Math.min(255, r) << 16) | (Math.min(255, g) << 8) | Math.min(255, b);
    }
}
