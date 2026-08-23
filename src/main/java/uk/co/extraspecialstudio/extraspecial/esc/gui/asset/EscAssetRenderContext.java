package uk.co.extraspecialstudio.extraspecial.esc.gui.asset;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import uk.co.extraspecialstudio.extraspecial.esc.gui.profile.EscGuiBindings;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscRect;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscUiStyle;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Runtime draw context passed to {@link EscAssetRenderer} implementations.
 */
public final class EscAssetRenderContext {
    private final GuiGraphics graphics;
    private final Font font;
    private final EscRect area;
    private final EscUiStyle style;
    private final EscGuiBindings bindings;
    private final Screen screen;
    private final Map<String, Runnable> actions;
    private final Consumer<String> actionInvoker;

    public EscAssetRenderContext(
        GuiGraphics graphics,
        Font font,
        EscRect area,
        EscUiStyle style,
        EscGuiBindings bindings,
        Screen screen,
        Map<String, Runnable> actions,
        Consumer<String> actionInvoker
    ) {
        this.graphics = graphics;
        this.font = font;
        this.area = area;
        this.style = style;
        this.bindings = bindings;
        this.screen = screen;
        this.actions = actions;
        this.actionInvoker = actionInvoker;
    }

    public GuiGraphics graphics() {
        return graphics;
    }

    public Font font() {
        return font;
    }

    public EscRect area() {
        return area;
    }

    public EscUiStyle style() {
        return style;
    }

    public EscGuiBindings bindings() {
        return bindings;
    }

    public Screen screen() {
        return screen;
    }

    public Map<String, Runnable> actions() {
        return actions;
    }

    public void invokeAction(String actionId) {
        if (actionInvoker != null) {
            actionInvoker.accept(actionId);
        }
    }

    public String resolve(String raw) {
        return bindings.resolve(raw);
    }
}
