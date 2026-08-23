package uk.co.extraspecialstudio.extraspecial.esc.ui.layout;

import uk.co.extraspecialstudio.extraspecial.esc.ui.EscRect;

import java.util.ArrayList;
import java.util.List;

/**
 * Band / tile planner — children always stay inside their parent, never overlap.
 */
public final class EscLayoutBands {

    private EscLayoutBands() {
    }

    public static boolean fits(EscRect child, EscRect parent) {
        if (child == null || parent == null) {
            return false;
        }
        return child.x() >= parent.x()
            && child.y() >= parent.y()
            && child.right() <= parent.right()
            && child.bottom() <= parent.bottom();
    }

    public static EscRect clampInto(EscRect child, EscRect parent) {
        if (child == null || parent == null) {
            return child;
        }
        int w = Math.min(child.width(), parent.width());
        int h = Math.min(child.height(), parent.height());
        int x = Math.max(parent.x(), Math.min(child.x(), parent.right() - w));
        int y = Math.max(parent.y(), Math.min(child.y(), parent.bottom() - h));
        return new EscRect(x, y, Math.max(0, w), Math.max(0, h));
    }

    /**
     * Split {@code parent} into rows by weight ratios, honouring optional minimum heights.
     * When the parent is too short, rows are scaled proportionally so nothing escapes {@code parent}.
     */
    public static EscRect[] rows(EscRect parent, float[] ratios, int gap, int[] minHeights) {
        if (parent == null || parent.height() <= 0 || ratios == null || ratios.length == 0) {
            return new EscRect[0];
        }
        int n = ratios.length;
        int g = Math.max(0, gap);
        int totalGaps = g * Math.max(0, n - 1);
        int available = Math.max(0, parent.height() - totalGaps);

        float totalRatio = 0f;
        float[] weights = new float[n];
        for (int i = 0; i < n; i++) {
            weights[i] = Math.max(0f, ratios[i]);
            totalRatio += weights[i];
        }
        if (totalRatio <= 0f) {
            totalRatio = n;
            for (int i = 0; i < n; i++) {
                weights[i] = 1f;
            }
        }

        int[] heights = new int[n];
        int[] mins = new int[n];
        int minSum = 0;
        for (int i = 0; i < n; i++) {
            mins[i] = minHeights != null && i < minHeights.length ? Math.max(0, minHeights[i]) : 0;
            minSum += mins[i];
        }

        if (minSum >= available) {
            float scale = available / (float) Math.max(1, minSum);
            for (int i = 0; i < n; i++) {
                heights[i] = Math.max(1, Math.round(mins[i] * scale));
            }
        } else {
            int flex = available - minSum;
            for (int i = 0; i < n; i++) {
                heights[i] = mins[i] + Math.round(flex * (weights[i] / totalRatio));
            }
        }

        int used = 0;
        for (int h : heights) {
            used += h;
        }
        if (used > available) {
            float scale = available / (float) used;
            used = 0;
            for (int i = 0; i < n; i++) {
                heights[i] = Math.max(1, Math.round(heights[i] * scale));
                used += heights[i];
            }
        } else if (used < available && n > 0) {
            heights[n - 1] += available - used;
        }

        EscRect[] out = new EscRect[n];
        int cursor = parent.y();
        for (int i = 0; i < n; i++) {
            int h = Math.min(heights[i], parent.bottom() - cursor);
            out[i] = new EscRect(parent.x(), cursor, parent.width(), Math.max(0, h));
            cursor += h + g;
        }
        return out;
    }

    /** Smallest tile that can still show a compact card row without looking broken. */
    public static final int MIN_TILE_H = 30;

    /**
     * Pack {@code count} tiles into {@code area} using {@code cols} columns.
     * Tile size never exceeds the area; overflow rows are omitted.
     */
    public static EscRect[] tiles(EscRect area, int count, int cols, int gap) {
        return tiles(area, count, cols, gap, MIN_TILE_H);
    }

    /**
     * As {@link #tiles(EscRect, int, int, int)} but never produces tiles shorter than
     * {@code minTileH} unless the area itself cannot fit one row that tall — rows that
     * would be squashed below it are dropped instead of collapsing into slivers.
     */
    public static EscRect[] tiles(EscRect area, int count, int cols, int gap, int minTileH) {
        if (area == null || area.width() <= 0 || area.height() <= 0 || count <= 0) {
            return new EscRect[0];
        }
        int c = Math.max(1, cols);
        int g = Math.max(0, gap);
        int rows = (int) Math.ceil(count / (double) c);
        int minH = Math.max(1, minTileH);
        // Rows that fit at minH; keep at least one so a short area still renders something.
        int maxRows = Math.max(1, (area.height() - g) / (minH + g));
        rows = Math.min(rows, maxRows);

        int tileW = Math.max(1, (area.width() - g * (c + 1)) / c);
        int tileH = Math.max(1, (area.height() - g * (rows + 1)) / rows);

        List<EscRect> out = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            int col = i % c;
            int row = i / c;
            if (row >= rows) {
                break;
            }
            int x = area.x() + g + col * (tileW + g);
            int y = area.y() + g + row * (tileH + g);
            EscRect tile = new EscRect(x, y, tileW, tileH);
            if (fits(tile, area)) {
                out.add(tile);
            }
        }
        return out.toArray(EscRect[]::new);
    }

    /**
     * True when {@code area} is tall enough to render at least one identity card row.
     */
    public static boolean canFitCard(EscRect area, int minCardH) {
        return area != null && area.height() >= Math.max(1, minCardH);
    }

    public static boolean validate(EscRect parent, EscRect[] children) {
        if (parent == null || children == null) {
            return false;
        }
        for (int i = 0; i < children.length; i++) {
            EscRect a = children[i];
            if (a == null || !fits(a, parent)) {
                return false;
            }
            for (int j = i + 1; j < children.length; j++) {
                EscRect b = children[j];
                if (b != null && overlaps(a, b)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean overlaps(EscRect a, EscRect b) {
        return a.x() < b.right() && a.right() > b.x() && a.y() < b.bottom() && a.bottom() > b.y();
    }
}
