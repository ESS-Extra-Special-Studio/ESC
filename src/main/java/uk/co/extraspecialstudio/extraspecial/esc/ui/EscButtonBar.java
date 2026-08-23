package uk.co.extraspecialstudio.extraspecial.esc.ui;

/**
 * Non-overlapping button slot geometry for ESC chrome.
 * <p>
 * Hard-coded {@code x + N} / {@code right - N} placements collide when the parent
 * shrinks. These helpers always keep slots inside {@code area}, never overlapping,
 * and shrink evenly with screen / panel width (labels can marquee).
 */
public final class EscButtonBar {

    public static final int DEFAULT_PAD = 4;
    public static final int DEFAULT_GAP = 3;
    public static final int DEFAULT_HEIGHT = 20;
    public static final int DEFAULT_ROW_GAP = 4;
    /** Soft floor — below this we still place (no overlap), but pack prefers equal-fill. */
    public static final int MIN_COMFORTABLE_WIDTH = 40;

    private EscButtonBar() {
    }

    /**
     * Equal-width slots in one horizontal row at {@code y}, filling {@code area} width
     * after padding. Last slot absorbs remainder so the right edge stays flush.
     */
    public static EscRect[] equalRow(EscRect area, int count, int y, int height) {
        return equalRow(area, count, y, height, DEFAULT_PAD, DEFAULT_GAP);
    }

    public static EscRect[] equalRow(EscRect area, int count, int y, int height, int pad, int gap) {
        if (area == null || count <= 0 || height <= 0) {
            return new EscRect[0];
        }
        int safePad = Math.max(0, pad);
        int safeGap = Math.max(0, gap);
        int innerW = Math.max(0, area.width() - safePad * 2);
        int gaps = safeGap * Math.max(0, count - 1);
        int slotW = Math.max(1, (innerW - gaps) / count);
        int leftover = Math.max(0, innerW - gaps - slotW * count);
        EscRect[] out = new EscRect[count];
        int x = area.x() + safePad;
        int rightLimit = area.right() - safePad;
        for (int i = 0; i < count; i++) {
            int w = slotW + (i < leftover ? 1 : 0);
            if (i == count - 1) {
                w = Math.max(1, rightLimit - x);
            }
            out[i] = new EscRect(x, y, w, height);
            x += w + safeGap;
        }
        return out;
    }

    /**
     * Stack {@code counts.length} equal rows top-down inside {@code area}.
     * Returns one array per row (same order as {@code counts}).
     */
    public static EscRect[][] equalRows(EscRect area, int[] counts, int height) {
        return equalRows(area, counts, height, DEFAULT_PAD, DEFAULT_GAP, DEFAULT_ROW_GAP);
    }

    public static EscRect[][] equalRows(EscRect area, int[] counts, int height, int pad, int gap, int rowGap) {
        if (area == null || counts == null || counts.length == 0) {
            return new EscRect[0][];
        }
        int safePad = Math.max(0, pad);
        int safeRowGap = Math.max(0, rowGap);
        int rows = counts.length;
        int y0 = area.y() + safePad;
        EscRect[][] out = new EscRect[rows][];
        for (int r = 0; r < rows; r++) {
            int y = y0 + r * (height + safeRowGap);
            out[r] = equalRow(area, counts[r], y, height, safePad, gap);
        }
        return out;
    }

    /**
     * Pack left using preferred widths when they fit; otherwise fall back to {@link #equalRow}.
     * Preferred widths are clamped so the row never overflows {@code area}.
     */
    public static EscRect[] packLeftOrEqual(EscRect area, int[] preferredWidths, int y, int height) {
        return packLeftOrEqual(area, preferredWidths, y, height, DEFAULT_PAD, DEFAULT_GAP);
    }

    public static EscRect[] packLeftOrEqual(EscRect area, int[] preferredWidths, int y, int height, int pad, int gap) {
        if (area == null || preferredWidths == null || preferredWidths.length == 0) {
            return new EscRect[0];
        }
        int n = preferredWidths.length;
        int safePad = Math.max(0, pad);
        int safeGap = Math.max(0, gap);
        int innerW = Math.max(0, area.width() - safePad * 2);
        int gaps = safeGap * Math.max(0, n - 1);
        int preferSum = 0;
        for (int w : preferredWidths) {
            preferSum += Math.max(1, w);
        }
        if (preferSum + gaps > innerW) {
            return equalRow(area, n, y, height, safePad, safeGap);
        }
        EscRect[] out = new EscRect[n];
        int x = area.x() + safePad;
        for (int i = 0; i < n; i++) {
            int w = Math.max(1, preferredWidths[i]);
            out[i] = new EscRect(x, y, w, height);
            x += w + safeGap;
        }
        return out;
    }

    /**
     * Left group packed from the left, right group from the right.
     * If the groups would collide or leave less than {@code gap} between them,
     * falls back to a single equal row of {@code left + right} slots.
     */
    public static EscRect[] splitOrEqual(
        EscRect area,
        int[] leftPreferred,
        int[] rightPreferred,
        int y,
        int height
    ) {
        return splitOrEqual(area, leftPreferred, rightPreferred, y, height, DEFAULT_PAD, DEFAULT_GAP);
    }

    public static EscRect[] splitOrEqual(
        EscRect area,
        int[] leftPreferred,
        int[] rightPreferred,
        int y,
        int height,
        int pad,
        int gap
    ) {
        int leftN = leftPreferred == null ? 0 : leftPreferred.length;
        int rightN = rightPreferred == null ? 0 : rightPreferred.length;
        if (area == null || leftN + rightN == 0) {
            return new EscRect[0];
        }
        if (leftN == 0) {
            return packRightOrEqual(area, rightPreferred, y, height, pad, gap);
        }
        if (rightN == 0) {
            return packLeftOrEqual(area, leftPreferred, y, height, pad, gap);
        }

        int safePad = Math.max(0, pad);
        int safeGap = Math.max(0, gap);
        int innerLeft = area.x() + safePad;
        int innerRight = area.right() - safePad;

        int leftSum = sumPreferred(leftPreferred) + safeGap * Math.max(0, leftN - 1);
        int rightSum = sumPreferred(rightPreferred) + safeGap * Math.max(0, rightN - 1);
        if (leftSum + rightSum + safeGap > innerRight - innerLeft) {
            return equalRow(area, leftN + rightN, y, height, safePad, safeGap);
        }

        EscRect[] left = packLeftOrEqual(area, leftPreferred, y, height, safePad, safeGap);
        EscRect[] right = packRightOrEqual(area, rightPreferred, y, height, safePad, safeGap);
        if (left.length == 0 || right.length == 0) {
            return equalRow(area, leftN + rightN, y, height, safePad, safeGap);
        }
        if (right[0].x() < left[left.length - 1].right() + safeGap) {
            return equalRow(area, leftN + rightN, y, height, safePad, safeGap);
        }

        EscRect[] out = new EscRect[leftN + rightN];
        System.arraycopy(left, 0, out, 0, leftN);
        System.arraycopy(right, 0, out, leftN, rightN);
        return out;
    }

    /** Pack from the right; overflow → equal row. */
    public static EscRect[] packRightOrEqual(EscRect area, int[] preferredWidths, int y, int height) {
        return packRightOrEqual(area, preferredWidths, y, height, DEFAULT_PAD, DEFAULT_GAP);
    }

    public static EscRect[] packRightOrEqual(EscRect area, int[] preferredWidths, int y, int height, int pad, int gap) {
        if (area == null || preferredWidths == null || preferredWidths.length == 0) {
            return new EscRect[0];
        }
        int n = preferredWidths.length;
        int safePad = Math.max(0, pad);
        int safeGap = Math.max(0, gap);
        int innerW = Math.max(0, area.width() - safePad * 2);
        int gaps = safeGap * Math.max(0, n - 1);
        if (sumPreferred(preferredWidths) + gaps > innerW) {
            return equalRow(area, n, y, height, safePad, safeGap);
        }
        EscRect[] out = new EscRect[n];
        int x = area.right() - safePad;
        for (int i = n - 1; i >= 0; i--) {
            int w = Math.max(1, preferredWidths[i]);
            x -= w;
            out[i] = new EscRect(x, y, w, height);
            if (i > 0) {
                x -= safeGap;
            }
        }
        return out;
    }

    /**
     * Prefer readable slot widths. If a single equal row would squeeze below
     * {@link #MIN_COMFORTABLE_WIDTH}, split into two equal rows (ceil/floor).
     * Returns a flat slot array in visual order (row0 left→right, then row1).
     * Caller must size {@code area} tall enough for two rows when {@link #needsTwoRows}
     * is true for the same inputs.
     */
    public static EscRect[] fitDense(EscRect area, int count, int height) {
        return fitDense(area, count, height, DEFAULT_PAD, DEFAULT_GAP, DEFAULT_ROW_GAP);
    }

    public static EscRect[] fitDense(EscRect area, int count, int height, int pad, int gap, int rowGap) {
        if (area == null || count <= 0 || height <= 0) {
            return new EscRect[0];
        }
        int safePad = Math.max(0, pad);
        int safeGap = Math.max(0, gap);
        int innerW = Math.max(0, area.width() - safePad * 2);
        int gaps = safeGap * Math.max(0, count - 1);
        int singleW = count == 0 ? innerW : Math.max(1, (innerW - gaps) / count);
        if (singleW >= MIN_COMFORTABLE_WIDTH || count <= 4) {
            return equalRow(area, count, area.y() + safePad, height, safePad, safeGap);
        }
        int top = (count + 1) / 2;
        int bottom = count - top;
        EscRect[][] rows = equalRows(area, new int[]{top, bottom}, height, safePad, safeGap, rowGap);
        EscRect[] out = new EscRect[count];
        System.arraycopy(rows[0], 0, out, 0, top);
        System.arraycopy(rows[1], 0, out, top, bottom);
        return out;
    }

    /** Height needed for {@link #fitDense} given count and panel width. */
    public static int fitDenseHeight(int areaWidth, int count, int height) {
        return fitDenseHeight(areaWidth, count, height, DEFAULT_PAD, DEFAULT_GAP, DEFAULT_ROW_GAP);
    }

    public static int fitDenseHeight(int areaWidth, int count, int height, int pad, int gap, int rowGap) {
        if (count <= 0) {
            return height + pad * 2;
        }
        int safePad = Math.max(0, pad);
        int safeGap = Math.max(0, gap);
        int innerW = Math.max(0, areaWidth - safePad * 2);
        int gaps = safeGap * Math.max(0, count - 1);
        int singleW = Math.max(1, (innerW - gaps) / count);
        if (singleW >= MIN_COMFORTABLE_WIDTH || count <= 4) {
            return height + safePad * 2;
        }
        return height * 2 + rowGap + safePad * 2;
    }

    public static boolean needsTwoRows(int areaWidth, int count) {
        return needsTwoRows(areaWidth, count, DEFAULT_PAD, DEFAULT_GAP);
    }

    public static boolean needsTwoRows(int areaWidth, int count, int pad, int gap) {
        if (count <= 4) {
            return false;
        }
        int safePad = Math.max(0, pad);
        int safeGap = Math.max(0, gap);
        int innerW = Math.max(0, areaWidth - safePad * 2);
        int gaps = safeGap * Math.max(0, count - 1);
        int singleW = Math.max(1, (innerW - gaps) / count);
        return singleW < MIN_COMFORTABLE_WIDTH;
    }

    /** True if every slot is inside {@code area} and no two slots overlap (axis-aligned). */
    public static boolean validate(EscRect area, EscRect[] slots) {
        if (area == null || slots == null) {
            return false;
        }
        for (int i = 0; i < slots.length; i++) {
            EscRect a = slots[i];
            if (a == null || a.width() <= 0 || a.height() <= 0) {
                return false;
            }
            if (a.x() < area.x() || a.y() < area.y() || a.right() > area.right() || a.bottom() > area.bottom()) {
                return false;
            }
            for (int j = i + 1; j < slots.length; j++) {
                if (overlaps(a, slots[j])) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean overlaps(EscRect a, EscRect b) {
        if (a == null || b == null) {
            return false;
        }
        return a.x() < b.right() && a.right() > b.x() && a.y() < b.bottom() && a.bottom() > b.y();
    }

    private static int sumPreferred(int[] widths) {
        int sum = 0;
        for (int w : widths) {
            sum += Math.max(1, w);
        }
        return sum;
    }
}
