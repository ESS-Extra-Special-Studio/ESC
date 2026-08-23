package uk.co.extraspecialstudio.extraspecial.esc.ui;

import net.minecraft.client.gui.GuiGraphics;

@FunctionalInterface
public interface EscListRowRenderer<T> {

    void render(
        GuiGraphics graphics,
        T row,
        int index,
        int left,
        int top,
        int width,
        int height,
        int mouseX,
        int mouseY,
        boolean hovered,
        float partialTick
    );
}
