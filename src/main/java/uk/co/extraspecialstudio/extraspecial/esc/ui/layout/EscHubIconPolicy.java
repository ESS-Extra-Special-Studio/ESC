package uk.co.extraspecialstudio.extraspecial.esc.ui.layout;

/**
 * How an {@link EscHubGroup} picks its badge when no explicit icon is passed.
 * <p>
 * Product contract — do not silently change defaults without updating ESH docs.
 */
public enum EscHubIconPolicy {
    /** Use the first leaf that has an icon (legacy default). */
    INHERIT_FIRST_LEAF,
    /** No automatic icon — caller must pass one (or leave null). */
    NONE,
    /** Resolve from a studio / author badge (ESH supplies ESS badge for studio authors). */
    AUTHOR_BADGE
}
