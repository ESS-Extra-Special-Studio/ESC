package uk.co.extraspecialstudio.extraspecial.esc.gui.asset;

import java.util.List;

/**
 * Schema entry for one configurable property on an asset definition.
 */
public final class EscAssetPropDef {
    private final String key;
    private final EscPropType type;
    private final String defaultValue;
    private final List<String> enumValues;
    private final String label;

    public EscAssetPropDef(String key, EscPropType type, String defaultValue, List<String> enumValues, String label) {
    if (key == null || key.trim().isEmpty()) {
    throw new IllegalArgumentException("key required");
    }
    if (type == null) {
    type = EscPropType.STRING;
    }
    if (defaultValue == null) {
    defaultValue = "";
    }
    if (enumValues == null) {
    enumValues = java.util.Collections.emptyList();
    }
    if (label == null || label.trim().isEmpty()) {
    label = key;
    }

        this.key = key;
        this.type = type;
        this.defaultValue = defaultValue;
        this.enumValues = enumValues;
        this.label = label;
    }

    public String key() { return this.key; }
    public EscPropType type() { return this.type; }
    public String defaultValue() { return this.defaultValue; }
    public List<String> enumValues() { return this.enumValues; }
    public String label() { return this.label; }

    

    public static EscAssetPropDef string(String key, String defaultValue, String label) {
        return new EscAssetPropDef(key, EscPropType.STRING, defaultValue, java.util.Collections.emptyList(), label);
    }

    public static EscAssetPropDef stringList(String key, String defaultCsv, String label) {
        return new EscAssetPropDef(key, EscPropType.STRING_LIST, defaultCsv, java.util.Collections.emptyList(), label);
    }

    public static EscAssetPropDef bool(String key, boolean defaultValue, String label) {
        return new EscAssetPropDef(key, EscPropType.BOOLEAN, Boolean.toString(defaultValue), java.util.Collections.emptyList(), label);
    }

    public static EscAssetPropDef anEnum(String key, String defaultValue, List<String> values, String label) {
        return new EscAssetPropDef(key, EscPropType.ENUM, defaultValue, java.util.Collections.unmodifiableList(new java.util.ArrayList<>(values)), label);
    }
}
