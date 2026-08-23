package uk.co.extraspecialstudio.extraspecial.esc.ui.layout;

import net.minecraft.client.gui.GuiGraphics;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscBackground;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscCard;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscCrtLayer;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscImage;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscPanel;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscRect;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscTypography;
import uk.co.extraspecialstudio.extraspecial.esc.ui.anim.EscAnim;

import java.util.List;

/**
 * PlayStation-style horizontal section carousel with detail accordion under focus.
 */
public final class EscCarouselLayout implements EscHubLayout {

    private static final int MIN_CARD_H = 36;

    private final EscAnim.FloatAnim focusBlend = new EscAnim.FloatAnim(1f, 0.28f);
    private int lastIndex = -1;

    @Override
    public EscHubLayoutId id() {
        return EscHubLayoutId.CAROUSEL;
    }

    @Override
    public void reset(EscHubLayoutContext ctx) {
        lastIndex = ctx.sectionIndex();
        focusBlend.snap(1f);
    }

    @Override
    public void tick(EscHubLayoutContext ctx) {
        if (ctx.sectionIndex() != lastIndex) {
            lastIndex = ctx.sectionIndex();
            focusBlend.snap(0f);
            focusBlend.setTarget(1f);
        }
        float speed = 0.28f * Math.max(0.2f, ctx.style().motionScale());
        focusBlend.setSpeed(speed);
        focusBlend.tick();
    }

    private static EscRect[] bands(EscRect bounds) {
        // Cards fill their band, so the band ratio is what sets card height. The detail
        // accordion keeps the larger share and its 72px floor.
        return EscLayoutBands.rows(bounds, new float[]{0.46f, 0.54f}, 8, new int[]{MIN_CARD_H, 72});
    }

    @Override
    public void render(GuiGraphics g, EscHubLayoutContext ctx, EscRect bounds, int mouseX, int mouseY, float partialTick) {
        List<EscHubSection> sections = ctx.sections();
        if (sections.isEmpty()) {
            return;
        }
        if (!ctx.hostDrawsChrome()) {
            EscBackground.render(g, bounds, ctx.style().theme());
        }
        EscRect[] split = bands(bounds);
        EscRect carousel = split[0];
        EscRect detail = split[1];

        if (!EscLayoutBands.canFitCard(carousel, MIN_CARD_H)) {
            renderStripFallback(g, ctx, carousel, sections);
        } else {
            renderCarousel(g, ctx, carousel, sections);
        }

        EscHubSection sel = ctx.selectedSection();
        if (sel != null) {
            EscAccordion.render(g, ctx, detail, sel.id());
        }
        if (!ctx.hostDrawsChrome()) {
            EscCrtLayer.render(g, bounds, ctx.style().theme());
        }
    }

    private void renderStripFallback(GuiGraphics g, EscHubLayoutContext ctx, EscRect strip, List<EscHubSection> sections) {
        EscPanel.renderPanel(g, strip, ctx.style());
        EscStripSlots.Plan plan = EscStripSlots.plan(strip, sections.size(), 4, 4);
        for (int i = 0; i < sections.size(); i++) {
            EscHubSection s = sections.get(i);
            EscRect t = EscStripSlots.slot(plan, strip, i, strip.height());
            boolean sel = i == ctx.sectionIndex();
            EscPanel.border(g, t, sel ? ctx.style().focusBorderColor() : ctx.style().panelBorderColor(), sel ? 2 : 1);
            int textX = t.x() + 4;
            if (s.icon() != null) {
                EscImage.drawAuto(g, s.icon(), t.x() + 3, t.y() + 5, 14, 14, 0xFFFFFFFF, sel ? 1f : 0.75f);
                textX = t.x() + 20;
            }
            String title = EscTypography.truncate(ctx.font(), s.title(), Math.max(8, t.right() - textX - 4));
            g.drawString(ctx.font(), title, textX, t.y() + 9,
                sel ? ctx.style().textColorTitle() : ctx.style().textColorBody(), false);
        }
    }

    private void renderCarousel(GuiGraphics g, EscHubLayoutContext ctx, EscRect carousel, List<EscHubSection> sections) {
        int focus = ctx.sectionIndex();
        EscResponsive.CardPlan plan = EscResponsive.carouselCards(carousel, sections.size());
        int centreX = carousel.x() + carousel.width() / 2;
        int halfVisible = plan.visible() / 2;

        for (int i = 0; i < sections.size(); i++) {
            int dist = i - focus;
            if (Math.abs(dist) > halfVisible) {
                continue;
            }
            float focus01 = i == focus
                ? EscAnim.easeOutCubic(focusBlend.value())
                : Math.max(0f, 0.55f - Math.abs(dist) * 0.18f);
            int offset = dist * (plan.cardW() + plan.gap());
            EscRect slot = EscLayoutBands.clampInto(
                new EscRect(centreX - plan.cardW() / 2 + offset, carousel.y() + 4, plan.cardW(), plan.cardH()),
                carousel
            );
            EscHubSection s = sections.get(i);
            String badge = s.badgeCount() > 0 ? String.valueOf(s.badgeCount()) : "";
            EscCard.render(g, ctx.font(), slot, ctx.style(), s.title(), s.subtitle(), badge, focus01, s.icon());
        }

        if (sections.size() > 1) {
            String counter = (focus + 1) + " / " + sections.size();
            int cx = carousel.x() + carousel.width() / 2 - ctx.font().width(counter) / 2;
            g.drawString(ctx.font(), counter, cx, carousel.bottom() - ctx.font().lineHeight - 2,
                ctx.style().textColorBody(), false);
        }

        if (focus > 0) {
            drawChevron(g, ctx, carousel.x() + 6, carousel.y() + carousel.height() / 2, true);
        }
        if (focus < sections.size() - 1) {
            drawChevron(g, ctx, carousel.right() - 14, carousel.y() + carousel.height() / 2, false);
        }
    }

    private static void drawChevron(GuiGraphics g, EscHubLayoutContext ctx, int x, int y, boolean left) {
        int color = ctx.style().accentColor() | 0xFF000000;
        String ch = left ? "\u25C0" : "\u25B6";
        g.drawString(ctx.font(), ch, x, y - ctx.font().lineHeight / 2, color, false);
    }

    @Override
    public boolean mouseClicked(EscHubLayoutContext ctx, EscRect bounds, double mouseX, double mouseY, int button) {
        if (button != 0) {
            return false;
        }
        EscRect[] split = bands(bounds);
        EscRect carousel = split[0];
        EscRect detail = split[1];
        List<EscHubSection> sections = ctx.sections();
        int focus = ctx.sectionIndex();

        if (focus > 0 && carousel.contains(mouseX, mouseY) && mouseX < carousel.x() + 20) {
            ctx.setSectionIndex(focus - 1);
            return true;
        }
        if (focus < sections.size() - 1 && carousel.contains(mouseX, mouseY) && mouseX > carousel.right() - 20) {
            ctx.setSectionIndex(focus + 1);
            return true;
        }

        if (!EscLayoutBands.canFitCard(carousel, MIN_CARD_H)) {
            EscStripSlots.Plan plan = EscStripSlots.plan(carousel, sections.size(), 4, 4);
            for (int i = 0; i < sections.size(); i++) {
                if (EscStripSlots.slot(plan, carousel, i, carousel.height()).contains(mouseX, mouseY)) {
                    ctx.setSectionIndex(i);
                    return true;
                }
            }
        } else {
            EscResponsive.CardPlan plan = EscResponsive.carouselCards(carousel, sections.size());
            int centreX = carousel.x() + carousel.width() / 2;
            int halfVisible = plan.visible() / 2;
            for (int i = 0; i < sections.size(); i++) {
                int dist = i - focus;
                if (Math.abs(dist) > halfVisible) {
                    continue;
                }
                float focus01 = i == focus ? 1f : Math.max(0f, 0.55f - Math.abs(dist) * 0.18f);
                int offset = dist * (plan.cardW() + plan.gap());
                EscRect slot = EscLayoutBands.clampInto(
                    new EscRect(centreX - plan.cardW() / 2 + offset, carousel.y() + 4, plan.cardW(), plan.cardH()),
                    carousel
                );
                if (EscCard.hit(slot, mouseX, mouseY, focus01)) {
                    ctx.setSectionIndex(i);
                    return true;
                }
            }
        }

        EscHubSection sel = ctx.selectedSection();
        return sel != null && EscAccordion.mouseClicked(ctx, detail, sel.id(), mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(EscHubLayoutContext ctx, EscRect bounds, double mouseX, double mouseY, double delta) {
        EscRect[] split = bands(bounds);
        EscHubSection sel = ctx.selectedSection();
        if (sel != null && EscAccordion.mouseScrolled(ctx, split[1], sel.id(), mouseX, mouseY, delta)) {
            return true;
        }
        if (!split[0].contains(mouseX, mouseY)) {
            return false;
        }
        if (delta > 0) {
            ctx.cycleSection(-1);
        } else if (delta < 0) {
            ctx.cycleSection(1);
        }
        return delta != 0;
    }

    @Override
    public boolean keyPressed(EscHubLayoutContext ctx, int keyCode, int scanCode, int modifiers) {
        return EscHubNav.keyPressed(ctx, keyCode);
    }

    @Override
    public EscRect detailBounds(EscHubLayoutContext ctx, EscRect bounds) {
        return bands(bounds)[1];
    }
}
