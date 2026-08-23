package uk.co.extraspecialstudio.extraspecial.esc.ui;

import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

public class EscSearchBox extends EditBox {
    private static final int TEXT_PAD_H = 4;
    private static final int TEXT_PAD_V = 2;

    private final int backgroundColor;
    private final int borderColor;
    private final int textColorRgb;
    private final int hintColorRgb;
    private final int cursorColor;
    private final Font typeface;
    private Component displayHint;

    public EscSearchBox(Font font, int x, int y, int width, int height, Component message) {
        this(font, x, y, width, height, message, 0xE0000000, 0xFFB0B0B0, 0xE0E0E0, 0xA0A0A0);
    }

    public EscSearchBox(
        Font font,
        int x,
        int y,
        int width,
        int height,
        Component message,
        int backgroundColor,
        int borderColor,
        int textColorRgb,
        int hintColorRgb
    ) {
        super(font, x, y, width, height, message);
        this.typeface = font;
        this.backgroundColor = backgroundColor;
        this.borderColor = borderColor;
        this.textColorRgb = textColorRgb & 0xFFFFFF;
        this.hintColorRgb = hintColorRgb & 0xFFFFFF;
        this.cursorColor = 0xFF000000 | this.textColorRgb;
        setBordered(false);
        setTextColor(this.textColorRgb);
        setTextColorUneditable(this.hintColorRgb);
        this.setFormatter((text, cursorPos) -> FormattedCharSequence.forward(text, Style.EMPTY.withFont(EscFonts.DEFAULT)));
    }

    /** Single-line height for anchor layout — matches rendered chrome and centered text. */
    public static int recommendedHeight(Font font) {
        return EscText.measureLineHeight(font, EscFonts.DEFAULT) + TEXT_PAD_V * 2;
    }

    public static EscSearchBox at(Font font, EscRect bounds, Component message) {
        int h = recommendedHeight(font);
        return new EscSearchBox(font, bounds.x(), bounds.y(), bounds.width(), h, message);
    }

    public static EscSearchBox at(Font font, EscRect bounds, Component message, EscUiStyle style) {
        int h = recommendedHeight(font);
        if (style == null) {
            return at(font, bounds, message);
        }
        int fill = EscPanel.withOpacity(style.panelFillColor(), Math.min(1f, style.panelOpacity()));
        return new EscSearchBox(
            font,
            bounds.x(),
            bounds.y(),
            bounds.width(),
            h,
            message,
            fill,
            style.panelBorderColor(),
            style.textColorBody(),
            style.mutedColor()
        );
    }

    public void layout(EscRect bounds) {
        int h = recommendedHeight(typeface);
        setX(bounds.x());
        setY(bounds.y());
        setWidth(bounds.width());
        setHeight(h);
    }

    @Override
    public void setHint(Component hint) {
        this.displayHint = hint;
        super.setHint(Component.empty());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.active && this.visible && this.isMouseOver(mouseX, mouseY)) {
            this.setFocused(true);
            return super.mouseClicked(mouseX, mouseY, button) || true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int x0 = getX();
        int y0 = getY();
        int x1 = x0 + getWidth();
        int y1 = y0 + getHeight();
        guiGraphics.fill(x0, y0, x1, y1, backgroundColor);
        guiGraphics.fill(x0, y0, x1, y0 + 1, borderColor);
        guiGraphics.fill(x0, y1 - 1, x1, y1, borderColor);
        guiGraphics.fill(x0, y0, x0 + 1, y1, borderColor);
        guiGraphics.fill(x1 - 1, y0, x1, y1, borderColor);

        int clipX0 = x0 + 1;
        int clipY0 = y0 + 1;
        int clipX1 = x1 - 1;
        int clipY1 = y1 - 1;
        int textW = Math.max(0, getWidth() - TEXT_PAD_H * 2);
        int textX = x0 + TEXT_PAD_H;
        int lh = EscText.measureLineHeight(typeface, EscFonts.DEFAULT);
        int textY = y0 + Math.max(TEXT_PAD_V, (getHeight() - lh) / 2);

        guiGraphics.enableScissor(clipX0, clipY0, clipX1, clipY1);
        try {
            String value = getValue();
            if (!isFocused()) {
                if (value.isEmpty()) {
                    if (displayHint != null) {
                        EscText.drawScrollingString(guiGraphics, typeface, displayHint.getString(),
                            textX, textY, textW, 0xFF000000 | hintColorRgb, EscFonts.DEFAULT);
                    }
                    return;
                }
                EscText.drawScrollingString(guiGraphics, typeface, value, textX, textY, textW,
                    0xFF000000 | textColorRgb, EscFonts.DEFAULT);
                return;
            }

            // Focused: pan to keep the caret in view instead of marquee-scrolling, so the
            // caret stays aligned with the glyphs while typing. Widths must be measured with
            // the same font the text is drawn in, or the caret drifts under the last letter.
            int caret = Math.max(0, Math.min(getCursorPosition(), value.length()));
            int caretX = EscText.width(typeface, value.substring(0, caret), EscFonts.DEFAULT);
            int fullW = EscText.width(typeface, value, EscFonts.DEFAULT);
            int scroll = Math.min(Math.max(0, fullW - textW), Math.max(0, caretX - textW + 1));
            if (caretX < scroll) {
                scroll = caretX;
            }

            EscText.drawString(guiGraphics, typeface, value, textX - scroll, textY,
                0xFF000000 | textColorRgb, false, EscFonts.DEFAULT);

            if (Util.getMillis() / 300L % 2L == 0L) {
                int cursorX = textX + caretX - scroll;
                guiGraphics.fill(cursorX, textY - 1, cursorX + 1, textY + lh, cursorColor);
            }
        } finally {
            guiGraphics.disableScissor();
        }
    }
}
