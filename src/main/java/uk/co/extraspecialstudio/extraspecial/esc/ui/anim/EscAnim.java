package uk.co.extraspecialstudio.extraspecial.esc.ui.anim;

/**
 * Frame-driven animation helpers for ESC hubs and panels.
 */
public final class EscAnim {

    private EscAnim() {
    }

    public static float clamp01(float t) {
        if (t <= 0f) return 0f;
        if (t >= 1f) return 1f;
        return t;
    }

    public static float lerp(float a, float b, float t) {
        return a + (b - a) * clamp01(t);
    }

    public static float easeOutCubic(float t) {
        float u = 1f - clamp01(t);
        return 1f - u * u * u;
    }

    public static float easeInOutQuad(float t) {
        t = clamp01(t);
        return t < 0.5f ? 2f * t * t : 1f - (float) Math.pow(-2f * t + 2f, 2) / 2f;
    }

    /**
     * Exponential approach toward {@code target}. {@code speed} is roughly fraction closed per tick (0.15–0.35 typical).
     */
    public static float tickToward(float current, float target, float speed) {
        float s = Math.max(0.01f, Math.min(1f, speed));
        return current + (target - current) * s;
    }

    /** Mutable float driven toward a target each tick. */
    public static final class FloatAnim {
        private float value;
        private float target;
        private float speed;

        public FloatAnim(float initial, float speed) {
            this.value = initial;
            this.target = initial;
            this.speed = Math.max(0.01f, speed);
        }

        public float value() {
            return value;
        }

        public float target() {
            return target;
        }

        public void setTarget(float target) {
            this.target = target;
        }

        public void snap(float v) {
            this.value = v;
            this.target = v;
        }

        public void setSpeed(float speed) {
            this.speed = Math.max(0.01f, speed);
        }

        public void tick() {
            value = tickToward(value, target, speed);
        }

        public boolean near(float epsilon) {
            return Math.abs(value - target) <= epsilon;
        }
    }
}
