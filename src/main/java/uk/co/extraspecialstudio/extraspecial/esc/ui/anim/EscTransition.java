package uk.co.extraspecialstudio.extraspecial.esc.ui.anim;

/**
 * Reusable screen / layout transition presets.
 */
public enum EscTransition {
    NONE,
    FADE,
    SLIDE,
    SCALE,
    WIPE,
    PUSH_H,
    PUSH_V,
    RADIAL,
    TERMINAL_BOOT;

    public float applyAlpha(float t) {
        float u = EscAnim.easeOutCubic(EscAnim.clamp01(t));
        return switch (this) {
            case NONE -> 1f;
            case FADE, TERMINAL_BOOT, RADIAL, SCALE -> u;
            case SLIDE, WIPE, PUSH_H, PUSH_V -> Math.min(1f, u * 1.1f);
        };
    }

    public float applyScale(float t) {
        float u = EscAnim.easeOutCubic(EscAnim.clamp01(t));
        return switch (this) {
            case SCALE -> EscAnim.lerp(0.92f, 1f, u);
            case TERMINAL_BOOT -> EscAnim.lerp(0.98f, 1f, u);
            case RADIAL -> EscAnim.lerp(0.85f, 1f, u);
            default -> 1f;
        };
    }

    public int applyOffsetX(float t, int width) {
        float u = EscAnim.easeOutCubic(EscAnim.clamp01(t));
        return switch (this) {
            case SLIDE, PUSH_H -> Math.round((1f - u) * width * 0.15f);
            case WIPE -> Math.round((1f - u) * width * 0.08f);
            default -> 0;
        };
    }

    public int applyOffsetY(float t, int height) {
        float u = EscAnim.easeOutCubic(EscAnim.clamp01(t));
        return switch (this) {
            case PUSH_V -> Math.round((1f - u) * height * 0.12f);
            case TERMINAL_BOOT -> Math.round((1f - u) * 12);
            default -> 0;
        };
    }

    public EscTransition next() {
        EscTransition[] all = values();
        return all[(ordinal() + 1) % all.length];
    }
}
