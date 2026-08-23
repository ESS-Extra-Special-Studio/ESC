package uk.co.extraspecialstudio.extraspecial.esc.ui;

/**
 * Marker for a screen that already provides its own way back to whatever opened it
 * (a footer Back / Cancel that returns to {@code parent}).
 * <p>
 * ES Hub injects a {@code ← Hub} button top-left of every screen opened from the hub.
 * On screens that draw their own chrome that button has nowhere safe to sit and lands on
 * the title bar, so ESH skips injection for anything implementing this.
 * <p>
 * Lives in ESC rather than ESH so ESC's own screens can opt out without depending on the
 * hub — ESH's {@code EshOwnsHubNav} extends this, and either is honoured.
 */
public interface EscOwnsBackNav {
}
