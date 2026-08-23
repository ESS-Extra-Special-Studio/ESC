package uk.co.extraspecialstudio.extraspecial.esc.theme;

/**
 * Player-customisable ESC colour slots.
 */
public enum EscColorRole {
    BORDER("Border"),
    TEXT("Text"),
    ACCENT("Accent");

    private final String label;

    EscColorRole(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
