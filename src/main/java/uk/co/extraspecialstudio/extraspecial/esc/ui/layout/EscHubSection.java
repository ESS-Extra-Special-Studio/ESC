package uk.co.extraspecialstudio.extraspecial.esc.ui.layout;

import net.minecraft.util.ResourceLocation;

import java.util.Objects;

/** Top-level hub section (e.g. ESS / MODS / UTILITY). */
public final class EscHubSection {
    private final String id;
    private final String title;
    private final String subtitle;
    private final int badgeCount;
    private final ResourceLocation icon;

    public EscHubSection(String id, String title, String subtitle, int badgeCount, ResourceLocation icon) {
    Objects.requireNonNull(id, "id");
    Objects.requireNonNull(title, "title");
    subtitle = subtitle == null ? "" : subtitle;

        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.badgeCount = badgeCount;
        this.icon = icon;
    }

    public String id() { return this.id; }
    public String title() { return this.title; }
    public String subtitle() { return this.subtitle; }
    public int badgeCount() { return this.badgeCount; }
    public ResourceLocation icon() { return this.icon; }

    public EscHubSection(String id, String title, String subtitle, int badgeCount) {
        this(id, title, subtitle, badgeCount, null);
    }

    
}
