package uk.co.extraspecialstudio.extraspecial.esc.ui;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;
import uk.co.extraspecialstudio.extraspecial.esc.sound.EscSound;
import uk.co.extraspecialstudio.extraspecial.esc.sound.EscUiCue;
import uk.co.extraspecialstudio.extraspecial.esc.theme.EscFrameStyle;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.IntConsumer;
import java.util.function.Supplier;

/**
 * Reusable hold-to-aim radial action wheel for ESC consumers.
 * <p>
 * Register once from client setup, pass a {@link KeyMapping}, options, and confirm callback.
 * ESC owns tick + overlay rendering — feature mods must not re-implement the wheel chrome.
 *
 * <pre>{@code
 * EscActionWheel.register("mymod.orders", MY_KEY)
 *     .options(() -> List.of(
 *         new EscActionWheelOption("mymod.opt.a", "mymod.blurb.a"),
 *         new EscActionWheelOption("mymod.opt.b", "mymod.blurb.b")))
 *     .canOpen(() -> hasTargets())
 *     .onConfirm(index -> sendOrder(index))
 *     .hubSubtitle(() -> Component.literal(count + " ready"));
 * }</pre>
 */
public final class EscActionWheel {
    private static final Map<String, EscActionWheel> REGISTRY = new LinkedHashMap<>();
    private static EscActionWheel openWheel;
    /** Hotbar slot to restore while a wheel owns input (digit hotkeys must not switch slots). */
    private static int lockedHotbarSlot = -1;

    private static final double DEADZONE_PX = 28.0;
    private static final double HYSTERESIS_FRAC = 0.18;
    private static final int RING_R = 62;
    private static final int CHIP_W = 58;
    private static final int CHIP_H = 18;
    private static final int OPEN_ANIM_TICKS = 5;
    private static final long DOUBLE_TAP_MS = 280L;
    private static final int[] DIGIT_KEYS = {
        GLFW.GLFW_KEY_1, GLFW.GLFW_KEY_2, GLFW.GLFW_KEY_3, GLFW.GLFW_KEY_4, GLFW.GLFW_KEY_5,
        GLFW.GLFW_KEY_6, GLFW.GLFW_KEY_7, GLFW.GLFW_KEY_8, GLFW.GLFW_KEY_9
    };

    private final String id;
    private final KeyMapping key;
    private Supplier<List<EscActionWheelOption>> options = List::of;
    private BooleanSupplier canOpen = () -> true;
    private IntConsumer onConfirm = i -> {};
    private Supplier<Component> hubSubtitle = () -> Component.empty();
    private Supplier<Component> cancelLabel = () -> Component.translatable("esc.action_wheel.cancel");
    private Supplier<Component> idleHint = () -> Component.translatable("esc.action_wheel.hint");
    private boolean doubleTapRepeat = true;
    private boolean releaseMouse = true;
    private boolean actionBarOnConfirm = true;

    private boolean open;
    private boolean wasDown;
    private boolean mouseReleased;
    private int hoverIndex = -1;
    private int lastHoverIndex = -1;
    private int openAnim;
    private int lastConfirmedIndex = -1;
    private long lastConfirmMs;
    private boolean suppressReleaseConfirm;
    private boolean ignoreUntilRelease;
    private boolean[] hotWasDown = new boolean[0];
    private List<EscActionWheelOption> cachedOptions = List.of();

    private EscActionWheel(String id, KeyMapping key) {
        this.id = Objects.requireNonNull(id, "id");
        this.key = Objects.requireNonNull(key, "key");
    }

    /**
     * Register (or replace) a wheel. Returns the instance for fluent configuration.
     * Safe to call from client setup; not from common constructors.
     */
    public static EscActionWheel register(String id, KeyMapping key) {
        EscActionWheel wheel = new EscActionWheel(id, key);
        EscActionWheel prev = REGISTRY.put(id, wheel);
        if (prev != null && prev.open) {
            prev.forceReset();
        }
        return wheel;
    }

    public static EscActionWheel get(String id) {
        return REGISTRY.get(id);
    }

    public static boolean anyOpen() {
        return openWheel != null && openWheel.open;
    }

    public static void forceResetAll() {
        for (EscActionWheel w : REGISTRY.values()) {
            w.forceReset();
        }
        openWheel = null;
        lockedHotbarSlot = -1;
    }

    public EscActionWheel options(Supplier<List<EscActionWheelOption>> options) {
        this.options = options != null ? options : List::of;
        return this;
    }

    public EscActionWheel options(List<EscActionWheelOption> fixed) {
        List<EscActionWheelOption> copy = List.copyOf(fixed != null ? fixed : List.of());
        this.options = () -> copy;
        return this;
    }

    public EscActionWheel canOpen(BooleanSupplier canOpen) {
        this.canOpen = canOpen != null ? canOpen : () -> true;
        return this;
    }

    public EscActionWheel onConfirm(IntConsumer onConfirm) {
        this.onConfirm = onConfirm != null ? onConfirm : i -> {};
        return this;
    }

    public EscActionWheel hubSubtitle(Supplier<Component> hubSubtitle) {
        this.hubSubtitle = hubSubtitle != null ? hubSubtitle : () -> Component.empty();
        return this;
    }

    public EscActionWheel cancelLabel(Supplier<Component> cancelLabel) {
        this.cancelLabel = cancelLabel != null ? cancelLabel : () -> Component.translatable("esc.action_wheel.cancel");
        return this;
    }

    public EscActionWheel idleHint(Supplier<Component> idleHint) {
        this.idleHint = idleHint != null ? idleHint : () -> Component.translatable("esc.action_wheel.hint");
        return this;
    }

    public EscActionWheel doubleTapRepeat(boolean enabled) {
        this.doubleTapRepeat = enabled;
        return this;
    }

    public EscActionWheel releaseMouse(boolean enabled) {
        this.releaseMouse = enabled;
        return this;
    }

    public EscActionWheel actionBarOnConfirm(boolean enabled) {
        this.actionBarOnConfirm = enabled;
        return this;
    }

    public String id() {
        return id;
    }

    public boolean isOpen() {
        return open;
    }

    public void forceReset() {
        closeWheel(true);
        wasDown = false;
        ignoreUntilRelease = false;
        suppressReleaseConfirm = false;
        if (openWheel == this) openWheel = null;
    }

    /** Called by ESC client bus — do not call from feature mods. */
    static void tickAll() {
        Minecraft mc = Minecraft.getInstance();
        // Reclaim cursor after a forced close that happened while a screen was open
        // (restoreMouse no-ops when mc.screen != null, leaving mouseReleased stuck).
        for (EscActionWheel w : REGISTRY.values()) {
            if (w.mouseReleased && !w.open && mc.screen == null) {
                w.restoreMouse(mc);
            }
        }
        if (mc.player == null || mc.level == null || mc.screen != null || mc.options.hideGui) {
            if (openWheel != null) openWheel.forceReset();
            for (EscActionWheel w : REGISTRY.values()) {
                w.wasDown = false;
                w.ignoreUntilRelease = false;
            }
            lockedHotbarSlot = -1;
            return;
        }
        // Prefer the currently open wheel; sync other wheels' key edges so they
        // don't reopen (or miss a release) from stale wasDown when this one closes.
        if (openWheel != null) {
            openWheel.tickInstance(mc);
            for (EscActionWheel w : REGISTRY.values()) {
                if (w != openWheel) {
                    w.wasDown = w.key.isDown();
                }
            }
            lockHotbarWhileOpen(mc);
            return;
        }
        for (EscActionWheel w : REGISTRY.values()) {
            w.tickInstance(mc);
            if (openWheel != null) {
                for (EscActionWheel other : REGISTRY.values()) {
                    if (other != openWheel) {
                        other.wasDown = other.key.isDown();
                    }
                }
                lockHotbarWhileOpen(mc);
                return;
            }
        }
        lockedHotbarSlot = -1;
    }

    /**
     * Re-apply hotbar lock. Call from client tick Pre as well as Post —
     * vanilla keybinds run between them and will otherwise leave the wrong
     * slot selected for most of the tick.
     */
    static void relockHotbarIfOpen() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        lockHotbarWhileOpen(mc);
    }

    private static void lockHotbarWhileOpen(Minecraft mc) {
        if (mc.player == null || openWheel == null || !openWheel.open) {
            lockedHotbarSlot = -1;
            return;
        }
        if (lockedHotbarSlot < 0) {
            lockedHotbarSlot = mc.player.getInventory().getSelectedSlot();
        } else if (mc.player.getInventory().getSelectedSlot() != lockedHotbarSlot) {
            mc.player.getInventory().setSelectedSlot(lockedHotbarSlot);
        }
    }

    /** Called by ESC overlay registration — do not call from feature mods. */
    static void renderAll(GuiGraphicsExtractor g, int screenWidth, int screenHeight) {
        if (openWheel != null && openWheel.open) {
            openWheel.renderInstance(g, screenWidth, screenHeight);
        }
    }

    private void tickInstance(Minecraft mc) {
        boolean down = key.isDown();
        boolean allowed = canOpen.getAsBoolean();
        long now = System.currentTimeMillis();

        if (ignoreUntilRelease) {
            if (!down) ignoreUntilRelease = false;
            wasDown = down;
            return;
        }

        if (down && !wasDown) {
            if (allowed) {
                refreshOptions();
                if (cachedOptions.isEmpty()) {
                    wasDown = down;
                    return;
                }
                if (doubleTapRepeat && lastConfirmedIndex >= 0 && lastConfirmMs > 0
                    && now - lastConfirmMs <= DOUBLE_TAP_MS
                    && lastConfirmedIndex < cachedOptions.size()) {
                    confirmOrder(lastConfirmedIndex);
                    suppressReleaseConfirm = true;
                    lastConfirmMs = 0;
                    wasDown = down;
                    return;
                }
                if (openWheel != null && openWheel != this) {
                    openWheel.forceReset();
                }
                openWheel();
            }
        } else if (!down && wasDown) {
            if (suppressReleaseConfirm) {
                suppressReleaseConfirm = false;
            } else if (open) {
                confirmOrCancel();
            }
        } else if (open && down) {
            if (openAnim > 0) openAnim--;
            long window = mc.getWindow().handle();
            // Cancel without confirming: RMB while held. Primary cancel is releasing
            // the wheel key in the deadzone (handled on key-up via confirmOrCancel).
            if (GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS) {
                closeWheel(true);
                ignoreUntilRelease = true;
                wasDown = down;
                return;
            }
            updateHover(mc);
            if (hoverIndex != lastHoverIndex) {
                if (hoverIndex >= 0) EscSound.playFocus();
                lastHoverIndex = hoverIndex;
            }
            int hot = pollHotkeyEdge(mc);
            if (hot >= 0 && hot < cachedOptions.size()) {
                hoverIndex = hot;
                confirmOrCancel();
                // Keep holding the wheel key from immediately re-opening.
                ignoreUntilRelease = true;
                wasDown = true;
                return;
            }
        }

        if (open && !allowed) {
            closeWheel(true);
            // Key may still be held — wait for release so we don't edge-open again.
            if (down) ignoreUntilRelease = true;
        }
        wasDown = down;
    }

    private void refreshOptions() {
        List<EscActionWheelOption> list = options.get();
        if (list == null || list.isEmpty()) {
            cachedOptions = List.of();
        } else {
            cachedOptions = List.copyOf(list);
        }
        int n = Math.min(DIGIT_KEYS.length, cachedOptions.size());
        if (hotWasDown.length != n) {
            hotWasDown = new boolean[n];
        }
    }

    private void openWheel() {
        open = true;
        openWheel = this;
        // Always start on cancel. Sticky-selecting lastConfirmed made a quick
        // open-and-release re-fire the previous order without aiming; double-tap
        // repeat already covers intentional re-orders. Last choice stays visible
        // via chip "last" styling.
        hoverIndex = -1;
        lastHoverIndex = -1;
        openAnim = OPEN_ANIM_TICKS;
        suppressReleaseConfirm = false;
        // Seed from current key state so a digit already held (hotbar) does not
        // instantly confirm on the first open tick.
        Minecraft mc = Minecraft.getInstance();
        var window = mc.getWindow();
        for (int i = 0; i < hotWasDown.length; i++) {
            hotWasDown[i] = InputConstants.isKeyDown(window, DIGIT_KEYS[i]);
        }
        if (mc.player != null) {
            lockedHotbarSlot = mc.player.getInventory().getSelectedSlot();
        }
        EscSound.play(EscUiCue.EXPAND);
        if (releaseMouse && !mouseReleased) {
            mc.mouseHandler.releaseMouse();
            mouseReleased = true;
        }
    }

    private void restoreMouse(Minecraft mc) {
        if (mouseReleased && mc.screen == null) {
            mc.mouseHandler.grabMouse();
            mouseReleased = false;
        }
    }

    private int pollHotkeyEdge(Minecraft mc) {
        var window = mc.getWindow();
        int pressed = -1;
        for (int i = 0; i < hotWasDown.length; i++) {
            boolean d = InputConstants.isKeyDown(window, DIGIT_KEYS[i]);
            if (d && !hotWasDown[i] && pressed < 0) pressed = i;
            hotWasDown[i] = d;
        }
        return pressed;
    }

    private void updateHover(Minecraft mc) {
        int guiW = mc.getWindow().getGuiScaledWidth();
        int guiH = mc.getWindow().getGuiScaledHeight();
        int screenW = mc.getWindow().getScreenWidth();
        int screenH = mc.getWindow().getScreenHeight();
        int count = cachedOptions.size();
        if (screenW <= 0 || screenH <= 0 || count <= 0) {
            hoverIndex = -1;
            return;
        }
        double mx = mc.mouseHandler.xpos() * guiW / (double) screenW;
        double my = mc.mouseHandler.ypos() * guiH / (double) screenH;
        double cx = guiW / 2.0;
        double cy = guiH / 2.0;
        double dx = mx - cx;
        double dy = my - cy;
        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist < DEADZONE_PX) {
            hoverIndex = -1;
            return;
        }
        double angle = Math.atan2(dx, -dy);
        if (angle < 0) angle += Math.PI * 2.0;
        double step = Math.PI * 2.0 / count;
        int raw = (int) Math.floor(angle / step) % count;
        if (hoverIndex >= 0 && raw != hoverIndex) {
            double intoRaw = angle - raw * step;
            if (intoRaw < 0) intoRaw += Math.PI * 2.0;
            intoRaw = intoRaw % step;
            if (intoRaw / step < HYSTERESIS_FRAC) {
                return;
            }
        }
        hoverIndex = raw;
    }

    private void confirmOrCancel() {
        int selected = hoverIndex;
        closeWheel(true);
        if (selected < 0 || selected >= cachedOptions.size()) return;
        confirmOrder(selected);
    }

    private void confirmOrder(int selected) {
        if (!canOpen.getAsBoolean()) return;
        if (selected < 0 || selected >= cachedOptions.size()) return;
        lastConfirmedIndex = selected;
        lastConfirmMs = System.currentTimeMillis();
        onConfirm.accept(selected);
        EscSound.play(EscUiCue.CONFIRM);
        if (actionBarOnConfirm) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                mc.player.sendOverlayMessage(cachedOptions.get(selected).label());
            }
        }
    }

    private void closeWheel(boolean restoreCursor) {
        boolean wasCurrent = openWheel == this;
        open = false;
        if (wasCurrent) {
            openWheel = null;
            lockedHotbarSlot = -1;
        }
        hoverIndex = -1;
        lastHoverIndex = -1;
        openAnim = 0;
        for (int i = 0; i < hotWasDown.length; i++) hotWasDown[i] = false;
        if (restoreCursor) {
            Minecraft mc = Minecraft.getInstance();
            if (mc != null) restoreMouse(mc);
        }
    }

    private void renderInstance(GuiGraphicsExtractor g, int screenWidth, int screenHeight) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;
        List<EscActionWheelOption> opts = cachedOptions;
        int count = opts.size();
        if (count <= 0) return;

        EscUiStyle style = EscUiStyle.active();
        int cx = screenWidth / 2;
        int cy = screenHeight / 2;

        float anim = 1f - (openAnim / (float) OPEN_ANIM_TICKS);
        anim = Mth.clamp(anim, 0f, 1f);
        anim = 1f - (1f - anim) * (1f - anim);
        int dimAlpha = (int) (0x66 * anim);
        EscPanel.fill(g, new EscRect(0, 0, screenWidth, screenHeight), (dimAlpha << 24));

        float scale = 0.85f + 0.15f * anim;
        int ringR = Math.round(RING_R * scale);
        int innerR = Math.round(22 * scale);

        if (hoverIndex >= 0 && hoverIndex < count) {
            drawWedgeFan(g, cx, cy, innerR, ringR + 8, hoverIndex, count,
                EscPanel.withOpacity(style.accentColor(), 0.28f));
        }

        EscPanel.renderFrame(g,
            new EscRect(cx - ringR - 6, cy - ringR - 6, (ringR + 6) * 2, (ringR + 6) * 2),
            opaque(style.panelBorderColor()), 1, EscFrameStyle.SQUARE);

        for (int i = 0; i < count; i++) {
            double a = (Math.PI * 2.0 / count) * i;
            int x0 = cx + (int) Math.round(Math.sin(a) * (ringR - 18));
            int y0 = cy - (int) Math.round(Math.cos(a) * (ringR - 18));
            int x1 = cx + (int) Math.round(Math.sin(a) * (ringR + 2));
            int y1 = cy - (int) Math.round(Math.cos(a) * (ringR + 2));
            drawLine(g, x0, y0, x1, y1, EscPanel.withOpacity(style.panelBorderColor(), 0.5f));
        }

        for (int i = 0; i < count; i++) {
            boolean hot = i == hoverIndex;
            boolean last = i == lastConfirmedIndex;
            double mid = wedgeMidAngle(i, count);
            int lx = cx + (int) Math.round(Math.sin(mid) * ringR);
            int ly = cy - (int) Math.round(Math.cos(mid) * ringR);
            int chipW = Math.round(CHIP_W * scale);
            int chipH = Math.round(CHIP_H * scale);
            EscRect chip = new EscRect(lx - chipW / 2, ly - chipH / 2, chipW, chipH);
            int fill = EscPanel.withOpacity(style.panelFillColor(),
                Math.min(1f, style.panelOpacity() + (hot ? 0.22f : 0.08f)));
            if (hot) {
                fill = EscPanel.withOpacity(style.accentColor(), 0.58f);
            } else if (last) {
                fill = EscPanel.withOpacity(style.secondaryColor(), 0.35f);
            }
            EscPanel.fill(g, chip, fill);
            int border = hot ? style.focusBorderColor()
                : (last ? style.accentColor() : style.panelBorderColor());
            EscPanel.renderFrame(g, chip, border, hot ? 2 : 1, EscFrameStyle.SQUARE);

            if (i < DIGIT_KEYS.length) {
                EscText.drawString(g, mc.font, String.valueOf(i + 1), chip.x() + 2, chip.y() + 2,
                    opaque(style.mutedColor()), false, EscFonts.DEFAULT);
            }

            String label = opts.get(i).label().getString();
            int maxLabelW = Math.max(8, chip.width() - 12);
            if (mc.font.width(label) > maxLabelW) {
                label = mc.font.plainSubstrByWidth(label, maxLabelW - mc.font.width("…")) + "…";
            }
            int color = hot ? opaque(style.textColorTitle()) : opaque(style.textColorBody());
            EscText.drawCentered(g, mc.font, EscText.literal(label),
                chip.x() + chip.width() / 2,
                chip.y() + Math.max(1, (chip.height() - 8) / 2),
                color, EscFonts.DEFAULT);
        }

        EscRect hub = new EscRect(cx - 56, cy - 26, 112, 52);
        EscPanel.fill(g, hub, EscPanel.withOpacity(style.panelFillColor(), 0.94f));
        EscPanel.renderFrame(g, hub,
            hoverIndex < 0 ? style.panelBorderColor() : style.focusBorderColor(),
            1, EscFrameStyle.SQUARE);

        Component center = hoverIndex < 0 || hoverIndex >= count
            ? cancelLabel.get()
            : opts.get(hoverIndex).label();
        EscText.drawCentered(g, mc.font, center, cx, cy - 14,
            opaque(style.textColorTitle()), EscFonts.DEFAULT);

        Component blurb = hoverIndex < 0 || hoverIndex >= count
            ? idleHint.get()
            : opts.get(hoverIndex).blurb();
        if (blurb != null && !blurb.getString().isEmpty()) {
            EscText.drawCentered(g, mc.font, blurb, cx, cy - 2,
                opaque(style.mutedColor()), EscFonts.DEFAULT);
        }

        Component sub = hubSubtitle.get();
        if (sub != null && !sub.getString().isEmpty()) {
            EscText.drawCentered(g, mc.font, sub, cx, cy + 10,
                opaque(style.secondaryColor()), EscFonts.DEFAULT);
        }
    }

    private static void drawWedgeFan(GuiGraphicsExtractor g, int cx, int cy, int inner, int outer,
                                     int index, int count, int argb) {
        double step = (Math.PI * 2.0) / count;
        double a0 = step * index;
        double a1 = step * (index + 1);
        for (int s = 0; s <= 14; s++) {
            double t = a0 + (a1 - a0) * (s / 14.0);
            int x0 = cx + (int) Math.round(Math.sin(t) * inner);
            int y0 = cy - (int) Math.round(Math.cos(t) * inner);
            int x1 = cx + (int) Math.round(Math.sin(t) * outer);
            int y1 = cy - (int) Math.round(Math.cos(t) * outer);
            drawLine(g, x0, y0, x1, y1, argb);
        }
    }

    private static void drawLine(GuiGraphicsExtractor g, int x0, int y0, int x1, int y1, int argb) {
        int steps = Math.max(Math.abs(x1 - x0), Math.abs(y1 - y0));
        if (steps <= 0) {
            EscPanel.fill(g, new EscRect(x0, y0, 1, 1), argb);
            return;
        }
        for (int i = 0; i <= steps; i++) {
            int x = x0 + (x1 - x0) * i / steps;
            int y = y0 + (y1 - y0) * i / steps;
            EscPanel.fill(g, new EscRect(x, y, 1, 1), argb);
        }
    }

    private static double wedgeMidAngle(int index, int count) {
        double step = (Math.PI * 2.0) / count;
        return step * index + step / 2.0;
    }

    private static int opaque(int rgbOrArgb) {
        return (rgbOrArgb & 0xFF000000) == 0 ? (0xFF000000 | (rgbOrArgb & 0xFFFFFF)) : rgbOrArgb;
    }
}
