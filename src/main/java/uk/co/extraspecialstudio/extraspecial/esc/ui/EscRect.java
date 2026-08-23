package uk.co.extraspecialstudio.extraspecial.esc.ui;

import java.util.ArrayList;
import java.util.List;

/**
 * Axis-aligned rectangle in screen pixel space.
 */
public record EscRect(int x, int y, int width, int height) {

    public int right() {
        return x + width;
    }

    public int bottom() {
        return y + height;
    }

    public boolean contains(double px, double py) {
        return px >= x && px < right() && py >= y && py < bottom();
    }

    public EscRect inset(EscInsets insets) {
        if (insets == null) {
            return this;
        }
        int nx = x + insets.left();
        int ny = y + insets.top();
        int nw = Math.max(0, width - insets.left() - insets.right());
        int nh = Math.max(0, height - insets.top() - insets.bottom());
        return new EscRect(nx, ny, nw, nh);
    }

    public EscRect withX(int nx) {
        return new EscRect(nx, y, width, height);
    }

    public EscRect withY(int ny) {
        return new EscRect(x, ny, width, height);
    }

    public EscRect withWidth(int nw) {
        return new EscRect(x, y, Math.max(0, nw), height);
    }

    public EscRect withHeight(int nh) {
        return new EscRect(x, y, width, Math.max(0, nh));
    }

    public EscRect union(EscRect other) {
        if (other == null || other.width() <= 0 || other.height() <= 0) {
            return this;
        }
        if (width <= 0 || height <= 0) {
            return other;
        }
        int x0 = Math.min(x, other.x());
        int y0 = Math.min(y, other.y());
        int x1 = Math.max(right(), other.right());
        int y1 = Math.max(bottom(), other.bottom());
        return new EscRect(x0, y0, x1 - x0, y1 - y0);
    }

    /**
     * Split into columns by weight ratios (e.g. {@code 0.26f, 0.50f, 0.24f}).
     */
    public EscRect[] splitColumns(float[] ratios, int gap) {
        if (ratios == null || ratios.length == 0) {
            return new EscRect[0];
        }
        float total = 0f;
        for (float r : ratios) {
            total += Math.max(0f, r);
        }
        if (total <= 0f) {
            return new EscRect[0];
        }
        int gaps = Math.max(0, ratios.length - 1) * gap;
        int available = Math.max(0, width - gaps);
        EscRect[] out = new EscRect[ratios.length];
        int cursor = x;
        for (int i = 0; i < ratios.length; i++) {
            int colW = (int) (available * (Math.max(0f, ratios[i]) / total));
            if (i == ratios.length - 1) {
                colW = right() - cursor;
            }
            out[i] = new EscRect(cursor, y, Math.max(0, colW), height);
            cursor += colW + gap;
        }
        return out;
    }

    /**
     * Split into rows by weight ratios.
     */
    public EscRect[] splitRows(float[] ratios, int gap) {
        if (ratios == null || ratios.length == 0) {
            return new EscRect[0];
        }
        float total = 0f;
        for (float r : ratios) {
            total += Math.max(0f, r);
        }
        if (total <= 0f) {
            return new EscRect[0];
        }
        int gaps = Math.max(0, ratios.length - 1) * gap;
        int available = Math.max(0, height - gaps);
        EscRect[] out = new EscRect[ratios.length];
        int cursor = y;
        for (int i = 0; i < ratios.length; i++) {
            int rowH = (int) (available * (Math.max(0f, ratios[i]) / total));
            if (i == ratios.length - 1) {
                rowH = bottom() - cursor;
            }
            out[i] = new EscRect(x, cursor, width, Math.max(0, rowH));
            cursor += rowH + gap;
        }
        return out;
    }

    /** Convenience: split parent into fixed-count equal columns. */
    public List<EscRect> columns(int count, int gap) {
        if (count <= 0) {
            return List.of();
        }
        float[] ratios = new float[count];
        for (int i = 0; i < count; i++) {
            ratios[i] = 1f;
        }
        return List.of(splitColumns(ratios, gap));
    }

    /** Convenience: split parent into fixed-count equal rows. */
    public List<EscRect> rows(int count, int gap) {
        if (count <= 0) {
            return List.of();
        }
        float[] ratios = new float[count];
        for (int i = 0; i < count; i++) {
            ratios[i] = 1f;
        }
        List<EscRect> out = new ArrayList<>(count);
        for (EscRect r : splitRows(ratios, gap)) {
            out.add(r);
        }
        return out;
    }
}
