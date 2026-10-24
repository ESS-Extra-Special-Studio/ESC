package uk.co.extraspecialstudio.extraspecial.esc.ui;

import net.minecraft.util.Util;
import net.minecraft.client.gui.FontRenderer;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscGraphics;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.math.MathHelper;

public final class EscWidgetText {

    private EscWidgetText() {
    }

    /** Label layout matches vanilla scrolling buttons, without the shadow pass (sharper TTF). */
    public static void renderScrollingStringNoShadow(
        EscGraphics guiGraphics,
        FontRenderer font,
        ITextComponent message,
        int leftX,
        int topY,
        int rightX,
        int bottomY,
        int color
    ) {
        int centerX = (leftX + rightX) / 2;
        int textWidth = font.width(message);
        int lineY = (topY + bottomY - 9) / 2 + 1;
        int clipWidth = rightX - leftX;
        if (textWidth > clipWidth) {
            int overflow = textWidth - clipWidth;
            double t = (double) Util.getMillis() / 1000.0;
            double period = Math.max((double) overflow * 0.5, 3.0);
            double phase = Math.sin((Math.PI / 2) * Math.cos((Math.PI * 2) * t / period)) / 2.0 + 0.5;
            double scroll = MathHelper.lerp(phase, 0.0, (double) overflow);
            guiGraphics.enableScissor(leftX, topY, rightX, bottomY);
            guiGraphics.drawString(font, message, leftX - (int) scroll, lineY, color, false);
            guiGraphics.disableScissor();
        } else {
            int clampedCenterX = MathHelper.clamp(centerX, leftX + textWidth / 2, rightX - textWidth / 2);
            IReorderingProcessor visual = message.getVisualOrderText();
            int w = font.width(visual);
            guiGraphics.drawString(font, visual, clampedCenterX - w / 2, lineY, color, false);
        }
    }
}
