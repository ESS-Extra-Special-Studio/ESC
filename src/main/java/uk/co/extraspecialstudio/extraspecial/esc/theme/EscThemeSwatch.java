package uk.co.extraspecialstudio.extraspecial.esc.theme;

/**
 * Player-cycleable colour swatches (border / text / accent knobs).
 */
public enum EscThemeSwatch {
    WHITE("White", 0xFFFFFFFF, 0xFFFFFF, 0xE0E0E0, 0xFF66A0FF),
    AMBER("Amber", 0xFFFFCC66, 0xFFCC66, 0xE0B84A, 0xFFFFAA33),
    CYAN("Cyan", 0xFF66E0FF, 0x66E0FF, 0x4AB8D0, 0xFF33CCFF),
    GREEN("Green", 0xFF33FF66, 0x33FF66, 0x2ACC52, 0xFF00FF41),
    MAGENTA("Magenta", 0xFFFF66CC, 0xFF66CC, 0xD04AA0, 0xFFFF33AA),
    RED("Red", 0xFFFF6666, 0xFF6666, 0xD04A4A, 0xFFFF4030),
    GOLD("Gold", 0xFFFFD700, 0xFFD700, 0xD0B000, 0xFFFFC000),
    PURPLE("Purple", 0xFFB066FF, 0xD0A0FF, 0xA080D0, 0xFF8844FF),
    RUST("Rust", 0xFFB05030, 0xC08060, 0x906040, 0xFFD04020),
    SOFT("Soft", 0xFFB0B0B0, 0xC0C0C0, 0x909090, 0xFF808080),
    // Kept at the end so existing saved swatch ordinals retain their colours.
    BLACK("Black", 0xFF000000, 0x000000, 0x202020, 0xFF000000);

    private final String label;
    private final int borderArgb;
    private final int titleRgb;
    private final int bodyRgb;
    private final int accentArgb;

    EscThemeSwatch(String label, int borderArgb, int titleRgb, int bodyRgb, int accentArgb) {
        this.label = label;
        this.borderArgb = borderArgb;
        this.titleRgb = titleRgb;
        this.bodyRgb = bodyRgb;
        this.accentArgb = accentArgb;
    }

    public String label() {
        return label;
    }

    public int borderArgb() {
        return borderArgb;
    }

    public int titleRgb() {
        return titleRgb;
    }

    public int bodyRgb() {
        return bodyRgb;
    }

    public int accentArgb() {
        return accentArgb;
    }

    public EscThemeSwatch next() {
        EscThemeSwatch[] all = values();
        return all[(ordinal() + 1) % all.length];
    }

    public static EscThemeSwatch byIndex(int index) {
        EscThemeSwatch[] all = values();
        return all[Math.floorMod(index, all.length)];
    }

    public static EscThemeSwatch byName(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        String key = raw.trim();
        for (EscThemeSwatch s : values()) {
            if (s.name().equalsIgnoreCase(key) || s.label.equalsIgnoreCase(key)) {
                return s;
            }
        }
        return null;
    }
}
