package uk.co.extraspecialstudio.extraspecial.esc.ui.layout;

/**
 * Selectable hub presentation profiles.
 */
public enum EscHubLayoutId {
    CAROUSEL,
    CONTROL_CENTRE,
    DASHBOARD,
    ORBITAL,
    CLASSIC;

    public String displayName() {
        return switch (this) {
            case CAROUSEL -> "Carousel";
            case CONTROL_CENTRE -> "Control Centre";
            case DASHBOARD -> "Dashboard";
            case ORBITAL -> "Orbital";
            case CLASSIC -> "Classic";
        };
    }

    public EscHubLayoutId next() {
        EscHubLayoutId[] all = values();
        return all[(ordinal() + 1) % all.length];
    }
}
