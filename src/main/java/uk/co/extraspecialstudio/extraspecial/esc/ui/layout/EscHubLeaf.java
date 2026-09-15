package uk.co.extraspecialstudio.extraspecial.esc.ui.layout;

import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

/** Openable leaf entry inside a hub group. */
public record EscHubLeaf(
    String id,
    String title,
    String subtitle,
    ResourceLocation icon,
    boolean enabled,
    String recommendHint
) {
    public EscHubLeaf(String id, String title, String subtitle, ResourceLocation icon) {
        this(id, title, subtitle, icon, true, "");
    }

    public EscHubLeaf(String id, String title, String subtitle) {
        this(id, title, subtitle, null, true, "");
    }

    public EscHubLeaf {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(title, "title");
        subtitle = subtitle == null ? "" : subtitle;
        recommendHint = recommendHint == null ? "" : recommendHint;
    }

    public boolean disabled() {
        return !enabled;
    }
}
