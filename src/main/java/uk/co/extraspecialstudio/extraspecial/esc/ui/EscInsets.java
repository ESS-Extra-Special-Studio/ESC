package uk.co.extraspecialstudio.extraspecial.esc.ui;

/**
 * Edge insets from a parent rectangle (left, top, right, bottom).
 */
public record EscInsets(int left, int top, int right, int bottom) {

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
