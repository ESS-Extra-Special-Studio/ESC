package uk.co.extraspecialstudio.extraspecial.esc.gui.asset;

import uk.co.extraspecialstudio.esl.registry.EslRegistry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Global registry of {@link EscAssetDef} entries for ESC GUI runtime and optional ESG authoring.
 */
public final class EscAssetRegistry {
    private static final EslRegistry<EscAssetDef> REGISTRY = new EslRegistry<>();
    private static boolean seeded;

    private EscAssetRegistry() {
    }

    public static void register(EscAssetDef def) {
        REGISTRY.registerOrReplace(def.id(), def);
    }

    public static Optional<EscAssetDef> get(String id) {
        return REGISTRY.get(id);
    }

    public static boolean contains(String id) {
        return REGISTRY.contains(id);
    }

    public static List<EscAssetDef> all() {
        List<EscAssetDef> out = new ArrayList<>(REGISTRY.values());
        out.sort(Comparator.comparing(EscAssetDef::id));
        return out;
    }

    public static List<EscAssetDef> byCategory(EscAssetCategory category) {
        return all().stream().filter(d -> d.category() == category).toList();
    }

    public static void clear() {
        REGISTRY.clear();
        seeded = false;
    }

    public static boolean isSeeded() {
        return seeded;
    }

    public static void markSeeded() {
        seeded = true;
    }
}
