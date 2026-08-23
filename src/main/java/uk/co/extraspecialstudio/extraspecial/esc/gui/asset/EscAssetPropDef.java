package uk.co.extraspecialstudio.extraspecial.esc.gui.asset;

import java.util.List;

/**
 * Schema entry for one configurable property on an asset definition.
 */
public record EscAssetPropDef(
    String key,
    EscPropType type,
    String defaultValue,
    List<String> enumValues,
    String label
) {
    public EscAssetPropDef {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("key required");
        }
        if (type == null) {
            type = EscPropType.STRING;
        }
        if (defaultValue == null) {
            defaultValue = "";
        }
        if (enumValues == null) {
            enumValues = List.of();
        }
        if (label == null || label.isBlank()) {
            label = key;
        }
    }

    public static EscAssetPropDef string(String key, String defaultValue, String label) {
        return new EscAssetPropDef(key, EscPropType.STRING, defaultValue, List.of(), label);
    }

    public static EscAssetPropDef stringList(String key, String defaultCsv, String label) {
        return new EscAssetPropDef(key, EscPropType.STRING_LIST, defaultCsv, List.of(), label);
    }

    public static EscAssetPropDef bool(String key, boolean defaultValue, String label) {
        return new EscAssetPropDef(key, EscPropType.BOOLEAN, Boolean.toString(defaultValue), List.of(), label);
    }

    public static EscAssetPropDef anEnum(String key, String defaultValue, List<String> values, String label) {
        return new EscAssetPropDef(key, EscPropType.ENUM, defaultValue, List.copyOf(values), label);
    }
}
