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
        if (raw == null || raw.trim().isEmpty()) {
            return BODY;
        }
        try {
            return valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
                        switch (raw.trim().toLowerCase()) {
                case "title":
                    {
                        return TITLE;
                    }
                case "header":
                    {
                        return HEADER;
                    }
                case "footer":
                    {
                        return FOOTER;
                    }
                case "sidebar":
                    {
                        return SIDEBAR;
                    }
                case "nav":
                    {
                        return NAV;
                    }
                case "detail":
                    {
                        return DETAIL;
                    }
                case "backdrop":
                    {
                        return BACKDROP;
                    }
                case "full":
                    {
                        return FULL;
                    }
                default:
                    {
                        return BODY;
                    }
            }
        }
    }
}
