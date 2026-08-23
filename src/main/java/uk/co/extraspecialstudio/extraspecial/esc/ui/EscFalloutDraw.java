package uk.co.extraspecialstudio.extraspecial.esc.ui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

/**
 * Shared Fallout / Pip-Boy CRT draw helpers (scanlines, phosphor rules, labeled bars).
 */
public final class EscFalloutDraw {

    private EscFalloutDraw() {
    }

    /** Phosphor green (bright). */
    public static final int PHOSPHOR = 0xFF00FF41;
    /** Dim phosphor for idle chrome. */
    public static final int PHOSPHOR_DIM = 0xFF338844;
    /** CRT black panel. */
    public static final int CRT_BLACK = 0xFF000000;
    /** Soft scanline tint (ARGB). */
    public static final int SCANLINE = 0x22001008;

    public static void scanlines(GuiGraphics graphics, EscRect rect, int spacing) {
        scanlines(graphics, rect, spacing, 1);
    }

    public static void scanlines(GuiGraphics graphics, EscRect rect, int spacing, int thickness) {
        int step = Math.max(2, spacing);
        int thick = Math.max(1, thickness);
        int y1 = rect.bottom();
        for (int y = rect.y(); y < y1; y += step) {
            graphics.fill(rect.x(), y, rect.right(), Math.min(y + thick, y1), SCANLINE);
        }
    }

    public static void greenRule(GuiGraphics graphics, int x0, int y, int x1, int color) {
        if (x1 <= x0) {
            return;
        }
        graphics.fill(x0, y, x1, y + 1, color);
    }

    /**
     * Horizontal rule with a gap under {@code gapX0..gapX1} (selected Fallout tab underline break).
     */
    public static void greenRuleWithGap(GuiGraphics graphics, int x0, int y, int x1, int gapX0, int gapX1, int color) {
        if (gapX1 <= gapX0) {
            greenRule(graphics, x0, y, x1, color);
            return;
        }
        greenRule(graphics, x0, y, Math.min(gapX0, x1), color);
        greenRule(graphics, Math.max(gapX1, x0), y, x1, color);
    }

    /**
     * Labeled fill bar (HP / LEVEL+XP). {@code fill01} is clamped 0..1.
     * Layout: {@code LABEL} then track; optional right caption (e.g. {@code 20/20}).
     */
    public static void labeledBar(
        GuiGraphics graphics,
        Font font,
        String label,
        String rightCaption,
        int x,
        int y,
        int width,
        int height,
        float fill01,
        int labelColor,
        int trackColor,
        int fillColor
    ) {
        int barH = Math.max(4, height);
        int labelW = font.width(label) + 4;
        graphics.drawString(font, Component.literal(label), x, y + Math.max(0, (barH - 8) / 2), labelColor, false);
        int trackX = x + labelW;
        int trackW = Math.max(8, width - labelW);
        if (rightCaption != null && !rightCaption.isEmpty()) {
            int rw = font.width(rightCaption);
            graphics.drawString(font, Component.literal(rightCaption),
                trackX + trackW - rw, y + Math.max(0, (barH - 8) / 2), labelColor, false);
            trackW = Math.max(8, trackW - rw - 4);
        }
        graphics.fill(trackX, y, trackX + trackW, y + barH, trackColor);
        float f = Mth.clamp(fill01, 0f, 1f);
        int fillW = Math.round(trackW * f);
        if (fillW > 0) {
            graphics.fill(trackX, y, trackX + fillW, y + barH, fillColor);
        }
        // Thin phosphor border
        graphics.fill(trackX, y, trackX + trackW, y + 1, PHOSPHOR_DIM);
        graphics.fill(trackX, y + barH - 1, trackX + trackW, y + barH, PHOSPHOR_DIM);
        graphics.fill(trackX, y, trackX + 1, y + barH, PHOSPHOR_DIM);
        graphics.fill(trackX + trackW - 1, y, trackX + trackW, y + barH, PHOSPHOR_DIM);
    }
}
