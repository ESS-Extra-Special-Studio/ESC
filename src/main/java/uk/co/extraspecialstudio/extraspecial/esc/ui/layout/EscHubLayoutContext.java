package uk.co.extraspecialstudio.extraspecial.esc.ui.layout;

import net.minecraft.client.gui.Font;
import uk.co.extraspecialstudio.extraspecial.esc.sound.EscSound;
import uk.co.extraspecialstudio.extraspecial.esc.sound.EscUiCue;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscUiStyle;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Runtime context shared by all {@link EscHubLayout} implementations.
 */
public final class EscHubLayoutContext {
    private final List<EscHubSection> sections;
    private final Function<String, List<EscHubGroup>> groupsForSection;
    private final Consumer<EscHubLeaf> openLeaf;
    private final EscUiStyle style;
    private final Font font;
    private int sectionIndex;
    private String expandedGroupId = "";
    private EscHubLayoutId layoutId = EscHubLayoutId.CAROUSEL;
    private List<String> headerTips = List.of();
    /** Flat accordion focus index within the selected section (-1 = none). */
    private int detailCursor = -1;
    /** Extra top padding inside the detail accordion (e.g. MODS search/filter bar). */
    private int detailTopInset;
    /**
     * When true, the host screen already drew {@code EscBackground} + {@code EscCrtLayer};
     * layouts skip their own copies.
     */
    private boolean hostDrawsChrome;
    /** Curated sections (e.g. ESS stack docs) use tree branches instead of leaf/group logos. */
    private java.util.function.Predicate<String> curatedTreeNav = id -> false;
    private String cachedGroupsSectionId;
    private List<EscHubGroup> cachedGroups;

    public EscHubLayoutContext(
        List<EscHubSection> sections,
        Function<String, List<EscHubGroup>> groupsForSection,
        Consumer<EscHubLeaf> openLeaf,
        EscUiStyle style,
        Font font
    ) {
        this.sections = List.copyOf(Objects.requireNonNull(sections, "sections"));
        this.groupsForSection = Objects.requireNonNull(groupsForSection, "groupsForSection");
        this.openLeaf = Objects.requireNonNull(openLeaf, "openLeaf");
        this.style = Objects.requireNonNull(style, "style");
        this.font = Objects.requireNonNull(font, "font");
        this.sectionIndex = sections.isEmpty() ? 0 : 0;
    }

    public List<EscHubSection> sections() {
        return sections;
    }

    public List<EscHubGroup> groupsFor(String sectionId) {
        if (sectionId != null && sectionId.equals(cachedGroupsSectionId) && cachedGroups != null) {
            return cachedGroups;
        }
        List<EscHubGroup> g = groupsForSection.apply(sectionId);
        List<EscHubGroup> out = g == null ? List.of() : g;
        cachedGroupsSectionId = sectionId;
        cachedGroups = out;
        return out;
    }

    /** Drop cached {@link #groupsFor} result (search/mode/logo updates). */
    public void invalidateGroupsCache() {
        cachedGroupsSectionId = null;
        cachedGroups = null;
    }

    public void openLeaf(EscHubLeaf leaf) {
        if (leaf != null) {
            EscSound.play(EscUiCue.CONFIRM);
            openLeaf.accept(leaf);
        }
    }

    public EscUiStyle style() {
        return style;
    }

    public Font font() {
        return font;
    }

    public int sectionIndex() {
        return sectionIndex;
    }

    public void setSectionIndex(int index) {
        setSectionIndex(index, true);
    }

    /**
     * @param audible when true and the index changes, plays a focus cue (mouse / programmatic).
     *                Keyboard carousel passes false and plays carousel cues itself.
     */
    public void setSectionIndex(int index, boolean audible) {
        if (sections.isEmpty()) {
            sectionIndex = 0;
            return;
        }
        int n = sections.size();
        int next = Math.floorMod(index, n);
        if (next != sectionIndex) {
            detailCursor = -1;
            if (audible) {
                EscSound.playFocus();
            }
        }
        sectionIndex = next;
    }

    public void cycleSection(int delta) {
        setSectionIndex(sectionIndex + delta, false);
    }

    public int detailCursor() {
        return detailCursor;
    }

    public void setDetailCursor(int detailCursor) {
        this.detailCursor = detailCursor;
    }

    public void resetDetailCursor() {
        this.detailCursor = -1;
    }

    public EscHubSection selectedSection() {
        if (sections.isEmpty()) {
            return null;
        }
        return sections.get(sectionIndex);
    }

    public String expandedGroupId() {
        return expandedGroupId;
    }

    public void toggleGroup(String groupId) {
        if (groupId == null || groupId.isBlank()) {
            return;
        }
        boolean collapsing = groupId.equals(expandedGroupId);
        expandedGroupId = collapsing ? "" : groupId;
        EscSound.play(collapsing ? EscUiCue.COLLAPSE : EscUiCue.EXPAND);
    }

    public void setExpandedGroupId(String groupId) {
        expandedGroupId = groupId == null ? "" : groupId;
    }

    public EscHubLayoutId layoutId() {
        return layoutId;
    }

    public void setLayoutId(EscHubLayoutId layoutId) {
        this.layoutId = layoutId == null ? EscHubLayoutId.CAROUSEL : layoutId;
    }

    public List<String> headerTips() {
        return headerTips;
    }

    public void setHeaderTips(List<String> headerTips) {
        this.headerTips = headerTips == null ? List.of() : List.copyOf(headerTips);
    }

    /** Pixels reserved at the top of the detail accordion for host chrome (search/filter). */
    public int detailTopInset() {
        return Math.max(0, detailTopInset);
    }

    public void setDetailTopInset(int detailTopInset) {
        this.detailTopInset = Math.max(0, detailTopInset);
    }

    public boolean hostDrawsChrome() {
        return hostDrawsChrome;
    }

    public void setHostDrawsChrome(boolean hostDrawsChrome) {
        this.hostDrawsChrome = hostDrawsChrome;
    }

    /** ESS-style curated lists: L-shaped tree branches instead of mod logos on leaves. */
    public void setCuratedTreeNav(java.util.function.Predicate<String> predicate) {
        this.curatedTreeNav = predicate == null ? id -> false : predicate;
    }

    public boolean curatedTreeNav(String sectionId) {
        return sectionId != null && curatedTreeNav.test(sectionId);
    }
}
