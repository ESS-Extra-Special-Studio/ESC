package uk.co.extraspecialstudio.extraspecial.esc.ui.anim;

/**
 * Tracks an open transition from 0→1.
 */
public final class EscTransitionDriver {
    private final EscAnim.FloatAnim progress;
    private EscTransition transition = EscTransition.FADE;

    public EscTransitionDriver() {
        this.progress = new EscAnim.FloatAnim(0f, 0.22f);
        this.progress.setTarget(1f);
    }

    public void restart(EscTransition transition) {
        this.transition = transition == null ? EscTransition.FADE : transition;
        progress.snap(0f);
        progress.setTarget(1f);
    }

    public void tick() {
        progress.tick();
    }

    public EscTransition transition() {
        return transition;
    }

    public float value() {
        return progress.value();
    }

    public float alpha() {
        return transition.applyAlpha(progress.value());
    }

    public float scale() {
        return transition.applyScale(progress.value());
    }

    public int offsetX(int width) {
        return transition.applyOffsetX(progress.value(), width);
    }

    public int offsetY(int height) {
        return transition.applyOffsetY(progress.value(), height);
    }
}
