package uk.co.extraspecialstudio.extraspecial.esc.theme;

/**
 * Built-in ESC theme presets (defaults + pack shortcuts).
 */
public enum EscThemePreset {
    VANILLA("Vanilla", EscTheme.vanilla()),
    FALLOUT("Fallout", EscTheme.fallout()),
    MAGIC("Magic", EscTheme.magic()),
    APOCALYPSE("Apocalypse", EscTheme.apocalypse()),
    CYBER("Cyber", EscTheme.cyber()),
    CLEAN("Clean", EscTheme.clean()),
    INDUSTRIAL("Industrial", EscTheme.industrial()),
    HIGH_CONTRAST("HighContrast", EscTheme.highContrast()),
    MATRIX("Matrix", EscTheme.matrix(), EscBackdropStyle.MATRIX),
    OCEAN("Ocean", EscTheme.ocean(), EscBackdropStyle.OCEAN),
    CLOUDS("Clouds", EscTheme.clouds(), EscBackdropStyle.CLOUDS),
    GROWTH("Growth", EscTheme.growth(), EscBackdropStyle.GROWTH),
    EMBER("Ember", EscTheme.ember(), EscBackdropStyle.EMBER);

    private final String label;
    private final EscTheme theme;
    private final EscBackdropStyle preferredBackdrop;

    EscThemePreset(String label, EscTheme theme) {
        this(label, theme, null);
    }

    EscThemePreset(String label, EscTheme theme, EscBackdropStyle preferredBackdrop) {
        this.label = label;
        this.theme = theme;
        this.preferredBackdrop = preferredBackdrop;
    }

    public String label() {
        return label;
    }

    public EscTheme theme() {
        return theme;
    }

    /**
     * Optional matching animated backdrop for ESH's composite hub themes.
     * Generic ESC presets leave the current backdrop unchanged.
     */
    public EscBackdropStyle preferredBackdrop() {
        return preferredBackdrop;
    }

    public EscThemePreset next() {
        EscThemePreset[] all = values();
        return all[(ordinal() + 1) % all.length];
    }

    public static EscThemePreset byName(String raw) {
        if (raw == null || raw.isBlank()) {
            return GROWTH;
        }
        String key = raw.trim();
        for (EscThemePreset p : values()) {
            if (p.name().equalsIgnoreCase(key) || p.label.equalsIgnoreCase(key)) {
                return p;
            }
        }
        return VANILLA;
    }
}
