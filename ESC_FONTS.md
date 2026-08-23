# ESC Fonts

ESC now ships a custom font key for UI usage:

- `EscFonts.FIRA_CODE` -> `extraspecialcore:fira_code`

## Usage

```java
// Single line
EscText.drawString(graphics, font, "Hello", x, y, 0xFFFFFF, false, EscFonts.FIRA_CODE);

// Wrapped paragraph
EscText.drawWrapped(graphics, font, text, x, y, width, 0xE0E0E0, EscFonts.FIRA_CODE);
```

Notes:

- This is opt-in per text draw call.
- Mods should use custom fonts selectively (titles/code-like text), not for every label.
