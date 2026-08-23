package uk.co.extraspecialstudio.extraspecial.esc.theme;

/**
 * Named effect packages mapped onto theme presentation tokens.
 */
public enum EscEffectPreset {
    NONE,
    TERMINAL,
    FALLOUT,
    CLEAN,
    HUD,
    CINEMATIC;

    public EscTheme apply(EscTheme base) {
        return switch (this) {
            case NONE -> base;
            case TERMINAL -> base.withPresentation(
                Math.max(base.panelOpacity(), 0.9f), EscFrameStyle.BRACKET,
                0.4f, 0.55f, 0.5f, 0.85f
            );
            case FALLOUT -> base.withPresentation(
                0.92f, EscFrameStyle.CHAMFER, 0.55f, 0.65f, 0.5f, 0.9f
            );
            case CLEAN -> base.withPresentation(
                0.75f, EscFrameStyle.ROUNDED_SOFT, 0.25f, 0.08f, 0f, 0.75f
            );
            case HUD -> base.withPresentation(
                0.7f, EscFrameStyle.BRACKET, 0.2f, 0.35f, 0.05f, 1.05f
            );
            case CINEMATIC -> base.withPresentation(
                0.85f, EscFrameStyle.ROUNDED_SOFT, 0.7f, 0.8f, 0.15f, 1.15f
            );
        };
    }

    public static EscEffectPreset byName(String raw) {
        if (raw == null || raw.isBlank()) {
            return NONE;
        }
        try {
            return valueOf(raw.trim().toUpperCase());
        } catch (Exception e) {
            return NONE;
        }
    }
}
