package uk.co.extraspecialstudio.extraspecial.esc.ui.layout;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Factory for hub layout profiles.
 */
public final class EscHubLayouts {

    private static final Map<EscHubLayoutId, Supplier<EscHubLayout>> FACTORIES = new EnumMap<>(EscHubLayoutId.class);

    static {
        FACTORIES.put(EscHubLayoutId.CAROUSEL, EscCarouselLayout::new);
        FACTORIES.put(EscHubLayoutId.CONTROL_CENTRE, EscControlCentreLayout::new);
        FACTORIES.put(EscHubLayoutId.DASHBOARD, EscDashboardLayout::new);
        FACTORIES.put(EscHubLayoutId.ORBITAL, EscOrbitalLayout::new);
        FACTORIES.put(EscHubLayoutId.CLASSIC, EscClassicLayout::new);
    }

    private EscHubLayouts() {
    }

    public static EscHubLayout create(EscHubLayoutId id) {
        Supplier<EscHubLayout> s = FACTORIES.get(id == null ? EscHubLayoutId.CAROUSEL : id);
        if (s == null) {
            return new EscCarouselLayout();
        }
        return s.get();
    }
}
