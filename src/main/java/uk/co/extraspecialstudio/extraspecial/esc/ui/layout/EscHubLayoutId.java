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
                switch (this) {
            case CAROUSEL:
                {
                    return "Carousel";
                }
            case CONTROL_CENTRE:
                {
                    return "Control Centre";
                }
            case DASHBOARD:
                {
                    return "Dashboard";
                }
            case ORBITAL:
                {
                    return "Orbital";
                }
            case CLASSIC:
                {
                    return "Classic";
                }
        }
        throw new IllegalStateException();
    }

    public EscHubLayoutId next() {
        EscHubLayoutId[] all = values();
        return all[(ordinal() + 1) % all.length];
    }
}
