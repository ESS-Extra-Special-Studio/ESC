package uk.co.extraspecialstudio.extraspecial.esc.gui.profile;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Runtime bind map for profile placeholders such as {@code {bind:title}}.
 */
public final class EscGuiBindings {
    private final Map<String, String> values = new LinkedHashMap<>();

    public EscGuiBindings bind(String key, String value) {
        if (key != null && !key.isBlank()) {
            values.put(key, value == null ? "" : value);
        }
        return this;
    }

    public Map<String, String> asMap() {
        return Map.copyOf(values);
    }

    public String get(String key) {
        return values.getOrDefault(key, "");
    }

    public String resolve(String raw) {
        if (raw == null) {
            return "";
        }
        String text = raw;
        if (text.startsWith("{bind:") && text.endsWith("}")) {
            String key = text.substring(6, text.length() - 1);
            return values.getOrDefault(key, text);
        }
        int start = text.indexOf("{bind:");
        while (start >= 0) {
            int end = text.indexOf('}', start);
            if (end < 0) {
                break;
            }
            String key = text.substring(start + 6, end);
            String replacement = values.getOrDefault(key, "{" + key + "}");
            text = text.substring(0, start) + replacement + text.substring(end + 1);
            start = text.indexOf("{bind:", start + replacement.length());
        }
        return text;
    }
}
