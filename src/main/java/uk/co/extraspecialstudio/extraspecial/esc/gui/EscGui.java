package uk.co.extraspecialstudio.extraspecial.esc.gui;

import net.minecraft.client.gui.screens.Screen;

/**
 * Public façade for opening declarative ESC screens from saved profiles.
 */
public final class EscGui {
    private EscGui() {
    }

    public static EscGuiSession open(Screen parent, String profileId) {
        return new EscGuiSession(parent, profileId);
    }

    public static EscGuiSession load(String profileId) {
        return open(null, profileId);
    }
}
