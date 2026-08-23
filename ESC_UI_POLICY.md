# ESC UI Policy — Mandatory anchoring

All mods that depend on **ExtraSpecialCore (ESC)** must follow these rules for client GUI code.

## Required patterns

1. **Extend `EscScreen`** for every mod-owned full-screen GUI.
   - Implement `buildLayout()` for widget placement; do not override `init()` for layout unless clearing widgets for tab switches.
   - Exception: hooks into vanilla screens (e.g. Pause/Inventory injection) may stay on vanilla `Screen` — document the exception in the mod README.

2. **Anchor all widgets** — no legacy center math in layout code:
   - Use `contentRect()`, `EscPanel.bodyBelowTitle()`, `EscPanel.footer()`
   - Use `EscLayoutSpec` + `addAnchoredButton` / `addAnchoredSearchBox`
   - Use `EscRect.splitColumns()` / `splitRows()` for bands and columns
   - Use `EscLayoutSpec.centeredPairLeft/Right` for side-by-side tab pairs

3. **Single source of truth for bounds**
   - Store layout as `EscRect` fields resolved in `buildLayout()`
   - `render()` and hit-testing (clicks, scroll) must use the same rects — never recompute `width/2 - N` in `render()`

4. **Text in cells**
   - Prefer `EscText.drawInRect`, `EscText.drawFitted`, `EscText.drawWrapped` over raw `drawCenteredString` with manual coordinates

## Forbidden in new or migrated screens

- `extends Screen` for mod GUIs (unless documented vanilla hook)
- `width / 2 - N` or `height / 2 + N` for widget placement in `init()` / `buildLayout()`
- Full-screen layout root `new EscRect(0, 0, width, height)` when `contentRect()` exists
- Separate render vs click math for the same visual region

## New mod checklist

- [ ] Composite-build ESC (`includeBuild('../ESC')`)
- [ ] `esc_version` and `esc_version_range=[1.0.0,2.0)` in `gradle.properties`; `${esc_version_range}` in `mods.toml` — see [ESC_DEPENDENCIES.md](ESC_DEPENDENCIES.md)
- [ ] Copy [HermesSettingsScreen](https://github.com/) as reference (see `hermes` project)
- [ ] Run [GUI_VISUAL_CHECKLIST.md](GUI_VISUAL_CHECKLIST.md) at GUI scales 1–4

## Reference

- [LAYOUT_MIGRATION.md](LAYOUT_MIGRATION.md) — before/after examples
- [GUI_VISUAL_CHECKLIST.md](GUI_VISUAL_CHECKLIST.md) — regression testing
- Hermes — fully migrated reference consumer
