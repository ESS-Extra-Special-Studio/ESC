package uk.co.extraspecialstudio.extraspecial.esc.ui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import uk.co.extraspecialstudio.extraspecial.esc.theme.EscFrameStyle;
import uk.co.extraspecialstudio.extraspecial.esc.theme.EscQualityMode;
import uk.co.extraspecialstudio.extraspecial.esc.theme.EscTheme;
import uk.co.extraspecialstudio.extraspecial.esc.theme.EscVisualBudget;
import uk.co.extraspecialstudio.extraspecial.esc.ui.anim.EscFocusVisual;

/**
 * Panel geometry + depth/glass/frame drawing for ESC chrome.
 */
public final class EscPanel {

    private EscPanel() {
    }

    public static EscRect contentRect(Screen screen, EscUiStyle style) {
        int x = style.screenMarginH();
        int y = style.screenMarginV();
        int w = screen.width - 2 * style.screenMarginH();
        int h = screen.height - 2 * style.screenMarginV();
        return new EscRect(x, y, w, h);
    }

    public static EscRect contentRect(EscRect parent, EscUiStyle style) {
        return parent.inset(EscInsets.of(style.screenMarginH(), style.screenMarginV()));
    }

    public static EscRect inset(EscRect parent, EscInsets insets) {
        return parent.inset(insets);
    }

    public static EscRect bodyBelowTabs(EscRect content, int tabBarHeight, int gap) {
        int top = content.y() + Math.max(0, tabBarHeight) + Math.max(0, gap);
        int h = Math.max(0, content.bottom() - top);
        return new EscRect(content.x(), top, content.width(), h);
    }

    public static EscRect titleBar(EscRect content, EscUiStyle style) {
        return new EscRect(content.x(), content.y(), content.width(), style.titleGap());
    }

    public static EscRect bodyBelowTitle(EscRect content, EscUiStyle style) {
        int top = content.y() + style.titleGap();
        int h = content.height() - style.titleGap() - style.footerReserve();
        return new EscRect(content.x(), top, content.width(), Math.max(0, h));
    }

    public static EscRect footer(EscRect content, EscUiStyle style) {
        int fh = style.footerReserve();
        return new EscRect(content.x(), content.bottom() - fh, content.width(), fh);
    }

    public static void fill(GuiGraphics graphics, EscRect rect, int argb) {
        graphics.fill(rect.x(), rect.y(), rect.right(), rect.bottom(), argb);
    }

    public static void border(GuiGraphics graphics, EscRect rect, int argb, int thickness) {
        int t = Math.max(1, thickness);
        int x0 = rect.x();
        int y0 = rect.y();
        int x1 = rect.right();
        int y1 = rect.bottom();
        graphics.fill(x0, y0, x1, y0 + t, argb);
        graphics.fill(x0, y1 - t, x1, y1, argb);
        graphics.fill(x0, y0, x0 + t, y1, argb);
        graphics.fill(x1 - t, y0, x1, y1, argb);
    }

    /** Soft drop shadow under a rect. */
    public static void dropShadow(GuiGraphics g, EscRect rect, float strength) {
        if (strength <= 0.01f) return;
        int layers = Math.max(1, Math.min(4, Math.round(strength * 4)));
        for (int i = layers; i >= 1; i--) {
            int o = i;
            int a = Math.max(8, Math.min(80, Math.round(28 * strength * (layers - i + 1f) / layers)));
            fill(g, new EscRect(rect.x() + o, rect.y() + o, rect.width(), rect.height()), (a << 24));
        }
    }

    /** Soft outer glow using border colour. */
    public static void outerGlow(GuiGraphics g, EscRect rect, int borderArgb, float strength) {
        if (strength <= 0.01f) return;
        int maxLayers = EscVisualBudget.quality() == EscQualityMode.HIGH ? 3 : 1;
        int layers = Math.max(1, Math.min(maxLayers, Math.round(strength * 3)));
        for (int i = layers; i >= 1; i--) {
            int a = Math.max(10, Math.min(70, Math.round(40 * strength / i)));
            int c = EscFocusVisual.applyAlpha(borderArgb | 0xFF000000, a / 255f);
            border(g, new EscRect(rect.x() - i, rect.y() - i, rect.width() + i * 2, rect.height() + i * 2), c, 1);
        }
    }

    /** Top edge highlight for raised panels. */
    public static void edgeHighlight(GuiGraphics g, EscRect rect, float strength) {
        if (strength <= 0.01f) return;
        int a = Math.max(10, Math.min(90, Math.round(70 * strength)));
        g.fill(rect.x() + 1, rect.y() + 1, rect.right() - 1, rect.y() + 2, (a << 24) | 0xFFFFFF);
    }

    /** Apply panel opacity to an ARGB fill. */
    public static int withOpacity(int argb, float opacity) {
        float o = Math.max(0f, Math.min(1f, opacity));
        int a = (argb >>> 24) & 0xFF;
        int na = Math.max(0, Math.min(255, Math.round(a * o)));
        return (na << 24) | (argb & 0x00FFFFFF);
    }

    public static void renderFrame(GuiGraphics g, EscRect rect, int borderArgb, int thickness, EscFrameStyle style) {
        EscFrameStyle fs = style == null ? EscFrameStyle.SQUARE : style;
        int t = Math.max(1, thickness);
        switch (fs) {
            case SQUARE, ROUNDED_SOFT -> border(g, rect, borderArgb, t);
            case CHAMFER -> drawChamfer(g, rect, borderArgb, t, 6);
            case CUT_CORNER -> drawChamfer(g, rect, borderArgb, t, 10);
            case BRACKET -> drawBrackets(g, rect, borderArgb, t);
        }
    }

    private static void drawChamfer(GuiGraphics g, EscRect r, int c, int t, int cut) {
        int x0 = r.x();
        int y0 = r.y();
        int x1 = r.right();
        int y1 = r.bottom();
        int k = Math.min(cut, Math.min(r.width(), r.height()) / 3);
        // top
        g.fill(x0 + k, y0, x1 - k, y0 + t, c);
        // bottom
        g.fill(x0 + k, y1 - t, x1 - k, y1, c);
        // left / right
        g.fill(x0, y0 + k, x0 + t, y1 - k, c);
        g.fill(x1 - t, y0 + k, x1, y1 - k, c);
        // corner stubs
        g.fill(x0, y0 + k, x0 + k, y0 + k + t, c);
        g.fill(x1 - k, y0 + k, x1, y0 + k + t, c);
        g.fill(x0, y1 - k - t, x0 + k, y1 - k, c);
        g.fill(x1 - k, y1 - k - t, x1, y1 - k, c);
        g.fill(x0 + k, y0, x0 + k + t, y0 + k, c);
        g.fill(x1 - k - t, y0, x1 - k, y0 + k, c);
        g.fill(x0 + k, y1 - k, x0 + k + t, y1, c);
        g.fill(x1 - k - t, y1 - k, x1 - k, y1, c);
    }

    private static void drawBrackets(GuiGraphics g, EscRect r, int c, int t) {
        int arm = Math.min(14, Math.min(r.width(), r.height()) / 4);
        int x0 = r.x();
        int y0 = r.y();
        int x1 = r.right();
        int y1 = r.bottom();
        // TL
        g.fill(x0, y0, x0 + arm, y0 + t, c);
        g.fill(x0, y0, x0 + t, y0 + arm, c);
        // TR
        g.fill(x1 - arm, y0, x1, y0 + t, c);
        g.fill(x1 - t, y0, x1, y0 + arm, c);
        // BL
        g.fill(x0, y1 - t, x0 + arm, y1, c);
        g.fill(x0, y1 - arm, x0 + t, y1, c);
        // BR
        g.fill(x1 - arm, y1 - t, x1, y1, c);
        g.fill(x1 - t, y1 - arm, x1, y1, c);
    }

    public static void renderPanel(GuiGraphics graphics, EscRect rect, EscUiStyle style) {
        float opacity = style.panelOpacity();
        float shadow = style.shadowStrength();
        float glow = style.glowStrength();
        EscTheme theme = style.theme();
        if (theme != null) {
            shadow = EscVisualBudget.shadow(theme);
            glow = EscVisualBudget.glow(theme);
            opacity = theme.panelOpacity();
        }
        dropShadow(graphics, rect, shadow);
        fill(graphics, rect, withOpacity(style.panelFillColor(), opacity));
        outerGlow(graphics, rect, style.panelBorderColor(), glow * 0.6f);
        edgeHighlight(graphics, rect, shadow * 0.5f);
        if (style.panelBorderWidth() > 0) {
            renderFrame(graphics, rect, style.panelBorderColor(), style.panelBorderWidth(), style.frameStyle());
        }
    }

    /** Progress-style animated border highlight (Wave C); no-op when budget disallows. */
    public static void animatedBorderSweep(GuiGraphics g, EscRect rect, int color, float progress01, float strength) {
        if (strength <= 0.01f || !EscVisualBudget.animatedBorders()) return;
        float p = Math.max(0f, Math.min(1f, progress01));
        int peri = 2 * (rect.width() + rect.height());
        if (peri <= 0) return;
        int pos = Math.round(p * peri);
        // MEDIUM: shorter sweep segment; HIGH: longer.
        int lenDiv = EscVisualBudget.quality() == EscQualityMode.HIGH ? 12 : 20;
        int len = Math.max(8, peri / lenDiv);
        drawPerimeterSegment(g, rect, color, pos, len);
    }

    private static void drawPerimeterSegment(GuiGraphics g, EscRect r, int c, int start, int len) {
        int w = r.width();
        int h = r.height();
        int peri = 2 * (w + h);
        if (peri <= 0 || len <= 0) {
            return;
        }
        // Emit contiguous edge runs as fills instead of per-pixel draws.
        int i = 0;
        while (i < len) {
            int s = Math.floorMod(start + i, peri);
            int run;
            if (s < w) {
                run = Math.min(len - i, w - s);
                g.fill(r.x() + s, r.y(), r.x() + s + run, r.y() + 1, c);
            } else if (s < w + h) {
                int ys = s - w;
                run = Math.min(len - i, h - ys);
                g.fill(r.right() - 1, r.y() + ys, r.right(), r.y() + ys + run, c);
            } else if (s < 2 * w + h) {
                int xs = s - w - h;
                run = Math.min(len - i, w - xs);
                int x1 = r.right() - xs;
                g.fill(x1 - run, r.bottom() - 1, x1, r.bottom(), c);
            } else {
                int ys = s - 2 * w - h;
                run = Math.min(len - i, h - ys);
                int y1 = r.bottom() - ys;
                g.fill(r.x(), y1 - run, r.x() + 1, y1, c);
            }
            i += Math.max(1, run);
        }
    }
}
