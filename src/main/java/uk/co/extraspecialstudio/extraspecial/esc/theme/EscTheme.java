package uk.co.extraspecialstudio.extraspecial.esc.theme;

import uk.co.extraspecialstudio.extraspecial.esc.ui.EscFalloutDraw;

/**
 * Resolved ESC visual tokens — colours, glass, frames, and effect budgets.
 * Mods ask for a panel; ESC supplies the look.
 */
public record EscTheme(
    String name,
    int borderArgb,
    int textTitleRgb,
    int textBodyRgb,
    int accentArgb,
    int panelFillArgb,
    int focusBorderArgb,
    int secondaryArgb,
    int mutedRgb,
    float panelOpacity,
    EscFrameStyle frameStyle,
    float shadowStrength,
    float glowStrength,
    float crtIntensity,
    float motionScale
) {
    /** Colour-only convenience for older call sites; presentation defaults to vanilla. */
    public EscTheme(
        String name,
        int borderArgb,
        int textTitleRgb,
        int textBodyRgb,
        int accentArgb,
        int panelFillArgb,
        int focusBorderArgb
    ) {
        this(
            name, borderArgb, textTitleRgb, textBodyRgb, accentArgb, panelFillArgb, focusBorderArgb,
            accentArgb, textBodyRgb & 0xFFFFFF, 0.85f, EscFrameStyle.SQUARE, 0.35f, 0.2f, 0f, 1f
        );
    }

    public int primaryArgb() {
        return borderArgb;
    }

    public int secondaryArgbResolved() {
        return secondaryArgb;
    }

    public EscTheme withBorder(int borderArgb) {
        return copy(borderArgb, textTitleRgb, textBodyRgb, accentArgb, panelFillArgb, focusBorderArgb,
            secondaryArgb, mutedRgb, panelOpacity, frameStyle, shadowStrength, glowStrength, crtIntensity, motionScale, name);
    }

    public EscTheme withText(int textTitleRgb, int textBodyRgb) {
        return copy(borderArgb, textTitleRgb, textBodyRgb, accentArgb, panelFillArgb, focusBorderArgb,
            secondaryArgb, mutedRgb, panelOpacity, frameStyle, shadowStrength, glowStrength, crtIntensity, motionScale, name);
    }

    public EscTheme withAccent(int accentArgb) {
        return copy(borderArgb, textTitleRgb, textBodyRgb, accentArgb, panelFillArgb, focusBorderArgb,
            secondaryArgb, mutedRgb, panelOpacity, frameStyle, shadowStrength, glowStrength, crtIntensity, motionScale, name);
    }

    public EscTheme withPanelFill(int panelFillArgb) {
        return copy(borderArgb, textTitleRgb, textBodyRgb, accentArgb, panelFillArgb, focusBorderArgb,
            secondaryArgb, mutedRgb, panelOpacity, frameStyle, shadowStrength, glowStrength, crtIntensity, motionScale, name);
    }

    public EscTheme withName(String name) {
        return copy(borderArgb, textTitleRgb, textBodyRgb, accentArgb, panelFillArgb, focusBorderArgb,
            secondaryArgb, mutedRgb, panelOpacity, frameStyle, shadowStrength, glowStrength, crtIntensity, motionScale, name);
    }

    public EscTheme withPresentation(
        float panelOpacity,
        EscFrameStyle frameStyle,
        float shadowStrength,
        float glowStrength,
        float crtIntensity,
        float motionScale
    ) {
        return copy(borderArgb, textTitleRgb, textBodyRgb, accentArgb, panelFillArgb, focusBorderArgb,
            secondaryArgb, mutedRgb, panelOpacity, frameStyle, shadowStrength, glowStrength, crtIntensity, motionScale, name);
    }

    public EscTheme withRoles(int secondaryArgb, int mutedRgb) {
        return copy(borderArgb, textTitleRgb, textBodyRgb, accentArgb, panelFillArgb, focusBorderArgb,
            secondaryArgb, mutedRgb, panelOpacity, frameStyle, shadowStrength, glowStrength, crtIntensity, motionScale, name);
    }

    /** Merge non-null / non-NaN fields from {@code over} onto this theme (inheritance). */
    public EscTheme overlay(EscThemePartial over) {
        if (over == null) return this;
        return new EscTheme(
            over.name() != null ? over.name() : name,
            over.borderArgb() != null ? over.borderArgb() : borderArgb,
            over.textTitleRgb() != null ? over.textTitleRgb() : textTitleRgb,
            over.textBodyRgb() != null ? over.textBodyRgb() : textBodyRgb,
            over.accentArgb() != null ? over.accentArgb() : accentArgb,
            over.panelFillArgb() != null ? over.panelFillArgb() : panelFillArgb,
            over.focusBorderArgb() != null ? over.focusBorderArgb() : focusBorderArgb,
            over.secondaryArgb() != null ? over.secondaryArgb() : secondaryArgb,
            over.mutedRgb() != null ? over.mutedRgb() : mutedRgb,
            over.panelOpacity() != null ? over.panelOpacity() : panelOpacity,
            over.frameStyle() != null ? over.frameStyle() : frameStyle,
            over.shadowStrength() != null ? over.shadowStrength() : shadowStrength,
            over.glowStrength() != null ? over.glowStrength() : glowStrength,
            over.crtIntensity() != null ? over.crtIntensity() : crtIntensity,
            over.motionScale() != null ? over.motionScale() : motionScale
        );
    }

    private EscTheme copy(
        int borderArgb, int textTitleRgb, int textBodyRgb, int accentArgb, int panelFillArgb, int focusBorderArgb,
        int secondaryArgb, int mutedRgb, float panelOpacity, EscFrameStyle frameStyle,
        float shadowStrength, float glowStrength, float crtIntensity, float motionScale, String name
    ) {
        return new EscTheme(
            name, borderArgb, textTitleRgb, textBodyRgb, accentArgb, panelFillArgb, focusBorderArgb,
            secondaryArgb, mutedRgb, panelOpacity, frameStyle, shadowStrength, glowStrength, crtIntensity, motionScale
        );
    }

    public static EscTheme vanilla() {
        return new EscTheme(
            "Vanilla", 0xFFFFFFFF, 0xFFFFFF, 0xE0E0E0, 0xFF66A0FF, 0xC0101010, 0xFFFFFFFF,
            0xFF88B0FF, 0x909090, 0.88f, EscFrameStyle.SQUARE, 0.4f, 0.15f, 0f, 1f
        );
    }

    public static EscTheme fallout() {
        return new EscTheme(
            "Fallout", EscFalloutDraw.PHOSPHOR, 0x00FF41, 0x88CC66, 0xFFFFCC33, EscFalloutDraw.CRT_BLACK, EscFalloutDraw.PHOSPHOR,
            0xFF88CC66, 0x448844, 0.92f, EscFrameStyle.CHAMFER, 0.55f, 0.65f, 0.45f, 0.9f
        );
    }

    public static EscTheme magic() {
        return new EscTheme(
            "Magic", 0xFFB066FF, 0xFFFFFF, 0xE0D0FF, 0xFF66E0FF, 0xC0181028, 0xFFD090FF,
            0xFF66E0FF, 0x8060A0, 0.82f, EscFrameStyle.ROUNDED_SOFT, 0.5f, 0.7f, 0.1f, 1.05f
        );
    }

    public static EscTheme apocalypse() {
        return new EscTheme(
            "Apocalypse", 0xFFB05030, 0xC0C0C0, 0x909090, 0xFFFF4030, 0xC0181008, 0xFFFF6040,
            0xFFD04020, 0x606060, 0.9f, EscFrameStyle.CUT_CORNER, 0.7f, 0.35f, 0.2f, 0.85f
        );
    }

    public static EscTheme cyber() {
        return new EscTheme(
            "Cyber", 0xFF00E5FF, 0xE0FFFF, 0x80C0D0, 0xFFFF00AA, 0xC0081018, 0xFF00FFFF,
            0xFFFF00AA, 0x406070, 0.8f, EscFrameStyle.BRACKET, 0.45f, 0.85f, 0.25f, 1.1f
        );
    }

    public static EscTheme clean() {
        return new EscTheme(
            "Clean", 0xFFE8E8E8, 0xF5F5F5, 0xC0C0C0, 0xFF4A90D9, 0xB0141418, 0xFFFFFFFF,
            0xFF4A90D9, 0x808080, 0.75f, EscFrameStyle.ROUNDED_SOFT, 0.25f, 0.1f, 0f, 0.8f
        );
    }

    public static EscTheme industrial() {
        return new EscTheme(
            "Industrial", 0xFFFFAA33, 0xFFE0C080, 0xB09060, 0xFFFF5533, 0xD0101008, 0xFFFFCC66,
            0xFFFF5533, 0x706040, 0.93f, EscFrameStyle.CHAMFER, 0.8f, 0.4f, 0.15f, 0.9f
        );
    }

    public static EscTheme matrix() {
        return new EscTheme(
            "Matrix", 0xFF117A2A, 0xCCFFCC, 0x55CC66, 0xFF00FF41, 0xF0020802, 0xFF33FF66,
            0xFF00CC33, 0x2D7838, 0.94f, EscFrameStyle.BRACKET, 0.35f, 0.6f, 0.35f, 0.72f
        );
    }

    public static EscTheme ocean() {
        return new EscTheme(
            "Ocean", 0xFF4AA8D8, 0xE8F8FF, 0x91CDE8, 0xFF52E0D0, 0xE8041428, 0xFF8CE8FF,
            0xFF3CB8D0, 0x547D94, 0.9f, EscFrameStyle.ROUNDED_SOFT, 0.45f, 0.5f, 0.08f, 0.65f
        );
    }

    /**
     * ESH fresh-install look: sky backdrop with sun-gold chrome.
     * Title/body stay a saturated yellow so type reads on the light-blue sky and on navy glass.
     */
    public static EscTheme clouds() {
        return new EscTheme(
            "Clouds", 0xFFFFD24A, 0xFFE566, 0xF0D060, 0xFFFFC428, 0xCC0A2048, 0xFFFFE566,
            0xFF7EC8F0, 0xC4A84A, 0.82f, EscFrameStyle.ROUNDED_SOFT, 0.4f, 0.45f, 0f, 0.7f
        );
    }

    /**
     * ESH fresh-install look: forest backdrop with ESH green accent and white chrome.
     * Pale leaf title/body stay readable on the dark canopy.
     */
    public static EscTheme growth() {
        return new EscTheme(
            "Growth", 0xFFFFFFFF, 0xE8F8DC, 0xA8CF86, 0xFF3DDC64, 0xEC06160A, 0xFF75E08A,
            0xFF2E7A3A, 0x657854, 0.91f, EscFrameStyle.SQUARE, 0.5f, 0.45f, 0.05f, 0.7f
        );
    }

    public static EscTheme ember() {
        return new EscTheme(
            "Ember", 0xFF9A3E20, 0xFFE6C8A8, 0xC88762, 0xFFFF7A24, 0xEE100604, 0xFFFFA040,
            0xFFD04820, 0x805040, 0.93f, EscFrameStyle.CHAMFER, 0.65f, 0.6f, 0.12f, 0.75f
        );
    }

    public static EscTheme highContrast() {
        return new EscTheme(
            "HighContrast", 0xFFFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF00, 0xFF000000, 0xFFFFFF00,
            0xFFFFFF00, 0xAAAAAA, 1f, EscFrameStyle.SQUARE, 0.2f, 0f, 0f, 0.5f
        );
    }
}
