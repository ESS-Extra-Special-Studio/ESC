package uk.co.extraspecialstudio.extraspecial.esc.ui;

/**
 * Anchor point within a parent {@link EscRect} for responsive layout.
 */
public enum EscAnchor {
    TOP_LEFT,
    TOP_CENTER,
    TOP_RIGHT,
    CENTER_LEFT,
    CENTER,
    CENTER_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_CENTER,
    BOTTOM_RIGHT,
    /** Full width, fixed height; use {@link EscLayoutSpec#stretchInsets()}. */
    STRETCH_H,
    /** Full height, fixed width; use {@link EscLayoutSpec#stretchInsets()}. */
    STRETCH_V,
    /** Fill parent minus {@link EscLayoutSpec#stretchInsets()}. */
    FILL
}
