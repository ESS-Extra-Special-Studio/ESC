package uk.co.extraspecialstudio.extraspecial.esc.theme;

/**
 * Reusable visual skins for cards/buttons.
 */
public enum EscComponentSkin {
    DEFAULT,
    TERMINAL,
    PILL,
    OUTLINE,
    CHAMFERED,
    MINIMAL,
    HUD;

    public EscFrameStyle preferredFrame() {
                switch (this) {
            case TERMINAL:
            case HUD:
                {
                    return EscFrameStyle.BRACKET;
                }
            case CHAMFERED:
                {
                    return EscFrameStyle.CHAMFER;
                }
            case PILL:
            case MINIMAL:
                {
                    return EscFrameStyle.ROUNDED_SOFT;
                }
            case OUTLINE:
            case DEFAULT:
                {
                    return EscFrameStyle.SQUARE;
                }
        }
        throw new IllegalStateException();
    }

    public float opacityBias() {
                switch (this) {
            case MINIMAL:
            case HUD:
                {
                    return -0.08f;
                }
            case TERMINAL:
                {
                    return 0.05f;
                }
            default:
                {
                    return 0f;
                }
        }
    }

    public EscTheme apply(EscTheme base) {
        float opacity = Math.max(0.4f, Math.min(1f, base.panelOpacity() + opacityBias()));
        return base.withPresentation(
            opacity, preferredFrame(),
            base.shadowStrength(), base.glowStrength(), base.crtIntensity(), base.motionScale()
        );
    }
}
