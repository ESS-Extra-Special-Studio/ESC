package uk.co.extraspecialstudio.extraspecial.esc.theme;

/**
 * Procedural panel frame styles (no textures required).
 */
public enum EscFrameStyle {
    SQUARE,
    ROUNDED_SOFT,
    CHAMFER,
    CUT_CORNER,
    BRACKET;

    public static EscFrameStyle byName(String raw) {
        if (raw == null || raw.isBlank()) {
            return SQUARE;
        }
        try {
            return valueOf(raw.trim().toUpperCase());
        } catch (Exception e) {
            return SQUARE;
        }
    }
}
