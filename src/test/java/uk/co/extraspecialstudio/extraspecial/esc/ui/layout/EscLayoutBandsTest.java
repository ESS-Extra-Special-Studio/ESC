package uk.co.extraspecialstudio.extraspecial.esc.ui.layout;

import org.junit.jupiter.api.Test;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscRect;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EscLayoutBandsTest {

    @Test
    void rowsStayInsideParentAcrossHeights() {
        for (int h = 40; h <= 400; h += 20) {
            EscRect parent = new EscRect(0, 0, 320, h);
            EscRect[] bands = EscLayoutBands.rows(parent, new float[]{0.42f, 0.58f}, 8, new int[]{48, 80});
            assertTrue(bands.length == 2);
            assertTrue(EscLayoutBands.validate(parent, bands));
        }
    }

    @Test
    void tilesNeverEscapeArea() {
        EscRect area = new EscRect(10, 20, 180, 120);
        for (int count = 1; count <= 8; count++) {
            EscRect[] tiles = EscLayoutBands.tiles(area, count, count <= 4 ? 2 : 3, 8);
            assertTrue(EscLayoutBands.validate(area, tiles));
        }
    }

    @Test
    void clampIntoKeepsChildInsideParent() {
        EscRect parent = new EscRect(0, 0, 100, 50);
        EscRect child = new EscRect(-10, 40, 120, 80);
        EscRect clamped = EscLayoutBands.clampInto(child, parent);
        assertTrue(EscLayoutBands.fits(clamped, parent));
    }

    @Test
    void shortParentStillProducesValidRows() {
        EscRect parent = new EscRect(0, 0, 200, 30);
        EscRect[] bands = EscLayoutBands.rows(parent, new float[]{0.5f, 0.5f}, 4, new int[]{40, 40});
        assertTrue(EscLayoutBands.validate(parent, bands));
    }
}
