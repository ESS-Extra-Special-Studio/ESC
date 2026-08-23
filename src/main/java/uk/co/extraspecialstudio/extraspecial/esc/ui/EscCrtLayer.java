package uk.co.extraspecialstudio.extraspecial.esc.ui;

import net.minecraft.client.gui.GuiGraphics;
import uk.co.extraspecialstudio.extraspecial.esc.theme.EscQualityMode;
import uk.co.extraspecialstudio.extraspecial.esc.theme.EscTheme;
import uk.co.extraspecialstudio.extraspecial.esc.theme.EscVisualBudget;

/**
 * Optional CRT / terminal overlay driven by theme intensity.
 */
public final class EscCrtLayer {

    private EscCrtLayer() {
    }

    public static void render(GuiGraphics g, EscRect area, EscTheme theme) {
        float intensity = EscVisualBudget.crt(theme);
        if (intensity <= 0.01f || area.width() <= 0) {
            return;
        }
        // MEDIUM: sparse scanlines; HIGH: denser but still thicker than 1px.
        int spacing;
        int thickness;
        if (EscVisualBudget.quality() == EscQualityMode.HIGH) {
            spacing = Math.max(3, Math.round(4 - intensity));
            thickness = 1;
        } else {
            spacing = Math.max(5, Math.round(8 - intensity * 2));
            thickness = 2;
        }
        EscFalloutDraw.scanlines(g, area, spacing, thickness);
        // vignette
        int a = Math.max(10, Math.min(90, Math.round(55 * intensity)));
        int v = (a << 24);
        int band = Math.max(4, area.height() / 12);
        EscPanel.fill(g, new EscRect(area.x(), area.y(), area.width(), band), v);
        EscPanel.fill(g, new EscRect(area.x(), area.bottom() - band, area.width(), band), v);
        // subtle flicker
        long t = System.currentTimeMillis();
        if ((t / 90) % 37 == 0) {
            EscPanel.fill(g, area, ((Math.round(18 * intensity)) << 24) | 0x00FF41);
        }
    }
}
