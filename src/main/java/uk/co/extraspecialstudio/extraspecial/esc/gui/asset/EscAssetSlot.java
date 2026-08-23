package uk.co.extraspecialstudio.extraspecial.esc.gui.asset;

/**
 * Layout band a profile component occupies on {@link uk.co.extraspecialstudio.extraspecial.esc.gui.EscGuiScreen}.
 */
public enum EscAssetSlot {
    TITLE,
    HEADER,
    BODY,
    FOOTER,
    SIDEBAR,
    NAV,
    DETAIL,
    BACKDROP,
    FULL;

    public static EscAssetSlot parse(String raw) {
        if (raw == null || raw.isBlank()) {
            return BODY;
        }
        try {
            return valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return switch (raw.trim().toLowerCase()) {
                case "title" -> TITLE;
                case "header" -> HEADER;
                case "footer" -> FOOTER;
                case "sidebar" -> SIDEBAR;
                case "nav" -> NAV;
                case "detail" -> DETAIL;
                case "backdrop" -> BACKDROP;
                case "full" -> FULL;
                default -> BODY;
            };
        }
    }
}
