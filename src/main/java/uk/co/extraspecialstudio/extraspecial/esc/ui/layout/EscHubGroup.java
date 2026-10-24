package uk.co.extraspecialstudio.extraspecial.esc.ui.layout;

import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.Objects;

/** Group of leaves under a section (e.g. Pantheon family / utility mod).
 * <p>
 * When {@code icon} is null, the constructor falls back to the first leaf icon
 * ({@link EscHubIconPolicy#INHERIT_FIRST_LEAF}). Pass an explicit icon whenever the
 * group represents an author, studio, or curated family — otherwise the badge will
 * silently follow whichever leaf sorts first.
 */
public final class EscHubGroup {
    private final String id;
    private final String title;
    private final List<EscHubLeaf> leaves;
    private final ResourceLocation icon;

    public EscHubGroup(String id, String title, List<EscHubLeaf> leaves, ResourceLocation icon) {
    Objects.requireNonNull(id, "id");
    Objects.requireNonNull(title, "title");
    leaves = leaves == null ? java.util.Collections.emptyList() : java.util.Collections.unmodifiableList(new java.util.ArrayList<>(leaves));
    if (icon == null) {
    icon = firstLeafIcon(leaves);
    }

        this.id = id;
        this.title = title;
        this.leaves = leaves;
        this.icon = icon;
    }

    public String id() { return this.id; }
    public String title() { return this.title; }
    public List<EscHubLeaf> leaves() { return this.leaves; }
    public ResourceLocation icon() { return this.icon; }

    public EscHubGroup(String id, String title, List<EscHubLeaf> leaves) {
        this(id, title, leaves, firstLeafIcon(leaves));
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
