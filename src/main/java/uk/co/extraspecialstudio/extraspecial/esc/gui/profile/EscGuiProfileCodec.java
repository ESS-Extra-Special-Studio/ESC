package uk.co.extraspecialstudio.extraspecial.esc.gui.profile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import uk.co.extraspecialstudio.extraspecial.esc.gui.asset.EscAssetInstance;
import uk.co.extraspecialstudio.extraspecial.esc.gui.asset.EscAssetSlot;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * JSON encode/decode for {@link EscGuiProfile} files.
 */
public final class EscGuiProfileCodec {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private EscGuiProfileCodec() {
    }

    public static String toJson(EscGuiProfile profile) {
        JsonObject root = new JsonObject();
        root.addProperty("id", profile.id());
        root.addProperty("title", profile.title());
        root.addProperty("layout", profile.layout().name());
        root.addProperty("themePreset", profile.themePreset());
        root.addProperty("backdrop", profile.backdrop());

        JsonArray components = new JsonArray();
        for (EscAssetInstance instance : profile.components()) {
            JsonObject comp = new JsonObject();
            comp.addProperty("asset", instance.assetId());
            comp.addProperty("slot", instance.slot().name().toLowerCase());
            JsonObject props = new JsonObject();
            instance.props().forEach(props::addProperty);
            comp.add("props", props);
            components.add(comp);
        }
        root.add("components", components);

        JsonObject actions = new JsonObject();
        profile.actions().forEach(actions::addProperty);
        root.add("actions", actions);

        return GSON.toJson(root);
    }

    public static EscGuiProfile fromJson(String json) {
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        EscGuiProfile profile = new EscGuiProfile();
        if (root.has("id")) {
            profile.setId(root.get("id").getAsString());
        }
        if (root.has("title")) {
            profile.setTitle(root.get("title").getAsString());
        }
        if (root.has("layout")) {
            profile.setLayout(EscGuiLayoutMode.parse(root.get("layout").getAsString()));
        }
        if (root.has("themePreset")) {
            profile.setThemePreset(root.get("themePreset").getAsString());
        }
        if (root.has("backdrop")) {
            profile.setBackdrop(root.get("backdrop").getAsString());
        }

        if (root.has("components") && root.get("components").isJsonArray()) {
            for (JsonElement el : root.getAsJsonArray("components")) {
                if (!el.isJsonObject()) {
                    continue;
                }
                JsonObject comp = el.getAsJsonObject();
                String asset = comp.has("asset") ? comp.get("asset").getAsString() : "esc:panel";
                EscAssetSlot slot = comp.has("slot")
                    ? EscAssetSlot.parse(comp.get("slot").getAsString())
                    : EscAssetSlot.BODY;
                Map<String, String> props = new LinkedHashMap<>();
                if (comp.has("props") && comp.get("props").isJsonObject()) {
                    JsonObject propObj = comp.getAsJsonObject("props");
                    for (Map.Entry<String, JsonElement> entry : propObj.entrySet()) {
                        props.put(entry.getKey(), entry.getValue().getAsString());
                    }
                }
                profile.components().add(new EscAssetInstance(asset, slot, props));
            }
        }

        if (root.has("actions") && root.get("actions").isJsonObject()) {
            JsonObject actionObj = root.getAsJsonObject("actions");
            for (Map.Entry<String, JsonElement> entry : actionObj.entrySet()) {
                profile.actions().put(entry.getKey(), entry.getValue().getAsString());
            }
        }
        return profile;
    }
}
