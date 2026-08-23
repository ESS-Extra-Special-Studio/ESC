package uk.co.extraspecialstudio.extraspecial.esc.gui.profile;

import uk.co.extraspecialstudio.extraspecial.esc.gui.asset.EscAssetInstance;
import uk.co.extraspecialstudio.extraspecial.esc.gui.asset.EscAssetRegistry;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Declarative ESC screen profile — look / composition / component knobs.
 */
public final class EscGuiProfile {
    private String id = "unknown:untitled";
    private String title = "Untitled";
    private EscGuiLayoutMode layout = EscGuiLayoutMode.CLASSIC;
    private String themePreset = "CLEAN";
    private String backdrop = "GRID";
    private final List<EscAssetInstance> components = new ArrayList<>();
    private final Map<String, String> actions = new LinkedHashMap<>();

    public EscGuiProfile() {
    }

    public EscGuiProfile(EscGuiProfile other) {
        this.id = other.id;
        this.title = other.title;
        this.layout = other.layout;
        this.themePreset = other.themePreset;
        this.backdrop = other.backdrop;
        for (EscAssetInstance c : other.components) {
            components.add(c.copy());
        }
        actions.putAll(other.actions);
    }

    public String id() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null || id.isBlank() ? "unknown:untitled" : id;
    }

    public String title() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? "" : title;
    }

    public EscGuiLayoutMode layout() {
        return layout;
    }

    public void setLayout(EscGuiLayoutMode layout) {
        this.layout = layout == null ? EscGuiLayoutMode.CLASSIC : layout;
    }

    public String themePreset() {
        return themePreset;
    }

    public void setThemePreset(String themePreset) {
        this.themePreset = themePreset == null ? "CLEAN" : themePreset;
    }

    public String backdrop() {
        return backdrop;
    }

    public void setBackdrop(String backdrop) {
        this.backdrop = backdrop == null ? "GRID" : backdrop;
    }

    public List<EscAssetInstance> components() {
        return components;
    }

    public Map<String, String> actions() {
        return actions;
    }

    public EscGuiProfile copy() {
        return new EscGuiProfile(this);
    }

    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        if (id.isBlank() || !id.contains(":")) {
            errors.add("Profile id must be namespace:path (e.g. mymod:settings)");
        }
        for (int i = 0; i < components.size(); i++) {
            EscAssetInstance instance = components.get(i);
            if (instance.assetId() == null || instance.assetId().isBlank()) {
                errors.add("Component " + i + " missing asset id");
                continue;
            }
            if (!EscAssetRegistry.contains(instance.assetId())) {
                errors.add("Unknown asset '" + instance.assetId() + "' at component " + i);
            }
        }
        return errors;
    }
}
