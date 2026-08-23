package uk.co.extraspecialstudio.extraspecial.esc.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import uk.co.extraspecialstudio.extraspecial.esc.theme.EscThemeConfigs;

import java.util.EnumMap;
import java.util.Map;

/**
 * Client UI SFX player — mute/volume from theme client config, per-cue anti-spam.
 * Safe to call from common code (no-ops on dedicated server).
 * <p>
 * Semantic {@link EscUiCue}s play only when bound to a Menu Sound via {@link EscUiCueMap}.
 * Use {@link #playMenu(int)} to audition the bank directly.
 */
public final class EscSound {
    private static final Map<EscUiCue, Long> LAST_PLAY = new EnumMap<>(EscUiCue.class);
    private static long lastMenuPlayMs;
    private static int lastMenuIndex;
    private static int focusAltToggle;

    private EscSound() {
    }

    public static void play(EscUiCue cue) {
        if (cue == null || !FMLEnvironment.dist.isClient()) {
            return;
        }
        int menu = EscUiCueMap.menuIndex(cue);
        if (menu > 0) {
            if (!allowCue(cue)) {
                return;
            }
            playMenuInternal(menu, cue.volume(), cue.pitch());
            return;
        }
        // Unbound: silent until assignment (avoids missing ui/*.ogg spam).
    }

    public static void play(EscUiCue cue, float volumeMul, float pitchMul) {
        if (cue == null || !FMLEnvironment.dist.isClient()) {
            return;
        }
        int menu = EscUiCueMap.menuIndex(cue);
        if (menu > 0) {
            if (!allowCue(cue)) {
                return;
            }
            playMenuInternal(menu, cue.volume() * volumeMul, cue.pitch() * pitchMul);
        }
    }

    /** Play Menu Sound 1–{@link EscSounds#MENU_BANK_SIZE} (audition / direct use). */
    public static void playMenu(int index) {
        if (!FMLEnvironment.dist.isClient()) {
            return;
        }
        playMenuInternal(index, 0.7f, 1f);
    }

    public static void playMenu(int index, float volume, float pitch) {
        if (!FMLEnvironment.dist.isClient()) {
            return;
        }
        playMenuInternal(index, volume, pitch);
    }

    /** Alternates focus cues when bound; otherwise silent. */
    public static void playFocus() {
        if (!FMLEnvironment.dist.isClient()) {
            return;
        }
        focusAltToggle++;
        play(focusAltToggle % 2 == 0 ? EscUiCue.FOCUS_ALT : EscUiCue.FOCUS);
    }

    public static void playCarousel(int delta) {
        if (delta < 0) {
            play(EscUiCue.CAROUSEL_LEFT);
        } else if (delta > 0) {
            play(EscUiCue.CAROUSEL_RIGHT);
        }
    }

    private static boolean allowCue(EscUiCue cue) {
        if (!EscThemeConfigs.uiSoundsEnabled()) {
            return false;
        }
        long now = System.currentTimeMillis();
        Long last = LAST_PLAY.get(cue);
        if (last != null && now - last < cue.cooldownMs()) {
            return false;
        }
        LAST_PLAY.put(cue, now);
        return true;
    }

    private static void playMenuInternal(int index, float volume, float pitch) {
        if (!EscThemeConfigs.uiSoundsEnabled()) {
            return;
        }
        if (index < 1 || index > EscSounds.MENU_BANK_SIZE) {
            return;
        }
        long now = System.currentTimeMillis();
        if (index == lastMenuIndex && now - lastMenuPlayMs < 40) {
            return;
        }
        SoundEvent event;
        try {
            event = EscSounds.menu(index).get();
        } catch (Exception e) {
            return;
        }
        if (event == null) {
            return;
        }
        float master = EscThemeConfigs.uiSoundVolume();
        float v = Math.max(0f, Math.min(1f, volume * master));
        if (v <= 0.001f) {
            return;
        }
        float p = Math.max(0.5f, Math.min(2f, pitch));
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.getSoundManager() == null) {
            return;
        }
        mc.getSoundManager().play(SimpleSoundInstance.forUI(event, p, v));
        lastMenuPlayMs = now;
        lastMenuIndex = index;
    }
}
