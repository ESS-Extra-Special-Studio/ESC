package uk.co.extraspecialstudio.extraspecial.esc.ui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Horizontal tab strip for multi-page ESC screens (e.g. Pip-Boy RADIO / SPECIAL / INV).
 * Layout-only helper — callers own selection state and click handling.
 */
public final class EscTabBar {

    public record Tab(String id, String label, boolean enabled) {
        public Tab {
            Objects.requireNonNull(id, "id");
            Objects.requireNonNull(label, "label");
        }

        public static Tab of(String id, String label) {
            return new Tab(id, label, true);
        }

        public static Tab disabled(String id, String label) {
            return new Tab(id, label, false);
        }
    }

    public record Style(
        int tabWidth,
        int tabHeight,
        int gap,
        int bgSelected,
        int bgIdle,
        int bgHover,
        int bgDisabled,
        int fgSelected,
        int fgIdle,
        int fgDisabled,
        int underlineSelected,
        int underlineIdle,
        /** When true, selected tab is drawn as {@code [ LABEL ]} with a rule gap under it (Fallout). */
        boolean bracketSelected,
        /** When true, skip opaque tab cell fills (text + rule only). */
        boolean textOnly
    ) {
        public Style(
            int tabWidth,
            int tabHeight,
            int gap,
            int bgSelected,
            int bgIdle,
            int bgHover,
            int bgDisabled,
            int fgSelected,
            int fgIdle,
            int fgDisabled,
            int underlineSelected,
            int underlineIdle
        ) {
            this(
                tabWidth, tabHeight, gap,
                bgSelected, bgIdle, bgHover, bgDisabled,
                fgSelected, fgIdle, fgDisabled,
                underlineSelected, underlineIdle,
                false, false
            );
        }

        /** Neon green on dark — Pip-Boy / Dead Air radio family. */
        public static Style pipBoy() {
            return new Style(
                72, 18, 4,
                0xFF446500, 0xFF2A4000, 0xFF335000, 0xFF1A2800,
                0xFF00FF41, 0xFF88CC66, 0xFF556644,
                0xFF00E050, 0xFF224400,
                false, false
            );
        }

        /** Fallout STAT strip: bracketed selected tab, phosphor text, rule under bar. */
        public static Style falloutPip() {
            return new Style(
                64, 16, 6,
                0x00000000, 0x00000000, 0x00000000, 0x00000000,
                EscFalloutDraw.PHOSPHOR, EscFalloutDraw.PHOSPHOR_DIM, 0xFF335533,
                EscFalloutDraw.PHOSPHOR, EscFalloutDraw.PHOSPHOR_DIM,
                true, true
            );
        }
    }

    private final List<Tab> tabs;
    private final Style style;
    private int selectedIndex;
    private EscRect bounds = new EscRect(0, 0, 0, 0);
    /** Runtime cell width (may shrink to fit). */
    private int cellWidth;
    /** Runtime gap between cells. */
    private int cellGap;

    public EscTabBar(List<Tab> tabs, Style style) {
        this.tabs = List.copyOf(tabs);
        this.style = Objects.requireNonNull(style, "style");
        this.selectedIndex = 0;
        this.cellWidth = style.tabWidth();
        this.cellGap = style.gap();
        if (this.tabs.isEmpty()) {
            throw new IllegalArgumentException("EscTabBar requires at least one tab");
        }
    }

    public static EscTabBar of(Style style, Tab... tabs) {
        return new EscTabBar(List.of(tabs), style);
    }

    public List<Tab> tabs() {
        return tabs;
    }

    public Style style() {
        return style;
    }

    public int selectedIndex() {
        return selectedIndex;
    }

    public Tab selected() {
        return tabs.get(selectedIndex);
    }

    public void setSelectedIndex(int index) {
        if (index >= 0 && index < tabs.size()) {
            this.selectedIndex = index;
        }
    }

    public void selectId(String id) {
        for (int i = 0; i < tabs.size(); i++) {
            if (tabs.get(i).id().equals(id)) {
                selectedIndex = i;
                return;
            }
        }
    }

    /** Place the bar at the top-left of {@code origin}; recomputes {@link #bounds()}. */
    public EscRect layoutAt(int x, int y) {
        cellWidth = style.tabWidth();
        cellGap = style.gap();
        int w = tabs.size() * cellWidth + Math.max(0, tabs.size() - 1) * cellGap;
        bounds = new EscRect(x, y, w, style.tabHeight());
        return bounds;
    }

    /**
     * Fit the tab strip into {@code maxWidth}: equal cells spanning the full width so nothing
     * spills past the chrome. Prefer this for Fallout Pip headers.
     */
    public EscRect layoutFit(int x, int y, int maxWidth) {
        int n = tabs.size();
        int width = Math.max(n * 8, maxWidth);
        // Prefer a small gap; collapse to 0 when space is tight.
        int gap = Math.min(style.gap(), 4);
        int inner = width - Math.max(0, n - 1) * gap;
        int tw = Math.max(1, inner / n);
        // If preferred style width fits with gaps, use it centered-ish by expanding gap.
        int preferred = style.tabWidth();
        int preferredTotal = n * preferred + Math.max(0, n - 1) * gap;
        if (preferredTotal <= width) {
            // Spread leftover space into gaps so the strip still spans the full chrome.
            int leftover = width - n * preferred;
            cellWidth = preferred;
            cellGap = n > 1 ? leftover / (n - 1) : 0;
            // Absorb rounding remainder into the last gap by stretching bounds to full width.
            bounds = new EscRect(x, y, width, style.tabHeight());
            return bounds;
        }
        cellWidth = tw;
        cellGap = gap;
        bounds = new EscRect(x, y, width, style.tabHeight());
        return bounds;
    }

    public EscRect bounds() {
        return bounds;
    }

    /** Content region under this bar inside {@code parent} (parent usually full chrome / content rect). */
    public EscRect bodyBelow(EscRect parent, int gapBelowTabs) {
        return EscPanel.bodyBelowTabs(parent, style.tabHeight(), gapBelowTabs);
    }

    public void render(GuiGraphics graphics, Font font, int mouseX, int mouseY) {
        EscRect selectedCell = null;
        for (int i = 0; i < tabs.size(); i++) {
            Tab tab = tabs.get(i);
            EscRect cell = tabRect(i);
            boolean selected = i == selectedIndex;
            boolean hover = cell.contains(mouseX, mouseY);
            int bg;
            int fg;
            if (!tab.enabled()) {
                bg = style.bgDisabled();
                fg = style.fgDisabled();
            } else if (selected) {
                bg = style.bgSelected();
                fg = style.fgSelected();
                selectedCell = cell;
            } else if (hover) {
                bg = style.bgHover();
                fg = style.fgIdle();
            } else {
                bg = style.bgIdle();
                fg = style.fgIdle();
            }
            if (!style.textOnly()) {
                EscPanel.fill(graphics, cell, bg);
                int underline = selected ? style.underlineSelected() : style.underlineIdle();
                graphics.fill(cell.x(), cell.bottom() - 1, cell.right(), cell.bottom(), underline);
            }
            String label = tab.label();
            if (style.bracketSelected() && selected) {
                label = "[ " + label + " ]";
            }
            int tw = font.width(label);
            // Keep label inside the cell — never spill into the next tab / past chrome.
            int tx = cell.x() + Math.max(0, (cell.width() - tw) / 2);
            if (tx + tw > cell.right()) {
                tx = Math.max(cell.x(), cell.right() - tw);
            }
            graphics.drawString(font, label,
                tx,
                cell.y() + Math.max(1, (cell.height() - 8) / 2),
                fg, false);
        }
        if (style.textOnly() || style.bracketSelected()) {
            int ruleY = bounds.bottom() - 1;
            // Rule spans the full laid-out strip (chrome width when using layoutFit).
            if (selectedCell != null && style.bracketSelected()) {
                EscFalloutDraw.greenRuleWithGap(
                    graphics,
                    bounds.x(), ruleY, bounds.right(),
                    selectedCell.x() + 2, selectedCell.right() - 2,
                    style.underlineSelected()
                );
            } else {
                EscFalloutDraw.greenRule(graphics, bounds.x(), ruleY, bounds.right(), style.underlineSelected());
            }
        }
    }

    /**
     * @return tab index under the cursor, or {@code -1}
     */
    public int hitTest(double mouseX, double mouseY) {
        for (int i = 0; i < tabs.size(); i++) {
            if (tabRect(i).contains(mouseX, mouseY)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Select the tab under the cursor if it is enabled.
     * @return true if selection changed or an enabled tab was clicked
     */
    public boolean mouseClicked(double mouseX, double mouseY) {
        int hit = hitTest(mouseX, mouseY);
        if (hit < 0) {
            return false;
        }
        Tab tab = tabs.get(hit);
        if (!tab.enabled() && hit != selectedIndex) {
            // Still allow selecting disabled stubs so "Coming soon" panels can show.
            selectedIndex = hit;
            return true;
        }
        selectedIndex = hit;
        return true;
    }

    private EscRect tabRect(int index) {
        int x = bounds.x() + index * (cellWidth + cellGap);
        // Last cell absorbs leftover pixels so the strip flush-fills layoutFit width.
        int w = cellWidth;
        if (index == tabs.size() - 1) {
            w = Math.max(cellWidth, bounds.right() - x);
        }
        return new EscRect(x, bounds.y(), w, style.tabHeight());
    }

    /** Mutable builder for callers that assemble tabs dynamically. */
    public static final class Builder {
        private final List<Tab> tabs = new ArrayList<>();
        private Style style = Style.pipBoy();

        public Builder style(Style style) {
            this.style = style;
            return this;
        }

        public Builder add(Tab tab) {
            tabs.add(tab);
            return this;
        }

        public Builder add(String id, String label, boolean enabled) {
            tabs.add(new Tab(id, label, enabled));
            return this;
        }

        public EscTabBar build() {
            return new EscTabBar(tabs, style);
        }
    }
}
