package uk.co.extraspecialstudio.extraspecial.esc.ui.layout;

import net.minecraft.util.ResourceLocation;

import java.util.Objects;

/** Openable leaf entry inside a hub group. */
public final class EscHubLeaf {
    private final String id;
    private final String title;
    private final String subtitle;
    private final ResourceLocation icon;
    private final boolean enabled;
    private final String recommendHint;

    public EscHubLeaf(String id, String title, String subtitle, ResourceLocation icon, boolean enabled, String recommendHint) {
    Objects.requireNonNull(id, "id");
    Objects.requireNonNull(title, "title");
    subtitle = subtitle == null ? "" : subtitle;
    recommendHint = recommendHint == null ? "" : recommendHint;

        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.icon = icon;
        this.enabled = enabled;
        this.recommendHint = recommendHint;
    }

    public String id() { return this.id; }
    public String title() { return this.title; }
    public String subtitle() { return this.subtitle; }
    public ResourceLocation icon() { return this.icon; }
    public boolean enabled() { return this.enabled; }
    public String recommendHint() { return this.recommendHint; }

    public EscHubLeaf(String id, String title, String subtitle, ResourceLocation icon) {
        this(id, title, subtitle, icon, true, "");
    }

    public EscHubLeaf(String id, String title, String subtitle) {
        this(id, title, subtitle, null, true, "");
    }

    

    public boolean disabled() {
        return !enabled;
    }
}
