package uk.co.extraspecialstudio.extraspecial.esc.gui.profile;

import org.junit.jupiter.api.Test;
import uk.co.extraspecialstudio.extraspecial.esc.gui.asset.EscAssetInstance;
import uk.co.extraspecialstudio.extraspecial.esc.gui.asset.EscAssetSlot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EscGuiProfileCodecTest {

    @Test
    void roundTripPreservesProfileFields() {
        EscGuiProfile original = new EscGuiProfile();
        original.setId("mymod:settings");
        original.setTitle("My Settings");
        original.setLayout(EscGuiLayoutMode.CLASSIC);
        original.setThemePreset("CLEAN");
        original.setBackdrop("GRID");
        original.components().add(new EscAssetInstance("esc:header", EscAssetSlot.TITLE,
            java.util.Map.of("title", "{bind:title}")));
        original.actions().put("apply", "action:apply");

        String json = EscGuiProfileCodec.toJson(original);
        EscGuiProfile decoded = EscGuiProfileCodec.fromJson(json);

        assertEquals("mymod:settings", decoded.id());
        assertEquals("My Settings", decoded.title());
        assertEquals(EscGuiLayoutMode.CLASSIC, decoded.layout());
        assertEquals("CLEAN", decoded.themePreset());
        assertEquals("GRID", decoded.backdrop());
        assertEquals(1, decoded.components().size());
        assertEquals("esc:header", decoded.components().get(0).assetId());
        assertEquals("action:apply", decoded.actions().get("apply"));
    }

    @Test
    void parsesDemoSettingsShape() {
        String json = """
            {
              "id": "extraspecialcore:demo_settings",
              "title": "Demo Settings",
              "layout": "CLASSIC",
              "components": [
                { "asset": "esc:button_bar", "slot": "footer", "props": { "buttons": "apply,back" } }
              ],
              "actions": { "back": "action:back" }
            }
            """;
        EscGuiProfile profile = EscGuiProfileCodec.fromJson(json);
        assertEquals("extraspecialcore:demo_settings", profile.id());
        assertEquals(1, profile.components().size());
        assertEquals("footer", profile.components().get(0).slot().name().toLowerCase());
        assertTrue(profile.actions().containsKey("back"));
    }
}
