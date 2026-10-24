package uk.co.extraspecialstudio.extraspecial.esc.theme;

/**
 * Client visual quality budget for expensive ESC effects.
 */
public enum EscQualityMode {
    OFF,
    LOW,
    MEDIUM,
    HIGH;

    public boolean shadows() {
        return this == MEDIUM || this == HIGH;
    }

    public boolean glow() {
        return this == HIGH || this == MEDIUM;
    }

    public boolean crt() {
        return this != OFF && this != LOW;
    }

    public boolean animatedBorders() {
        return this == MEDIUM || this == HIGH;
    }

    public boolean dynamicBackground() {
        return this == HIGH;
    }

    public float effectScale() {
                switch (this) {
            case OFF:
                {
                    return 0f;
                }
            case LOW:
                {
                    return 0.35f;
                }
            case MEDIUM:
                {
                    return 0.7f;
                }
            case HIGH:
                {
                    return 1f;
                }
        }
        throw new IllegalStateException();
    }

    public EscQualityMode next() {
        EscQualityMode[] all = values();
        return all[(ordinal() + 1) % all.length];
    }

    public static EscQualityMode byName(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return MEDIUM;
        }
        try {
            return valueOf(raw.trim().toUpperCase());
        } catch (Exception e) {
            return MEDIUM;
        }
    }
}
