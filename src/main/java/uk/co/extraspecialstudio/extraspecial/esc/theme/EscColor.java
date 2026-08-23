package uk.co.extraspecialstudio.extraspecial.esc.theme;

/**
 * ARGB colour helpers for ESC theming and the colour picker.
 */
public final class EscColor {

    private EscColor() {
    }

    public static int opaque(int rgb) {
        return 0xFF000000 | (rgb & 0xFFFFFF);
    }

    public static int withAlpha(int rgb, int alpha) {
        return ((alpha & 0xFF) << 24) | (rgb & 0xFFFFFF);
    }

    public static int alpha(int argb) {
        return (argb >>> 24) & 0xFF;
    }

    public static int red(int argb) {
        return (argb >>> 16) & 0xFF;
    }

    public static int green(int argb) {
        return (argb >>> 8) & 0xFF;
    }

    public static int blue(int argb) {
        return argb & 0xFF;
    }

    public static int rgb(int argb) {
        return argb & 0xFFFFFF;
    }

    public static String toHexRgb(int argb) {
        return String.format("%06X", rgb(argb));
    }

    public static String toHexArgb(int argb) {
        return String.format("%08X", argb);
    }

    public static Integer parseHex(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        String s = raw.trim();
        if (s.startsWith("#")) {
            s = s.substring(1);
        }
        if (s.regionMatches(true, 0, "0x", 0, 2)) {
            s = s.substring(2);
        }
        try {
            if (s.length() == 6 || s.length() == 8) {
                long v = Long.parseLong(s, 16);
                if (s.length() == 6) {
                    return opaque((int) v);
                }
                return (int) v;
            }
        } catch (NumberFormatException ignored) {
        }
        return null;
    }

    public static int dim(int rgb) {
        int r = ((rgb >> 16) & 0xFF) * 5 / 6;
        int g = ((rgb >> 8) & 0xFF) * 5 / 6;
        int b = (rgb & 0xFF) * 5 / 6;
        return (r << 16) | (g << 8) | b;
    }

    public static int darken(int rgb, float factor) {
        float f = Math.max(0f, Math.min(1f, factor));
        int r = Math.round(((rgb >> 16) & 0xFF) * f);
        int g = Math.round(((rgb >> 8) & 0xFF) * f);
        int b = Math.round((rgb & 0xFF) * f);
        return (clampByte(r) << 16) | (clampByte(g) << 8) | clampByte(b);
    }

    public static float[] toHsv(int argb) {
        float r = red(argb) / 255f;
        float g = green(argb) / 255f;
        float b = blue(argb) / 255f;
        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));
        float d = max - min;
        float h;
        if (d < 1e-6f) {
            h = 0f;
        } else if (max == r) {
            h = 60f * (((g - b) / d) % 6f);
        } else if (max == g) {
            h = 60f * (((b - r) / d) + 2f);
        } else {
            h = 60f * (((r - g) / d) + 4f);
        }
        if (h < 0f) {
            h += 360f;
        }
        float s = max <= 1e-6f ? 0f : d / max;
        return new float[]{h, s, max};
    }

    public static int fromHsv(float h, float s, float v, int alpha) {
        h = ((h % 360f) + 360f) % 360f;
        s = clamp01(s);
        v = clamp01(v);
        float c = v * s;
        float x = c * (1f - Math.abs((h / 60f) % 2f - 1f));
        float m = v - c;
        float r1;
        float g1;
        float b1;
        if (h < 60f) {
            r1 = c;
            g1 = x;
            b1 = 0f;
        } else if (h < 120f) {
            r1 = x;
            g1 = c;
            b1 = 0f;
        } else if (h < 180f) {
            r1 = 0f;
            g1 = c;
            b1 = x;
        } else if (h < 240f) {
            r1 = 0f;
            g1 = x;
            b1 = c;
        } else if (h < 300f) {
            r1 = x;
            g1 = 0f;
            b1 = c;
        } else {
            r1 = c;
            g1 = 0f;
            b1 = x;
        }
        int r = clampByte(Math.round((r1 + m) * 255f));
        int g = clampByte(Math.round((g1 + m) * 255f));
        int b = clampByte(Math.round((b1 + m) * 255f));
        return withAlpha((r << 16) | (g << 8) | b, alpha);
    }

    public static int distanceRgb(int a, int b) {
        int dr = red(a) - red(b);
        int dg = green(a) - green(b);
        int db = blue(a) - blue(b);
        return dr * dr + dg * dg + db * db;
    }

    public static EscThemeSwatch nearestSwatch(int argb) {
        EscThemeSwatch best = EscThemeSwatch.WHITE;
        int bestDist = Integer.MAX_VALUE;
        for (EscThemeSwatch sw : EscThemeSwatch.values()) {
            int d = Math.min(
                distanceRgb(argb, sw.borderArgb()),
                Math.min(distanceRgb(argb, opaque(sw.titleRgb())), distanceRgb(argb, sw.accentArgb()))
            );
            if (d < bestDist) {
                bestDist = d;
                best = sw;
            }
        }
        return best;
    }

    private static float clamp01(float v) {
        if (v <= 0f) {
            return 0f;
        }
        if (v >= 1f) {
            return 1f;
        }
        return v;
    }

    private static int clampByte(int v) {
        if (v < 0) {
            return 0;
        }
        if (v > 255) {
            return 255;
        }
        return v;
    }
}
