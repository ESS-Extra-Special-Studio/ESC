package uk.co.extraspecialstudio.extraspecial.esc.ui.layout;

import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

/** Top-level hub section (e.g. ESS / MODS / UTILITY). */
public record EscHubSection(String id, String title, String subtitle, int badgeCount, ResourceLocation icon) {
    public EscHubSection(String id, String title, String subtitle, int badgeCount) {
        this(id, title, subtitle, badgeCount, null);
    }

    public EscHubSection {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(title, "title");
        subtitle = subtitle == null ? "" : subtitle;
    }
}
