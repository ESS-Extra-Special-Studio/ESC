package uk.co.extraspecialstudio.extraspecial.esc.ui.layout;

import uk.co.extraspecialstudio.extraspecial.esc.ui.EscRect;

/**
 * Equal-width, inset, centered slot packing for hub strips / tab bars.
 */
public final class EscStripSlots {
    private EscStripSlots() {
    }

    public record Plan(int slotW, int gap, int startX, int count) {
    }

    /**
     * Pack {@code count} equal slots inside {@code strip} with padding and gaps,
     * centered when leftover pixels remain. Never overflows the strip.
     */
    public static Plan plan(EscRect strip, int count, int pad, int gap) {
        int n = Math.max(1, count);
        int p = Math.max(0, pad);
        int g = Math.max(0, gap);
        int inner = Math.max(n, strip.width() - p * 2);
        int totalGaps = g * Math.max(0, n - 1);
        int slotW = Math.max(8, (inner - totalGaps) / n);
        int used = slotW * n + totalGaps;
        int startX = strip.x() + p + Math.max(0, (inner - used) / 2);
        return new Plan(slotW, g, startX, n);
    }

    public static EscRect slot(Plan plan, EscRect strip, int index, int height) {
        int x = plan.startX() + index * (plan.slotW() + plan.gap());
        return new EscRect(x, strip.y(), plan.slotW(), height);
    }
}
