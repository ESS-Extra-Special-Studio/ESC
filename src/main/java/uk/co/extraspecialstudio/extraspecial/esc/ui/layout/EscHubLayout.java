package uk.co.extraspecialstudio.extraspecial.esc.ui.layout;

import net.minecraft.client.gui.GuiGraphics;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscRect;

/**
 * Pluggable hub chrome. Implementations own local animation state.
 */
public interface EscHubLayout {

    EscHubLayoutId id();

    /** Called when this layout becomes active or the hub is rebuilt. */
    void reset(EscHubLayoutContext ctx);

    /** Advance animations (client tick). */
    void tick(EscHubLayoutContext ctx);

    void render(GuiGraphics g, EscHubLayoutContext ctx, EscRect bounds, int mouseX, int mouseY, float partialTick);

    boolean mouseClicked(EscHubLayoutContext ctx, EscRect bounds, double mouseX, double mouseY, int button);

    default boolean mouseScrolled(EscHubLayoutContext ctx, EscRect bounds, double mouseX, double mouseY, double delta) {
        return false;
    }

    default boolean keyPressed(EscHubLayoutContext ctx, int keyCode, int scanCode, int modifiers) {
        return false;
    }

    /**
     * Bounds of the detail / accordion region inside {@code bounds}.
     * Host UIs (e.g. MODS search) can place chrome here.
     */
    default EscRect detailBounds(EscHubLayoutContext ctx, EscRect bounds) {
        return bounds;
    }
}
