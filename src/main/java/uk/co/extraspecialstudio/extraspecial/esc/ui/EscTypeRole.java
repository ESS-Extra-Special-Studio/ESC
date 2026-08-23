package uk.co.extraspecialstudio.extraspecial.esc.ui;

/**
 * Typography roles for ESC hierarchy.
 */
public enum EscTypeRole {
    TITLE,
    SUBTITLE,
    BODY,
    META,
    NUMERIC;

    public boolean uppercase() {
        return this == TITLE || this == META;
    }

    public float trackingExtra() {
        return switch (this) {
            case TITLE -> 0.5f;
            case META, NUMERIC -> 0.25f;
            default -> 0f;
        };
    }

    public int color(EscUiStyle style) {
        return switch (this) {
            case TITLE -> style.textColorTitle();
            case SUBTITLE, BODY -> style.textColorBody();
            case META -> style.mutedColor();
            case NUMERIC -> style.accentColor() & 0xFFFFFF;
        };
    }
}
