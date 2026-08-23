# ESC Scope

`ExtraSpecialCore (ESC)` is the **UI spine** of the Creatopia mod ecosystem.

Its purpose is to provide reusable, consistent UI building blocks across mods without owning gameplay behavior.

## Core Identity

- ESC owns **generic UI structure and presentation**.
- Individual mods own **gameplay meaning, state, and decisions**.

In short:

- ESC = how it looks and behaves (generically)
- Mod = what it means and what it does

## What Belongs In ESC

- Reusable UI primitives (panels, buttons, rows, lists, layout helpers)
- Generic visual style tokens (spacing, sizing, colors, typography helpers)
- Generic interaction patterns (pagination scaffolding, search/filter widgets, selection widgets)
- UI utility helpers that are mod-agnostic

## What Must Stay Out Of ESC

- Player progression/state storage
- Inventory or gameplay reward logic
- Mod-specific rules (e.g., "what counts as a guidebook")
- Business/domain decisions for any individual mod
- Network payload semantics tied to one mod's gameplay

## Separation Rule (Non-Negotiable)

- ESC may emit generic UI events (selected, confirmed, filtered, changed page).
- Mods interpret those events and execute domain logic.

Example:

- ESC: selectable list, search bar, confirm button
- NAG: identify guidebooks, grant items, track first-join progress

## Decision Test For New Code

Before adding code to ESC, ask:

1. Can this be reused by at least two mods?
2. Does it avoid mod-specific gameplay assumptions?
3. If copied to another mod, would names/logic still make sense?

If any answer is "no", it belongs in the mod, not ESC.

## Maintenance Guardrails

- Prefer small, composable UI primitives over large opinionated screens.
- Keep ESC APIs stable; evolve by adding optional parameters, not breaking behavior.
- Avoid "just one mod-specific exception" in ESC.
- If a feature starts generic but needs domain knowledge, split it:
  - generic part stays in ESC
  - domain adapter moves to the mod

## Goal

Create a consistent polished feel across mods while keeping each mod independent, clear, and maintainable.
