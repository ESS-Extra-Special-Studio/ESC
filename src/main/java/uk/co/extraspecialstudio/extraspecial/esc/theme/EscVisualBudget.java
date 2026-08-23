package uk.co.extraspecialstudio.extraspecial.esc.theme;

/**
 * Runtime quality / motion gates for ESC draw helpers.
 */
public final class EscVisualBudget {
    private EscVisualBudget() {
    }

    public static EscQualityMode quality() {
        return EscThemeConfigs.qualityMode();
    }

    public static boolean reducedMotion() {
        return EscThemeConfigs.reducedMotion();
    }

    public static float motionScale(EscTheme theme) {
        float q = quality().effectScale();
        float t = theme == null ? 1f : theme.motionScale();
        if (reducedMotion()) {
            return 0.15f * t;
        }
        return q * t;
    }

    public static float shadow(EscTheme theme) {
        if (!quality().shadows() || theme == null) return 0f;
        return theme.shadowStrength() * quality().effectScale();
    }

    public static float glow(EscTheme theme) {
        if (!quality().glow() || theme == null) return 0f;
        return theme.glowStrength() * quality().effectScale();
    }

    public static float crt(EscTheme theme) {
        if (!quality().crt() || theme == null) return 0f;
        return theme.crtIntensity() * quality().effectScale();
    }

    public static boolean animatedBorders() {
        return quality().animatedBorders() && !reducedMotion();
    }

    /** True when Q=HIGH — hub backdrop art is drawn (frozen when Motion is OFF). */
    public static boolean showDynamicBackground() {
        return quality().dynamicBackground();
    }

    /** True when backdrop animation should advance (Q=HIGH and Motion ON). */
    public static boolean dynamicBackground() {
        return showDynamicBackground() && !reducedMotion();
    }
}
