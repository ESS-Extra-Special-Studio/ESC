package uk.co.extraspecialstudio.extraspecial.esc.ui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EscLayoutTest {

    private static final EscRect PARENT = new EscRect(100, 50, 800, 600);

    @Test
    void topLeftWithOffset() {
        EscRect r = EscLayout.resolve(PARENT, EscLayoutSpec.of(EscAnchor.TOP_LEFT, 10, 20, 120, 30));
        assertEquals(110, r.x());
        assertEquals(70, r.y());
        assertEquals(120, r.width());
        assertEquals(30, r.height());
    }

    @Test
    void topCenter() {
        EscRect r = EscLayout.resolve(PARENT, EscLayoutSpec.of(EscAnchor.TOP_CENTER, 0, 0, 200, 40));
        assertEquals(400, r.x());
        assertEquals(50, r.y());
        assertEquals(200, r.width());
    }

    @Test
    void center() {
        EscRect r = EscLayout.resolve(PARENT, EscLayoutSpec.of(EscAnchor.CENTER, 0, 0, 100, 100));
        assertEquals(450, r.x());
        assertEquals(300, r.y());
    }

    @Test
    void fillWithInsets() {
        EscRect r = EscLayout.resolve(PARENT, EscLayoutSpec.fill(EscInsets.of(8)));
        assertEquals(108, r.x());
        assertEquals(58, r.y());
        assertEquals(784, r.width());
        assertEquals(584, r.height());
    }

    @Test
    void splitColumnsRatios() {
        EscRect[] cols = PARENT.splitColumns(new float[]{0.25f, 0.50f, 0.25f}, 6);
        assertEquals(3, cols.length);
        assertEquals(100, cols[0].x());
        assertEquals(100 + cols[0].width() + 6, cols[1].x());
        assertEquals(PARENT.right(), cols[2].right());
    }

    @Test
    void insetShrinksRect() {
        EscRect inner = PARENT.inset(EscInsets.of(10, 20, 30, 40));
        assertEquals(110, inner.x());
        assertEquals(70, inner.y());
        assertEquals(760, inner.width());
        assertEquals(540, inner.height());
    }

    @Test
    void fitContainedCentersAndPreservesAspect() {
        EscRect narrow = new EscRect(100, 50, 300, 600);
        EscRect fit = EscLayout.fitContained(narrow, 400, 200, EscInsets.ZERO);
        assertEquals(300, fit.width());
        assertEquals(150, fit.height());
        assertEquals(100, fit.x());
        assertEquals(275, fit.y());
    }

    @Test
    void fitScaleMatchesContainedSize() {
        EscRect narrow = new EscRect(100, 50, 300, 600);
        float scale = EscLayout.fitScale(narrow, 400, 200, EscInsets.ZERO);
        assertEquals(0.75f, scale, 0.001f);
        EscRect fit = EscLayout.fitContained(narrow, 400, 200, EscInsets.ZERO);
        assertEquals(Math.round(400 * scale), fit.width());
        assertEquals(Math.round(200 * scale), fit.height());
    }

    @Test
    void gridLayoutEqualCells() {
        EscRect parent = new EscRect(0, 0, 100, 40);
        EscRect[][] cells = EscGridLayout.grid(parent, 4, 2, 2);
        assertEquals(2, cells.length);
        assertEquals(4, cells[0].length);
        assertEquals(0, cells[0][0].x());
        assertEquals(0, cells[0][0].y());
        assertEquals(100, cells[1][3].right());
        assertEquals(40, cells[1][0].bottom());
    }

    @Test
    void centeredPairMatchesLegacyScreenCenterOffsets() {
        EscRect tabBar = new EscRect(0, 0, 854, 28);
        int tabW = 80;
        int tabH = 22;
        int gap = 4;
        EscRect radio = EscLayout.resolve(tabBar,
            EscLayoutSpec.centeredPairLeft(EscAnchor.TOP_CENTER, 4, tabW, tabH, gap));
        EscRect walkie = EscLayout.resolve(tabBar,
            EscLayoutSpec.centeredPairRight(EscAnchor.TOP_CENTER, 4, tabW, tabH, gap));
        assertEquals(854 / 2 - tabW - gap, radio.x());
        assertEquals(4, radio.y());
        assertEquals(854 / 2 + gap, walkie.x());
        assertEquals(4, walkie.y());
    }
}
