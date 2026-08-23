package uk.co.extraspecialstudio.extraspecial.esc.ui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import com.mojang.blaze3d.vertex.PoseStack;

public final class EscText {

    private static final float DEFAULT_MIN_SCALE = 0.5f;

    private EscText() {
    }

    public static int wrapHeight(Font font, String text, int maxWidth) {
        return wrapHeight(font, text, maxWidth, EscFonts.DEFAULT);
    }

    public static Component literal(String text) {
        return literalCompat(text).withStyle(s -> s.withFont(EscFonts.DEFAULT));
    }

    public static Component styled(Component text) {
        return copyCompat(text).withStyle(s -> s.withFont(EscFonts.DEFAULT));
    }

    public static void drawWrapped(GuiGraphics graphics, Font font, String text, int x, int y, int maxWidth, int color) {
        drawWrapped(graphics, font, text, x, y, maxWidth, color, EscFonts.DEFAULT);
    }

    public static int headingHeight(Font font, String heading, int maxWidth) {
        return wrapHeight(font, heading, maxWidth, EscFonts.DEFAULT);
    }

    public static int wrapHeight(Font font, String text, int maxWidth, ResourceLocation fontId) {
        int lines = font.split(literalCompat(text).withStyle(s -> s.withFont(fontId)), maxWidth).size();
        if (lines <= 0) return 0;
        int step = wrappedLineAdvance(font);
        return (lines - 1) * step + font.lineHeight;
    }

    public static int wrapHeight(Font font, Component text, int maxWidth) {
        return wrapHeight(font, text, maxWidth, EscFonts.DEFAULT);
    }

    public static int wrapHeight(Font font, Component text, int maxWidth, ResourceLocation fontId) {
        if (maxWidth <= 0) {
            return 0;
        }
        int lines = font.split(copyCompat(text).withStyle(s -> s.withFont(fontId)), maxWidth).size();
        if (lines <= 0) return 0;
        int step = wrappedLineAdvance(font);
        return (lines - 1) * step + font.lineHeight;
    }

    public static void drawWrappedComponent(GuiGraphics graphics, Font font, Component text, int x, int y, int maxWidth, int color) {
        drawWrappedComponent(graphics, font, text, x, y, maxWidth, color, EscFonts.DEFAULT);
    }

    public static void drawWrappedComponent(
        GuiGraphics graphics,
        Font font,
        Component text,
        int x,
        int y,
        int maxWidth,
        int color,
        ResourceLocation fontId
    ) {
        if (maxWidth <= 0) {
            return;
        }
        int step = wrappedLineAdvance(font);
        for (FormattedCharSequence line : font.split(copyCompat(text).withStyle(s -> s.withFont(fontId)), maxWidth)) {
            graphics.drawString(font, line, x, y, color, false);
            y += step;
        }
    }

    /**
     * Draw wrapped {@link Component} text clipped to a rect (word wrap + scissor). Returns bottom Y after last line.
     */
    public static int drawWrappedComponentInRect(
        GuiGraphics graphics,
        Font font,
        Component text,
        EscRect bounds,
        int color
    ) {
        if (bounds.width() <= 0 || bounds.height() <= 0) {
            return bounds.y();
        }
        graphics.enableScissor(bounds.x(), bounds.y(), bounds.right(), bounds.bottom());
        int y = bounds.y();
        try {
            int step = wrappedLineAdvance(font);
            for (FormattedCharSequence line : font.split(
                copyCompat(text).withStyle(s -> s.withFont(EscFonts.DEFAULT)), Math.max(1, bounds.width()))) {
                if (y + font.lineHeight > bounds.bottom()) {
                    break;
                }
                graphics.drawString(font, line, bounds.x(), y, color, false);
                y += step;
            }
        } finally {
            graphics.disableScissor();
        }
        return y;
    }

    public static void drawWrapped(GuiGraphics graphics, Font font, String text, int x, int y, int maxWidth, int color, ResourceLocation fontId) {
        int step = wrappedLineAdvance(font);
        for (FormattedCharSequence line : font.split(literalCompat(text).withStyle(s -> s.withFont(fontId)), maxWidth)) {
            graphics.drawString(font, line, x, y, color, false);
            y += step;
        }
    }

    /** One extra px between baselines avoids descenders crashing into ascenders with TTF-heavy fonts (e.g. Fira). */
    private static int wrappedLineAdvance(Font font) {
        return font.lineHeight + 1;
    }

    public static void drawString(GuiGraphics graphics, Font font, String text, int x, int y, int color, boolean shadow, ResourceLocation fontId) {
        graphics.drawString(font, literalCompat(text).withStyle(s -> s.withFont(fontId)), x, y, color, shadow);
    }

    public static void drawComponent(GuiGraphics graphics, Font font, Component text, int x, int y, int color, boolean shadow) {
        drawComponent(graphics, font, text, x, y, color, shadow, EscFonts.DEFAULT);
    }

    public static void drawComponent(GuiGraphics graphics, Font font, Component text, int x, int y, int color, boolean shadow, ResourceLocation fontId) {
        graphics.drawString(font, copyCompat(text).withStyle(s -> s.withFont(fontId)), x, y, color, shadow);
    }

    public static void drawCentered(GuiGraphics graphics, Font font, Component text, int centerX, int y, int color) {
        drawCentered(graphics, font, text, centerX, y, color, EscFonts.DEFAULT);
    }

    public static void drawCentered(GuiGraphics graphics, Font font, Component text, int centerX, int y, int color, ResourceLocation fontId) {
        FormattedCharSequence visual = copyCompat(text).withStyle(s -> s.withFont(fontId)).getVisualOrderText();
        int w = font.width(visual);
        graphics.drawString(font, visual, centerX - w / 2, y, color, false);
    }

    /** Line height for a font id — use for vertical centering when drawing with {@link EscFonts#DEFAULT}. */
    public static int measureLineHeight(Font font, ResourceLocation fontId) {
        return wrappedLineAdvance(font);
    }

    /**
     * Rendered width of {@code text} under {@code fontId}.
     * Use instead of raw {@code font.width} whenever the text is drawn through ESC,
     * otherwise measurements drift from the glyphs actually shown (carets, rules, chips).
     */
    public static int width(Font font, String text, ResourceLocation fontId) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        return font.width(literalCompat(text).withStyle(s -> s.withFont(fontId)));
    }

    public static int width(Font font, String text) {
        return width(font, text, EscFonts.DEFAULT);
    }

    public static int measureLineHeight(Font font) {
        return measureLineHeight(font, EscFonts.DEFAULT);
    }

    /** Draw text centered in a rect; scales down if wider than bounds, else truncates with ellipsis. */
    public static void drawFitted(GuiGraphics graphics, Font font, Component text, EscRect bounds, int color) {
        drawFitted(graphics, font, text, bounds, color, EscFonts.DEFAULT, DEFAULT_MIN_SCALE);
    }

    public static void drawFitted(
        GuiGraphics graphics,
        Font font,
        Component text,
        EscRect bounds,
        int color,
        ResourceLocation fontId,
        float minScale
    ) {
        drawInRect(graphics, font, text, bounds, color, fontId, true, minScale);
    }

    /** Draw text inside a rect with scissor clip; optionally scale down to fit width. */
    public static void drawInRect(
        GuiGraphics graphics,
        Font font,
        Component text,
        EscRect bounds,
        int color,
        ResourceLocation fontId,
        boolean scaleDown,
        float minScale
    ) {
        if (bounds.width() <= 0 || bounds.height() <= 0) {
            return;
        }
        FormattedCharSequence visual = copyCompat(text).withStyle(s -> s.withFont(fontId)).getVisualOrderText();
        int textWidth = font.width(visual);
        int lineHeight = measureLineHeight(font, fontId);

        graphics.enableScissor(bounds.x(), bounds.y(), bounds.right(), bounds.bottom());
        if (scaleDown && textWidth > bounds.width()) {
            float scale = Math.max(minScale, (float) bounds.width() / (float) textWidth);
            PoseStack pose = graphics.pose();
            pose.pushPose();
            float cx = bounds.x() + bounds.width() / 2f;
            float cy = bounds.y() + bounds.height() / 2f;
            pose.translate(cx, cy, 0f);
            pose.scale(scale, scale, 1f);
            pose.translate(-cx, -cy, 0f);
            int tx = bounds.x() + (bounds.width() - textWidth) / 2;
            int ty = bounds.y() + (bounds.height() - lineHeight) / 2;
            graphics.drawString(font, visual, tx, ty, color, false);
            pose.popPose();
        } else {
            String plain = text.getString();
            if (textWidth > bounds.width()) {
                plain = truncateToWidth(font, plain, bounds.width(), fontId);
                visual = literalCompat(plain).withStyle(s -> s.withFont(fontId)).getVisualOrderText();
                textWidth = font.width(visual);
            }
            int tx = bounds.x() + (bounds.width() - textWidth) / 2;
            int ty = bounds.y() + (bounds.height() - lineHeight) / 2;
            graphics.drawString(font, visual, tx, ty, color, false);
        }
        graphics.disableScissor();
    }

    private static String truncateToWidth(Font font, String text, int maxWidth, ResourceLocation fontId) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        String ellipsis = "...";
        if (font.width(literalCompat(text).withStyle(s -> s.withFont(fontId))) <= maxWidth) {
            return text;
        }
        for (int len = text.length() - 1; len > 0; len--) {
            String candidate = text.substring(0, len) + ellipsis;
            if (font.width(literalCompat(candidate).withStyle(s -> s.withFont(fontId))) <= maxWidth) {
                return candidate;
            }
        }
        return ellipsis;
    }

    private static MutableComponent literalCompat(String text) {
        return Component.empty().append(text);
    }

    private static MutableComponent copyCompat(Component text) {
        return Component.empty().append(text);
    }

    public static String truncate(String text, int maxChars) {
        if (text == null || text.length() <= maxChars) {
            return text;
        }
        return text.substring(0, maxChars - 1) + "...";
    }

    /** Marquee text clipped to {@code maxWidth}; static when it already fits. */
    public static void drawScrollingString(
        GuiGraphics graphics,
        Font font,
        String text,
        int x,
        int y,
        int maxWidth,
        int color
    ) {
        drawScrollingString(graphics, font, text, x, y, maxWidth, color, EscFonts.DEFAULT);
    }

    public static void drawScrollingString(
        GuiGraphics graphics,
        Font font,
        String text,
        int x,
        int y,
        int maxWidth,
        int color,
        ResourceLocation fontId
    ) {
        if (text == null || text.isEmpty() || maxWidth <= 0) {
            return;
        }
        FormattedCharSequence visual = literalCompat(text).withStyle(s -> s.withFont(fontId)).getVisualOrderText();
        int textWidth = font.width(visual);
        if (textWidth <= maxWidth) {
            graphics.drawString(font, visual, x, y, color, false);
            return;
        }
        int scrollOffset = scrollOffset(textWidth, maxWidth);
        graphics.enableScissor(x, y - 1, x + maxWidth, y + measureLineHeight(font, fontId) + 2);
        try {
            graphics.drawString(font, visual, x - scrollOffset, y, color, false);
        } finally {
            graphics.disableScissor();
        }
    }

    private static int scrollOffset(int textWidth, int maxWidth) {
        int scrollRange = textWidth - maxWidth;
        int scrollSpeed = 40;
        int pauseMs = 600;
        long ms = System.currentTimeMillis();
        long scrollTimeMs = (long) ((scrollRange / (double) scrollSpeed) * 1000);
        long halfCycleMs = scrollTimeMs + pauseMs;
        long cycleMs = halfCycleMs * 2;
        long t = ms % cycleMs;
        if (t < halfCycleMs) {
            return t < scrollTimeMs ? (int) (t * scrollRange / scrollTimeMs) : scrollRange;
        }
        long backT = t - halfCycleMs;
        return backT < scrollTimeMs ? scrollRange - (int) (backT * scrollRange / scrollTimeMs) : 0;
    }
}
