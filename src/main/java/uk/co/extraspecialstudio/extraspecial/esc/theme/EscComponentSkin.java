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
        return switch (this) {
            case TERMINAL, HUD -> EscFrameStyle.BRACKET;
            case CHAMFERED -> EscFrameStyle.CHAMFER;
            case PILL, MINIMAL -> EscFrameStyle.ROUNDED_SOFT;
            case OUTLINE, DEFAULT -> EscFrameStyle.SQUARE;
        };
    }

    public float opacityBias() {
        return switch (this) {
            case MINIMAL, HUD -> -0.08f;
            case TERMINAL -> 0.05f;
            default -> 0f;
        };
    }

    public EscTheme apply(EscTheme base) {
        float opacity = Math.max(0.4f, Math.min(1f, base.panelOpacity() + opacityBias()));
        return base.withPresentation(
            opacity, preferredFrame(),
            base.shadowStrength(), base.glowStrength(), base.crtIntensity(), base.motionScale()
        );
    }
}
