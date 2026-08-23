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
        return switch (this) {
            case OFF -> 0f;
            case LOW -> 0.35f;
            case MEDIUM -> 0.7f;
            case HIGH -> 1f;
        };
    }

    public EscQualityMode next() {
        EscQualityMode[] all = values();
        return all[(ordinal() + 1) % all.length];
    }

    public static EscQualityMode byName(String raw) {
        if (raw == null || raw.isBlank()) {
            return MEDIUM;
        }
        try {
            return valueOf(raw.trim().toUpperCase());
        } catch (Exception e) {
            return MEDIUM;
        }
    }
}
