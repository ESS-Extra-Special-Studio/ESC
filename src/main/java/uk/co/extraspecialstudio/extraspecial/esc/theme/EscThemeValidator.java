package uk.co.extraspecialstudio.extraspecial.esc.theme;

import java.util.ArrayList;
import java.util.List;

/**
 * Usability clamps / contrast warnings for ESC themes.
 */
public final class EscThemeValidator {
    private EscThemeValidator() {
    }

    public static EscTheme clamp(EscTheme theme) {
        if (theme == null) {
            return EscTheme.vanilla();
        }
        float opacity = clamp01(theme.panelOpacity(), 0.35f, 1f);
        float shadow = clamp01(theme.shadowStrength(), 0f, 1f);
        float glow = clamp01(theme.glowStrength(), 0f, 1f);
        float crt = clamp01(theme.crtIntensity(), 0f, 0.85f);
        float motion = clamp01(theme.motionScale(), 0.1f, 1.5f);
        return theme.withPresentation(opacity, theme.frameStyle(), shadow, glow, crt, motion);
    }

    public static List<String> validate(EscTheme theme) {
        List<String> issues = new ArrayList<>();
        if (theme == null) {
            issues.add("Theme is null");
            return issues;
        }
        if (theme.panelOpacity() < 0.4f) {
            issues.add("Panel opacity very low — text may be hard to read over the world");
        }
        if (luminance(theme.textTitleRgb()) < 0.2 && luminance(theme.panelFillArgb()) < 0.25) {
            issues.add("Title text and panel are both dark — contrast may be poor");
        }
        if (theme.crtIntensity() > 0.7f) {
            issues.add("CRT intensity high — may harm readability");
        }
        if (theme.motionScale() > 1.3f) {
            issues.add("Motion scale high — consider reduced motion for accessibility");
        }
        return issues;
    }

    private static float clamp01(float v, float min, float max) {
        return Math.max(min, Math.min(max, v));
    }

    private static float luminance(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        return (0.2126f * r + 0.7152f * g + 0.0722f * b) / 255f;
    }
}
