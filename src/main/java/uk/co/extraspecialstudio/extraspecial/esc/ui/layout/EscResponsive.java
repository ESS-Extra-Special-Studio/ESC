package uk.co.extraspecialstudio.extraspecial.esc.ui.layout;

import uk.co.extraspecialstudio.extraspecial.esc.ui.EscRect;

/**
 * Suggest card sizes / spacing from available bounds and item counts.
 */
public final class EscResponsive {

    private EscResponsive() {
    }

    public record CardPlan(int cardW, int cardH, int gap, int visible) {
    }

    public static CardPlan carouselCards(EscRect carousel, int sectionCount) {
        int n = Math.max(1, sectionCount);
        // Fewer simultaneous cards + wider gaps — scroll / arrows cover the rest.
        int visible = Math.min(4, n);
        int gap = Math.max(16, Math.min(40, carousel.width() / 24));
        int cardH = Math.max(32, carousel.height() - 8);
        int cardW = Math.min(240, Math.max(96, (carousel.width() - gap * (visible + 1)) / visible));
        return new CardPlan(cardW, cardH, gap, visible);
    }

    public static CardPlan dashboardTiles(EscRect area, int sectionCount) {
        int cols = sectionCount <= 2 ? 2 : (sectionCount <= 4 ? 2 : 3);
        int rows = Math.max(1, (int) Math.ceil((sectionCount + 1) / (double) cols));
        int gap = 8;
        int cardW = Math.max(80, (area.width() - gap * (cols + 1)) / cols);
        int cardH = Math.max(48, (area.height() - gap * (rows + 1)) / rows);
        return new CardPlan(cardW, cardH, gap, cols * rows);
    }
}
