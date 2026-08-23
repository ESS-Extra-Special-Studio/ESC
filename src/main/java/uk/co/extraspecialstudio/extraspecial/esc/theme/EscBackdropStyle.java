package uk.co.extraspecialstudio.extraspecial.esc.theme;

/**
 * Animated ESC backdrop styles (drawn only when Quality is HIGH and Motion is ON).
 */
public enum EscBackdropStyle {
    GRID("Grid"),
    SCAN("Scan"),
    RAIN("Rain"),
    PULSE("Pulse"),
    SPARKS("Sparks"),
    MATRIX("Matrix"),
    OCEAN("Ocean"),
    CLOUDS("Clouds"),
    AURORA("Aurora"),
    EMBER("Ember"),
    STARFIELD("Stars"),
    LIGHTNING("Bolt"),
    GROWTH("Growth");

    private final String label;

    EscBackdropStyle(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    public EscBackdropStyle next() {
        EscBackdropStyle[] all = values();
        return all[(ordinal() + 1) % all.length];
    }

    public static EscBackdropStyle byName(String raw) {
        if (raw == null || raw.isBlank()) {
            return GROWTH;
        }
        try {
            return valueOf(raw.trim().toUpperCase());
        } catch (Exception e) {
            return GROWTH;
        }
    }
}
