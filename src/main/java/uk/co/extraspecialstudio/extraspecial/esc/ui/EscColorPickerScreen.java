package uk.co.extraspecialstudio.extraspecial.esc.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import uk.co.extraspecialstudio.extraspecial.esc.sound.EscSound;
import uk.co.extraspecialstudio.extraspecial.esc.sound.EscUiCue;
import uk.co.extraspecialstudio.extraspecial.esc.theme.EscColor;
import uk.co.extraspecialstudio.extraspecial.esc.theme.EscColorRole;
import uk.co.extraspecialstudio.extraspecial.esc.theme.EscTheme;
import uk.co.extraspecialstudio.extraspecial.esc.theme.EscThemeConfigs;
import uk.co.extraspecialstudio.extraspecial.esc.theme.EscThemeManager;
import uk.co.extraspecialstudio.extraspecial.esc.theme.EscThemeSwatch;

import java.util.List;
import java.util.Locale;
import java.util.function.IntConsumer;

/**
 * Reusable ESC colour workstation: hue wheel, SV square, HEX/RGB/HSV, recent/saved, live preview.
 * Left-click hub colour buttons to open; right-click still cycles presets.
 */
public final class EscColorPickerScreen extends EscScreen implements EscOwnsBackNav {

    private static final int WHEEL_OUTER_MAX = 78;
    private static final int WHEEL_OUTER_MIN = 30;
    private static final float WHEEL_INNER_RATIO = 0.66f;
    private static final int SV_MAX = 78;
    private static final int SV_MIN = 36;
    private static final int CHIP = 18;
    private static final int PREVIEW_H = 24;
    private static final int GAP = 8;

    /** Sized from the available panel each layout — fixed radii overflowed the footer. */
    private int wheelOuter = WHEEL_OUTER_MAX;
    private int wheelInner = (int) (WHEEL_OUTER_MAX * WHEEL_INNER_RATIO);
    private int svSize = SV_MAX;

    private final Screen parent;
    private final String heading;
    private final int initialArgb;
    private final IntConsumer onApply;
    private final Runnable onReset;
    private final EscColorRole role;

    private float hue;
    private float sat;
    private float val;
    private int alpha;
    private int workingArgb;

    private EscRect wheelArea;
    private EscRect svArea;
    private EscRect previewArea;
    private EscRect recentArea;
    private EscRect savedArea;
    private EscRect presetArea;
    private EscRect footerBounds;

    private EscSearchBox hexBox;
    private EscSearchBox rBox;
    private EscSearchBox gBox;
    private EscSearchBox bBox;
    private EscSearchBox hBox;
    private EscSearchBox sBox;
    private EscSearchBox vBox;
    private EscSearchBox aBox;

    private boolean draggingWheel;
    private boolean draggingSv;
    private boolean syncingFields;

    public EscColorPickerScreen(Screen parent, String heading, int initialArgb, IntConsumer onApply) {
        this(parent, heading, initialArgb, onApply, null, null);
    }

    public EscColorPickerScreen(
        Screen parent,
        String heading,
        int initialArgb,
        IntConsumer onApply,
        Runnable onReset,
        EscColorRole role
    ) {
        super(EscText.literal(heading == null ? "Colour" : heading));
        this.parent = parent;
        this.heading = heading == null ? "Colour" : heading;
        this.initialArgb = EscColor.opaque(initialArgb);
        this.onApply = onApply;
        this.onReset = onReset;
        this.role = role;
        setFromArgb(this.initialArgb);
    }

    /** Open a freeform picker. */
    public static void open(Screen parent, String title, int initialArgb, IntConsumer onApply) {
        Minecraft mc = Minecraft.getInstance();
        if (mc != null) {
            mc.setScreen(new EscColorPickerScreen(parent, title, initialArgb, onApply));
        }
    }

    /** Open picker bound to a player colour slot (persists via {@link EscThemeConfigs}). */
    public static void openPlayerSlot(Screen parent, EscColorRole role) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || role == null) {
            return;
        }
        EscTheme base = EscThemeManager.resolveBase("extraspecialhub");
        int current = EscThemeConfigs.playerColorArgb(role, defaultForRole(base, role));
        mc.setScreen(new EscColorPickerScreen(
            parent,
            role.label() + " Colour",
            current,
            argb -> {
                EscThemeConfigs.setPlayerCustomColor(role, argb);
                EscThemeManager.invalidateCache();
            },
            () -> {
                EscThemeConfigs.clearPlayerCustomColor(role);
                EscThemeManager.invalidateCache();
            },
            role
        ));
    }

    private static int defaultForRole(EscTheme base, EscColorRole role) {
        return switch (role) {
            case BORDER -> base.borderArgb();
            case TEXT -> EscColor.opaque(base.textTitleRgb());
            case ACCENT -> base.accentArgb();
        };
    }

    private void setFromArgb(int argb) {
        workingArgb = argb;
        float[] hsv = EscColor.toHsv(argb);
        hue = hsv[0];
        sat = hsv[1];
        val = hsv[2];
        alpha = EscColor.alpha(argb);
        if (alpha == 0 && (argb & 0xFFFFFF) != 0) {
            alpha = 255;
        }
    }

    private void rebuildWorking() {
        workingArgb = EscColor.fromHsv(hue, sat, val, alpha);
    }

    @Override
    protected void buildLayout() {
        clearWidgets();
        setStyle(EscUiStyle.active("extraspecialhub"));
        EscRect content = contentRect();
        footerBounds = EscPanel.footer(content, style);
        EscRect body = new EscRect(content.x(), content.y() + 28, content.width(),
            Math.max(80, footerBounds.y() - content.y() - 34));

        int leftW = Math.min(220, body.width() / 2);
        EscRect left = new EscRect(body.x() + 8, body.y() + 4, leftW, body.height() - 8);
        EscRect right = new EscRect(left.right() + 10, body.y() + 4,
            Math.max(120, body.right() - left.right() - 18), body.height() - 8);

        // Wheel + SV + preview share the left column's height, so they scale to it.
        int budget = Math.max(72, left.height() - PREVIEW_H - GAP * 2);
        int wheelD = Math.min(left.width(), Math.round(budget * 0.58f));
        wheelOuter = clampInt(wheelD / 2, WHEEL_OUTER_MIN, WHEEL_OUTER_MAX);
        wheelInner = Math.max(8, Math.round(wheelOuter * WHEEL_INNER_RATIO));
        svSize = clampInt(Math.min(left.width() - 24, budget - wheelOuter * 2), SV_MIN, SV_MAX);

        int wheelCx = left.x() + left.width() / 2;
        int wheelCy = left.y() + wheelOuter + 4;
        wheelArea = new EscRect(wheelCx - wheelOuter, wheelCy - wheelOuter, wheelOuter * 2, wheelOuter * 2);
        svArea = new EscRect(left.x() + (left.width() - svSize) / 2, wheelArea.bottom() + GAP, svSize, svSize);
        previewArea = new EscRect(
            left.x() + 8,
            Math.min(svArea.bottom() + GAP, Math.max(left.y(), left.bottom() - PREVIEW_H)),
            left.width() - 16,
            PREVIEW_H
        );

        int fieldW = Math.min(90, (right.width() - 24) / 2);
        int fy = right.y() + 4;
        int lh = EscSearchBox.recommendedHeight(font) + 4;

        hexBox = addField(new EscRect(right.x(), fy, right.width() - 4, EscSearchBox.recommendedHeight(font)), "HEX");
        fy += lh + 4;
        rBox = addField(new EscRect(right.x(), fy, fieldW, EscSearchBox.recommendedHeight(font)), "R");
        gBox = addField(new EscRect(right.x() + fieldW + 8, fy, fieldW, EscSearchBox.recommendedHeight(font)), "G");
        fy += lh;
        bBox = addField(new EscRect(right.x(), fy, fieldW, EscSearchBox.recommendedHeight(font)), "B");
        aBox = addField(new EscRect(right.x() + fieldW + 8, fy, fieldW, EscSearchBox.recommendedHeight(font)), "A");
        fy += lh + 4;
        hBox = addField(new EscRect(right.x(), fy, fieldW, EscSearchBox.recommendedHeight(font)), "H");
        sBox = addField(new EscRect(right.x() + fieldW + 8, fy, fieldW, EscSearchBox.recommendedHeight(font)), "S");
        fy += lh;
        vBox = addField(new EscRect(right.x(), fy, fieldW, EscSearchBox.recommendedHeight(font)), "V");

        // Swatch rows are only placed while they still clear the footer; a row that
        // does not fit is left null and skipped rather than drawn over the buttons.
        int labelH = font.lineHeight + 2;
        int rowStep = labelH + CHIP + 4;
        int cursor = fy + lh + 4;
        recentArea = placeChipRow(right, cursor, labelH);
        cursor += rowStep;
        savedArea = placeChipRow(right, cursor, labelH);
        cursor += rowStep;
        presetArea = placeChipRow(right, cursor, labelH);
        if (presetArea == null && savedArea != null) {
            // Presets are worth keeping even when the column is a few pixels short.
            int top = right.bottom() - CHIP;
            if (top - labelH > savedArea.bottom() + 2) {
                presetArea = new EscRect(right.x(), top, right.width() - 4, CHIP);
            }
        }

        wireFieldListeners();
        syncFieldsFromWorking();

        EscRect[] slots = EscButtonBar.equalRow(footerBounds, 4, footerBounds.y() + 6, EscButtonBar.DEFAULT_HEIGHT, 4, 4);
        addButton(EscText.literal("Reset"), slots[0], b -> {
            EscSound.play(EscUiCue.SELECT);
            if (onReset != null) {
                onReset.run();
            }
            setFromArgb(initialArgb);
            syncFieldsFromWorking();
        });
        addButton(EscText.literal("Save"), slots[1], b -> {
            EscSound.play(EscUiCue.NOTIFICATION);
            EscThemeConfigs.addSavedColor(workingArgb);
            rebuild();
        });
        addButton(EscText.literal("Apply"), slots[2], b -> {
            EscSound.play(EscUiCue.CONFIRM);
            EscThemeConfigs.pushRecentColor(workingArgb);
            if (onApply != null) {
                onApply.accept(workingArgb);
            }
            Minecraft.getInstance().setScreen(parent);
        });
        addButton(EscText.literal("Cancel"), slots[3], b -> {
            EscSound.play(EscUiCue.BACK);
            Minecraft.getInstance().setScreen(parent);
        });
    }

    /** Chip row with its caption above, or null when it would spill past {@code column}. */
    private static EscRect placeChipRow(EscRect column, int top, int labelH) {
        if (top + labelH + CHIP > column.bottom()) {
            return null;
        }
        return new EscRect(column.x(), top + labelH, column.width() - 4, CHIP);
    }

    private static int clampInt(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }

    private EscSearchBox addField(EscRect bounds, String hint) {
        EscSearchBox box = addSearchBox(bounds, EscText.literal(hint), EscText.literal(hint));
        box.setMaxLength(8);
        return box;
    }

    private void wireFieldListeners() {
        hexBox.setResponder(s -> {
            if (syncingFields) return;
            Integer parsed = EscColor.parseHex(s);
            if (parsed != null) {
                setFromArgb(parsed);
                syncFieldsFromWorking(hexBox);
            }
        });
        rBox.setResponder(s -> applyRgbFromFields());
        gBox.setResponder(s -> applyRgbFromFields());
        bBox.setResponder(s -> applyRgbFromFields());
        aBox.setResponder(s -> applyRgbFromFields());
        hBox.setResponder(s -> applyHsvFromFields());
        sBox.setResponder(s -> applyHsvFromFields());
        vBox.setResponder(s -> applyHsvFromFields());
    }

    private void applyRgbFromFields() {
        if (syncingFields) return;
        Integer r = parseByte(rBox.getValue());
        Integer g = parseByte(gBox.getValue());
        Integer b = parseByte(bBox.getValue());
        Integer a = parseByte(aBox.getValue());
        if (r == null || g == null || b == null) return;
        alpha = a == null ? 255 : a;
        setFromArgb(EscColor.withAlpha((r << 16) | (g << 8) | b, alpha));
        syncFieldsFromWorking(rBox, gBox, bBox, aBox);
    }

    private void applyHsvFromFields() {
        if (syncingFields) return;
        Float h = parseFloat(hBox.getValue());
        Float s = parseFloat(sBox.getValue());
        Float v = parseFloat(vBox.getValue());
        if (h == null || s == null || v == null) return;
        hue = ((h % 360f) + 360f) % 360f;
        sat = clamp01(s > 1f ? s / 100f : s);
        val = clamp01(v > 1f ? v / 100f : v);
        rebuildWorking();
        syncFieldsFromWorking(hBox, sBox, vBox);
    }

    private void syncFieldsFromWorking(EscSearchBox... skip) {
        syncingFields = true;
        try {
            if (!isSkipped(hexBox, skip)) hexBox.setValue(EscColor.toHexRgb(workingArgb));
            if (!isSkipped(rBox, skip)) rBox.setValue(String.valueOf(EscColor.red(workingArgb)));
            if (!isSkipped(gBox, skip)) gBox.setValue(String.valueOf(EscColor.green(workingArgb)));
            if (!isSkipped(bBox, skip)) bBox.setValue(String.valueOf(EscColor.blue(workingArgb)));
            if (!isSkipped(aBox, skip)) aBox.setValue(String.valueOf(alpha));
            if (!isSkipped(hBox, skip)) hBox.setValue(String.format(Locale.ROOT, "%.0f", hue));
            if (!isSkipped(sBox, skip)) sBox.setValue(String.format(Locale.ROOT, "%.0f", sat * 100f));
            if (!isSkipped(vBox, skip)) vBox.setValue(String.format(Locale.ROOT, "%.0f", val * 100f));
        } finally {
            syncingFields = false;
        }
    }

    private static boolean isSkipped(EscSearchBox box, EscSearchBox[] skip) {
        for (EscSearchBox s : skip) {
            if (s == box) return true;
        }
        return false;
    }

    private static Integer parseByte(String raw) {
        try {
            int v = Integer.parseInt(raw.trim());
            if (v < 0 || v > 255) return null;
            return v;
        } catch (Exception e) {
            return null;
        }
    }

    private static Float parseFloat(String raw) {
        try {
            return Float.parseFloat(raw.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private static float clamp01(float v) {
        if (v <= 0f) return 0f;
        if (v >= 1f) return 1f;
        return v;
    }

    private void rebuild() {
        clearWidgets();
        buildLayout();
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        renderWorldDim(g);
        EscRect content = contentRect();
        EscBackground.render(g, content, style.theme());
        EscTypography.drawCentered(g, font, heading.toUpperCase(Locale.ROOT),
            content.x() + content.width() / 2, content.y() + 6, style, EscTypeRole.TITLE, 1f);
        EscTypography.draw(g, font, "Wheel · SV · HEX / RGB / HSV · LMB drag · Apply saves",
            content.x(), content.y() + 18, style, EscTypeRole.META, content.width(), 1f);

        if (wheelArea != null) {
            drawHueWheel(g);
            drawSvSquare(g);
            drawPreview(g);
            drawChipRow(g, recentArea, EscThemeConfigs.recentColors(), "Recent");
            drawChipRow(g, savedArea, EscThemeConfigs.savedColors(), "Saved");
            drawPresetRow(g);
        }

        EscPanel.renderPanel(g, footerBounds, style);
        EscCrtLayer.render(g, content, style.theme());
        super.render(g, mouseX, mouseY, partialTick);
    }

    private void drawHueWheel(GuiGraphics g) {
        int cx = wheelArea.x() + wheelArea.width() / 2;
        int cy = wheelArea.y() + wheelArea.height() / 2;
        for (int a = 0; a < 360; a += 2) {
            double rad = Math.toRadians(a - 90);
            int color = EscColor.fromHsv(a, 1f, 1f, 255);
            for (int r = wheelInner; r <= wheelOuter; r += 2) {
                int x = cx + (int) Math.round(Math.cos(rad) * r);
                int y = cy + (int) Math.round(Math.sin(rad) * r);
                g.fill(x, y, x + 2, y + 2, color);
            }
        }
        double markerRad = Math.toRadians(hue - 90);
        int mx = cx + (int) Math.round(Math.cos(markerRad) * ((wheelInner + wheelOuter) / 2.0));
        int my = cy + (int) Math.round(Math.sin(markerRad) * ((wheelInner + wheelOuter) / 2.0));
        EscPanel.border(g, new EscRect(mx - 3, my - 3, 6, 6), 0xFFFFFFFF, 1);

        int half = Math.max(6, Math.round(wheelInner * 0.6f));
        EscRect swatch = new EscRect(cx - half, cy - half, half * 2, half * 2);
        EscPanel.fill(g, swatch, workingArgb);
        EscPanel.border(g, swatch, style.panelBorderColor(), 1);
    }

    private void drawSvSquare(GuiGraphics g) {
        for (int y = 0; y < svSize; y += 2) {
            float vv = 1f - y / (float) Math.max(1, svSize - 1);
            for (int x = 0; x < svSize; x += 2) {
                float ss = x / (float) Math.max(1, svSize - 1);
                int c = EscColor.fromHsv(hue, ss, vv, 255);
                g.fill(svArea.x() + x, svArea.y() + y, svArea.x() + x + 2, svArea.y() + y + 2, c);
            }
        }
        EscPanel.border(g, svArea, style.focusBorderColor(), 1);
        int px = svArea.x() + Math.round(sat * (svSize - 1));
        int py = svArea.y() + Math.round((1f - val) * (svSize - 1));
        EscPanel.border(g, new EscRect(px - 3, py - 3, 6, 6), 0xFFFFFFFF, 1);
        EscPanel.border(g, new EscRect(px - 2, py - 2, 4, 4), 0xFF000000, 1);
    }

    private void drawPreview(GuiGraphics g) {
        EscPanel.fill(g, previewArea, workingArgb);
        EscPanel.border(g, previewArea, style.panelBorderColor(), 1);
        String label = "#" + EscColor.toHexRgb(workingArgb)
            + "  RGB(" + EscColor.red(workingArgb) + "," + EscColor.green(workingArgb) + "," + EscColor.blue(workingArgb) + ")";
        int textColor = val > 0.55f ? 0xFF101010 : 0xFFE8E8E8;
        g.drawString(font, label, previewArea.x() + 6, previewArea.y() + (previewArea.height() - font.lineHeight) / 2, textColor, false);
    }

    private void drawChipRow(GuiGraphics g, EscRect area, List<Integer> colors, String title) {
        if (area == null) return;
        g.drawString(font, title, area.x(), area.y() - font.lineHeight - 2, style.textColorBody() | 0xFF000000, false);
        int x = area.x();
        for (int i = 0; i < colors.size() && i < 10; i++) {
            EscRect chip = new EscRect(x, area.y(), CHIP, CHIP);
            EscPanel.fill(g, chip, colors.get(i));
            EscPanel.border(g, chip, style.panelBorderColor(), 1);
            x += CHIP + 4;
        }
    }

    private void drawPresetRow(GuiGraphics g) {
        if (presetArea == null) return;
        g.drawString(font, "Presets (RMB on hub = cycle)", presetArea.x(),
            presetArea.y() - font.lineHeight - 2, style.textColorBody() | 0xFF000000, false);
        int x = presetArea.x();
        for (EscThemeSwatch sw : EscThemeSwatch.values()) {
            EscRect chip = new EscRect(x, presetArea.y(), CHIP, CHIP);
            int color = switch (role == null ? EscColorRole.ACCENT : role) {
                case BORDER -> sw.borderArgb();
                case TEXT -> EscColor.opaque(sw.titleRgb());
                case ACCENT -> sw.accentArgb();
            };
            EscPanel.fill(g, chip, color);
            EscPanel.border(g, chip, style.panelBorderColor(), 1);
            x += CHIP + 3;
            if (x + CHIP > presetArea.right()) break;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (hitWheel(mouseX, mouseY)) {
                draggingWheel = true;
                applyWheel(mouseX, mouseY);
                return true;
            }
            if (svArea != null && svArea.contains(mouseX, mouseY)) {
                draggingSv = true;
                applySv(mouseX, mouseY);
                return true;
            }
            if (pickChip(mouseX, mouseY, recentArea, EscThemeConfigs.recentColors())) return true;
            if (pickChip(mouseX, mouseY, savedArea, EscThemeConfigs.savedColors())) return true;
            if (pickPreset(mouseX, mouseY)) return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean pickChip(double mx, double my, EscRect area, List<Integer> colors) {
        if (area == null || colors.isEmpty()) return false;
        int x = area.x();
        for (int i = 0; i < colors.size() && i < 10; i++) {
            EscRect chip = new EscRect(x, area.y(), CHIP, CHIP);
            if (chip.contains(mx, my)) {
                setFromArgb(colors.get(i));
                syncFieldsFromWorking();
                EscSound.play(EscUiCue.SELECT);
                return true;
            }
            x += CHIP + 4;
        }
        return false;
    }

    private boolean pickPreset(double mx, double my) {
        if (presetArea == null) return false;
        int x = presetArea.x();
        for (EscThemeSwatch sw : EscThemeSwatch.values()) {
            EscRect chip = new EscRect(x, presetArea.y(), CHIP, CHIP);
            if (chip.contains(mx, my)) {
                int color = switch (role == null ? EscColorRole.ACCENT : role) {
                    case BORDER -> sw.borderArgb();
                    case TEXT -> EscColor.opaque(sw.titleRgb());
                    case ACCENT -> sw.accentArgb();
                };
                setFromArgb(color);
                syncFieldsFromWorking();
                EscSound.play(EscUiCue.SELECT);
                return true;
            }
            x += CHIP + 3;
            if (x + CHIP > savedArea.right()) break;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button == 0) {
            if (draggingWheel) {
                applyWheel(mouseX, mouseY);
                return true;
            }
            if (draggingSv) {
                applySv(mouseX, mouseY);
                return true;
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        draggingWheel = false;
        draggingSv = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private boolean hitWheel(double mx, double my) {
        if (wheelArea == null) return false;
        int cx = wheelArea.x() + wheelArea.width() / 2;
        int cy = wheelArea.y() + wheelArea.height() / 2;
        double dx = mx - cx;
        double dy = my - cy;
        double dist = Math.sqrt(dx * dx + dy * dy);
        return dist >= wheelInner - 2 && dist <= wheelOuter + 2;
    }

    private void applyWheel(double mx, double my) {
        int cx = wheelArea.x() + wheelArea.width() / 2;
        int cy = wheelArea.y() + wheelArea.height() / 2;
        double angle = Math.toDegrees(Math.atan2(my - cy, mx - cx)) + 90.0;
        hue = (float) ((angle % 360.0 + 360.0) % 360.0);
        rebuildWorking();
        syncFieldsFromWorking();
    }

    private void applySv(double mx, double my) {
        float nx = (float) ((mx - svArea.x()) / Math.max(1, svSize - 1));
        float ny = (float) ((my - svArea.y()) / Math.max(1, svSize - 1));
        sat = clamp01(nx);
        val = clamp01(1f - ny);
        rebuildWorking();
        syncFieldsFromWorking();
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
