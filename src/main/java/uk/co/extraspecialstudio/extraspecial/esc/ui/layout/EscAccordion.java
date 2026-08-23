package uk.co.extraspecialstudio.extraspecial.esc.ui.layout;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscImage;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscPanel;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscRect;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscTypeRole;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscTypography;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscUiStyle;
import uk.co.extraspecialstudio.extraspecial.esc.ui.anim.EscFocusVisual;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Nested group/leaf list with scroll overflow and keyboard focus.
 */
public final class EscAccordion {
    private static final Map<String, Integer> SCROLL = new HashMap<>();
    private static String navCacheKey;
    private static List<NavItem> navCache = List.of();
    private static String rowLabelCacheKey;
    private static final Map<Integer, String> rowLabelCache = new HashMap<>();

    private EscAccordion() {
    }

    private sealed interface NavItem {
        record Group(EscHubGroup group) implements NavItem {
        }

        record Leaf(EscHubLeaf leaf, EscHubGroup parent) implements NavItem {
        }
    }

    public static void render(GuiGraphics g, EscHubLayoutContext ctx, EscRect area, String sectionId) {
        EscUiStyle style = ctx.style();
        Font font = ctx.font();
        EscPanel.renderPanel(g, area, style);
        int inset = ctx.detailTopInset();
        int scroll = SCROLL.getOrDefault(sectionId, 0);
        int tipLine = font.lineHeight + 4;
        int row = font.lineHeight + 6;
        int contentBottom = measureContentHeight(ctx, sectionId, tipLine, row);
        int viewH = Math.max(1, area.height() - 12 - inset);
        int maxScroll = Math.max(0, contentBottom - viewH);
        if (scroll > maxScroll) {
            scroll = maxScroll;
            SCROLL.put(sectionId, scroll);
        }
        int contentTop = area.y() + 6 + inset;
        int y = contentTop - scroll;

        g.enableScissor(area.x() + 1, contentTop - 2, area.right() - 1, area.bottom() - 1);
        try {
            List<String> tips = ctx.headerTips();
            if (tips != null) {
                for (String tip : tips) {
                    EscTypography.draw(g, font, tip, area.x() + 10, y, style, EscTypeRole.META, area.width() - 20, 1f);
                    y += tipLine;
                }
                if (!tips.isEmpty()) {
                    y += 4;
                }
            }
            List<EscHubGroup> groups = ctx.groupsFor(sectionId);
            if (groups.isEmpty()) {
                EscTypography.draw(g, font, "Nothing registered here yet.", area.x() + 10, y, style, EscTypeRole.BODY, area.width() - 20, 1f);
                return;
            }

            int navIndex = 0;
            int focus = ctx.detailCursor();
            boolean treeNav = ctx.curatedTreeNav(sectionId);
            ensureRowLabels(ctx, sectionId, groups, focus, treeNav);
            for (EscHubGroup group : groups) {
                boolean singleton = group.singleton();
                boolean open = !singleton && (group.id().equals(ctx.expandedGroupId()) || groups.size() == 1);
                boolean focused = focus == navIndex;
                if (focused) {
                    drawFocusRow(g, area, y, row, style);
                }
                int textX = area.x() + 10;
                if (group.icon() != null) {
                    EscImage.drawAuto(g, group.icon(), area.x() + 10, y, 14, 14, 0xFFFFFFFF, 1f);
                    textX = area.x() + 28;
                }
                String groupLabel = rowLabelCache.getOrDefault(navIndex,
                    singleton ? group.title() : ((open ? "▼ " : "▶ ") + group.title()));
                EscTypography.draw(g, font, groupLabel, textX, y, style, EscTypeRole.TITLE,
                    area.width() - (textX - area.x()) - 8, 1f);
                y += row;
                navIndex++;
                if (open) {
                    List<EscHubLeaf> leaves = group.leaves();
                    for (int li = 0; li < leaves.size(); li++) {
                        EscHubLeaf leaf = leaves.get(li);
                        boolean leafFocus = focus == navIndex;
                        if (leafFocus) {
                            drawFocusRow(g, area, y, row, style);
                        }
                        int leafTextX = area.x() + (treeNav ? 28 : 18);
                        if (treeNav) {
                            drawTreeBranch(g, area.x() + 10, y, row, style, li, leaves.size());
                        } else if (leaf.icon() != null) {
                            EscImage.drawAuto(g, leaf.icon(), area.x() + 12, y, 14, 14, 0xFFFFFFFF, 1f);
                            leafTextX = area.x() + 30;
                        }
                        String label = rowLabelCache.getOrDefault(navIndex, leafRowLabel(leaf, leafFocus, treeNav));
                        EscTypography.draw(g, font, label, leafTextX, y, style, EscTypeRole.BODY,
                            area.width() - (leafTextX - area.x()) - 8, 1f);
                        y += row;
                        navIndex++;
                    }
                }
                y += 2;
            }
        } finally {
            g.disableScissor();
        }

        if (maxScroll > 0) {
            int trackH = area.height() - 8;
            int thumbH = Math.max(12, trackH * area.height() / Math.max(area.height(), contentBottom));
            int thumbY = area.y() + 4 + (int) ((trackH - thumbH) * (scroll / (float) maxScroll));
            EscPanel.fill(g, new EscRect(area.right() - 4, thumbY, 2, thumbH), style.accentColor());
        }
    }

    private static void drawFocusRow(GuiGraphics g, EscRect area, int y, int row, EscUiStyle style) {
        int fill = EscFocusVisual.applyAlpha(style.accentColor() | 0xFF000000, 0.22f);
        EscPanel.fill(g, new EscRect(area.x() + 2, y - 1, area.width() - 6, row), fill);
        EscPanel.border(g, new EscRect(area.x() + 2, y - 1, area.width() - 6, row),
            EscFocusVisual.applyAlpha(style.focusBorderColor() | 0xFF000000, 0.7f), 1);
    }

    /** L-shaped branch: down from the spine, then right toward the leaf label. */
    private static void drawTreeBranch(
        GuiGraphics g, int baseX, int y, int rowH, EscUiStyle style, int index, int count
    ) {
        int color = (style.textColorBody() & 0xFFFFFF) | 0xCC000000;
        int spineX = baseX + 4;
        int armY = y + rowH / 2;
        int tipX = baseX + 14;
        g.fill(spineX, armY, tipX, armY + 1, color);
        if (count <= 1) {
            g.fill(spineX, armY - 4, spineX + 1, armY + 1, color);
            return;
        }
        if (index == 0) {
            g.fill(spineX, armY, spineX + 1, y + rowH, color);
        } else if (index < count - 1) {
            g.fill(spineX, y, spineX + 1, y + rowH, color);
        } else {
            g.fill(spineX, y, spineX + 1, armY + 1, color);
        }
    }

    public static boolean moveFocus(EscHubLayoutContext ctx, String sectionId, int delta) {
        List<NavItem> nav = buildNav(ctx, sectionId);
        if (nav.isEmpty()) {
            return false;
        }
        int cur = ctx.detailCursor();
        if (cur < 0) {
            cur = delta > 0 ? 0 : nav.size() - 1;
            ctx.setDetailCursor(cur);
            ensureVisible(ctx, sectionId, cur);
            return true;
        }
        int next = cur + delta;
        if (next < 0 || next >= nav.size()) {
            return false;
        }
        ctx.setDetailCursor(next);
        ensureVisible(ctx, sectionId, next);
        return true;
    }

    public static boolean activateFocused(EscHubLayoutContext ctx, String sectionId) {
        List<NavItem> nav = buildNav(ctx, sectionId);
        if (nav.isEmpty()) {
            return false;
        }
        int cur = ctx.detailCursor();
        if (cur < 0) {
            cur = 0;
            ctx.setDetailCursor(0);
        }
        if (cur >= nav.size()) {
            return false;
        }
        NavItem item = nav.get(cur);
        if (item instanceof NavItem.Group g) {
            if (g.group().singleton()) {
                ctx.openLeaf(g.group().leaves().get(0));
                return true;
            }
            ctx.toggleGroup(g.group().id());
            // Keep cursor on the group header after toggle
            ctx.setDetailCursor(indexOfGroup(ctx, sectionId, g.group().id()));
            return true;
        }
        if (item instanceof NavItem.Leaf leaf) {
            ctx.openLeaf(leaf.leaf());
            return true;
        }
        return false;
    }

    private static int indexOfGroup(EscHubLayoutContext ctx, String sectionId, String groupId) {
        List<NavItem> nav = buildNav(ctx, sectionId);
        for (int i = 0; i < nav.size(); i++) {
            if (nav.get(i) instanceof NavItem.Group g && g.group().id().equals(groupId)) {
                return i;
            }
        }
        return 0;
    }

    private static void ensureVisible(EscHubLayoutContext ctx, String sectionId, int navIndex) {
        Font font = ctx.font();
        int tipLine = font.lineHeight + 4;
        int row = font.lineHeight + 6;
        int tipsH = 0;
        List<String> tips = ctx.headerTips();
        if (tips != null && !tips.isEmpty()) {
            tipsH = tips.size() * tipLine + 4;
        }
        int y = tipsH + navIndex * row;
        int scroll = SCROLL.getOrDefault(sectionId, 0);
        // Approximate: keep focused row near top third — layouts pass area height via scroll map only.
        // Use a virtual viewport of ~120px if unknown; scroll by row steps.
        int view = 140;
        if (y < scroll) {
            SCROLL.put(sectionId, Math.max(0, y));
        } else if (y + row > scroll + view) {
            SCROLL.put(sectionId, Math.max(0, y + row - view));
        }
    }

    private static List<NavItem> buildNav(EscHubLayoutContext ctx, String sectionId) {
        List<EscHubGroup> groups = ctx.groupsFor(sectionId);
        String key = sectionId + "|" + ctx.expandedGroupId() + "|" + System.identityHashCode(groups)
            + "|" + groups.size();
        if (key.equals(navCacheKey) && navCache != null) {
            return navCache;
        }
        List<NavItem> nav = new ArrayList<>();
        for (EscHubGroup group : groups) {
            nav.add(new NavItem.Group(group));
            if (group.singleton()) {
                continue;
            }
            boolean open = group.id().equals(ctx.expandedGroupId()) || groups.size() == 1;
            if (open) {
                for (EscHubLeaf leaf : group.leaves()) {
                    nav.add(new NavItem.Leaf(leaf, group));
                }
            }
        }
        navCacheKey = key;
        navCache = nav;
        return nav;
    }

    private static void ensureRowLabels(EscHubLayoutContext ctx, String sectionId, List<EscHubGroup> groups, int focus, boolean treeNav) {
        String key = sectionId + "|" + ctx.expandedGroupId() + "|" + System.identityHashCode(groups)
            + "|" + focus + "|" + treeNav;
        if (key.equals(rowLabelCacheKey)) {
            return;
        }
        rowLabelCache.clear();
        int navIndex = 0;
        for (EscHubGroup group : groups) {
            boolean singleton = group.singleton();
            boolean open = !singleton && (group.id().equals(ctx.expandedGroupId()) || groups.size() == 1);
            rowLabelCache.put(navIndex, singleton ? group.title() : ((open ? "▼ " : "▶ ") + group.title()));
            navIndex++;
            if (open) {
                for (EscHubLeaf leaf : group.leaves()) {
                    rowLabelCache.put(navIndex, leafRowLabel(leaf, focus == navIndex, treeNav));
                    navIndex++;
                }
            }
        }
        rowLabelCacheKey = key;
    }

    private static String leafRowLabel(EscHubLeaf leaf, boolean focused, boolean treeNav) {
        String sub = leaf.subtitle().isBlank() ? "" : "  — " + leaf.subtitle();
        if (treeNav) {
            return leaf.title() + sub;
        }
        String prefix = focused ? "> " : "  ";
        return prefix + leaf.title() + sub;
    }

    public static boolean mouseScrolled(EscHubLayoutContext ctx, EscRect area, String sectionId, double mx, double my, double delta) {
        if (!area.contains(mx, my)) {
            return false;
        }
        Font font = ctx.font();
        int tipLine = font.lineHeight + 4;
        int row = font.lineHeight + 6;
        int contentBottom = measureContentHeight(ctx, sectionId, tipLine, row);
        int maxScroll = Math.max(0, contentBottom - Math.max(1, area.height() - 12 - ctx.detailTopInset()));
        if (maxScroll <= 0) {
            return false;
        }
        int scroll = SCROLL.getOrDefault(sectionId, 0);
        scroll = Math.max(0, Math.min(maxScroll, scroll - (int) Math.signum(delta) * row));
        SCROLL.put(sectionId, scroll);
        return true;
    }

    public static boolean mouseClicked(EscHubLayoutContext ctx, EscRect area, String sectionId, double mx, double my) {
        if (!area.contains(mx, my)) {
            return false;
        }
        Font font = ctx.font();
        int scroll = SCROLL.getOrDefault(sectionId, 0);
        int y = area.y() + 6 + ctx.detailTopInset() - scroll;
        int tipLine = font.lineHeight + 4;
        List<String> tips = ctx.headerTips();
        if (tips != null && !tips.isEmpty()) {
            y += tips.size() * tipLine + 4;
        }
        List<EscHubGroup> groups = ctx.groupsFor(sectionId);
        int row = font.lineHeight + 6;
        int navIndex = 0;
        for (EscHubGroup group : groups) {
            EscRect header = new EscRect(area.x(), y, area.width(), row);
            boolean singleton = group.singleton();
            boolean open = !singleton && (group.id().equals(ctx.expandedGroupId()) || groups.size() == 1);
            if (header.contains(mx, my)) {
                ctx.setDetailCursor(navIndex);
                if (singleton) {
                    ctx.openLeaf(group.leaves().get(0));
                } else {
                    ctx.toggleGroup(group.id());
                }
                return true;
            }
            y += row;
            navIndex++;
            if (open) {
                for (EscHubLeaf leaf : group.leaves()) {
                    EscRect leafRow = new EscRect(area.x(), y, area.width(), row);
                    if (leafRow.contains(mx, my)) {
                        ctx.setDetailCursor(navIndex);
                        ctx.openLeaf(leaf);
                        return true;
                    }
                    y += row;
                    navIndex++;
                }
            }
            y += 2;
        }
        return false;
    }

    private static int measureContentHeight(EscHubLayoutContext ctx, String sectionId, int tipLine, int row) {
        int h = 6;
        List<String> tips = ctx.headerTips();
        if (tips != null && !tips.isEmpty()) {
            h += tips.size() * tipLine + 4;
        }
        List<EscHubGroup> groups = ctx.groupsFor(sectionId);
        for (EscHubGroup group : groups) {
            h += row;
            boolean singleton = group.singleton();
            boolean open = !singleton && (group.id().equals(ctx.expandedGroupId()) || groups.size() == 1);
            if (open) {
                h += group.leaves().size() * row;
            }
            h += 2;
        }
        return h + 6;
    }
}
