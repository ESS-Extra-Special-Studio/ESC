package uk.co.extraspecialstudio.extraspecial.esc.ui;

import uk.co.extraspecialstudio.extraspecial.esc.theme.EscFrameStyle;
import uk.co.extraspecialstudio.extraspecial.esc.theme.EscTheme;
import uk.co.extraspecialstudio.extraspecial.esc.theme.EscThemeManager;

/**
 * Visual metrics for ESC UI helpers. Prefer {@link #active()} so pack/player themes apply automatically.
 */
public final class EscUiStyle {
    private final int screenMarginH;
    private final int screenMarginV;
    private final int titleGap;
    private final int panelFillColor;
    private final int panelBorderColor;
    private final int panelBorderWidth;
    private final int defaultButtonWidth;
    private final int defaultButtonHeight;
    private final int listRowHeight;
    private final int textColorTitle;
    private final int textColorBody;
    private final int footerReserve;
    private final int listScrollbarGutter;
    private final int accentColor;
    private final int focusBorderColor;
    private final int secondaryColor;
    private final int mutedColor;
    private final float panelOpacity;
    private final EscFrameStyle frameStyle;
    private final float shadowStrength;
    private final float glowStrength;
    private final float crtIntensity;
    private final float motionScale;
    private final EscTheme theme;

    public EscUiStyle(int screenMarginH, int screenMarginV, int titleGap, int panelFillColor, int panelBorderColor, int panelBorderWidth, int defaultButtonWidth, int defaultButtonHeight, int listRowHeight, int textColorTitle, int textColorBody, int footerReserve, int listScrollbarGutter, int accentColor, int focusBorderColor, int secondaryColor, int mutedColor, float panelOpacity, EscFrameStyle frameStyle, float shadowStrength, float glowStrength, float crtIntensity, float motionScale, EscTheme theme) {
        this.screenMarginH = screenMarginH;
        this.screenMarginV = screenMarginV;
        this.titleGap = titleGap;
        this.panelFillColor = panelFillColor;
        this.panelBorderColor = panelBorderColor;
        this.panelBorderWidth = panelBorderWidth;
        this.defaultButtonWidth = defaultButtonWidth;
        this.defaultButtonHeight = defaultButtonHeight;
        this.listRowHeight = listRowHeight;
        this.textColorTitle = textColorTitle;
        this.textColorBody = textColorBody;
        this.footerReserve = footerReserve;
        this.listScrollbarGutter = listScrollbarGutter;
        this.accentColor = accentColor;
        this.focusBorderColor = focusBorderColor;
        this.secondaryColor = secondaryColor;
        this.mutedColor = mutedColor;
        this.panelOpacity = panelOpacity;
        this.frameStyle = frameStyle;
        this.shadowStrength = shadowStrength;
        this.glowStrength = glowStrength;
        this.crtIntensity = crtIntensity;
        this.motionScale = motionScale;
        this.theme = theme;
    }

    public int screenMarginH() { return this.screenMarginH; }
    public int screenMarginV() { return this.screenMarginV; }
    public int titleGap() { return this.titleGap; }
    public int panelFillColor() { return this.panelFillColor; }
    public int panelBorderColor() { return this.panelBorderColor; }
    public int panelBorderWidth() { return this.panelBorderWidth; }
    public int defaultButtonWidth() { return this.defaultButtonWidth; }
    public int defaultButtonHeight() { return this.defaultButtonHeight; }
    public int listRowHeight() { return this.listRowHeight; }
    public int textColorTitle() { return this.textColorTitle; }
    public int textColorBody() { return this.textColorBody; }
    public int footerReserve() { return this.footerReserve; }
    public int listScrollbarGutter() { return this.listScrollbarGutter; }
    public int accentColor() { return this.accentColor; }
    public int focusBorderColor() { return this.focusBorderColor; }
    public int secondaryColor() { return this.secondaryColor; }
    public int mutedColor() { return this.mutedColor; }
    public float panelOpacity() { return this.panelOpacity; }
    public EscFrameStyle frameStyle() { return this.frameStyle; }
    public float shadowStrength() { return this.shadowStrength; }
    public float glowStrength() { return this.glowStrength; }
    public float crtIntensity() { return this.crtIntensity; }
    public float motionScale() { return this.motionScale; }
    public EscTheme theme() { return this.theme; }


    public static EscUiStyle active() {
        return EscThemeManager.style();
    }

    public static EscUiStyle active(String applicationId) {
        return EscThemeManager.style(applicationId);
    }

    public static EscUiStyle fromTheme(EscTheme theme) {
        EscTheme t = theme == null ? EscTheme.vanilla() : theme;
        EscUiStyle metrics = t.name() != null && t.name().toLowerCase().contains("fallout")
            ? falloutPipMetrics()
            : vanillaMetrics();
        return new EscUiStyle(
            metrics.screenMarginH,
            metrics.screenMarginV,
            metrics.titleGap,
            t.panelFillArgb(),
            t.borderArgb(),
            metrics.panelBorderWidth,
            metrics.defaultButtonWidth,
            metrics.defaultButtonHeight,
            metrics.listRowHeight,
            t.textTitleRgb() & 0xFFFFFF,
            t.textBodyRgb() & 0xFFFFFF,
            metrics.footerReserve,
            metrics.listScrollbarGutter,
            t.accentArgb(),
            t.focusBorderArgb(),
            t.secondaryArgbResolved(),
            t.mutedRgb() & 0xFFFFFF,
            t.panelOpacity(),
            t.frameStyle(),
            t.shadowStrength(),
            t.glowStrength(),
            t.crtIntensity(),
            t.motionScale(),
            t
        );
    }

    public static EscUiStyle vanillaLike() {
        return fromTheme(EscTheme.vanilla());
    }

    public static EscUiStyle falloutPip() {
        return fromTheme(EscTheme.fallout());
    }

    private static EscUiStyle vanillaMetrics() {
        return new EscUiStyle(
            16, 16, 28, 0xC0101010, 0xFFFFFFFF, 1, 120, 20, 20, 0xFFFFFF, 0xE0E0E0, 36, 10,
            0xFF66A0FF, 0xFFFFFFFF, 0xFF88B0FF, 0x909090, 0.88f, EscFrameStyle.SQUARE,
            0.4f, 0.15f, 0f, 1f, EscTheme.vanilla()
        );
    }

    private static EscUiStyle falloutPipMetrics() {
        return new EscUiStyle(
            12, 12, 22, EscFalloutDraw.CRT_BLACK, EscFalloutDraw.PHOSPHOR, 2, 120, 20, 18, 0x00FF41, 0x88CC66, 52, 10,
            0xFFFFCC33, EscFalloutDraw.PHOSPHOR, 0xFF88CC66, 0x448844, 0.92f, EscFrameStyle.CHAMFER,
            0.55f, 0.65f, 0.45f, 0.9f, EscTheme.fallout()
        );
    }

    public EscUiStyle withPanelBorderColor(int panelBorderColor) {
        return new EscUiStyle(
            screenMarginH, screenMarginV, titleGap, panelFillColor, panelBorderColor, panelBorderWidth,
            defaultButtonWidth, defaultButtonHeight, listRowHeight, textColorTitle, textColorBody,
            footerReserve, listScrollbarGutter, accentColor, focusBorderColor, secondaryColor, mutedColor,
            panelOpacity, frameStyle, shadowStrength, glowStrength, crtIntensity, motionScale, theme
        );
    }

    public EscUiStyle withTextColors(int textColorTitle, int textColorBody) {
        return new EscUiStyle(
            screenMarginH, screenMarginV, titleGap, panelFillColor, panelBorderColor, panelBorderWidth,
            defaultButtonWidth, defaultButtonHeight, listRowHeight, textColorTitle, textColorBody,
            footerReserve, listScrollbarGutter, accentColor, focusBorderColor, secondaryColor, mutedColor,
            panelOpacity, frameStyle, shadowStrength, glowStrength, crtIntensity, motionScale, theme
        );
    }

    public EscUiStyle withAccentColor(int accentColor) {
        return new EscUiStyle(
            screenMarginH, screenMarginV, titleGap, panelFillColor, panelBorderColor, panelBorderWidth,
            defaultButtonWidth, defaultButtonHeight, listRowHeight, textColorTitle, textColorBody,
            footerReserve, listScrollbarGutter, accentColor, focusBorderColor, secondaryColor, mutedColor,
            panelOpacity, frameStyle, shadowStrength, glowStrength, crtIntensity, motionScale, theme
        );
    }
}
