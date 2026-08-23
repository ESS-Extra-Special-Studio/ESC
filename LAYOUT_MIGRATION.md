# ESC Layout Migration Guide

## Before (absolute pixels)

```java
int x = width / 2 - 100;
int y = height - 30;
addRenderableWidget(EscButtons.button(label, x, y, 200, 20, onPress));
```

## After (anchor + offset)

```java
EscRect content = EscPanel.contentRect(this, style);
EscRect footer = EscPanel.footer(content, style);
addAnchoredButton(label, footer,
    EscLayoutSpec.of(EscAnchor.BOTTOM_CENTER, 0, -4, 200, 20), onPress);
```

## Three-column split

```java
EscRect body = EscPanel.bodyBelowTitle(content, style);
EscRect[] cols = body.splitColumns(new float[]{0.26f, 0.50f, 0.24f}, 6);
leftPanel.layout(cols[0]);
centerPanel.layout(cols[1]);
rightPanel.layout(cols[2]);
```

## EscScreen subclass

Extend `EscScreen`, implement `buildLayout()` only. `init()` (including resize) calls it automatically.

## Text in bounded cells

Use `EscText.drawFitted(graphics, font, label, cellRect, color)` instead of raw `drawString`.

## Keyboard / grid panels

Split the parent into row and column bands by ratio, then place cells with weighted column splits:

```java
EscRect inner = EscLayout.resolve(panelBounds, EscLayoutSpec.fill(4));
EscRect[] sections = inner.splitRows(new float[]{6f, 4f}, gap);
EscRect[] bottomCols = sections[1].splitColumns(new float[]{3f, 3f, 4f}, gap);
EscRect[] keyCells = EscGridLayout.rowCells(mainRows[0], EscGridLayout.equalWeights(12), gap);
```

Use relative key weights (Tab 1.5×, Space 6.5×, etc.) — not pixel thresholds or minimum widths.

## Centered tab pair (two buttons side-by-side)

When migrating from absolute coords like `width/2 - tabW - 4`, **do not** pass those values as `CENTER`/`TOP_CENTER` offsets. Anchor modes already center the widget; offsets are nudges from that center.

```java
int tabW = 80, tabH = 22, gap = 4;
EscRect tabBar = new EscRect(0, 0, screenWidth, 28);

addAnchoredButton(Component.literal("Radio"), tabBar,
    EscLayoutSpec.centeredPairLeft(EscAnchor.TOP_CENTER, 4, tabW, tabH, gap), onRadio);
addAnchoredButton(Component.literal("Walkie"), tabBar,
    EscLayoutSpec.centeredPairRight(EscAnchor.TOP_CENTER, 4, tabW, tabH, gap), onWalkie);
```

This places Radio at `screenWidth/2 - tabW - gap` and Walkie at `screenWidth/2 + gap` (same as legacy pixel math).

## EscTabBar (multi-tab strip)

For three-or-more tabs (Pip-Boy STAT/INV/MAP/RADIO/SPECIAL, etc.), prefer `EscTabBar` over a hand-rolled button pair:

```java
EscTabBar tabs = EscTabBar.of(
    EscTabBar.Style.falloutPip(),
    EscTabBar.Tab.of("stat", "STAT"),
    EscTabBar.Tab.of("inv", "INV"),
    EscTabBar.Tab.disabled("map", "MAP"),
    EscTabBar.Tab.of("radio", "RADIO"),
    EscTabBar.Tab.disabled("special", "SPECIAL")
);
EscRect chrome = EscPanel.contentRect(this, EscUiStyle.falloutPip());
tabs.layoutFit(chrome.x(), chrome.y(), chrome.width());
EscRect body = tabs.bodyBelow(chrome, 4);
// render: tabs.render(graphics, font, mouseX, mouseY);
// click: tabs.mouseClicked(mouseX, mouseY);
```

Use `Style.pipBoy()` for filled neon cells, or `Style.falloutPip()` for bracketed selected text + rule gap (`EscFalloutDraw`).

## CTL tab bar + scroll viewport

Main panel: fixed-height tab row, then main area; centre tab pair on the tab row.

```java
EscRect body = EscPanel.bodyBelowTitle(content, style);
tabRow = new EscRect(body.x(), body.y(), body.width(), TAB_ROW_BAND);
EscRect mainBody = new EscRect(body.x(), tabRow.bottom() + 4, body.width(),
    Math.max(0, body.bottom() - tabRow.bottom() - 4));

int guideTabW = TAB_W + 48;
addButton(Component.literal("Guide"), resolve(tabRow,
    EscLayoutSpec.centeredPairLeft(EscAnchor.TOP_CENTER, 0, guideTabW, TAB_H, 6)), onGuide);
addButton(Component.literal("Leaks"), resolve(tabRow,
    EscLayoutSpec.centeredPairRight(EscAnchor.TOP_CENTER, 0, leaksTabW, TAB_H, 6)), onLeaks);

// Guide tab: scroll viewport + footer action stack (fixed pixel heights — not splitRows weights)
int stackH = Math.min(mainBody.height(), Math.max(minStackH, 52));
EscRect actionStack = new EscRect(mainBody.x(), mainBody.bottom() - stackH, mainBody.width(), stackH);
EscRect guideViewport = new EscRect(mainBody.x(), mainBody.y(), mainBody.width(),
    Math.max(0, actionStack.y() - mainBody.y() - 8)).inset(...);
```

Scroll lists: store `leakViewport` / `guideViewport` as `EscRect`; use `contains(mouseX, mouseY)` for wheel and click hit-testing; scissor to the same rect in `render()`.

## Policy

All new ESC consumer GUIs must follow [ESC_UI_POLICY.md](ESC_UI_POLICY.md).
