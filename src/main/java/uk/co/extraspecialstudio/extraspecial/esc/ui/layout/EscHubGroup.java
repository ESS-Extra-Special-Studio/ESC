package uk.co.extraspecialstudio.extraspecial.esc.ui.layout;

import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Objects;

/** Group of leaves under a section (e.g. Pantheon family / utility mod).
 * <p>
 * When {@code icon} is null, the constructor falls back to the first leaf icon
 * ({@link EscHubIconPolicy#INHERIT_FIRST_LEAF}). Pass an explicit icon whenever the
 * group represents an author, studio, or curated family — otherwise the badge will
 * silently follow whichever leaf sorts first.
 */
public record EscHubGroup(String id, String title, List<EscHubLeaf> leaves, ResourceLocation icon) {
    public EscHubGroup(String id, String title, List<EscHubLeaf> leaves) {
        this(id, title, leaves, firstLeafIcon(leaves));
    }

    public EscHubGroup {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(title, "title");
        leaves = leaves == null ? List.of() : List.copyOf(leaves);
        if (icon == null) {
            icon = firstLeafIcon(leaves);
        }
    }

    /** True when this group is a single openable entry (common for UTILITY). */
    public boolean singleton() {
        return leaves.size() == 1;
    }

    private static ResourceLocation firstLeafIcon(List<EscHubLeaf> leaves) {
        if (leaves == null) {
            return null;
        }
        for (EscHubLeaf leaf : leaves) {
            if (leaf != null && leaf.icon() != null) {
                return leaf.icon();
            }
        }
        return null;
    }
}
