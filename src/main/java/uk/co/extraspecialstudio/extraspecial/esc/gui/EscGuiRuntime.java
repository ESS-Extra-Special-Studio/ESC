package uk.co.extraspecialstudio.extraspecial.esc.gui;

import uk.co.extraspecialstudio.extraspecial.esc.gui.asset.EscAssetCategory;
import uk.co.extraspecialstudio.extraspecial.esc.gui.asset.EscAssetDef;
import uk.co.extraspecialstudio.extraspecial.esc.gui.asset.EscAssetInstance;
import uk.co.extraspecialstudio.extraspecial.esc.gui.asset.EscAssetPropDef;
import uk.co.extraspecialstudio.extraspecial.esc.gui.asset.EscAssetRegistry;
import uk.co.extraspecialstudio.extraspecial.esc.gui.asset.EscAssetRenderContext;
import uk.co.extraspecialstudio.extraspecial.esc.gui.asset.EscAssetSlot;
import uk.co.extraspecialstudio.extraspecial.esc.gui.asset.EscAssetType;
import uk.co.extraspecialstudio.extraspecial.esc.gui.profile.EscGuiBindings;
import uk.co.extraspecialstudio.extraspecial.esc.gui.profile.EscGuiProfile;
import uk.co.extraspecialstudio.extraspecial.esc.theme.EscFrameStyle;
import uk.co.extraspecialstudio.extraspecial.esc.theme.EscThemePreset;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscButtonBar;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscCard;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscInsets;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscPanel;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscRect;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscScreen;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscSearchBox;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscText;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscTypeRole;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscTypography;
import uk.co.extraspecialstudio.extraspecial.esc.ui.EscUiStyle;
import uk.co.extraspecialstudio.extraspecial.esc.ui.layout.EscHubLayoutId;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Resolves profile slots and renders / wires registered ESC assets.
 */
public final class EscGuiRuntime {
    private EscGuiRuntime() {
    }

    public static Map<EscAssetSlot, EscRect> layoutSlots(EscRect content, EscGuiProfile profile, EscUiStyle style) {
        Map<EscAssetSlot, EscRect> slots = new EnumMap<>(EscAssetSlot.class);
        EscRect title = EscPanel.titleBar(content, style);
        EscRect footer = EscPanel.footer(content, style);
        EscRect body = EscPanel.bodyBelowTitle(content, style);
        body = new EscRect(body.x(), body.y(), body.width(), Math.max(0, footer.y() - body.y() - 4));

        switch (profile.layout()) {
            case FULL -> slots.put(EscAssetSlot.FULL, content);
            case SIMPLE -> {
                slots.put(EscAssetSlot.BODY, body);
                slots.put(EscAssetSlot.FOOTER, footer);
            }
            default -> {
                slots.put(EscAssetSlot.TITLE, title);
                slots.put(EscAssetSlot.HEADER, title);
                slots.put(EscAssetSlot.BODY, body);
                slots.put(EscAssetSlot.FOOTER, footer);
                int sidebarW = Math.min(120, Math.max(80, content.width() / 4));
                EscRect sidebar = new EscRect(content.x(), body.y(), sidebarW, body.height());
                EscRect detail = new EscRect(sidebar.right() + 4, body.y(), content.right() - sidebar.right() - 4, body.height());
                slots.put(EscAssetSlot.SIDEBAR, sidebar);
                slots.put(EscAssetSlot.DETAIL, detail);
                slots.put(EscAssetSlot.NAV, sidebar);
                slots.put(EscAssetSlot.BACKDROP, content);
            }
        }
        return slots;
    }

    public static void renderProfile(
        EscAssetRenderContext ctx,
        EscGuiProfile profile,
        EscRect content
    ) {
        Map<EscAssetSlot, EscRect> slots = layoutSlots(content, profile, ctx.style());
        for (EscAssetInstance instance : profile.components()) {
            EscAssetRegistry.get(instance.assetId()).ifPresent(def -> {
                if (def.interactive()) {
                    return;
                }
                EscRect area = slots.getOrDefault(instance.slot(), slots.get(EscAssetSlot.BODY));
                if (area == null || area.width() <= 0 || area.height() <= 0) {
                    return;
                }
                EscAssetRenderContext local = new EscAssetRenderContext(
                    ctx.graphics(),
                    ctx.font(),
                    area,
                    ctx.style(),
                    ctx.bindings(),
                    ctx.screen(),
                    ctx.actions(),
                    ctx::invokeAction
                );
                if (def.renderer() != null) {
                    def.renderer().render(local, instance);
                }
            });
        }
    }

    public static void buildWidgets(
        EscScreen screen,
        EscGuiProfile profile,
        EscRect content,
        EscGuiBindings bindings,
        Map<String, Runnable> actions
    ) {
        Map<EscAssetSlot, EscRect> slots = layoutSlots(content, profile, screen.uiStyle());
        for (EscAssetInstance instance : profile.components()) {
            EscAssetRegistry.get(instance.assetId()).ifPresent(def -> {
                if (!def.interactive()) {
                    return;
                }
                EscRect area = slots.getOrDefault(instance.slot(), slots.get(EscAssetSlot.BODY));
                if (area == null) {
                    return;
                }
                switch (def.type()) {
                    case BUTTON_BAR -> buildButtonBar(screen, area, instance, profile, bindings, actions);
                    case SEARCH_BAR -> buildSearchBar(screen, area, instance, bindings);
                    default -> {
                    }
                }
            });
        }
    }

    private static void buildButtonBar(
        EscScreen screen,
        EscRect area,
        EscAssetInstance instance,
        EscGuiProfile profile,
        EscGuiBindings bindings,
        Map<String, Runnable> actions
    ) {
        List<String> buttons = parseCsv(bindings.resolve(instance.prop("buttons")));
        if (buttons.isEmpty()) {
            buttons = List.of("back");
        }
        int y = area.y() + EscButtonBar.DEFAULT_PAD;
        EscRect[] row = EscButtonBar.equalRow(area, buttons.size(), y, EscButtonBar.DEFAULT_HEIGHT);
        for (int i = 0; i < buttons.size(); i++) {
            String id = buttons.get(i);
            String label = capitalize(id);
            String mapped = profile.actions().getOrDefault(id, "action:" + id);
            String actionId = mapped.startsWith("action:") ? mapped.substring(7) : mapped;
            EscRect slot = row[i];
            screen.escButton(EscText.literal(label), slot, b -> {
                Runnable handler = actions.get(actionId);
                if (handler != null) {
                    handler.run();
                }
            });
        }
    }

    private static void buildSearchBar(
        EscScreen screen,
        EscRect area,
        EscAssetInstance instance,
        EscGuiBindings bindings
    ) {
        String hint = bindings.resolve(instance.prop("hint"));
        EscSearchBox box = screen.escSearchBox(area, EscText.literal(bindings.resolve(instance.prop("label"))));
        if (!hint.isBlank()) {
            box.setHint(EscText.literal(hint));
        }
    }

    public static void registerBuiltins() {
        if (EscAssetRegistry.isSeeded()) {
            return;
        }

        registerCore();
        registerNavigation();
        registerEffects();
        registerEsh();
        EscAssetRegistry.markSeeded();
    }

    private static void registerCore() {
        EscAssetRegistry.register(EscAssetDef.builder("esc:header", EscAssetType.HEADER, EscAssetCategory.CORE)
            .displayName("Header")
            .description("Title band text")
            .prop(EscAssetPropDef.string("title", "{bind:title}", "Title"))
            .prop(EscAssetPropDef.string("subtitle", "", "Subtitle"))
            .renderer((ctx, inst) -> {
                String title = ctx.resolve(inst.prop("title"));
                String sub = ctx.resolve(inst.prop("subtitle"));
                EscTypography.draw(ctx.graphics(), ctx.font(), title, ctx.area().x() + 8, ctx.area().y() + 6,
                    ctx.style(), EscTypeRole.TITLE, ctx.area().width() - 16, 1f);
                if (!sub.isBlank()) {
                    EscTypography.draw(ctx.graphics(), ctx.font(), sub, ctx.area().x() + 8, ctx.area().y() + 20,
                        ctx.style(), EscTypeRole.META, ctx.area().width() - 16, 1f);
                }
            })
            .build());

        EscAssetRegistry.register(EscAssetDef.builder("esc:footer", EscAssetType.FOOTER, EscAssetCategory.CORE)
            .displayName("Footer")
            .description("Footer hint line")
            .prop(EscAssetPropDef.string("hint", "", "Hint"))
            .renderer((ctx, inst) -> {
                String hint = ctx.resolve(inst.prop("hint"));
                if (!hint.isBlank()) {
                    EscTypography.draw(ctx.graphics(), ctx.font(), hint, ctx.area().x() + 8, ctx.area().y() + 4,
                        ctx.style(), EscTypeRole.META, ctx.area().width() - 16, 1f);
                }
            })
            .build());

        EscAssetRegistry.register(EscAssetDef.builder("esc:panel", EscAssetType.PANEL, EscAssetCategory.CORE)
            .displayName("Panel")
            .description("Framed ESC panel")
            .prop(EscAssetPropDef.anEnum("frame", "GLASS", frameValues(), "Frame"))
            .renderer((ctx, inst) -> EscPanel.renderPanel(ctx.graphics(), ctx.area(), ctx.style()))
            .build());

        EscAssetRegistry.register(EscAssetDef.builder("esc:card", EscAssetType.CARD, EscAssetCategory.CORE)
            .displayName("Card")
            .description("Titled hub card")
            .prop(EscAssetPropDef.string("title", "{bind:title}", "Title"))
            .prop(EscAssetPropDef.string("subtitle", "", "Subtitle"))
            .prop(EscAssetPropDef.string("badge", "", "Badge"))
            .renderer((ctx, inst) -> EscCard.render(
                ctx.graphics(), ctx.font(), ctx.area(), ctx.style(),
                ctx.resolve(inst.prop("title")),
                ctx.resolve(inst.prop("subtitle")),
                ctx.resolve(inst.prop("badge")),
                0f
            ))
            .build());

        EscAssetRegistry.register(EscAssetDef.builder("esc:button", EscAssetType.BUTTON, EscAssetCategory.CORE)
            .displayName("Button")
            .description("Single action button (visual only in builder preview)")
            .prop(EscAssetPropDef.string("label", "Action", "Label"))
            .prop(EscAssetPropDef.string("action", "action:click", "Action id"))
            .renderer((ctx, inst) -> {
                EscRect b = ctx.area().inset(EscInsets.of(4));
                EscPanel.renderPanel(ctx.graphics(), b, ctx.style());
                EscTypography.draw(ctx.graphics(), ctx.font(), ctx.resolve(inst.prop("label")),
                    b.x() + 8, b.y() + (b.height() - ctx.font().lineHeight) / 2,
                    ctx.style(), EscTypeRole.BODY, b.width() - 16, 1f);
            })
            .build());

        EscAssetRegistry.register(EscAssetDef.builder("esc:button_bar", EscAssetType.BUTTON_BAR, EscAssetCategory.CORE)
            .displayName("Button bar")
            .description("Footer action row")
            .prop(EscAssetPropDef.stringList("buttons", "apply,reset,back", "Button ids"))
            .interactive(true)
            .build());

        EscAssetRegistry.register(EscAssetDef.builder("esc:search_bar", EscAssetType.SEARCH_BAR, EscAssetCategory.CORE)
            .displayName("Search bar")
            .description("Filter input")
            .prop(EscAssetPropDef.string("label", "Search", "Label"))
            .prop(EscAssetPropDef.string("hint", "Type to filter…", "Hint"))
            .interactive(true)
            .build());

        EscAssetRegistry.register(EscAssetDef.builder("esc:status", EscAssetType.STATUS, EscAssetCategory.CORE)
            .displayName("Status")
            .description("Single status line")
            .prop(EscAssetPropDef.string("text", "{bind:status}", "Text"))
            .renderer((ctx, inst) -> EscTypography.draw(ctx.graphics(), ctx.font(), ctx.resolve(inst.prop("text")),
                ctx.area().x() + 8, ctx.area().y() + 8, ctx.style(), EscTypeRole.BODY, ctx.area().width() - 16, 1f))
            .build());

        EscAssetRegistry.register(EscAssetDef.builder("esc:doc_panel", EscAssetType.DOC_PANEL, EscAssetCategory.CORE)
            .displayName("Doc panel")
            .description("Wrapped documentation text")
            .prop(EscAssetPropDef.string("text", "{bind:doc}", "Body"))
            .renderer((ctx, inst) -> {
                EscPanel.renderPanel(ctx.graphics(), ctx.area(), ctx.style());
                EscText.drawWrapped(ctx.graphics(), ctx.font(), ctx.resolve(inst.prop("text")),
                    ctx.area().x() + 10, ctx.area().y() + 10, ctx.area().width() - 20, ctx.style().textColorBody());
            })
            .build());
    }

    private static void registerNavigation() {
        EscAssetRegistry.register(EscAssetDef.builder("esc:accordion", EscAssetType.ACCORDION, EscAssetCategory.NAVIGATION)
            .displayName("Accordion")
            .description("Nested nav list (preview)")
            .prop(EscAssetPropDef.string("sectionId", "{bind:section}", "Section label"))
            .renderer((ctx, inst) -> {
                EscPanel.renderPanel(ctx.graphics(), ctx.area(), ctx.style());
                EscTypography.draw(ctx.graphics(), ctx.font(), "▶ " + ctx.resolve(inst.prop("sectionId")),
                    ctx.area().x() + 10, ctx.area().y() + 10, ctx.style(), EscTypeRole.TITLE, ctx.area().width() - 20, 1f);
                EscTypography.draw(ctx.graphics(), ctx.font(), "  > Item A", ctx.area().x() + 10, ctx.area().y() + 28,
                    ctx.style(), EscTypeRole.BODY, ctx.area().width() - 20, 1f);
                EscTypography.draw(ctx.graphics(), ctx.font(), "  > Item B", ctx.area().x() + 10, ctx.area().y() + 42,
                    ctx.style(), EscTypeRole.BODY, ctx.area().width() - 20, 1f);
            })
            .build());

        EscAssetRegistry.register(EscAssetDef.builder("esc:tabs", EscAssetType.TABS, EscAssetCategory.NAVIGATION)
            .displayName("Tabs")
            .description("Horizontal tab strip")
            .prop(EscAssetPropDef.stringList("tabs", "General,Advanced", "Tab labels"))
            .renderer((ctx, inst) -> {
                List<String> tabs = parseCsv(ctx.resolve(inst.prop("tabs")));
                int x = ctx.area().x() + 4;
                for (String tab : tabs) {
                    int w = ctx.font().width(tab) + 16;
                    EscRect tabRect = new EscRect(x, ctx.area().y() + 4, w, 18);
                    EscPanel.fill(ctx.graphics(), tabRect, ctx.style().panelFillColor());
                    EscPanel.border(ctx.graphics(), tabRect, ctx.style().panelBorderColor(), 1);
                    EscTypography.draw(ctx.graphics(), ctx.font(), tab, tabRect.x() + 8, tabRect.y() + 5,
                        ctx.style(), EscTypeRole.BODY, tabRect.width() - 8, 1f);
                    x += w + 4;
                }
            })
            .build());

        EscAssetRegistry.register(EscAssetDef.builder("esc:carousel_band", EscAssetType.CAROUSEL, EscAssetCategory.NAVIGATION)
            .displayName("Carousel band")
            .description("Hub carousel layout band")
            .prop(EscAssetPropDef.anEnum("layout", "CAROUSEL", hubLayoutValues(), "Layout id"))
            .renderer((ctx, inst) -> {
                EscPanel.renderPanel(ctx.graphics(), ctx.area(), ctx.style());
                EscTypography.draw(ctx.graphics(), ctx.font(), "Carousel · " + inst.prop("layout"),
                    ctx.area().x() + 10, ctx.area().y() + ctx.area().height() / 2 - 6,
                    ctx.style(), EscTypeRole.META, ctx.area().width() - 20, 1f);
            })
            .build());

        EscAssetRegistry.register(EscAssetDef.builder("esc:classic_strip", EscAssetType.CLASSIC_STRIP, EscAssetCategory.NAVIGATION)
            .displayName("Classic strip")
            .description("Classic hub nav strip")
            .renderer((ctx, inst) -> {
                EscRect strip = new EscRect(ctx.area().x(), ctx.area().y() + 4, ctx.area().width(), 22);
                EscPanel.fill(ctx.graphics(), strip, ctx.style().accentColor() & 0x33FFFFFF | 0x22000000);
                EscTypography.draw(ctx.graphics(), ctx.font(), "Classic navigation strip",
                    strip.x() + 8, strip.y() + 7, ctx.style(), EscTypeRole.BODY, strip.width() - 16, 1f);
            })
            .build());

        EscAssetRegistry.register(EscAssetDef.builder("esc:control_rail", EscAssetType.CONTROL_RAIL, EscAssetCategory.NAVIGATION)
            .displayName("Control rail")
            .description("Control-centre left rail")
            .renderer((ctx, inst) -> {
                EscRect rail = new EscRect(ctx.area().x(), ctx.area().y(), Math.min(100, ctx.area().width() / 3), ctx.area().height());
                EscPanel.renderPanel(ctx.graphics(), rail, ctx.style());
                EscTypography.draw(ctx.graphics(), ctx.font(), "Rail", rail.x() + 8, rail.y() + 8,
                    ctx.style(), EscTypeRole.TITLE, rail.width() - 16, 1f);
            })
            .build());
    }

    private static void registerEffects() {
        EscAssetRegistry.register(EscAssetDef.builder("esc:background", EscAssetType.BACKGROUND, EscAssetCategory.EFFECTS)
            .displayName("Background")
            .description("Backdrop style token (screen-level)")
            .prop(EscAssetPropDef.anEnum("style", "GRID", backdropValues(), "Backdrop"))
            .renderer((ctx, inst) -> EscTypography.draw(ctx.graphics(), ctx.font(),
                "Backdrop: " + inst.prop("style"), ctx.area().x() + 8, ctx.area().y() + 8,
                ctx.style(), EscTypeRole.META, ctx.area().width() - 16, 1f))
            .build());

        EscAssetRegistry.register(EscAssetDef.builder("esc:crt_layer", EscAssetType.CRT_LAYER, EscAssetCategory.EFFECTS)
            .displayName("CRT layer")
            .description("CRT overlay toggle (quality-gated at runtime)")
            .prop(EscAssetPropDef.bool("enabled", true, "Enabled"))
            .renderer((ctx, inst) -> EscTypography.draw(ctx.graphics(), ctx.font(),
                "CRT: " + inst.prop("enabled"), ctx.area().x() + 8, ctx.area().y() + 8,
                ctx.style(), EscTypeRole.META, ctx.area().width() - 16, 1f))
            .build());

        EscAssetRegistry.register(EscAssetDef.builder("esc:color_chip", EscAssetType.COLOR_CHIP, EscAssetCategory.EFFECTS)
            .displayName("Colour chip")
            .description("Theme colour swatch preview")
            .prop(EscAssetPropDef.anEnum("role", "ACCENT", List.of("BORDER", "TEXT", "ACCENT"), "Role"))
            .renderer((ctx, inst) -> {
                int color = switch (inst.prop("role")) {
                    case "BORDER" -> ctx.style().panelBorderColor();
                    case "TEXT" -> ctx.style().textColorBody();
                    default -> ctx.style().accentColor();
                };
                EscRect chip = new EscRect(ctx.area().x() + 8, ctx.area().y() + 8, 48, 16);
                EscPanel.fill(ctx.graphics(), chip, color | 0xFF000000);
                EscPanel.border(ctx.graphics(), chip, ctx.style().focusBorderColor(), 1);
            })
            .build());
    }

    private static void registerEsh() {
        EscAssetRegistry.register(EscAssetDef.builder("esc:section_card", EscAssetType.SECTION_CARD, EscAssetCategory.ESH)
            .displayName("Section card")
            .description("ESH hub section identity card")
            .prop(EscAssetPropDef.string("title", "ESS", "Title"))
            .prop(EscAssetPropDef.string("subtitle", "Studio stack", "Subtitle"))
            .renderer((ctx, inst) -> EscCard.render(
                ctx.graphics(), ctx.font(), ctx.area(), ctx.style(),
                ctx.resolve(inst.prop("title")),
                ctx.resolve(inst.prop("subtitle")),
                "ESH", 0.5f))
            .build());

        EscAssetRegistry.register(EscAssetDef.builder("esc:mod_badge", EscAssetType.MOD_BADGE, EscAssetCategory.ESH)
            .displayName("Mod badge")
            .description("Small mod author badge")
            .prop(EscAssetPropDef.string("label", "{bind:mod}", "Label"))
            .renderer((ctx, inst) -> {
                String label = ctx.resolve(inst.prop("label"));
                int w = Math.min(ctx.area().width() - 8, ctx.font().width(label) + 12);
                EscRect badge = new EscRect(ctx.area().x() + 8, ctx.area().y() + 8, w, 14);
                EscPanel.fill(ctx.graphics(), badge, ctx.style().accentColor() & 0x44FFFFFF | 0x33000000);
                EscTypography.draw(ctx.graphics(), ctx.font(), label, badge.x() + 6, badge.y() + 3,
                    ctx.style(), EscTypeRole.META, badge.width() - 8, 1f);
            })
            .build());
    }

    private static List<String> parseCsv(String raw) {
        if (raw == null || raw.isBlank()) {
            return List.of();
        }
        List<String> out = new ArrayList<>();
        for (String part : raw.split(",")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                out.add(trimmed);
            }
        }
        return out;
    }

    private static String capitalize(String id) {
        if (id == null || id.isBlank()) {
            return "Action";
        }
        return id.substring(0, 1).toUpperCase() + id.substring(1);
    }

    private static List<String> frameValues() {
        List<String> values = new ArrayList<>();
        for (EscFrameStyle style : EscFrameStyle.values()) {
            values.add(style.name());
        }
        return values;
    }

    private static List<String> hubLayoutValues() {
        List<String> values = new ArrayList<>();
        for (EscHubLayoutId id : EscHubLayoutId.values()) {
            values.add(id.name());
        }
        return values;
    }

    private static List<String> backdropValues() {
        List<String> values = new ArrayList<>();
        for (EscThemePreset preset : EscThemePreset.values()) {
            if (preset.preferredBackdrop() != null) {
                values.add(preset.preferredBackdrop().name());
            }
        }
        values.add("GRID");
        return values.stream().distinct().toList();
    }
}
