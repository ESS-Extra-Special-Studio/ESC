package uk.co.extraspecialstudio.extraspecial.esc.gui.profile;

/**
 * High-level screen composition mode for declarative GUI profiles.
 */
public enum EscGuiLayoutMode {
    CLASSIC,
    SIMPLE,
    FULL;

    public static EscGuiLayoutMode parse(String raw) {
        if (raw == null || raw.isBlank()) {
            return CLASSIC;
        }
        try {
            return valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return CLASSIC;
        }
    }
}
