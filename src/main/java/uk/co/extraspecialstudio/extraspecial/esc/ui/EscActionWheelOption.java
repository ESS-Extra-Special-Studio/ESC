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
        if (label == null) label = new net.minecraft.network.chat.TextComponent("");
        if (blurb == null) blurb = new net.minecraft.network.chat.TextComponent("");
    }

    public EscActionWheelOption(Component label) {
        this(label, new net.minecraft.network.chat.TextComponent(""));
    }

    public EscActionWheelOption(String labelKey) {
        this(new net.minecraft.network.chat.TranslatableComponent(labelKey), new net.minecraft.network.chat.TextComponent(""));
    }

    public EscActionWheelOption(String labelKey, String blurbKey) {
        this(new net.minecraft.network.chat.TranslatableComponent(labelKey), new net.minecraft.network.chat.TranslatableComponent(blurbKey));
    }
}
