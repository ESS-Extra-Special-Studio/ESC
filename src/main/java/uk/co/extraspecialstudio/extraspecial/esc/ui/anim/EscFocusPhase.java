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
        return switch (this) {
            case UNFOCUSED -> 0.86f;
            case APPROACHING -> 1.02f;
            case FOCUSED -> 1.14f;
            case SETTLING -> 1.10f;
        };
    }

    public float alphaMul() {
        return switch (this) {
            case UNFOCUSED -> 0.48f;
            case APPROACHING -> 0.78f;
            case FOCUSED -> 1f;
            case SETTLING -> 0.96f;
        };
    }

    public float glowMul() {
        return switch (this) {
            case UNFOCUSED -> 0f;
            case APPROACHING -> 0.35f;
            case FOCUSED -> 1f;
            case SETTLING -> 0.7f;
        };
    }

    public float shadowMul() {
        return switch (this) {
            case UNFOCUSED -> 0.25f;
            case APPROACHING -> 0.55f;
            case FOCUSED -> 1f;
            case SETTLING -> 0.85f;
        };
    }
}
