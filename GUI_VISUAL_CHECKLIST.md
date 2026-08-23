# ESC GUI visual regression checklist

Run after layout changes. Test each mod screen at **GUI Scale 1, 2, 3, 4** and window sizes **854×480**, **1920×1080**, and one ultrawide if available.

**Test instance (Forge 1.20.1):** `C:\Users\Ksivi\curseforge\minecraft\Instances\C.Ideas\mods`

## ExtraSpecialCore 1.2.1 foundation

- [ ] `EscLayoutTest` passes (`./gradlew test` in ExtraSpecialCore)
- [ ] Anchor resolve: TOP_CENTER, FILL, splitColumns, centeredPair produce expected rects

## Hermes — DONE (anchor reference)

- [ ] Settings shell: Back + tabs do not overlap title strip
- [ ] Keyboard tab 3-column: command list | visual keyboard | key details
- [ ] Visual keyboard: no overlapping key labels; mouse, arrows, and numpad always visible
- [ ] Click targets match drawn keys (hitboxes from last render frame)
- [ ] Narrow fallback: list-only below width threshold

## Not Another Guide Book — migrated 1.2.1

- [ ] First-join intro: footer button centered
- [ ] Guide carousel: item strip from splitColumns; click matches drawn item

## RadioOS — migrated 1.2.1

- [ ] Internet station picker: buttons in body band, no clip
- [ ] Call airdrop / panel settings: list | controls columns; search aligned

## Dead Air — migrated 1.2.1

- [ ] Config screen: row splits; Done button in footer
- [ ] Walkie tuning: three columns; dial inside center panel
- [ ] Field guide: nav buttons in footer band; index scroll viewport

## Dead Letters — migrated 1.2.1

- [ ] Note screen: page centered; pagination anchored to page edges
- [ ] Scrapbook: same anchor patterns as Note

## Calm The Leaks — migrated 1.2.1

- [ ] Main panel: centred tab pair + scroll viewports
- [ ] Narrow / detail: back in footer; scroll scissor matches viewport

## Pantheon API — migrated 1.2.1

- [ ] Hub: dock (22%) + content (78%) columns
- [ ] Console / approvals / audit: list area from bodyBelowTitle inset

## Pre-commit grep (optional)

From each consumer `src/` (exclude reference/backup):

```
width / 2|width/2
extends Screen
```

Document intentional exceptions in mod README (e.g. vanilla screen hooks).

## Pass criteria

- No text drawn outside panel borders (scissor or `EscText.drawFitted`)
- No widgets off-screen at any scale listed above
- Resize window / change GUI scale: layout reflows without stale positions

See [ESC_UI_POLICY.md](ESC_UI_POLICY.md) for mandatory rules.
