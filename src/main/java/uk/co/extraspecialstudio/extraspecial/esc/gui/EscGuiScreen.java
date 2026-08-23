package uk.co.extraspecialstudio.extraspecial.esc.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import uk.co.extraspecialstudio.extraspecial.esc.gui.asset.EscAssetRenderContext;
import uk.co.extraspecialstudio.extraspecial.esc.gui.profile.EscGuiBindings;
import uk.co.extraspecialstudio.extraspecial.esc.gui.profile.EscGuiProfile;
import uk.co.extraspecialstudio.extraspecial.esc.sound.EscSound;
import uk.co.extraspecialstudio.extraspecial.esc.sound.EscUiCue;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscBackground;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscCrtLayer;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscPanel;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscRect;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscScreen;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscText;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscUiStyle;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Runtime screen that renders a declarative {@link EscGuiProfile} via ESC bricks.
 */
public final class EscGuiScreen extends EscScreen {
    private final Screen parent;
    private final EscGuiProfile profile;
    private final EscGuiBindings bindings;
    private final Map<String, Runnable> actions;
    private EscRect contentBounds = new EscRect(0, 0, 0, 0);

    public EscGuiScreen(
        Screen parent,
        EscGuiProfile profile,
        EscGuiBindings bindings,
        Map<String, Runnable> actions,
        String applicationId
    ) {
        super(EscText.literal(profile.title()), EscUiStyle.active(applicationId == null ? modIdFromProfile(profile) : applicationId));
        this.parent = parent;
        this.profile = profile.copy();
        this.bindings = bindings == null ? new EscGuiBindings() : bindings;
        this.actions = actions == null ? new LinkedHashMap<>() : new LinkedHashMap<>(actions);
        if (!this.actions.containsKey("back") && parent != null) {
            this.actions.put("back", () -> minecraft.setScreen(parent));
        }
    }

    public EscGuiProfile profile() {
        return profile;
    }

    public EscGuiBindings bindings() {
        return bindings;
    }

    @Override
    protected void buildLayout() {
        clearWidgets();
        contentBounds = contentRect();
        EscGuiRuntime.buildWidgets(this, profile, contentBounds, bindings, actions);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        renderBackground(g, mouseX, mouseY, partialTick);
        EscBackground.render(g, new EscRect(0, 0, width, height), style.theme());
        EscPanel.renderPanel(g, contentBounds, style);
        EscAssetRenderContext ctx = new EscAssetRenderContext(
            g, font, contentBounds, style, bindings, this, actions, this::invokeAction
        );
        EscGuiRuntime.renderProfile(ctx, profile, contentBounds);
        super.render(g, mouseX, mouseY, partialTick);
        EscCrtLayer.render(g, contentBounds, style.theme());
    }

    private void invokeAction(String actionId) {
        Runnable handler = actions.get(actionId);
        if (handler != null) {
            EscSound.play(EscUiCue.CONFIRM);
            handler.run();
        }
    }

    private static String modIdFromProfile(EscGuiProfile profile) {
        int colon = profile.id().indexOf(':');
        return colon > 0 ? profile.id().substring(0, colon) : "extraspecialcore";
    }
}
