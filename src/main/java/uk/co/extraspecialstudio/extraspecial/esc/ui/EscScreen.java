package uk.co.extraspecialstudio.extraspecial.esc.ui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import uk.co.extraspecialstudio.extraspecial.esc.theme.EscThemeConfigs;

/**
 * Base screen with {@link EscUiStyle}, content rect helpers, and anchor-based widget placement.
 * Subclasses implement {@link #buildLayout()} — called from {@code init()} after resize.
 * <p>
 * Starter pattern: Desktop docs {@code ESC_SCREEN_COOKBOOK.md}.
 */
public abstract class EscScreen extends Screen {

    private static final int MIN_WIDGET_WIDTH = 4;
    private static final int MIN_WIDGET_HEIGHT = 20;

    protected EscUiStyle style;

    protected EscScreen(Component title) {
        this(title, EscUiStyle.active());
    }

    protected EscScreen(Component title, EscUiStyle style) {
        super(title);
        this.style = style;
    }

    protected void setStyle(EscUiStyle style) {
        if (style != null) {
            this.style = style;
        }
    }

    protected EscRect contentRect() {
        return EscPanel.contentRect(this, style);
    }

    @Override
    protected void init() {
        super.init();
        buildLayout();
    }

    /** Define widget positions and custom panel bounds. Called on every {@code init()} (including resize). */
    protected abstract void buildLayout();

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        if (EscThemeConfigs.layoutDebugEnabled()) {
            renderLayoutDebug(graphics);
        }
    }

    /**
     * Draws content + default footer outlines. Subclasses that override {@link #render}
     * without calling {@code super.render} should call this when debugging layout.
     */
    protected void renderLayoutDebug(GuiGraphics g) {
        EscRect content = contentRect();
        EscRect footer = EscPanel.footer(content, style);
        EscPanel.border(g, content, 0xFF00FF88, 1);
        EscPanel.border(g, footer, 0xFFFFAA00, 1);
    }

    protected void layoutWidget(AbstractWidget widget, EscRect bounds) {
        widget.setX(bounds.x());
        widget.setY(bounds.y());
        widget.setWidth(Math.max(MIN_WIDGET_WIDTH, bounds.width()));
        widget.setHeight(Math.max(MIN_WIDGET_HEIGHT, bounds.height()));
    }

    protected EscRect resolve(EscRect parent, EscLayoutSpec spec) {
        return EscLayout.resolve(parent, spec);
    }

    protected Button addAnchoredButton(Component label, EscRect parent, EscLayoutSpec spec, Button.OnPress onPress) {
        EscRect bounds = clampWidgetBounds(EscLayout.resolve(parent, spec));
        Button button = EscButtons.button(label, bounds.x(), bounds.y(), bounds.width(), bounds.height(), style, onPress);
        addRenderableWidget(button);
        return button;
    }

    protected Button addButton(Component label, EscRect bounds, Button.OnPress onPress) {
        EscRect sized = clampWidgetBounds(bounds);
        Button button = EscButtons.button(label, sized.x(), sized.y(), sized.width(), sized.height(), style, onPress);
        addRenderableWidget(button);
        return button;
    }

    protected EscSearchBox addSearchBox(EscRect bounds, Component message) {
        return addSearchBox(bounds, message, null);
    }

    protected EscSearchBox addSearchBox(EscRect bounds, Component message, Component hint) {
        EscRect sized = new EscRect(bounds.x(), bounds.y(), bounds.width(), EscSearchBox.recommendedHeight(font));
        EscSearchBox box = EscSearchBox.at(font, sized, message, style);
        if (hint != null) {
            box.setHint(hint);
        }
        addRenderableWidget(box);
        return box;
    }

    protected EscSearchBox addAnchoredSearchBox(EscRect parent, EscLayoutSpec spec, Component message, Component hint) {
        return addSearchBox(EscLayout.resolve(parent, spec), message, hint);
    }

    protected EscSearchBox addAnchoredSearchBox(EscRect parent, EscLayoutSpec spec, Component message) {
        return addAnchoredSearchBox(parent, spec, message, null);
    }

    private EscRect clampWidgetBounds(EscRect bounds) {
        return new EscRect(
            bounds.x(),
            bounds.y(),
            Math.max(MIN_WIDGET_WIDTH, bounds.width()),
            Math.max(MIN_WIDGET_HEIGHT, bounds.height())
        );
    }

    /** Public accessors for declarative GUI runtime in {@code esc.gui}. */
    public EscUiStyle uiStyle() {
        return style;
    }

    public Button escButton(Component label, EscRect bounds, Button.OnPress onPress) {
        return addButton(label, bounds, onPress);
    }

    public EscSearchBox escSearchBox(EscRect bounds, Component message) {
        return addSearchBox(bounds, message);
    }

    public EscSearchBox escSearchBox(EscRect bounds, Component message, Component hint) {
        return addSearchBox(bounds, message, hint);
    }
}
