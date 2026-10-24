package uk.co.extraspecialstudio.extraspecial.esc.ui;

import net.minecraft.network.chat.Component;

/**
 * One wedge on an {@link EscActionWheel}.
 *
 * @param label short chip / hub title
 * @param blurb one-line hub subtitle while hovered (may be empty)
 */
public record EscActionWheelOption(Component label, Component blurb) {
    public EscActionWheelOption {
        if (label == null) label = Component.empty();
        if (blurb == null) blurb = Component.empty();
    }

    public EscActionWheelOption(Component label) {
        this(label, Component.empty());
    }

    public EscActionWheelOption(String labelKey) {
        this(Component.translatable(labelKey), Component.empty());
    }

    public EscActionWheelOption(String labelKey, String blurbKey) {
        this(Component.translatable(labelKey), Component.translatable(blurbKey));
    }
}
