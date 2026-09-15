ExtraSpecialCore 2.0.3
Added:
Hub leaf recommended-stub styling so grey badges can mark missing optional tools without looking installed.
Changed:
Accordion hub rows respect recommended leaf state for muted presentation.

# ExtraSpecialCore — Changelog

## Version 2.0.2

Fixed:
NeoForge EscScreen no longer double-dims hub and consumer UIs (ESH / Pip-Boy stayed muddy after chrome paint). World dimming is explicit via renderWorldDim before panel chrome.
Forge jar rebuilt at the same version for loader parity.

# ExtraSpecialCore â€” Changelog

## Version 2.0.1

Update by: Extra_Special_K

**Consumer mods:** still depend on ESC `[2.0.0,3.0)` â€” no Gradle range change. Requires **ES Library (ESL) 1.0.0** or newer.

Fixed:

- **Apply to: ESH** now scopes the **base theme preset** as well as player Border / Text / Accent colours. With the default **Apply to: ESH**, only the F9 hub uses your chosen preset (e.g. Growth); other ESC screens (Calm The Leaks, Pantheon, Dead Air, etc.) use Vanilla chrome until you toggle **Apply to: ALL**.

---

## Version 2.0.0

Update by: Extra_Special_K

**Consumer mods:** depend on ESC `[2.0.0,3.0)` (set Gradle `esc_version=2.0.0`). Requires **ES Library (ESL) 1.0.0** or newer.

Added:

- Declarative GUI profiles â€” JSON profile format, asset registry, and `EscGui.open(...)` runtime faÃ§ade (authoring lives in optional Extra Special GUI / ESG).
- Theme system â€” pack/common + player/client theme configs, presets (including Matrix, Ocean, Clouds, Growth, Ember), colour workstation (wheel / SV / HEX), animated backdrops, CRT layer, quality and reduced-motion knobs.
- Fresh-install defaults â€” Growth preset (white chrome, ESH green accent, forest backdrop), Growth backdrop, Quality HIGH so the animation actually draws.
- Motion OFF freezes the hub backdrop at the current frame (Q=HIGH still required); it no longer hides the backdrop.
- Theme sync â€” pack theme override can push to clients; player colours stay this-PC unless locked.
- Menu sound bank â€” `EscSound` / `EscUiCue` (menu sounds 1â€“20); semantic cue binding optional.
- `EscUiStyle.active()` / `fromTheme(...)` so screens pick up the resolved theme automatically.

Changed:

- `EscUiStyle` carries theme presentation fields (breaking for hand-built constructors â€” use `active()`, `vanillaLike()`, `falloutPip()`, or `fromTheme`).
- Major version bump: open consumer dependency ranges to `[2.0.0,3.0)` (old `[â€¦,2.0)` excludes this release).

## Version 1.3.0 â€” Fallout Pip UI + library helpers

**Consumer mods:** FML range `[1.0.0,2.0)`. Set Gradle `esc_version=1.3.0` when using the APIs below.

- **Hub layout stack** â€” `EscAnim` / `EscFocusVisual` / `EscCard`; `EscHubLayout` profiles (`CAROUSEL`, `CONTROL_CENTRE`, `DASHBOARD`, `ORBITAL`, `CLASSIC`) with accordion groups for nested hub navigation.
- **ESS domain swap** â€” Java packages and branding moved from `uk.creatopiaâ€¦` to `uk.co.extraspecialstudioâ€¦`.
- **`EscFalloutDraw`** â€” CRT phosphor helpers (scanlines, rules, labeled HP/XP bars).
- **`EscTabBar`** â€” multi-tab strip with `pipBoy` and `falloutPip` styles.
- **`EscUiStyle.falloutPip()`** and **`EscPanel.bodyBelowTabs(...)`**.
- **`EscText.headingHeight(...)`** and **`EscText.width(...)`** (measures under an ESC font id, so carets and rules line up with the drawn glyphs).
- **`EscTypography.drawScrolling(...)`** â€” role-aware marquee line: same call shape as `draw(...)` but overflow scrolls instead of ellipsising, for panes where the full id / version / path is the point.
- **Card text stays inside its card** â€” `EscCard` clips title/subtitle to the border and marquee-scrolls overflow instead of bleeding across neighbouring cards (Carousel, Dashboard).
- **Text box caret fixed** â€” the caret now sits after the character just typed, and long values pan to follow it rather than marquee out of sync.
- **Colour picker fits its panel** â€” the hue wheel, SV square, and swatch rows in `EscColorPickerScreen` scale to the space available instead of running over the footer buttons.
- **`EscOwnsBackNav`** â€” marker for screens with their own Back/Cancel; ES Hub skips its injected `â† Hub` button on them (the ESC colour picker now opts out).
- **`EscButtonBar.fitDense`** â€” when equal-width slots would drop below a comfortable width, splits into two rows (hub footer uses this).
- **Layout debug** â€” client config `visual.layoutDebug` draws content/footer outlines on `EscScreen` (dev aid; off by default).
- **DX docs** (Desktop): `ESC_SCREEN_COOKBOOK.md`, `ESC_PITFALLS.md`.
- **Theme scope helpers** â€” `EscThemeManager.isPackOverrideActive()` / `isPlayerThemeLocked()`; player colour knobs stay CLIENT-only unless a pack/server override locks them.
- **`EscConfig`**, **`EscMods`**, and **`Config.debugLogging`**.
- **Requires ES Library (`extraspeciallib`)** â€” `EscConfig` / `EscMods` now delegate to ESL; install ESL alongside ESC.
- Consumer docs updated (`LAYOUT_MIGRATION`, `ESC_DEPENDENCIES`, `JAR_NAMING`, `ESS_FRAMEWORK`, `ESS_MASTER_CONTEXT`).

---

## Version 1.2.2 â€” ES migration wave

**Consumer mods should depend on `[1.2.0,2.0)`.**

### Release

- Semver aligned with the Extra Special (ES) ESC migration release (`1.2.2` across Forge consumers).
- Anchor layout, search box, scroll list, and text helpers unchanged from 1.2.1 â€” no breaking API changes.

---

## Version 1.2.1 â€” Centred tab-pair layout

### Layout

- **`EscLayoutSpec.centeredPairLeft` / `centeredPairRight`** â€” Helpers for a horizontally centred pair of widgets (e.g. side-by-side tabs) from a single row anchor.
- **Unit tests** â€” `EscLayoutTest` covers centred-pair offsets against legacy screen-centre math.
- **`LAYOUT_MIGRATION.md`** â€” Documented centred tab-pair pattern for consumer mods.

---

## Version 1.2.0 â€” Anchor layout foundation

**Consumer mods should depend on `[1.2.0,2.0)` for anchor APIs and search-box helpers.**

### Layout & screens

- **Anchor layout API** â€” `EscAnchor`, `EscInsets`, `EscLayoutSpec`, `EscLayout`, and `EscRect` helpers for resolving widget and panel bounds from parent rects instead of hard-coded pixel math.
- **`EscScreen` base class** â€” Subclasses implement `buildLayout()`; called automatically from `init()` on open and resize. Includes `addAnchoredButton`, `addButton`, and anchor-based widget placement helpers.
- **Column splits** â€” `EscRect.splitColumns()` for proportional multi-column layouts (e.g. 25% / 50% / 25%).
- **Fixed-aspect fitting** â€” `EscLayout.fitContained()` and `EscLayout.fitScale()` for content with a design size (keyboards, diagrams) that scales uniformly inside a parent rect.
- **Unit tests** â€” `EscLayoutTest` covers anchor resolve, fill, stretch, and column splits.

### Text & widgets

- **`EscText` helpers** â€” `drawFitted` / `drawInRect` for bounded cells; `drawScrollingString` for single-line overflow; `measureLineHeight` for font-aware vertical centering.
- **`EscSearchBox`** â€” Single-line search field with scrolling hint, clip, and ESC font formatting. `recommendedHeight(Font)` sizes the field to one line of text. Text and hint render vertically centered; cursor uses `getScreenX()`.
- **`EscScreen.addSearchBox()`** â€” Creates, sizes, and registers search widgets on the screen (panels expose bounds via `bindSearchBox()` rather than constructing widgets themselves). Overloads for message + hint; `addAnchoredSearchBox()` for anchor specs.

### Migration

- See `LAYOUT_MIGRATION.md` for before/after patterns when moving mod screens off absolute pixels.
- See `GUI_VISUAL_CHECKLIST.md` for scale/resolution regression testing after layout changes.

---

## Version 1.1.0 â€” ESC UI foundation

- Initial shared UI layer: `EscUiStyle`, `EscPanel`, `EscButtons`, `EscSimpleScrollList`, bundled fonts (`EscFonts`), and text helpers used across Extra Special (ES) mods.
- Pantheon API, Not Another Guide Book, RadioOS, Dead Air, Dead Letters, Calm The Leaks, and others integrated for fonts and buttons.



