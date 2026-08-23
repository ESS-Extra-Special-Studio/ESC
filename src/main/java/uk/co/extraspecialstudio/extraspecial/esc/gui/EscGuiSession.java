package uk.co.extraspecialstudio.extraspecial.esc.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import uk.co.extraspecialstudio.extraspecial.esc.gui.profile.EscGuiBindings;
import uk.co.extraspecialstudio.extraspecial.esc.gui.profile.EscGuiProfile;
import uk.co.extraspecialstudio.extraspecial.esc.gui.profile.EscGuiProfileStore;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Fluent builder for opening a declarative {@link EscGuiScreen}.
 */
public final class EscGuiSession {
    private final Screen parent;
    private final String profileId;
    private EscGuiProfile profileOverride;
    private final EscGuiBindings bindings = new EscGuiBindings();
    private final Map<String, Runnable> actions = new LinkedHashMap<>();
    private String applicationId;

    EscGuiSession(Screen parent, String profileId) {
        this.parent = parent;
        this.profileId = profileId;
    }

    public EscGuiSession profile(EscGuiProfile profile) {
        this.profileOverride = profile == null ? null : profile.copy();
        return this;
    }

    public EscGuiSession bind(String key, String value) {
        bindings.bind(key, value);
        return this;
    }

    public EscGuiSession action(String id, Runnable handler) {
        if (id != null && !id.isBlank() && handler != null) {
            actions.put(id, handler);
        }
        return this;
    }

    public EscGuiSession applicationId(String applicationId) {
        this.applicationId = applicationId;
        return this;
    }

    public EscGuiProfile resolveProfile() {
        if (profileOverride != null) {
            return profileOverride.copy();
        }
        return EscGuiProfileStore.load(profileId).orElseGet(() -> {
            EscGuiProfile blank = EscGuiProfileStore.blank(profileId);
            blank.setTitle(profileId);
            return blank;
        });
    }

    public void open() {
        EscGuiProfile profile = resolveProfile();
        Minecraft.getInstance().setScreen(new EscGuiScreen(parent, profile, bindings, actions, applicationId));
    }

    public EscGuiScreen createScreen() {
        return new EscGuiScreen(parent, resolveProfile(), bindings, actions, applicationId);
    }

    Map<String, Runnable> actions() {
        return actions;
    }

    EscGuiBindings bindings() {
        return bindings;
    }

    Screen parent() {
        return parent;
    }
}
