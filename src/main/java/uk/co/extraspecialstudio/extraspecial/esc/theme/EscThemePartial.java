package uk.co.extraspecialstudio.extraspecial.esc.theme;

/**
 * Partial theme override for inheritance (null = leave underlying value).
 */
public final class EscThemePartial {
    private final String name;
    private final Integer borderArgb;
    private final Integer textTitleRgb;
    private final Integer textBodyRgb;
    private final Integer accentArgb;
    private final Integer panelFillArgb;
    private final Integer focusBorderArgb;
    private final Integer secondaryArgb;
    private final Integer mutedRgb;
    private final Float panelOpacity;
    private final EscFrameStyle frameStyle;
    private final Float shadowStrength;
    private final Float glowStrength;
    private final Float crtIntensity;
    private final Float motionScale;

    public EscThemePartial(String name, Integer borderArgb, Integer textTitleRgb, Integer textBodyRgb, Integer accentArgb, Integer panelFillArgb, Integer focusBorderArgb, Integer secondaryArgb, Integer mutedRgb, Float panelOpacity, EscFrameStyle frameStyle, Float shadowStrength, Float glowStrength, Float crtIntensity, Float motionScale) {
        this.name = name;
        this.borderArgb = borderArgb;
        this.textTitleRgb = textTitleRgb;
        this.textBodyRgb = textBodyRgb;
        this.accentArgb = accentArgb;
        this.panelFillArgb = panelFillArgb;
        this.focusBorderArgb = focusBorderArgb;
        this.secondaryArgb = secondaryArgb;
        this.mutedRgb = mutedRgb;
        this.panelOpacity = panelOpacity;
        this.frameStyle = frameStyle;
        this.shadowStrength = shadowStrength;
        this.glowStrength = glowStrength;
        this.crtIntensity = crtIntensity;
        this.motionScale = motionScale;
    }

    public String name() { return this.name; }
    public Integer borderArgb() { return this.borderArgb; }
    public Integer textTitleRgb() { return this.textTitleRgb; }
    public Integer textBodyRgb() { return this.textBodyRgb; }
    public Integer accentArgb() { return this.accentArgb; }
    public Integer panelFillArgb() { return this.panelFillArgb; }
    public Integer focusBorderArgb() { return this.focusBorderArgb; }
    public Integer secondaryArgb() { return this.secondaryArgb; }
    public Integer mutedRgb() { return this.mutedRgb; }
    public Float panelOpacity() { return this.panelOpacity; }
    public EscFrameStyle frameStyle() { return this.frameStyle; }
    public Float shadowStrength() { return this.shadowStrength; }
    public Float glowStrength() { return this.glowStrength; }
    public Float crtIntensity() { return this.crtIntensity; }
    public Float motionScale() { return this.motionScale; }

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
