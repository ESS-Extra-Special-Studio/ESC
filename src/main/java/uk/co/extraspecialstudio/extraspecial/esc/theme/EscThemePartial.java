package uk.co.extraspecialstudio.extraspecial.esc.theme;

/**
 * Partial theme override for inheritance (null = leave underlying value).
 */
public record EscThemePartial(
    String name,
    Integer borderArgb,
    Integer textTitleRgb,
    Integer textBodyRgb,
    Integer accentArgb,
    Integer panelFillArgb,
    Integer focusBorderArgb,
    Integer secondaryArgb,
    Integer mutedRgb,
    Float panelOpacity,
    EscFrameStyle frameStyle,
    Float shadowStrength,
    Float glowStrength,
    Float crtIntensity,
    Float motionScale
) {
    public static EscThemePartial empty() {
        return new EscThemePartial(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String name;
        private Integer borderArgb;
        private Integer textTitleRgb;
        private Integer textBodyRgb;
        private Integer accentArgb;
        private Integer panelFillArgb;
        private Integer focusBorderArgb;
        private Integer secondaryArgb;
        private Integer mutedRgb;
        private Float panelOpacity;
        private EscFrameStyle frameStyle;
        private Float shadowStrength;
        private Float glowStrength;
        private Float crtIntensity;
        private Float motionScale;

        public Builder name(String name) { this.name = name; return this; }
        public Builder border(int v) { this.borderArgb = v; return this; }
        public Builder textTitle(int v) { this.textTitleRgb = v; return this; }
        public Builder textBody(int v) { this.textBodyRgb = v; return this; }
        public Builder accent(int v) { this.accentArgb = v; return this; }
        public Builder panel(int v) { this.panelFillArgb = v; return this; }
        public Builder focus(int v) { this.focusBorderArgb = v; return this; }
        public Builder secondary(int v) { this.secondaryArgb = v; return this; }
        public Builder muted(int v) { this.mutedRgb = v; return this; }
        public Builder opacity(float v) { this.panelOpacity = v; return this; }
        public Builder frame(EscFrameStyle v) { this.frameStyle = v; return this; }
        public Builder shadow(float v) { this.shadowStrength = v; return this; }
        public Builder glow(float v) { this.glowStrength = v; return this; }
        public Builder crt(float v) { this.crtIntensity = v; return this; }
        public Builder motion(float v) { this.motionScale = v; return this; }

        public EscThemePartial build() {
            return new EscThemePartial(
                name, borderArgb, textTitleRgb, textBodyRgb, accentArgb, panelFillArgb, focusBorderArgb,
                secondaryArgb, mutedRgb, panelOpacity, frameStyle, shadowStrength, glowStrength, crtIntensity, motionScale
            );
        }
    }
}
