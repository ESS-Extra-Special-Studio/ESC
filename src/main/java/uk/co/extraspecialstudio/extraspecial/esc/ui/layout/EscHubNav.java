package uk.co.extraspecialstudio.extraspecial.esc.ui.layout;

import com.mojang.blaze3d.platform.InputConstants;
import uk.co.extraspecialstudio.extraspecial.esc.sound.EscSound;
import uk.co.extraspecialstudio.extraspecial.esc.sound.EscUiCue;

/**
 * Shared keyboard navigation for ESC hub layouts.
 * <ul>
 *   <li>Left / Right (A / D) — cycle sections</li>
 *   <li>Up / Down (W / S) — move accordion focus (no wrap; false at edges so hosts can leave to chrome)</li>
 *   <li>Enter / Space — toggle group or open leaf</li>
 * </ul>
 */
public final class EscHubNav {

    private EscHubNav() {
    }

    public static boolean keyPressed(EscHubLayoutContext ctx, int keyCode) {
        if (ctx == null || ctx.sections().isEmpty()) {
            return false;
        }
        if (keyCode == InputConstants.KEY_LEFT || keyCode == InputConstants.KEY_A) {
            int before = ctx.sectionIndex();
            ctx.cycleSection(-1);
            if (ctx.sectionIndex() != before) {
                EscSound.playCarousel(-1);
            }
            ctx.resetDetailCursor();
            return true;
        }
        if (keyCode == InputConstants.KEY_RIGHT || keyCode == InputConstants.KEY_D) {
            int before = ctx.sectionIndex();
            ctx.cycleSection(1);
            if (ctx.sectionIndex() != before) {
                EscSound.playCarousel(1);
            }
            ctx.resetDetailCursor();
            return true;
        }
        EscHubSection sel = ctx.selectedSection();
        if (sel == null) {
            return false;
        }
        if (keyCode == InputConstants.KEY_UP || keyCode == InputConstants.KEY_W) {
            if (EscAccordion.moveFocus(ctx, sel.id(), -1)) {
                EscSound.play(EscUiCue.MOVE);
                return true;
            }
            return false;
        }
        if (keyCode == InputConstants.KEY_DOWN || keyCode == InputConstants.KEY_S) {
            if (EscAccordion.moveFocus(ctx, sel.id(), 1)) {
                EscSound.play(EscUiCue.MOVE);
                return true;
            }
            return false;
        }
        if (keyCode == InputConstants.KEY_RETURN || keyCode == InputConstants.KEY_SPACE) {
            if (EscAccordion.activateFocused(ctx, sel.id())) {
                return true;
            }
            EscSound.play(EscUiCue.UNAVAILABLE);
            return true;
        }
        return false;
    }
}
