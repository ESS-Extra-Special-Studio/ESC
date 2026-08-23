package uk.co.extraspecialstudio.extraspecial.esc.sound;

import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * Semantic UI cues for ESC / ESH. Maps to {@link EscSounds} with default volume/pitch.
 */
public enum EscUiCue {
    FOCUS(EscSounds.FOCUS, 0.55f, 1f, 45),
    FOCUS_ALT(EscSounds.FOCUS_ALT, 0.5f, 1.05f, 45),
    SELECT(EscSounds.SELECT, 0.6f, 1f, 40),
    CONFIRM(EscSounds.CONFIRM, 0.7f, 1f, 80),
    BACK(EscSounds.BACK, 0.55f, 1f, 80),
    CLOSE(EscSounds.CLOSE, 0.6f, 1f, 100),
    CAROUSEL_LEFT(EscSounds.CAROUSEL_LEFT, 0.55f, 1f, 50),
    CAROUSEL_RIGHT(EscSounds.CAROUSEL_RIGHT, 0.55f, 1f, 50),
    MOVE(EscSounds.MOVE, 0.45f, 1f, 35),
    EXPAND(EscSounds.EXPAND, 0.55f, 1f, 70),
    COLLAPSE(EscSounds.COLLAPSE, 0.5f, 0.95f, 70),
    LAYOUT_SWITCH(EscSounds.LAYOUT_SWITCH, 0.65f, 1f, 120),
    LAYOUT_CONFIRM(EscSounds.LAYOUT_CONFIRM, 0.65f, 1f, 120),
    ERROR(EscSounds.ERROR, 0.7f, 0.9f, 150),
    UNAVAILABLE(EscSounds.UNAVAILABLE, 0.5f, 0.85f, 120),
    NOTIFICATION(EscSounds.NOTIFICATION, 0.6f, 1f, 100);

    private final DeferredHolder<SoundEvent, SoundEvent> event;
    private final float volume;
    private final float pitch;
    private final int cooldownMs;

    EscUiCue(DeferredHolder<SoundEvent, SoundEvent> event, float volume, float pitch, int cooldownMs) {
        this.event = event;
        this.volume = volume;
        this.pitch = pitch;
        this.cooldownMs = cooldownMs;
    }

    public DeferredHolder<SoundEvent, SoundEvent> event() {
        return event;
    }

    public float volume() {
        return volume;
    }

    public float pitch() {
        return pitch;
    }

    public int cooldownMs() {
        return cooldownMs;
    }

    public SoundEvent sound() {
        return event.get();
    }
}
