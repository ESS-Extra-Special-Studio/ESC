package uk.co.extraspecialstudio.extraspecial.esc.ui.layout;

import net.minecraft.client.gui.GuiGraphics;
import uk.co.extraspecialstudio.extraspecial.esc.theme.EscBackdropStyle;
import uk.co.extraspecialstudio.extraspecial.esc.theme.EscThemeConfigs;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscBackground;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscCrtLayer;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscImage;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscPanel;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscRect;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscTypography;

import java.util.List;

/**
 * Left section rail + right detail pane — a true control-centre layout.
 */
public final class EscControlCentreLayout implements EscHubLayout {

    private static final int CHIP_H = 16;
    private static final int CHIP_GAP = 4;
    private static final int ROW_H = 28;
    private static final int STRIP_H = 26;

    @Override
    public EscHubLayoutId id() {
        return EscHubLayoutId.CONTROL_CENTRE;
    }

    @Override
    public void reset(EscHubLayoutContext ctx) {
    }

    @Override
    public void tick(EscHubLayoutContext ctx) {
    }

    private enum RailMode {
        FULL,
        ICON_ONLY,
        STRIP
    }

    private static RailMode railMode(EscRect bounds) {
        if (bounds.width() < 160) {
            return RailMode.STRIP;
        }
        if (bounds.width() < 280) {
            return RailMode.ICON_ONLY;
        }
        return RailMode.FULL;
    }

    private static EscRect[] panes(EscRect bounds, RailMode mode) {
        return switch (mode) {
            case STRIP -> new EscRect[]{bounds, new EscRect(bounds.x(), bounds.bottom(), 0, 0)};
            case ICON_ONLY -> bounds.splitColumns(new float[]{0.14f, 0.86f}, 8);
            case FULL -> bounds.splitColumns(new float[]{0.30f, 0.70f}, 8);
        };
    }

    @Override
    public void render(GuiGraphics g, EscHubLayoutContext ctx, EscRect bounds, int mouseX, int mouseY, float partialTick) {
        if (!ctx.hostDrawsChrome()) {
            EscBackground.render(g, bounds, ctx.style().theme());
        }
        List<EscHubSection> sections = ctx.sections();
        RailMode mode = railMode(bounds);
        EscRect[] panes = panes(bounds, mode);
        EscRect rail = panes[0];
        EscRect detail = panes.length > 1 ? panes[1] : bounds;

        if (mode == RailMode.STRIP) {
            renderStrip(g, ctx, rail, sections);
            EscHubSection sel = ctx.selectedSection();
            if (sel != null) {
                EscRect body = new EscRect(bounds.x(), rail.bottom() + 6, bounds.width(),
                    Math.max(40, bounds.height() - STRIP_H - 6));
                EscAccordion.render(g, ctx, body, sel.id());
            }
        } else {
            EscPanel.renderPanel(g, rail, ctx.style());
            int chipBlock = CHIP_H * 2 + CHIP_GAP + 8;
            EscRect listArea = new EscRect(rail.x(), rail.y() + 4, rail.width(),
                Math.max(ROW_H, rail.height() - chipBlock - 4));
            renderRail(g, ctx, listArea, sections, mode);

            int chipY = rail.bottom() - chipBlock;
            drawChip(g, ctx, new EscRect(rail.x() + 6, chipY, rail.width() - 12, CHIP_H),
                "Motion " + (EscThemeConfigs.reducedMotion() ? "OFF" : "ON"));
            EscBackdropStyle bg = EscThemeConfigs.backdropStyle();
            drawChip(g, ctx, new EscRect(rail.x() + 6, chipY + CHIP_H + CHIP_GAP, rail.width() - 12, CHIP_H),
                "BG: " + bg.label());

            EscHubSection sel = ctx.selectedSection();
            if (sel != null) {
                EscAccordion.render(g, ctx, detail, sel.id());
            }
        }

        if (!ctx.hostDrawsChrome()) {
            EscCrtLayer.render(g, bounds, ctx.style().theme());
        }
    }

    private void renderRail(GuiGraphics g, EscHubLayoutContext ctx, EscRect listArea, List<EscHubSection> sections, RailMode mode) {
        int rowH = Math.min(ROW_H, Math.max(22, listArea.height() / Math.max(1, sections.size())));
        for (int i = 0; i < sections.size(); i++) {
            EscHubSection s = sections.get(i);
            int y = listArea.y() + i * rowH;
            if (y + rowH > listArea.bottom()) {
                break;
            }
            EscRect row = new EscRect(listArea.x() + 4, y, listArea.width() - 8, rowH - 2);
            boolean sel = i == ctx.sectionIndex();
            EscPanel.fill(g, row, ctx.style().panelFillColor());
            if (sel) {
                EscPanel.border(g, row, ctx.style().focusBorderColor(), 2);
                int lip = (0xCC << 24) | (ctx.style().accentColor() & 0xFFFFFF);
                g.fill(row.x(), row.y(), row.x() + 3, row.bottom(), lip);
            } else {
                EscPanel.border(g, row, ctx.style().panelBorderColor(), 1);
            }

            int textX = row.x() + 6;
            if (s.icon() != null) {
                EscImage.drawAuto(g, s.icon(), row.x() + 4, row.y() + (row.height() - 14) / 2, 14, 14,
                    0xFFFFFFFF, sel ? 1f : 0.75f);
                textX = row.x() + 22;
            }
            if (mode == RailMode.FULL) {
                String title = EscTypography.truncate(ctx.font(), s.title(), Math.max(8, row.right() - textX - 24));
                g.drawString(ctx.font(), title, textX, row.y() + (row.height() - ctx.font().lineHeight) / 2,
                    sel ? ctx.style().textColorTitle() : ctx.style().textColorBody(), false);
                if (s.badgeCount() > 0) {
                    String badge = String.valueOf(s.badgeCount());
                    int bw = ctx.font().width(badge) + 8;
                    EscRect chip = new EscRect(row.right() - bw - 4, row.y() + 4, bw, row.height() - 8);
                    EscPanel.border(g, chip, ctx.style().accentColor() | 0xFF000000, 1);
                    g.drawString(ctx.font(), badge, chip.x() + 4, chip.y() + 2, ctx.style().accentColor() | 0xFF000000, false);
                }
            }
        }
    }

    private void renderStrip(GuiGraphics g, EscHubLayoutContext ctx, EscRect strip, List<EscHubSection> sections) {
        EscPanel.renderPanel(g, strip, ctx.style());
        EscStripSlots.Plan plan = EscStripSlots.plan(strip, sections.size(), 4, 4);
        for (int i = 0; i < sections.size(); i++) {
            EscHubSection s = sections.get(i);
            EscRect slot = EscStripSlots.slot(plan, strip, i, STRIP_H);
            boolean sel = i == ctx.sectionIndex();
            if (sel) {
                EscPanel.border(g, slot, ctx.style().focusBorderColor(), 1);
            }
            int textX = slot.x() + 4;
            if (s.icon() != null) {
                EscImage.drawAuto(g, s.icon(), slot.x() + 3, slot.y() + 6, 14, 14, 0xFFFFFFFF, sel ? 1f : 0.7f);
                textX = slot.x() + 20;
            }
            String label = sel ? "[" + s.title() + "]" : s.title();
            label = EscTypography.truncate(ctx.font(), label, Math.max(8, slot.right() - textX - 4));
            g.drawString(ctx.font(), label, textX, slot.y() + 9,
                sel ? ctx.style().textColorTitle() : ctx.style().textColorBody(), false);
        }
    }

    private static void drawChip(GuiGraphics g, EscHubLayoutContext ctx, EscRect chip, String label) {
        EscPanel.fill(g, chip, ctx.style().panelFillColor());
        EscPanel.border(g, chip, ctx.style().panelBorderColor(), 1);
        String text = EscTypography.truncate(ctx.font(), label, Math.max(8, chip.width() - 8));
        g.drawString(ctx.font(), text, chip.x() + 4, chip.y() + (chip.height() - ctx.font().lineHeight) / 2,
            ctx.style().textColorBody(), false);
    }

    @Override
    public boolean mouseClicked(EscHubLayoutContext ctx, EscRect bounds, double mouseX, double mouseY, int button) {
        if (button != 0) {
            return false;
        }
        List<EscHubSection> sections = ctx.sections();
        RailMode mode = railMode(bounds);
        EscRect[] panes = panes(bounds, mode);

        if (mode == RailMode.STRIP) {
            EscRect strip = panes[0];
            EscStripSlots.Plan plan = EscStripSlots.plan(strip, sections.size(), 4, 4);
            for (int i = 0; i < sections.size(); i++) {
                if (EscStripSlots.slot(plan, strip, i, STRIP_H).contains(mouseX, mouseY)) {
                    ctx.setSectionIndex(i);
                    return true;
                }
            }
            EscHubSection sel = ctx.selectedSection();
            EscRect body = new EscRect(bounds.x(), strip.bottom() + 6, bounds.width(),
                Math.max(40, bounds.height() - STRIP_H - 6));
            return sel != null && EscAccordion.mouseClicked(ctx, body, sel.id(), mouseX, mouseY);
        }

        EscRect rail = panes[0];
        EscRect detail = panes[1];
        int chipBlock = CHIP_H * 2 + CHIP_GAP + 8;
        EscRect listArea = new EscRect(rail.x(), rail.y() + 4, rail.width(),
            Math.max(ROW_H, rail.height() - chipBlock - 4));
        int rowH = Math.min(ROW_H, Math.max(22, listArea.height() / Math.max(1, sections.size())));
        for (int i = 0; i < sections.size(); i++) {
            int y = listArea.y() + i * rowH;
            EscRect row = new EscRect(listArea.x() + 4, y, listArea.width() - 8, rowH - 2);
            if (row.contains(mouseX, mouseY)) {
                ctx.setSectionIndex(i);
                return true;
            }
        }
        EscHubSection sel = ctx.selectedSection();
        return sel != null && EscAccordion.mouseClicked(ctx, detail, sel.id(), mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(EscHubLayoutContext ctx, EscRect bounds, double mouseX, double mouseY, double delta) {
        EscRect detail = detailBounds(ctx, bounds);
        EscHubSection sel = ctx.selectedSection();
        return sel != null && EscAccordion.mouseScrolled(ctx, detail, sel.id(), mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(EscHubLayoutContext ctx, int keyCode, int scanCode, int modifiers) {
        return EscHubNav.keyPressed(ctx, keyCode);
    }

    @Override
    public EscRect detailBounds(EscHubLayoutContext ctx, EscRect bounds) {
        RailMode mode = railMode(bounds);
        if (mode == RailMode.STRIP) {
            return new EscRect(bounds.x(), bounds.y() + STRIP_H + 6, bounds.width(),
                Math.max(40, bounds.height() - STRIP_H - 6));
        }
        return panes(bounds, mode)[1];
    }
}
