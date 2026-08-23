package uk.co.extraspecialstudio.extraspecial.esc.ui;

/**
 * Anchor + offset layout specification relative to a parent {@link EscRect}.
 * Width/height of {@code -1} means derive from anchor mode (stretch/fill).
 */
public record EscLayoutSpec(
    EscAnchor anchor,
    int offsetX,
    int offsetY,
    int width,
    int height,
    EscInsets stretchInsets
) {
    public static EscLayoutSpec of(EscAnchor anchor, int offsetX, int offsetY, int width, int height) {
        return new EscLayoutSpec(anchor, offsetX, offsetY, width, height, EscInsets.ZERO);
    }

    public static EscLayoutSpec fill(EscInsets insets) {
        return new EscLayoutSpec(EscAnchor.FILL, 0, 0, -1, -1, insets);
    }

    public static EscLayoutSpec fill(int inset) {
        return fill(EscInsets.of(inset));
    }

    public static EscLayoutSpec stretchH(int offsetY, int height, EscInsets horizontalInsets) {
        return new EscLayoutSpec(EscAnchor.STRETCH_H, 0, offsetY, -1, height, horizontalInsets);
    }

    public static EscLayoutSpec stretchV(int offsetX, int width, EscInsets verticalInsets) {
        return new EscLayoutSpec(EscAnchor.STRETCH_V, offsetX, 0, width, -1, verticalInsets);
    }

    /**
     * Left widget of a horizontally centered pair (e.g. tab buttons).
     * {@code gap} is the space between the inner edges of the two widgets.
     * Do not pass legacy {@code width/2 ± n} offsets — CENTER/TOP_CENTER already center the widget.
     */
    public static EscLayoutSpec centeredPairLeft(EscAnchor rowAnchor, int offsetY, int width, int height, int gap) {
        int half = width / 2 + gap;
        return EscLayoutSpec.of(rowAnchor, -half, offsetY, width, height);
    }

    /**
     * Right widget of a horizontally centered pair. See {@link #centeredPairLeft}.
     */
    public static EscLayoutSpec centeredPairRight(EscAnchor rowAnchor, int offsetY, int width, int height, int gap) {
        int half = width / 2 + gap;
        return EscLayoutSpec.of(rowAnchor, half, offsetY, width, height);
    }
}
