# ExtraSpecialCore (NeoForge) — Changelog

**Platform:** Minecraft 1.21.1 · NeoForge 21.1.x  
**Jar suffix:** `*-neoforge.jar`

---

## Unreleased

Fixed:
EscScreen keeps `renderBackground` empty (no blur, no dim). World dimming uses `renderWorldDim()` once before chrome so `super.render` cannot darken ESH, Pip-Boy, or other already-drawn panels.
Call Airdrop / Internet / Panel Settings call `renderWorldDim` for Forge-parity backdrop without double-dim.

## Version 2.0.1

Update by: Extra_Special_K

**Consumer mods:** still depend on ESC `[2.0.0,3.0)` — no Gradle range change. Requires **ES Library (ESL) 1.0.0** or newer (NeoForge jar).

Fixed:

- **Apply to: ESH** now scopes the **base theme preset** as well as player Border / Text / Accent colours. With the default **Apply to: ESH**, only the F9 hub uses your chosen preset (e.g. Growth); other ESC screens use Vanilla chrome until you toggle **Apply to: ALL**.

---

## Version 2.0.0

Update by: Extra_Special_K

**Consumer mods:** depend on ESC `[2.0.0,3.0)` (set Gradle `esc_version=2.0.0`). Requires **ES Library (ESL) 1.0.0** or newer (NeoForge jar).

Changed:

- Hard dependency on `extraspeciallib` (ESL).
- `EscConfig` / `EscMods` are thin delegates to `EslConfig` / `EslMods`.

Note: Forge ESC 2.0.0 theme system, declarative GUI profiles, and sound bank are not in this NeoForge build yet — UI surface remains the 1.3.0 NeoForge set. Version aligns with Forge for consumer `esc_version_range` / ESL requirement.

---

## Version 1.3.0 — Fallout Pip UI + library helpers

Same public API as Forge ExtraSpecialCore **1.3.0**. FML range `[1.0.0,2.0)`; set Gradle `esc_version=1.3.0` when using the APIs below.

- **ESS domain swap** — Java packages and branding moved from `uk.creatopia…` to `uk.co.extraspecialstudio…`.
- **`EscFalloutDraw`** — CRT phosphor helpers (scanlines, rules, labeled HP/XP bars).
- **`EscTabBar`** — multi-tab strip with `pipBoy` and `falloutPip` styles.
- **`EscUiStyle.falloutPip()`** and **`EscPanel.bodyBelowTabs(...)`**.
- **`EscText.headingHeight(...)`**.
- **`EscConfig`**, **`EscMods`**, and **`Config.debugLogging`**.
- Consumer docs updated (`LAYOUT_MIGRATION`, `ESC_DEPENDENCIES`, `JAR_NAMING`).

---

## Version 1.2.2 — Forge parity (NeoForge)

This release brings NeoForge ESC to **full parity with Forge ExtraSpecialCore 1.2.2**. Consumer mods should depend on **`[1.2.0,2.0)`**.

### Layout & screens

- **Anchor layout API** — `EscAnchor`, `EscInsets`, `EscLayoutSpec`, `EscLayout`, `EscRect` splits, and `EscGridLayout` match Forge 1.2.x.
- **`EscScreen` base class** — `buildLayout()` on open/resize; `addAnchoredButton`, `addSearchBox`, and anchor-based widget placement.
- **`EscLayoutSpec.centeredPairLeft` / `centeredPairRight`** — centred tab-pair helpers for walkie-style UIs.

### Text & widgets

- **`EscText`** — `drawFitted`, `drawInRect`, `drawScrollingString`, `drawWrapped`, `measureLineHeight`, and `EscFonts.DEFAULT` integration.
- **`EscSearchBox`** — single-line search with scrolling hint and `recommendedHeight(Font)`.
- **`EscSimpleScrollList` / `EscListRowRenderer`** — viewport-clipped scroll lists aligned with Forge.

### Build & quality

- **JUnit 5** — `EscLayoutTest` covers anchor resolve, fill, stretch, column splits, and centred-pair offsets.
- Output jar: `extraspecialcore-1.2.2-neoforge.jar`.

### Consumer mods

- **Calm The Leaks (NeoForge) 1.2.2** — `esc_version=1.2.2`, dependency range `[1.0.0,2.0)`.

---

## Version 1.21.1-1.0.0 — Initial NeoForge UI foundation (superseded)

- First NeoForge port of the shared UI layer (pre–1.2.0 APIs only).
- **Superseded by 1.2.2 / 1.3.0** — use `1.3.0` and depend on `[1.0.0,2.0)` for current APIs.
