package uk.co.extraspecialstudio.extraspecial.esc.ui.layout;

import net.minecraft.client.gui.GuiGraphics;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscBackground;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscCard;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscCrtLayer;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscFonts;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscPanel;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscRect;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscText;

import java.util.ArrayList;
import java.util.List;

/** Side-by-side tile column + detail accordion. */
public final class EscDashboardLayout implements EscHubLayout {

    private static final int GAP = 8;
    private static final int STATUS_H = 36;
    private static final int MAX_TILE_COLS = 4;

    @Override
    public EscHubLayoutId id() {
        return EscHubLayoutId.DASHBOARD;
    }

    @Override
    public void reset(EscHubLayoutContext ctx) {
    }

    @Override
    public void tick(EscHubLayoutContext ctx) {
    }

    private static EscRect[] panes(EscRect bounds) {
        if (bounds.width() < 220) {
            EscRect[] rows = EscLayoutBands.rows(bounds, new float[]{0.45f, 0.55f}, GAP, new int[]{48, 72});
            return new EscRect[]{rows[0], rows[1]};
        }
        return bounds.splitColumns(new float[]{0.40f, 0.60f}, GAP);
    }

    private static int tileCols(EscRect tileColumn) {
        return tileColumn.width() >= 180 ? 2 : 1;
    }

    private record TileSlot(EscRect rect, int sectionIndex) {
    }

    private static List<TileSlot> buildTiles(EscRect tileColumn, List<EscHubSection> sections) {
        List<TileSlot> slots = new ArrayList<>();
        EscRect statusRow = new EscRect(tileColumn.x(), tileColumn.y(), tileColumn.width(), STATUS_H);
        slots.add(new TileSlot(statusRow, -1));

        EscRect grid = new EscRect(tileColumn.x(), statusRow.bottom() + GAP, tileColumn.width(),
            Math.max(0, tileColumn.height() - STATUS_H - GAP));
        // Tiles below MIN_TILE_H are dropped, which would hide sections — add columns
        // until every section has a slot rather than losing tabs off the bottom.
        int cols = tileCols(grid);
        EscRect[] tiles = EscLayoutBands.tiles(grid, sections.size(), cols, GAP);
        while (tiles.length < sections.size() && cols < MAX_TILE_COLS) {
            cols++;
            tiles = EscLayoutBands.tiles(grid, sections.size(), cols, GAP);
        }
        for (int i = 0; i < tiles.length; i++) {
            slots.add(new TileSlot(tiles[i], i));
        }
        return slots;
    }

    @Override
    public void render(GuiGraphics g, EscHubLayoutContext ctx, EscRect bounds, int mouseX, int mouseY, float partialTick) {
        List<EscHubSection> sections = ctx.sections();
        if (!ctx.hostDrawsChrome()) {
            EscBackground.render(g, bounds, ctx.style().theme());
        }
        EscRect[] panes = panes(bounds);
        EscRect tileColumn = panes[0];
        EscRect detail = panes[1];

        List<TileSlot> slots = buildTiles(tileColumn, sections);
        for (TileSlot slot : slots) {
            if (slot.sectionIndex() < 0) {
                renderStatus(g, ctx, slot.rect(), sections.size());
            } else {
                EscHubSection s = sections.get(slot.sectionIndex());
                float focus = slot.sectionIndex() == ctx.sectionIndex() ? 1f : 0.45f;
                String badge = s.badgeCount() > 0 ? String.valueOf(s.badgeCount()) : "";
                EscCard.render(g, ctx.font(), slot.rect(), ctx.style(),
                    s.title(), s.subtitle(), badge, focus, s.icon());
            }
        }

        EscHubSection sel = ctx.selectedSection();
        if (sel != null) {
            EscAccordion.render(g, ctx, detail, sel.id());
        } else {
            EscPanel.renderPanel(g, detail, ctx.style());
        }
        if (!ctx.hostDrawsChrome()) {
            EscCrtLayer.render(g, bounds, ctx.style().theme());
        }
    }

    private static void renderStatus(GuiGraphics g, EscHubLayoutContext ctx, EscRect row, int sectionCount) {
        EscPanel.renderPanel(g, row, ctx.style());
        int pad = 10;
        int accent = ctx.style().accentColor() | 0xFF000000;
        String badge = sectionCount + " sections";
        int bw = EscText.width(ctx.font(), badge) + 10;
        EscRect chip = new EscRect(row.right() - bw - pad, row.y() + (row.height() - ctx.font().lineHeight - 4) / 2,
            bw, ctx.font().lineHeight + 4);

        int textMax = Math.max(0, chip.x() - pad - (row.x() + pad));
        g.enableScissor(row.x() + 1, row.y() + 1, row.right() - 1, row.bottom() - 1);
        try {
            EscText.drawScrollingString(g, ctx.font(), "STATUS", row.x() + pad, row.y() + 6,
                textMax, ctx.style().textColorTitle() | 0xFF000000);
            int subY = row.y() + 6 + ctx.font().lineHeight + 2;
            if (subY + ctx.font().lineHeight <= row.bottom() - 2) {
                EscText.drawScrollingString(g, ctx.font(), "Extra Special Hub", row.x() + pad, subY,
                    textMax, ctx.style().textColorBody() | 0xFF000000);
            }
        } finally {
            g.disableScissor();
        }

        EscPanel.border(g, chip, accent, 1);
        EscText.drawString(g, ctx.font(), badge, chip.x() + 5, chip.y() + 2, accent, false, EscFonts.DEFAULT);
    }

    @Override
    public boolean mouseClicked(EscHubLayoutContext ctx, EscRect bounds, double mouseX, double mouseY, int button) {
        if (button != 0) {
            return false;
        }
        List<EscHubSection> sections = ctx.sections();
        EscRect detail = panes(bounds)[1];
        List<TileSlot> slots = buildTiles(panes(bounds)[0], sections);

        for (TileSlot slot : slots) {
            if (slot.sectionIndex() < 0) {
                continue;
            }
            float focus = slot.sectionIndex() == ctx.sectionIndex() ? 1f : 0.45f;
            if (EscCard.hit(slot.rect(), mouseX, mouseY, focus)) {
                ctx.setSectionIndex(slot.sectionIndex());
                return true;
            }
        }
        EscHubSection sel = ctx.selectedSection();
        return sel != null && EscAccordion.mouseClicked(ctx, detail, sel.id(), mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(EscHubLayoutContext ctx, EscRect bounds, double mouseX, double mouseY, double delta) {
        EscHubSection sel = ctx.selectedSection();
        return sel != null && EscAccordion.mouseScrolled(ctx, detailBounds(ctx, bounds), sel.id(), mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(EscHubLayoutContext ctx, int keyCode, int scanCode, int modifiers) {
        return EscHubNav.keyPressed(ctx, keyCode);
    }

    @Override
    public EscRect detailBounds(EscHubLayoutContext ctx, EscRect bounds) {
        return panes(bounds)[1];
    }
}
