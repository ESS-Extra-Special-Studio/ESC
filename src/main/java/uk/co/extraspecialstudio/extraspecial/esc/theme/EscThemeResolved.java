package uk.co.extraspecialstudio.extraspecial.esc.theme;

/**
 * Result of resolving the ESC theme stack.
 */
public final class EscThemeResolved {
    private final EscTheme theme;
    private final boolean packActive;
    private final boolean playerLocked;
    private final boolean playerActive;

    public EscThemeResolved(EscTheme theme, boolean packActive, boolean playerLocked, boolean playerActive) {
        this.theme = theme;
        this.packActive = packActive;
        this.playerLocked = playerLocked;
        this.playerActive = playerActive;
    }

    public EscTheme theme() { return this.theme; }
    public boolean packActive() { return this.packActive; }
    public boolean playerLocked() { return this.playerLocked; }
    public boolean playerActive() { return this.playerActive; }

    public String borderLabel() {
        Integer custom = EscColor.parseHex(EscThemeConfigs.playerBorderColorRaw());
        if (custom != null && EscColor.rgb(custom) == EscColor.rgb(theme.borderArgb())) {
            return "#" + EscColor.toHexRgb(custom);
        }
        for (EscThemeSwatch sw : EscThemeSwatch.values()) {
            if (sw.borderArgb() == theme.borderArgb()) {
                return sw.label();
            }
        }
        return hexLabel(theme.borderArgb());
    }

    public String textLabel() {
        Integer custom = EscColor.parseHex(EscThemeConfigs.playerTextColorRaw());
        if (custom != null && EscColor.rgb(custom) == (theme.textTitleRgb() & 0xFFFFFF)) {
            return "#" + EscColor.toHexRgb(custom);
        }
        for (EscThemeSwatch sw : EscThemeSwatch.values()) {
            if (sw.titleRgb() == (theme.textTitleRgb() & 0xFFFFFF)) {
                return sw.label();
            }
        }
        return hexLabel(theme.textTitleRgb());
    }

    public String accentLabel() {
        Integer custom = EscColor.parseHex(EscThemeConfigs.playerAccentColorRaw());
        if (custom != null && EscColor.rgb(custom) == EscColor.rgb(theme.accentArgb())) {
            return "#" + EscColor.toHexRgb(custom);
        }
        for (EscThemeSwatch sw : EscThemeSwatch.values()) {
            if (sw.accentArgb() == theme.accentArgb()) {
                return sw.label();
            }
        }
        return hexLabel(theme.accentArgb());
    }

    private static String hexLabel(int color) {
        return String.format("#%06X", color & 0xFFFFFF);
    }
}
