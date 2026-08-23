package uk.co.extraspecialstudio.extraspecial.esc.mod;

import uk.co.extraspecialstudio.esl.mod.EslMods;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * @deprecated Prefer {@link EslMods}. Thin delegate kept for one release of ESC consumers.
 */
@Deprecated
public final class EscMods {

    private EscMods() {
    }

    public static boolean isLoaded(String modId) {
        return EslMods.isLoaded(modId);
    }

    public static Optional<String> getDisplayName(String modId) {
        return EslMods.getDisplayName(modId);
    }

    public static void runIfLoaded(String modId, Runnable action) {
        EslMods.runIfLoaded(modId, action);
    }

    public static <T> void acceptIfLoaded(String modId, Consumer<T> consumer, T value) {
        EslMods.acceptIfLoaded(modId, consumer, value);
    }
}
