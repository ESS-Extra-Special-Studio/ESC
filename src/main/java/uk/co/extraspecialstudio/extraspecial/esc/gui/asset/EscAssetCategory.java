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
        return switch (this) {
            case CORE -> "Core";
            case NAVIGATION -> "Navigation";
            case EFFECTS -> "Effects";
            case ESH -> "ESH";
            case CUSTOM -> "Custom";
        };
    }
}
