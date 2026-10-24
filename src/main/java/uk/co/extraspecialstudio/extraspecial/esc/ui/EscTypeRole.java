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
                switch (this) {
            case TITLE:
                {
                    return 0.5f;
                }
            case META:
            case NUMERIC:
                {
                    return 0.25f;
                }
            default:
                {
                    return 0f;
                }
        }
    }

    public int color(EscUiStyle style) {
                switch (this) {
            case TITLE:
                {
                    return style.textColorTitle();
                }
            case SUBTITLE:
            case BODY:
                {
                    return style.textColorBody();
                }
            case META:
                {
                    return style.mutedColor();
                }
            case NUMERIC:
                {
                    return style.accentColor() & 0xFFFFFF;
                }
        }
        throw new IllegalStateException();
    }
}
