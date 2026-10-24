package uk.co.extraspecialstudio.extraspecial.esc.ui;

import net.minecraft.client.gui.GuiGraphicsExtractor;

@FunctionalInterface
public interface EscListRowRenderer<T> {

    void render(
        GuiGraphicsExtractor graphics,
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
