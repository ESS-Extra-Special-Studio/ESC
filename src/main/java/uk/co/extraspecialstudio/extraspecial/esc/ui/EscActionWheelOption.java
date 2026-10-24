package uk.co.extraspecialstudio.extraspecial.esc.ui;

import net.minecraft.util.text.ITextComponent;

/**
 * One wedge on an {@link EscActionWheel}.
 *
 * @param label short chip / hub title
 * @param blurb one-line hub subtitle while hovered (may be empty)
 */
public final class EscActionWheelOption {
    private final ITextComponent label;
    private final ITextComponent blurb;

    public EscActionWheelOption(ITextComponent label, ITextComponent blurb) {
    if (label == null) label = new net.minecraft.util.text.StringTextComponent("");
    if (blurb == null) blurb = new net.minecraft.util.text.StringTextComponent("");

        this.label = label;
        this.blurb = blurb;
    }

    public ITextComponent label() { return this.label; }
    public ITextComponent blurb() { return this.blurb; }

    

    public EscActionWheelOption(ITextComponent label) {
        this(label, new net.minecraft.util.text.StringTextComponent(""));
    }

    public EscActionWheelOption(String labelKey) {
        this(new net.minecraft.util.text.TranslationTextComponent(labelKey), new net.minecraft.util.text.StringTextComponent(""));
    }

    public EscActionWheelOption(String labelKey, String blurbKey) {
        this(new net.minecraft.util.text.TranslationTextComponent(labelKey), new net.minecraft.util.text.TranslationTextComponent(blurbKey));
    }
}
