package uk.co.extraspecialstudio.extraspecial.esc.ui.layout;

import net.minecraft.client.gui.GuiGraphics;
import uk.co.extraspecialstudio.extraspecial.esc.theme.EscVisualBudget;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscBackground;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscCrtLayer;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscImage;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscPanel;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscRect;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscTypography;
import uk.co.extraspecialstudio.extraspecial.esc.ui.anim.EscAnim;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Centre hub node with continuously orbiting section satellites; accordion in lower band. */
public final class EscOrbitalLayout implements EscHubLayout {

    private static final long REVOLUTION_MS = 24_000L;

    private final EscAnim.FloatAnim selectionSpring = new EscAnim.FloatAnim(0f, 0.15f);
    private int lastIndex = -1;

    @Override
    public EscHubLayoutId id() {
        return EscHubLayoutId.ORBITAL;
    }

    @Override
    public void reset(EscHubLayoutContext ctx) {
        lastIndex = ctx.sectionIndex();
        selectionSpring.snap(ctx.sectionIndex());
    }

    @Override
    public void tick(EscHubLayoutContext ctx) {
        if (ctx.sectionIndex() != lastIndex) {
            lastIndex = ctx.sectionIndex();
        }
        selectionSpring.setTarget(ctx.sectionIndex());
        selectionSpring.tick();
    }

    private static EscRect[] bands(EscRect bounds) {
        return EscLayoutBands.rows(bounds, new float[]{0.55f, 0.45f}, 8, new int[]{80, 72});
    }

    private record OrbitGeom(EscRect orbit, EscRect hub, int rx, int ry, int nodeW, int nodeH, float baseAngle) {
    }

    private static OrbitGeom geometry(EscHubLayoutContext ctx, EscRect orbit, float selectionOffset) {
        int cx = orbit.x() + orbit.width() / 2;
        int cy = orbit.y() + orbit.height() / 2;
        int hubW = Math.min(72, Math.max(48, orbit.width() / 5));
        int hubH = Math.min(32, Math.max(24, orbit.height() / 6));
        EscRect hub = new EscRect(cx - hubW / 2, cy - hubH / 2, hubW, hubH);

        int nodeW = Math.min(88, Math.max(56, orbit.width() / 6));
        int nodeH = Math.min(28, Math.max(20, orbit.height() / 8));

        int minRx = hubW / 2 + nodeW / 2 + 12;
        int minRy = hubH / 2 + nodeH / 2 + 12;
        int rx = Math.max(minRx, orbit.width() / 2 - nodeW / 2 - 8);
        int ry = Math.max(minRy, orbit.height() / 2 - nodeH / 2 - 8);

        float motion = EscVisualBudget.motionScale(ctx.style().theme());
        float timeAngle = EscVisualBudget.reducedMotion()
            ? 0f
            : (float) ((System.currentTimeMillis() % REVOLUTION_MS) / (double) REVOLUTION_MS * Math.PI * 2.0) * motion;
        float baseAngle = timeAngle + selectionOffset;
        return new OrbitGeom(orbit, hub, rx, ry, nodeW, nodeH, baseAngle);
    }

    private static float selectionOffset(EscHubLayoutContext ctx, EscAnim.FloatAnim spring) {
        int n = Math.max(1, ctx.sections().size());
        float idx = spring.value();
        return (float) (-idx * (Math.PI * 2.0 / n));
    }

    private static EscRect nodeRect(OrbitGeom geom, int index, int count, boolean selected) {
        double a = -Math.PI / 2.0 + index * (Math.PI * 2.0 / Math.max(1, count)) + geom.baseAngle();
        int cx = geom.orbit().x() + geom.orbit().width() / 2;
        int cy = geom.orbit().y() + geom.orbit().height() / 2;
        int x = cx + (int) Math.round(Math.cos(a) * geom.rx()) - geom.nodeW() / 2;
        int y = cy + (int) Math.round(Math.sin(a) * geom.ry()) - geom.nodeH() / 2;
        EscRect node = new EscRect(x, y, geom.nodeW(), geom.nodeH());
        if (selected) {
            // Bias selected node slightly outward for readability
            int dx = x + geom.nodeW() / 2 - cx;
            int dy = y + geom.nodeH() / 2 - cy;
            if (dx != 0 || dy != 0) {
                float len = (float) Math.sqrt(dx * dx + dy * dy);
                int push = 4;
                node = new EscRect(
                    x + Math.round(dx / len * push),
                    y + Math.round(dy / len * push),
                    geom.nodeW(),
                    geom.nodeH()
                );
            }
        }
        return EscLayoutBands.clampInto(node, geom.orbit());
    }

    @Override
    public void render(GuiGraphics g, EscHubLayoutContext ctx, EscRect bounds, int mouseX, int mouseY, float partialTick) {
        if (!ctx.hostDrawsChrome()) {
            EscBackground.render(g, bounds, ctx.style().theme());
        }
        EscRect[] split = bands(bounds);
        EscRect orbitBand = split[0];
        EscRect detail = split[1];

        List<EscHubSection> sections = ctx.sections();
        OrbitGeom geom = geometry(ctx, orbitBand, selectionOffset(ctx, selectionSpring));
        int focus = ctx.sectionIndex();

        List<Integer> drawOrder = new ArrayList<>();
        for (int i = 0; i < sections.size(); i++) {
            if (i != focus) {
                drawOrder.add(i);
            }
        }
        drawOrder.sort(Comparator.comparingDouble(i -> {
            EscRect n = nodeRect(geom, i, sections.size(), false);
            return n.y();
        }));
        if (focus >= 0 && focus < sections.size()) {
            drawOrder.add(focus);
        }

        for (int i : drawOrder) {
            EscHubSection section = sections.get(i);
            boolean sel = i == focus;
            EscRect node = nodeRect(geom, i, sections.size(), sel);
            EscPanel.fill(g, node, ctx.style().panelFillColor());
            EscPanel.border(g, node, sel ? ctx.style().focusBorderColor() : ctx.style().panelBorderColor(), sel ? 2 : 1);
            int textX = node.x() + 6;
            if (section.icon() != null) {
                EscImage.draw(g, section.icon(), node.x() + 4, node.y() + (node.height() - 14) / 2, 14, 14,
                    0xFFFFFFFF, sel ? 1f : 0.75f);
                textX = node.x() + 22;
            }
            String t = EscTypography.truncate(ctx.font(), section.title(), Math.max(8, node.right() - textX - 4));
            g.drawString(ctx.font(), t, textX, node.y() + (node.height() - ctx.font().lineHeight) / 2,
                sel ? ctx.style().textColorTitle() : ctx.style().textColorBody(), false);
        }

        EscPanel.renderPanel(g, geom.hub(), ctx.style());
        String hubLabel = "ESH";
        g.drawString(ctx.font(), hubLabel,
            geom.hub().x() + geom.hub().width() / 2 - ctx.font().width(hubLabel) / 2,
            geom.hub().y() + (geom.hub().height() - ctx.font().lineHeight) / 2,
            ctx.style().textColorTitle(), false);

        EscHubSection sel = ctx.selectedSection();
        if (sel != null) {
            EscAccordion.render(g, ctx, detail, sel.id());
        }
        if (!ctx.hostDrawsChrome()) {
            EscCrtLayer.render(g, bounds, ctx.style().theme());
        }
    }

    @Override
    public boolean mouseClicked(EscHubLayoutContext ctx, EscRect bounds, double mouseX, double mouseY, int button) {
        if (button != 0) {
            return false;
        }
        EscRect[] split = bands(bounds);
        OrbitGeom geom = geometry(ctx, split[0], selectionOffset(ctx, selectionSpring));
        List<EscHubSection> sections = ctx.sections();
        int focus = ctx.sectionIndex();

        for (int i = sections.size() - 1; i >= 0; i--) {
            EscRect node = nodeRect(geom, i, sections.size(), i == focus);
            if (node.contains(mouseX, mouseY)) {
                ctx.setSectionIndex(i);
                return true;
            }
        }

        EscHubSection sel = ctx.selectedSection();
        return sel != null && EscAccordion.mouseClicked(ctx, split[1], sel.id(), mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(EscHubLayoutContext ctx, EscRect bounds, double mouseX, double mouseY, double delta) {
        EscHubSection sel = ctx.selectedSection();
        return sel != null && EscAccordion.mouseScrolled(ctx, bands(bounds)[1], sel.id(), mouseX, mouseY, delta);
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
