package uk.co.extraspecialstudio.extraspecial.esc.ui.anim;

/**
 * Focus / neighbour visual multipliers for hub cards.
 */
public final class EscFocusVisual {

    private EscFocusVisual() {
    }

    public static float scale(boolean focused, boolean neighbour) {
        if (focused) return 1.12f;
        if (neighbour) return 0.88f;
        return 0.72f;
    }

    public static float alpha(boolean focused, boolean neighbour) {
        if (focused) return 1f;
        if (neighbour) return 0.72f;
        return 0.45f;
    }

    public static int applyAlpha(int argb, float alpha) {
        int a = (argb >>> 24) & 0xFF;
        int na = Math.max(0, Math.min(255, Math.round(a * EscAnim.clamp01(alpha))));
        return (na << 24) | (argb & 0x00FFFFFF);
    }

    /** Brighten RGB channels toward white by {@code amount} (0–1). */
    public static int brighten(int argb, float amount) {
        float t = EscAnim.clamp01(amount);
        int a = (argb >>> 24) & 0xFF;
        int r = (argb >>> 16) & 0xFF;
        int g = (argb >>> 8) & 0xFF;
        int b = argb & 0xFF;
        r = Math.min(255, Math.round(r + (255 - r) * t));
        g = Math.min(255, Math.round(g + (255 - g) * t));
        b = Math.min(255, Math.round(b + (255 - b) * t));
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
