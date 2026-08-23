package uk.co.extraspecialstudio.extraspecial.esc.theme;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EscColorTest {

    @Test
    void hsvRoundTripPreservesPrimaryHues() {
        int red = EscColor.opaque(0xFF0000);
        float[] hsv = EscColor.toHsv(red);
        int back = EscColor.fromHsv(hsv[0], hsv[1], hsv[2], 255);
        assertEquals(0xFF0000, EscColor.rgb(back));
    }

    @Test
    void parseHexAcceptsHashAndAlpha() {
        assertEquals(0xFF112233, EscColor.parseHex("#112233").intValue());
        assertEquals(0x80112233, EscColor.parseHex("#80112233").intValue());
    }

    @Test
    void nearestSwatchFindsBlack() {
        assertEquals(EscThemeSwatch.BLACK, EscColor.nearestSwatch(0xFF000000));
    }

    @Test
    void toHexRgbIsSixDigits() {
        assertEquals("00FF41", EscColor.toHexRgb(0xFF00FF41));
        assertNotNull(EscColor.toHexArgb(0xFF00FF41));
    }
}
