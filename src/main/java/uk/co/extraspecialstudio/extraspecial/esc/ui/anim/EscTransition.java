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
                switch (this) {
            case NONE:
                {
                    return 1f;
                }
            case FADE:
            case TERMINAL_BOOT:
            case RADIAL:
            case SCALE:
                {
                    return u;
                }
            case SLIDE:
            case WIPE:
            case PUSH_H:
            case PUSH_V:
                {
                    return Math.min(1f, u * 1.1f);
                }
        }
        throw new IllegalStateException();
    }

    public float applyScale(float t) {
        float u = EscAnim.easeOutCubic(EscAnim.clamp01(t));
                switch (this) {
            case SCALE:
                {
                    return EscAnim.lerp(0.92f, 1f, u);
                }
            case TERMINAL_BOOT:
                {
                    return EscAnim.lerp(0.98f, 1f, u);
                }
            case RADIAL:
                {
                    return EscAnim.lerp(0.85f, 1f, u);
                }
            default:
                {
                    return 1f;
                }
        }
    }

    public int applyOffsetX(float t, int width) {
        float u = EscAnim.easeOutCubic(EscAnim.clamp01(t));
                switch (this) {
            case SLIDE:
            case PUSH_H:
                {
                    return Math.round((1f - u) * width * 0.15f);
                }
            case WIPE:
                {
                    return Math.round((1f - u) * width * 0.08f);
                }
            default:
                {
                    return 0;
                }
        }
    }

    public int applyOffsetY(float t, int height) {
        float u = EscAnim.easeOutCubic(EscAnim.clamp01(t));
                switch (this) {
            case PUSH_V:
                {
                    return Math.round((1f - u) * height * 0.12f);
                }
            case TERMINAL_BOOT:
                {
                    return Math.round((1f - u) * 12);
                }
            default:
                {
                    return 0;
                }
        }
    }

    public EscTransition next() {
        EscTransition[] all = values();
        return all[(ordinal() + 1) % all.length];
    }
}
