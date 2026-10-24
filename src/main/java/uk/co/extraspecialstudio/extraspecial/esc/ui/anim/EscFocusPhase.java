package uk.co.extraspecialstudio.extraspecial.esc.ui.anim;

/**
 * First-class focus visual state for ESC cards and nodes.
 */
public enum EscFocusPhase {
    UNFOCUSED,
    APPROACHING,
    FOCUSED,
    SETTLING;

    /** Map a 0–1 focus blend into a phase. */
    public static EscFocusPhase fromFocus01(float focus01) {
        float f = EscAnim.clamp01(focus01);
        if (f < 0.2f) return UNFOCUSED;
        if (f < 0.55f) return APPROACHING;
        if (f < 0.92f) return FOCUSED;
        return SETTLING;
    }

    public float scaleMul() {
                switch (this) {
            case UNFOCUSED:
                {
                    return 0.86f;
                }
            case APPROACHING:
                {
                    return 1.02f;
                }
            case FOCUSED:
                {
                    return 1.14f;
                }
            case SETTLING:
                {
                    return 1.10f;
                }
        }
        throw new IllegalStateException();
    }

    public float alphaMul() {
                switch (this) {
            case UNFOCUSED:
                {
                    return 0.48f;
                }
            case APPROACHING:
                {
                    return 0.78f;
                }
            case FOCUSED:
                {
                    return 1f;
                }
            case SETTLING:
                {
                    return 0.96f;
                }
        }
        throw new IllegalStateException();
    }

    public float glowMul() {
                switch (this) {
            case UNFOCUSED:
                {
                    return 0f;
                }
            case APPROACHING:
                {
                    return 0.35f;
                }
            case FOCUSED:
                {
                    return 1f;
                }
            case SETTLING:
                {
                    return 0.7f;
                }
        }
        throw new IllegalStateException();
    }

    public float shadowMul() {
                switch (this) {
            case UNFOCUSED:
                {
                    return 0.25f;
                }
            case APPROACHING:
                {
                    return 0.55f;
                }
            case FOCUSED:
                {
                    return 1f;
                }
            case SETTLING:
                {
                    return 0.85f;
                }
        }
        throw new IllegalStateException();
    }
}
