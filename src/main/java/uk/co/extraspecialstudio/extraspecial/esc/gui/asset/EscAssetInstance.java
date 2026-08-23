package uk.co.extraspecialstudio.extraspecial.esc.gui.asset;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * One placed component inside a GUI profile.
 */
public final class EscAssetInstance {
    private String assetId;
    private EscAssetSlot slot;
    private final Map<String, String> props;

    public EscAssetInstance(String assetId, EscAssetSlot slot) {
        this.assetId = assetId;
        this.slot = slot == null ? EscAssetSlot.BODY : slot;
        this.props = new LinkedHashMap<>();
    }

    public EscAssetInstance(String assetId, EscAssetSlot slot, Map<String, String> props) {
        this(assetId, slot);
        if (props != null) {
            this.props.putAll(props);
        }
    }

    public String assetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public EscAssetSlot slot() {
        return slot;
    }

    public void setSlot(EscAssetSlot slot) {
        this.slot = slot == null ? EscAssetSlot.BODY : slot;
    }

    public Map<String, String> props() {
        return props;
    }

    public String prop(String key) {
        return props.getOrDefault(key, "");
    }

    public void setProp(String key, String value) {
        props.put(key, value == null ? "" : value);
    }

    public EscAssetInstance copy() {
        EscAssetInstance copy = new EscAssetInstance(assetId, slot);
        copy.props.putAll(props);
        return copy;
    }
}
