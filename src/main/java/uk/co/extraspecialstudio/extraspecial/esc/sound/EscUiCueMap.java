package uk.co.extraspecialstudio.extraspecial.esc.sound;

import java.util.EnumMap;
import java.util.Map;

/**
 * Optional mapping from semantic {@link EscUiCue} → Menu Sound 1–20.
 * Unbound cues stay silent (no missing-file spam) until you assign them.
 */
public final class EscUiCueMap {
    private static final EnumMap<EscUiCue, Integer> TO_MENU = new EnumMap<>(EscUiCue.class);

    private EscUiCueMap() {
    }

    /** @param menuIndex 1–{@link EscSounds#MENU_BANK_SIZE}, or 0 to clear */
    public static void bind(EscUiCue cue, int menuIndex) {
        if (cue == null) {
            return;
        }
        if (menuIndex <= 0) {
            TO_MENU.remove(cue);
            return;
        }
        if (menuIndex > EscSounds.MENU_BANK_SIZE) {
            throw new IllegalArgumentException("Menu Sound index 1–" + EscSounds.MENU_BANK_SIZE);
        }
        TO_MENU.put(cue, menuIndex);
    }

    public static void clear(EscUiCue cue) {
        if (cue != null) {
            TO_MENU.remove(cue);
        }
    }

    public static void clearAll() {
        TO_MENU.clear();
    }

    /** @return 1–20 if bound, else 0 */
    public static int menuIndex(EscUiCue cue) {
        if (cue == null) {
            return 0;
        }
        return TO_MENU.getOrDefault(cue, 0);
    }

    public static boolean isBound(EscUiCue cue) {
        return menuIndex(cue) > 0;
    }

    public static Map<EscUiCue, Integer> snapshot() {
        return Map.copyOf(TO_MENU);
    }
}
