package uk.co.extraspecialstudio.extraspecial.esc.ui;

import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Viewport-clipped list with mouse-wheel scrolling (same idea as hand-rolled lists in Dead Air / CTL).
 */
public final class EscSimpleScrollList<T> {

    private final EscUiStyle style;
    private final EscListRowRenderer<T> renderer;

    private List<T> items = List.of();
    private EscRect viewport = new EscRect(0, 0, 0, 0);
    private double scrollPixels;

    public EscSimpleScrollList(EscUiStyle style, EscListRowRenderer<T> renderer) {
        this.style = Objects.requireNonNull(style);
        this.renderer = Objects.requireNonNull(renderer);
    }

    public void setViewport(EscRect viewport) {
        this.viewport = Objects.requireNonNull(viewport);
        clampScroll();
    }

    public void setViewportFrom(EscRect parent, EscInsets insets) {
        setViewport(parent.inset(insets != null ? insets : EscInsets.ZERO));
    }

    public EscRect viewport() {
        return viewport;
    }

    public void replaceItems(Collection<? extends T> next) {
        this.items = new ArrayList<>(next);
        clampScroll();
    }

    public List<T> items() {
        return List.copyOf(items);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (!viewport.contains(mouseX, mouseY)) {
            return false;
        }
        scrollPixels -= delta * style.listRowHeight();
        clampScroll();
        return true;
    }

    private int contentHeight() {
        int row = style.listRowHeight();
        if (row <= 0) {
            return 0;
        }
        return items.size() * row;
    }

    private int maxScroll() {
        return Math.max(0, contentHeight() - viewport.height());
    }

    private void clampScroll() {
        if (scrollPixels < 0) {
            scrollPixels = 0;
        } else if (scrollPixels > maxScroll()) {
            scrollPixels = maxScroll();
        }
    }

    /** Current scroll offset in pixels (0 = top). */
    public double scrollOffset() {
        return scrollPixels;
    }

    public void setScrollOffset(double pixels) {
        this.scrollPixels = pixels;
        clampScroll();
    }

    public int maxScrollPixels() {
        return maxScroll();
    }

    public double scrollPercent() {
        int m = maxScroll();
        return m <= 0 ? 0.0D : scrollPixels / (double) m;
    }

    public void setScrollPercent(double percent) {
        scrollPixels = percent * maxScroll();
        clampScroll();
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int x0 = viewport.x();
        int y0 = viewport.y();
        int x1 = viewport.right();
        int y1 = viewport.bottom();
        graphics.enableScissor(x0, y0, x1, y1);

        int rowH = style.listRowHeight();
        int yStart = y0 - (int) scrollPixels;
        for (int i = 0; i < items.size(); i++) {
            int top = yStart + i * rowH;
            int bottom = top + rowH;
            if (bottom < y0 || top > y1) {
                continue;
            }
            T row = items.get(i);
            boolean hovered = viewport.contains(mouseX, mouseY)
                && mouseY >= Math.max(top, y0)
                && mouseY < Math.min(bottom, y1)
                && mouseX >= x0
                && mouseX < x1;
            renderer.render(graphics, row, i, x0, top, viewport.width(), rowH, mouseX, mouseY, hovered, partialTick);
        }

        graphics.disableScissor();
    }
}
