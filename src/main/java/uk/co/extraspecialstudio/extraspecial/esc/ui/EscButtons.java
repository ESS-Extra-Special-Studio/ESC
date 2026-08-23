package uk.co.extraspecialstudio.extraspecial.esc.ui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import uk.co.extraspecialstudio.extraspecial.esc.theme.EscFrameStyle;

/**
 * ESC-styled buttons — sharp text + themed chrome fill/frame (not vanilla rounded widgets).
 * Prefer overloads that pass an {@link EscUiStyle} from the owning screen so hub player
 * colours do not leak into unrelated ESC windows.
 */
public final class EscButtons {

    private static final int TEXT_MARGIN = 3;

    private EscButtons() {
    }

    public static Button button(Component label, int x, int y, int width, int height, Button.OnPress onPress) {
        return button(label, x, y, width, height, null, onPress);
    }

    public static Button button(
        Component label,
        int x,
        int y,
        int width,
        int height,
        EscUiStyle style,
        Button.OnPress onPress
    ) {
        Component styled = EscText.styled(label);
        return new EscChromeButton(x, y, width, height, styled, style, onPress);
    }

    public static Button button(Component label, int x, int y, EscUiStyle style, Button.OnPress onPress) {
        return button(label, x, y, style.defaultButtonWidth(), style.defaultButtonHeight(), style, onPress);
    }

    public static Button button(Component label, EscRect parent, EscLayoutSpec spec, Button.OnPress onPress) {
        EscRect resolved = EscLayout.resolve(parent, spec);
        return button(label, resolved.x(), resolved.y(), resolved.width(), resolved.height(), onPress);
    }

    public static Button button(
        Component label,
        EscRect parent,
        EscLayoutSpec spec,
        EscUiStyle style,
        Button.OnPress onPress
    ) {
        EscRect resolved = EscLayout.resolve(parent, spec);
        return button(label, resolved.x(), resolved.y(), resolved.width(), resolved.height(), style, onPress);
    }

    private static final class EscChromeButton extends Button {
        private final EscUiStyle styleOverride;

        EscChromeButton(
            int x,
            int y,
            int width,
            int height,
            Component message,
            EscUiStyle styleOverride,
            OnPress onPress
        ) {
            super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
            this.styleOverride = styleOverride;
        }

        @Override
        protected void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
            EscUiStyle style = styleOverride != null ? styleOverride : EscUiStyle.active();
            EscRect box = new EscRect(getX(), getY(), getWidth(), getHeight());
            boolean hot = isHoveredOrFocused();
            int fill = EscPanel.withOpacity(style.panelFillColor(), Math.min(1f, style.panelOpacity() + (hot ? 0.08f : 0f)));
            EscPanel.fill(g, box, fill);
            int border = hot ? style.focusBorderColor() : style.panelBorderColor();
            // Buttons stay rectangular so themed cut-corners / brackets cannot clip labels.
            EscPanel.renderFrame(g, box, border, Math.max(1, style.panelBorderWidth()), EscFrameStyle.SQUARE);
            int textColor = hot ? (style.textColorTitle() | 0xFF000000) : (style.textColorBody() | 0xFF000000);
            renderString(g, net.minecraft.client.Minecraft.getInstance().font, textColor);
        }

        @Override
        public void renderString(GuiGraphics guiGraphics, Font font, int color) {
            EscWidgetText.renderScrollingStringNoShadow(
                guiGraphics,
                font,
                getMessage(),
                getX() + TEXT_MARGIN,
                getY(),
                getX() + getWidth() - TEXT_MARGIN,
                getY() + getHeight(),
                color
            );
        }
    }
}
