package uk.co.extraspecialstudio.extraspecial.esc.ui;

/**
 * Resolves {@link EscLayoutSpec} against a parent rectangle to absolute screen coordinates.
 */
public final class EscLayout {

    private EscLayout() {
    }

    public static EscRect resolve(EscRect parent, EscLayoutSpec spec) {
        if (parent == null || spec == null) {
            return new EscRect(0, 0, 0, 0);
        }
        EscInsets pad = spec.stretchInsets() != null ? spec.stretchInsets() : EscInsets.ZERO;

        return switch (spec.anchor()) {
            case FILL -> parent.inset(pad);
            case STRETCH_H -> {
                EscRect inner = parent.inset(pad);
                int h = spec.height() >= 0 ? spec.height() : inner.height();
                yield new EscRect(inner.x(), inner.y() + spec.offsetY(), inner.width(), h);
            }
            case STRETCH_V -> {
                EscRect inner = parent.inset(pad);
                int w = spec.width() >= 0 ? spec.width() : inner.width();
                yield new EscRect(inner.x() + spec.offsetX(), inner.y(), w, inner.height());
            }
            default -> resolvePointAnchor(parent, spec);
        };
    }

    private static EscRect resolvePointAnchor(EscRect parent, EscLayoutSpec spec) {
        int w = Math.max(0, spec.width());
        int h = Math.max(0, spec.height());
        int x = anchorX(parent, spec.anchor(), w, spec.offsetX());
        int y = anchorY(parent, spec.anchor(), h, spec.offsetY());
        return new EscRect(x, y, w, h);
    }

    private static int anchorX(EscRect parent, EscAnchor anchor, int width, int offsetX) {
        return switch (anchor) {
            case TOP_LEFT, CENTER_LEFT, BOTTOM_LEFT -> parent.x() + offsetX;
            case TOP_CENTER, CENTER, BOTTOM_CENTER -> parent.x() + (parent.width() - width) / 2 + offsetX;
            case TOP_RIGHT, CENTER_RIGHT, BOTTOM_RIGHT -> parent.right() - width + offsetX;
            default -> parent.x() + offsetX;
        };
    }

    private static int anchorY(EscRect parent, EscAnchor anchor, int height, int offsetY) {
        return switch (anchor) {
            case TOP_LEFT, TOP_CENTER, TOP_RIGHT -> parent.y() + offsetY;
            case CENTER_LEFT, CENTER, CENTER_RIGHT -> parent.y() + (parent.height() - height) / 2 + offsetY;
            case BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT -> parent.bottom() - height + offsetY;
            default -> parent.y() + offsetY;
        };
    }

    /**
     * Largest rect with the given content aspect ratio that fits inside {@code parent} (after insets),
     * centered. Use for diagrams, texture-like UI, or any content with a fixed design size that should
     * scale uniformly on resize — the usual Minecraft GUI pattern.
     */
    public static EscRect fitContained(EscRect parent, int contentWidth, int contentHeight, EscInsets insets) {
        if (parent == null || contentWidth <= 0 || contentHeight <= 0) {
            return parent != null ? parent : new EscRect(0, 0, 0, 0);
        }
        EscRect avail = parent.inset(insets != null ? insets : EscInsets.ZERO);
        if (avail.width() <= 0 || avail.height() <= 0) {
            return avail;
        }
        float scale = Math.min((float) avail.width() / contentWidth, (float) avail.height() / contentHeight);
        int w = Math.max(1, Math.round(contentWidth * scale));
        int h = Math.max(1, Math.round(contentHeight * scale));
        int x = avail.x() + (avail.width() - w) / 2;
        int y = avail.y() + (avail.height() - h) / 2;
        return new EscRect(x, y, w, h);
    }

    /** Uniform scale factor when fitting {@code contentWidth}×{@code contentHeight} into {@code parent}. */
    public static float fitScale(EscRect parent, int contentWidth, int contentHeight, EscInsets insets) {
        if (parent == null || contentWidth <= 0 || contentHeight <= 0) {
            return 1f;
        }
        EscRect avail = parent.inset(insets != null ? insets : EscInsets.ZERO);
        if (avail.width() <= 0 || avail.height() <= 0) {
            return 1f;
        }
        return Math.min((float) avail.width() / contentWidth, (float) avail.height() / contentHeight);
    }
}
