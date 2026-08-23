package uk.co.extraspecialstudio.extraspecial.esc.ui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EscButtonBarTest {

    @Test
    void equalRowNeverOverlapsAndStaysInBounds() {
        EscRect area = new EscRect(10, 100, 320, 28);
        EscRect[] slots = EscButtonBar.equalRow(area, 8, 104, 20, 4, 3);
        assertEquals(8, slots.length);
        assertTrue(EscButtonBar.validate(area, slots));
        assertEquals(area.x() + 4, slots[0].x());
        assertEquals(area.right() - 4, slots[7].right());
        int minW = slots[0].width();
        int maxW = slots[0].width();
        for (EscRect s : slots) {
            minW = Math.min(minW, s.width());
            maxW = Math.max(maxW, s.width());
        }
        assertTrue(maxW - minW <= 1);
    }

    @Test
    void equalRowNarrowPanelStillValid() {
        EscRect area = new EscRect(0, 0, 200, 24);
        EscRect[] slots = EscButtonBar.equalRow(area, 7, 2, 20);
        assertTrue(EscButtonBar.validate(area, slots));
        for (EscRect s : slots) {
            assertTrue(s.width() >= 1);
        }
    }

    @Test
    void splitFallsBackWhenGroupsCollide() {
        EscRect area = new EscRect(0, 0, 240, 28);
        // Left wants 4*70, right wants 3*70 — won't fit → equal 7
        EscRect[] slots = EscButtonBar.splitOrEqual(
            area,
            new int[]{70, 70, 70, 70},
            new int[]{70, 70, 80},
            4,
            20
        );
        assertEquals(7, slots.length);
        assertTrue(EscButtonBar.validate(area, slots));
    }

    @Test
    void splitKeepsGroupsApartWhenWideEnough() {
        EscRect area = new EscRect(0, 0, 600, 28);
        EscRect[] slots = EscButtonBar.splitOrEqual(
            area,
            new int[]{70, 70, 70, 78},
            new int[]{70, 70, 80},
            4,
            20
        );
        assertEquals(7, slots.length);
        assertTrue(EscButtonBar.validate(area, slots));
        assertTrue(slots[4].x() >= slots[3].right() + EscButtonBar.DEFAULT_GAP);
    }

    @Test
    void packLeftFallsBackOnOverflow() {
        EscRect area = new EscRect(0, 0, 150, 28);
        EscRect[] slots = EscButtonBar.packLeftOrEqual(area, new int[]{80, 80, 80}, 4, 20);
        assertEquals(3, slots.length);
        assertTrue(EscButtonBar.validate(area, slots));
    }

    @Test
    void fitDenseUsesOneRowWhenWideEnough() {
        EscRect area = new EscRect(0, 0, 854, 48);
        assertFalse(EscButtonBar.needsTwoRows(854, 9));
        EscRect[] slots = EscButtonBar.fitDense(area, 9, 20);
        assertEquals(9, slots.length);
        assertTrue(EscButtonBar.validate(area, slots));
        int y0 = slots[0].y();
        for (EscRect s : slots) {
            assertEquals(y0, s.y());
        }
    }

    @Test
    void fitDenseSplitsToTwoRowsWhenNarrow() {
        EscRect area = new EscRect(0, 0, 200, 56);
        assertTrue(EscButtonBar.needsTwoRows(200, 8));
        int h = EscButtonBar.fitDenseHeight(200, 8, 20);
        assertTrue(h >= 20 * 2);
        EscRect tall = new EscRect(0, 0, 200, h);
        EscRect[] slots = EscButtonBar.fitDense(tall, 8, 20);
        assertEquals(8, slots.length);
        assertTrue(EscButtonBar.validate(tall, slots));
        assertTrue(slots[0].y() < slots[7].y());
    }

    @Test
    void fitDenseAt320StillValid() {
        int count = 9;
        int h = EscButtonBar.fitDenseHeight(320, count, 20);
        EscRect area = new EscRect(10, 100, 320, h);
        EscRect[] slots = EscButtonBar.fitDense(area, count, 20);
        assertEquals(count, slots.length);
        assertTrue(EscButtonBar.validate(area, slots));
    }

    @Test
    void validateDetectsOverlap() {
        EscRect area = new EscRect(0, 0, 100, 30);
        EscRect[] bad = {
            new EscRect(0, 0, 60, 20),
            new EscRect(40, 0, 60, 20)
        };
        assertFalse(EscButtonBar.validate(area, bad));
    }
}
