package uk.co.extraspecialstudio.extraspecial.esc.gui.asset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.extraspecialstudio.extraspecial.esc.gui.EscGuiRuntime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EscAssetRegistryTest {

    @BeforeEach
    void seed() {
        EscAssetRegistry.clear();
        EscGuiRuntime.registerBuiltins();
    }

    @Test
    void builtinsRegisterCoreAssets() {
        assertTrue(EscAssetRegistry.contains("esc:header"));
        assertTrue(EscAssetRegistry.contains("esc:panel"));
        assertTrue(EscAssetRegistry.contains("esc:button_bar"));
    }

    @Test
    void byCategoryFiltersAssets() {
        assertFalse(EscAssetRegistry.byCategory(EscAssetCategory.CORE).isEmpty());
        assertFalse(EscAssetRegistry.byCategory(EscAssetCategory.NAVIGATION).isEmpty());
    }

    @Test
    void createInstanceAppliesDefaults() {
        EscAssetDef header = EscAssetRegistry.get("esc:header").orElseThrow();
        EscAssetInstance instance = header.createInstance(EscAssetSlot.TITLE);
        assertEquals("esc:header", instance.assetId());
        assertEquals(EscAssetSlot.TITLE, instance.slot());
        assertEquals("{bind:title}", instance.prop("title"));
    }
}
