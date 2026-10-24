package uk.co.extraspecialstudio.extraspecial.esc.ui;

import uk.co.extraspecialstudio.extraspecial.esc.ui.EscGraphics;

@FunctionalInterface
public interface EscListRowRenderer<T> {

    void render(
        EscGraphics graphics,
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
