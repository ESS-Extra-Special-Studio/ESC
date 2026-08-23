package uk.co.extraspecialstudio.extraspecial.esc.ui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import uk.co.extraspecialstudio.extraspecial.esc.ui.anim.EscFocusVisual;

/**
 * Role-based text drawing (fit, truncate, uppercase).
 */
public final class EscTypography {

    private EscTypography() {
    }

    public static void draw(
        GuiGraphics g,
        Font font,
        String text,
        int x,
        int y,
        EscUiStyle style,
        EscTypeRole role,
        int maxWidth,
        float alpha
    ) {
        if (text == null) text = "";
        EscTypeRole r = role == null ? EscTypeRole.BODY : role;
        String drawn = r.uppercase() ? text.toUpperCase() : text;
        if (maxWidth > 0) {
            drawn = truncate(font, drawn, maxWidth);
        }
        int color = EscFocusVisual.applyAlpha(r.color(style) | 0xFF000000, alpha);
        g.drawString(font, drawn, x, y, color, false);
    }

    /**
     * Same as {@link #draw} but marquee-scrolls instead of truncating with an ellipsis.
     * Use for detail panes and rows where the full value matters (ids, versions, paths);
     * text that already fits is drawn static, so this is safe as a default for such lines.
     */
    public static void drawScrolling(
        GuiGraphics g,
        Font font,
        String text,
        int x,
        int y,
        EscUiStyle style,
        EscTypeRole role,
        int maxWidth,
        float alpha
    ) {
        if (text == null) text = "";
        EscTypeRole r = role == null ? EscTypeRole.BODY : role;
        String drawn = r.uppercase() ? text.toUpperCase() : text;
        int color = EscFocusVisual.applyAlpha(r.color(style) | 0xFF000000, alpha);
        if (maxWidth <= 0) {
            g.drawString(font, drawn, x, y, color, false);
            return;
        }
        EscText.drawScrollingString(g, font, drawn, x, y, maxWidth, color);
    }

    public static void drawCentered(
        GuiGraphics g,
        Font font,
        String text,
        int centerX,
        int y,
        EscUiStyle style,
        EscTypeRole role,
        float alpha
    ) {
        if (text == null) text = "";
        EscTypeRole r = role == null ? EscTypeRole.TITLE : role;
        String drawn = r.uppercase() ? text.toUpperCase() : text;
        int w = font.width(drawn);
        int color = EscFocusVisual.applyAlpha(r.color(style) | 0xFF000000, alpha);
        g.drawString(font, drawn, centerX - w / 2, y, color, false);
    }

    public static String truncate(Font font, String text, int maxWidth) {
        if (font.width(text) <= maxWidth) {
            return text;
        }
        String ellipsis = "...";
        int budget = maxWidth - font.width(ellipsis);
        if (budget <= 0) {
            return ellipsis;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            sb.append(text.charAt(i));
            if (font.width(sb.toString()) > budget) {
                sb.setLength(Math.max(0, sb.length() - 1));
                break;
            }
        }
        return sb + ellipsis;
    }

    public static Component styled(String text, EscTypeRole role) {
        String t = text == null ? "" : text;
        if (role != null && role.uppercase()) {
            t = t.toUpperCase();
        }
        return Component.literal(t).withStyle(Style.EMPTY.withFont(EscFonts.DEFAULT));
    }

    /** Word-wrap {@code text} to {@code maxWidth}; blank input yields one empty line. */
    public static java.util.List<String> wrap(Font font, String text, int maxWidth) {
        java.util.List<String> out = new java.util.ArrayList<>();
        if (text == null || text.isEmpty()) {
            out.add("");
            return out;
        }
        if (maxWidth <= 0) {
            out.add(text);
            return out;
        }
        String[] words = text.split(" ", -1);
        StringBuilder line = new StringBuilder();
        for (String word : words) {
            String candidate = line.isEmpty() ? word : line + " " + word;
            if (font.width(candidate) <= maxWidth) {
                line.setLength(0);
                line.append(candidate);
            } else {
                if (!line.isEmpty()) {
                    out.add(line.toString());
                    line.setLength(0);
                }
                if (font.width(word) <= maxWidth) {
                    line.append(word);
                } else {
                    out.add(truncate(font, word, maxWidth));
                }
            }
        }
        if (!line.isEmpty() || out.isEmpty()) {
            out.add(line.toString());
        }
        return out;
    }

    /**
     * Draws paragraph lines; returns Y after the last line.
     * Empty strings in {@code lines} render as a half-line spacer.
     */
    public static int drawParagraph(
        GuiGraphics g,
        Font font,
        java.util.List<String> lines,
        int x,
        int y,
        EscUiStyle style,
        EscTypeRole role,
        int maxWidth,
        float alpha,
        int scrollPx
    ) {
        int cursor = y - Math.max(0, scrollPx);
        int lineH = font.lineHeight + 2;
        for (String raw : lines) {
            if (raw == null || raw.isBlank()) {
                cursor += lineH / 2;
                continue;
            }
            for (String wrapped : wrap(font, raw, maxWidth)) {
                if (cursor + lineH >= y - lineH && cursor <= y + 4000) {
                    draw(g, font, wrapped, x, cursor, style, role, 0, alpha);
                }
                cursor += lineH;
            }
        }
        return cursor;
    }
}
