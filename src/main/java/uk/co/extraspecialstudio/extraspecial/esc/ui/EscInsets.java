package uk.co.extraspecialstudio.extraspecial.esc.ui;

/**
 * Edge insets from a parent rectangle (left, top, right, bottom).
 */
public final class EscInsets {
    private final int left;
    private final int top;
    private final int right;
    private final int bottom;

    public EscInsets(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public int left() { return this.left; }
    public int top() { return this.top; }
    public int right() { return this.right; }
    public int bottom() { return this.bottom; }


    public static final EscInsets ZERO = new EscInsets(0, 0, 0, 0);

    public static EscInsets of(int all) {
        return new EscInsets(all, all, all, all);
    }

    public static EscInsets of(int horizontal, int vertical) {
        return new EscInsets(horizontal, vertical, horizontal, vertical);
    }

    public static EscInsets of(int left, int top, int right, int bottom) {
        return new EscInsets(left, top, right, bottom);
    }
}
