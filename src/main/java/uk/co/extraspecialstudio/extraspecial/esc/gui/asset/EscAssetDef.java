package uk.co.extraspecialstudio.extraspecial.esc.gui.asset;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Registry entry describing one reusable ESC GUI brick.
 */
public final class EscAssetDef {
    private final String id;
    private final EscAssetType type;
    private final EscAssetCategory category;
    private final String displayName;
    private final String description;
    private final List<EscAssetPropDef> properties;
    private final EscAssetRenderer renderer;
    private final boolean interactive;

    public EscAssetDef(
        String id,
        EscAssetType type,
        EscAssetCategory category,
        String displayName,
        String description,
        List<EscAssetPropDef> properties,
        EscAssetRenderer renderer,
        boolean interactive
    ) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id required");
        }
        this.id = id;
        this.type = type == null ? EscAssetType.PANEL : type;
        this.category = category == null ? EscAssetCategory.CORE : category;
        this.displayName = displayName == null || displayName.isBlank() ? id : displayName;
        this.description = description == null ? "" : description;
        this.properties = properties == null ? List.of() : List.copyOf(properties);
        this.renderer = renderer;
        this.interactive = interactive;
    }

    public String id() {
        return id;
    }

    public EscAssetType type() {
        return type;
    }

    public EscAssetCategory category() {
        return category;
    }

    public String displayName() {
        return displayName;
    }

    public String description() {
        return description;
    }

    public List<EscAssetPropDef> properties() {
        return properties;
    }

    public EscAssetRenderer renderer() {
        return renderer;
    }

    public boolean interactive() {
        return interactive;
    }

    public Map<String, String> defaultProps() {
        Map<String, String> out = new LinkedHashMap<>();
        for (EscAssetPropDef prop : properties) {
            out.put(prop.key(), prop.defaultValue());
        }
        return out;
    }

    public EscAssetInstance createInstance(EscAssetSlot slot) {
        EscAssetInstance instance = new EscAssetInstance(id, slot);
        instance.props().putAll(defaultProps());
        return instance;
    }

    public static Builder builder(String id, EscAssetType type, EscAssetCategory category) {
        return new Builder(id, type, category);
    }

    public static final class Builder {
        private final String id;
        private final EscAssetType type;
        private final EscAssetCategory category;
        private String displayName;
        private String description = "";
        private final List<EscAssetPropDef> properties = new ArrayList<>();
        private EscAssetRenderer renderer;
        private boolean interactive;

        private Builder(String id, EscAssetType type, EscAssetCategory category) {
            this.id = id;
            this.type = type;
            this.category = category;
            this.displayName = id;
        }

        public Builder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder prop(EscAssetPropDef prop) {
            properties.add(prop);
            return this;
        }

        public Builder renderer(EscAssetRenderer renderer) {
            this.renderer = renderer;
            return this;
        }

        public Builder interactive(boolean interactive) {
            this.interactive = interactive;
            return this;
        }

        public EscAssetDef build() {
            return new EscAssetDef(id, type, category, displayName, description, properties, renderer, interactive);
        }
    }
}
