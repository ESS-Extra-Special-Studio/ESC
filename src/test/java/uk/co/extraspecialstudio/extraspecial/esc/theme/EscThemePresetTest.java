package uk.co.extraspecialstudio.extraspecial.esc.theme;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EscThemePresetTest {

    @Test
    void compositeHubPresetsHaveMatchingBackdrops() {
        assertEquals(EscBackdropStyle.MATRIX, EscThemePreset.MATRIX.preferredBackdrop());
        assertEquals(EscBackdropStyle.OCEAN, EscThemePreset.OCEAN.preferredBackdrop());
        assertEquals(EscBackdropStyle.CLOUDS, EscThemePreset.CLOUDS.preferredBackdrop());
        assertEquals(EscBackdropStyle.GROWTH, EscThemePreset.GROWTH.preferredBackdrop());
        assertEquals(EscBackdropStyle.EMBER, EscThemePreset.EMBER.preferredBackdrop());
    }

    @Test
    void compositeHubPresetsProvideCompleteThemes() {
        for (EscThemePreset preset : new EscThemePreset[]{
            EscThemePreset.MATRIX,
            EscThemePreset.OCEAN,
            EscThemePreset.CLOUDS,
            EscThemePreset.GROWTH,
            EscThemePreset.EMBER
        }) {
            assertNotNull(preset.theme());
            assertEquals(preset.label(), preset.theme().name());
        }
    }

    @Test
    void blankLookupUsesGrowthDefault() {
        assertEquals(EscThemePreset.GROWTH, EscThemePreset.byName(null));
        assertEquals(EscThemePreset.GROWTH, EscThemePreset.byName(""));
        assertEquals(EscBackdropStyle.GROWTH, EscBackdropStyle.byName(null));
        assertEquals(EscBackdropStyle.GROWTH, EscBackdropStyle.byName(""));
    }

    @Test
    void blackSwatchIsAvailableForBorderAndAccent() {
        assertEquals(0xFF000000, EscThemeSwatch.BLACK.borderArgb());
        assertEquals(0xFF000000, EscThemeSwatch.BLACK.accentArgb());
    }
}
