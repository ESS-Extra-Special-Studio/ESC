package uk.co.extraspecialstudio.extraspecial.esc.gui.asset;

/**
 * Palette grouping for ESC GUI assets (runtime and optional ESG Asset Library).
 */
public enum EscAssetCategory {
    CORE,
    NAVIGATION,
    EFFECTS,
    ESH,
    CUSTOM;

    public String displayName() {
                switch (this) {
            case CORE:
                {
                    return "Core";
                }
            case NAVIGATION:
                {
                    return "Navigation";
                }
            case EFFECTS:
                {
                    return "Effects";
                }
            case ESH:
                {
                    return "ESH";
                }
            case CUSTOM:
                {
                    return "Custom";
                }
        }
        throw new IllegalStateException();
    }
}
