package uk.co.extraspecialstudio.extraspecial.esc.ui.layout;

import net.minecraft.client.gui.GuiGraphics;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscBackground;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscCrtLayer;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscImage;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscPanel;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscRect;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscTypography;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscTypeRole;

import java.util.List;

/** Flat section tabs + accordion — readable fallback. */
public final class EscClassicLayout implements EscHubLayout {

    private static final int TAB_H = 26;
    private static final int PAD = 4;
    private static final int GAP = 4;

    @Override
    public EscHubLayoutId id() {
        return EscHubLayoutId.CLASSIC;
    }

    @Override
    public void reset(EscHubLayoutContext ctx) {
    }

    @Override
    public void tick(EscHubLayoutContext ctx) {
    }

    @Override
    public void render(GuiGraphics g, EscHubLayoutContext ctx, EscRect bounds, int mouseX, int mouseY, float partialTick) {
        if (!ctx.hostDrawsChrome()) {
            EscBackground.render(g, bounds, ctx.style().theme());
        }
        List<EscHubSection> sections = ctx.sections();
        EscRect tabs = new EscRect(bounds.x(), bounds.y(), bounds.width(), TAB_H);
        EscPanel.renderPanel(g, tabs, ctx.style());
        if (!sections.isEmpty()) {
            EscStripSlots.Plan plan = EscStripSlots.plan(tabs, sections.size(), PAD, GAP);
            for (int i = 0; i < sections.size(); i++) {
                EscHubSection s = sections.get(i);
                EscRect t = EscStripSlots.slot(plan, tabs, i, TAB_H);
                boolean sel = i == ctx.sectionIndex();
                EscPanel.fill(g, t, ctx.style().panelFillColor());
                EscPanel.border(g, t,
                    sel ? ctx.style().focusBorderColor() : ctx.style().panelBorderColor(),
                    sel ? 2 : 1);
                int textX = t.x() + 4;
                if (s.icon() != null) {
                    EscImage.drawAuto(g, s.icon(), t.x() + 3, t.y() + 5, 14, 14, 0xFFFFFFFF, sel ? 1f : 0.75f);
                    textX = t.x() + 20;
                }
                int maxTw = Math.max(8, t.right() - textX - 4);
                String title = EscTypography.truncate(ctx.font(), s.title(), maxTw);
                g.drawString(ctx.font(), title, textX, t.y() + 9,
                    sel ? ctx.style().textColorTitle() : ctx.style().textColorBody(), false);
            }
        }
        EscRect body = new EscRect(bounds.x(), tabs.bottom() + 6, bounds.width(), bounds.height() - TAB_H - 6);
        EscHubSection sel = ctx.selectedSection();
        if (sel != null) {
            EscAccordion.render(g, ctx, body, sel.id());
        }
        if (!ctx.hostDrawsChrome()) {
            EscCrtLayer.render(g, bounds, ctx.style().theme());
        }
    }

    @Override
    public boolean mouseClicked(EscHubLayoutContext ctx, EscRect bounds, double mouseX, double mouseY, int button) {
        if (button != 0) return false;
        List<EscHubSection> sections = ctx.sections();
        EscRect tabs = new EscRect(bounds.x(), bounds.y(), bounds.width(), TAB_H);
        if (!sections.isEmpty()) {
            EscStripSlots.Plan plan = EscStripSlots.plan(tabs, sections.size(), PAD, GAP);
            for (int i = 0; i < sections.size(); i++) {
                if (EscStripSlots.slot(plan, tabs, i, TAB_H).contains(mouseX, mouseY)) {
                    ctx.setSectionIndex(i);
                    return true;
                }
            }
        }
        EscRect body = new EscRect(bounds.x(), tabs.bottom() + 6, bounds.width(), bounds.height() - TAB_H - 6);
        EscHubSection sel = ctx.selectedSection();
        return sel != null && EscAccordion.mouseClicked(ctx, body, sel.id(), mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(EscHubLayoutContext ctx, EscRect bounds, double mouseX, double mouseY, double delta) {
        EscRect body = new EscRect(bounds.x(), bounds.y() + TAB_H + 6, bounds.width(), bounds.height() - TAB_H - 6);
        EscHubSection sel = ctx.selectedSection();
        return sel != null && EscAccordion.mouseScrolled(ctx, body, sel.id(), mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(EscHubLayoutContext ctx, int keyCode, int scanCode, int modifiers) {
        return EscHubNav.keyPressed(ctx, keyCode);
    }

    @Override
    public EscRect detailBounds(EscHubLayoutContext ctx, EscRect bounds) {
        return new EscRect(bounds.x(), bounds.y() + TAB_H + 6, bounds.width(), Math.max(40, bounds.height() - TAB_H - 6));
    }
}
