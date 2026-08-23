# ESC Public Toolkit Roadmap (Draft)

This note captures a potential future direction for `ExtraSpecialCore (ESC)` as a reusable UI toolkit for other mod developers.

It is intentionally a planning document only. It does not change current scope rules.

## Positioning

ESC can be positioned as a lightweight, opinionated Forge UI toolkit that is:

- Simple: fast to adopt with minimal setup
- Composable: small primitives that combine cleanly
- Stable: predictable APIs and semver discipline
- Neutral: no mod-specific gameplay logic in core

## Why This Direction Makes Sense

- UI repeats across many mods and benefits from consistency
- Shared primitives reduce duplicate code and speed up delivery
- A stable toolkit improves polish for users and lowers maintenance cost for authors

## Adoption Goals

For external adoption, ESC should provide:

- A clear starter path (`EscScreen` style entrypoint, panels, buttons, text, layout helpers)
- A small set of polished default themes (vanilla-like, dark, high-contrast)
- Practical docs with copy-paste examples
- A demo mod with common patterns:
  - settings screen
  - list + search
  - modal confirmation
- API stability policy with migration notes for each release

## Boundary Guardrails (Keep ESC Clean)

- ESC owns rendering and generic interaction patterns
- Consumer mods own domain meaning, gameplay logic, and state decisions

Do not move mod-specific rules into ESC (inventory semantics, progression rules, reward decisions, etc.).

## Suggested Versioned Roadmap

## MVP (Current Foundation)

- Core primitives (`EscPanel`, `EscButtons`, `EscRect`, `EscText`, style helpers)
- Default typography support (Fira Code available, font-aware text APIs)
- Existing integrations across internal mods as real-world validation

## 1.1 (External Readiness)

- `EscScreen` base abstraction for faster setup
- Basic layout helpers (stack/row/columns with spacing) — `EscLayout`, `EscRect.splitColumns`, `EscAnchor` + `EscLayoutSpec`
- Two or three maintained themes
- "Getting Started" docs and a minimal showcase project — see `LAYOUT_MIGRATION.md`
- API reference for text, buttons, panels, and layout primitives

## 1.2 (Ecosystem Quality)

- Component library growth (filter bar, list scaffold, tabs, empty-state panel)
- Accessibility pass (contrast presets, scale checks, keyboard navigation expectations)
- Backward-compatibility policy document
- Migration guide template for each release
- CI checks for demo/screenshots to catch visual regressions early

## Non-Goals

- No gameplay framework responsibilities
- No per-mod business logic adapters in ESC core
- No "one-off" domain exceptions in shared UI code

## Success Criteria

- New mod screens can be assembled quickly from ESC primitives
- Visual style remains consistent across multiple independent mods
- External devs can adopt ESC from docs/examples without deep internal context
- ESC upgrades are low-risk due to stable APIs and clear migration notes

## 1.2.1 status (anchor migration complete)

- All flagship ESC consumers migrated to `EscScreen` + anchor layout (Forge 1.20.1)
- NeoForge CTL ported with same GUI patterns (1.21.1 API differences documented in CTL)
- Mandatory policy: [ESC_UI_POLICY.md](ESC_UI_POLICY.md)
- Platform vision (ESH / ESL / ESN / ESA): [ESS_FRAMEWORK.md](ESS_FRAMEWORK.md) — ESH/ESL/ESN/ESA implementation deferred
- **ESA (Extra Special Anchor)** — planned sibling ESS module: **Kerbal-style live bone-anchored transform editor** + per-context saveable profiles (FP / FP+gun / TP / hostAnim). Not an ESC feature; toolkit vs attach-tooling stay separate. North-star write-up in ESS_FRAMEWORK § ESA (2026-08-09)

## Next (platform — future pass)

- Sequencing: finish Pip TACZ gun-arm → ship Pip-Boy 1.0.0 → ESL/ESN/ESH → ESA later (see ESS_FRAMEWORK.md)
- Domain/packages already on `extraspecialstudio.co.uk` / `uk.co.extraspecialstudio`
- ESH hub + ESL shared library + ESN network fabric; then ESA tooling (see ESS_FRAMEWORK.md)
- Optional ESC helpers: `EscPanel.tabBar`, `EscScrollViewport` if duplication grows
