package uk.co.extraspecialstudio.extraspecial.esc.ui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import uk.co.extraspecialstudio.extraspecial.esc.theme.EscTheme;
import uk.co.extraspecialstudio.extraspecial.esc.theme.EscVisualBudget;
import uk.co.extraspecialstudio.extraspecial.esc.ui.anim.EscAnim;
import uk.co.extraspecialstudio.extraspecial.esc.ui.anim.EscFocusPhase;
import uk.co.extraspecialstudio.extraspecial.esc.ui.anim.EscFocusVisual;
import uk.co.extraspecialstudio.extraspecial.esc.ui.layout.EscLayoutBands;

/**
 * Titled hub card with depth, focus phases, and optional icon.
 */
public final class EscCard {

    private EscCard() {
    }

    public static void render(
        GuiGraphics g,
        Font font,
        EscRect area,
        EscUiStyle style,
        String title,
        String subtitle,
        String badge,
        float focus01
    ) {
        render(g, font, area, style, title, subtitle, badge, focus01, null);
    }

    public static void render(
        GuiGraphics g,
        Font font,
        EscRect area,
        EscUiStyle style,
        String title,
        String subtitle,
        String badge,
        float focus01,
        ResourceLocation icon
    ) {
        // Tall slots with icons get the showcase identity layout.
        if (icon != null && area.height() >= 64) {
            renderIdentity(g, font, area, style, title, subtitle, badge, focus01, icon);
            return;
        }
        renderCompact(g, font, area, style, title, subtitle, badge, focus01, icon);
    }

    /**
     * Hub section showcase card — large icon, accent rule, badge chip.
     */
    public static void renderIdentity(
        GuiGraphics g,
        Font font,
        EscRect area,
        EscUiStyle style,
        String title,
        String subtitle,
        String badge,
        float focus01,
        ResourceLocation icon
    ) {
        float f = EscAnim.clamp01(focus01);
        EscFocusPhase phase = EscFocusPhase.fromFocus01(f);
        float motion = EscVisualBudget.motionScale(style.theme());
        float scale = EscAnim.lerp(0.88f, phase.scaleMul(), EscAnim.easeOutCubic(f) * Math.max(0.25f, motion));
        float alpha = EscAnim.lerp(0.5f, phase.alphaMul(), f);

        int cx = area.x() + area.width() / 2;
        int cy = area.y() + area.height() / 2;
        int w = Math.min(area.width(), Math.max(48, Math.round(area.width() * scale)));
        int h = Math.min(area.height(), Math.max(40, Math.round(area.height() * scale)));
        EscRect box = EscLayoutBands.clampInto(new EscRect(cx - w / 2, cy - h / 2, w, h), area);

        EscTheme theme = style.theme();
        float shadow = EscVisualBudget.shadow(theme) * phase.shadowMul();
        float glow = EscVisualBudget.glow(theme) * phase.glowMul();

        EscPanel.dropShadow(g, box, shadow);
        int fill = EscFocusVisual.applyAlpha(
            EscPanel.withOpacity(style.panelFillColor(), style.panelOpacity()), alpha);
        EscPanel.fill(g, box, fill);
        EscPanel.outerGlow(g, box, style.focusBorderColor(), glow);
        EscPanel.edgeHighlight(g, box, shadow * 0.65f);

        int borderBase = f > 0.55f ? style.focusBorderColor() : style.panelBorderColor();
        int border = EscFocusVisual.applyAlpha(
            f > 0.55f ? EscFocusVisual.brighten(borderBase, 0.18f) : borderBase, alpha);
        EscPanel.renderFrame(g, box, border, Math.max(1, style.panelBorderWidth()), style.frameStyle());

        if (EscVisualBudget.animatedBorders() && f > 0.65f) {
            float sweep = (System.currentTimeMillis() % 1800L) / 1800f;
            EscPanel.animatedBorderSweep(g, box, EscFocusVisual.applyAlpha(style.accentColor() | 0xFF000000, 0.9f), sweep, glow);
        }

        int titleColor = EscFocusVisual.applyAlpha(style.textColorTitle() | 0xFF000000, alpha);
        int bodyColor = EscFocusVisual.applyAlpha(style.textColorBody() | 0xFF000000, alpha);
        int accent = EscFocusVisual.applyAlpha(style.accentColor() | 0xFF000000, alpha);

        int iconSize = Math.min(32, Math.max(18, box.height() / 3));
        int contentLeft = box.x() + 12;
        int contentTop = box.y() + 10;
        if (icon != null) {
            EscImage.drawAuto(g, icon, contentLeft, contentTop, iconSize, iconSize, 0xFFFFFFFF, alpha);
            // Soft icon plate
            EscPanel.border(g, new EscRect(contentLeft - 2, contentTop - 2, iconSize + 4, iconSize + 4),
                EscFocusVisual.applyAlpha(style.panelBorderColor(), alpha * 0.7f), 1);
        }

        int textX = contentLeft + (icon != null ? iconSize + 10 : 0);
        int textMax = Math.max(0, box.right() - textX - 12);
        String t = title == null ? "" : title;
        boolean hasBadge = badge != null && !badge.isBlank();
        int chipTop = box.bottom() - (font.lineHeight + 4) - 8;
        int chipLeft = box.right() - 10 - (EscText.width(font, hasBadge ? badge : "") + 10);

        // Text is clipped to the card so nothing bleeds past the border; overflow marquees instead.
        g.enableScissor(box.x() + 1, box.y() + 1, box.right() - 1, box.bottom() - 1);
        try {
            EscText.drawScrollingString(g, font, t, textX, contentTop + 2, textMax, titleColor);

            // Accent rule under title when focused
            if (f > 0.4f) {
                int ruleW = Math.min(textMax, Math.max(24, EscText.width(font, t)));
                int ruleA = Math.max(40, Math.min(220, Math.round(180 * f * alpha)));
                int ruleColor = (ruleA << 24) | (style.accentColor() & 0xFFFFFF);
                int ruleY = contentTop + font.lineHeight + 5;
                if (ruleY + 2 <= box.bottom() - 2) {
                    g.fill(textX, ruleY, textX + ruleW, ruleY + 2, ruleColor);
                }
            }

            if (subtitle != null && !subtitle.isBlank()) {
                int subY = box.y() + Math.max(iconSize + 16, 36);
                if (subY + font.lineHeight <= box.bottom() - 2) {
                    EscText.drawScrollingString(g, font, subtitle, contentLeft, subY,
                        rowWidth(contentLeft, subY, font, box, hasBadge, chipTop, chipLeft), bodyColor);
                }
            }
        } finally {
            g.disableScissor();
        }

        if (badge != null && !badge.isBlank()) {
            drawBadgeChip(g, font, box, badge, accent, alpha, style);
        }
    }

    private static void renderCompact(
        GuiGraphics g,
        Font font,
        EscRect area,
        EscUiStyle style,
        String title,
        String subtitle,
        String badge,
        float focus01,
        ResourceLocation icon
    ) {
        float f = EscAnim.clamp01(focus01);
        EscFocusPhase phase = EscFocusPhase.fromFocus01(f);
        float motion = EscVisualBudget.motionScale(style.theme());
        float scale = EscAnim.lerp(0.86f, phase.scaleMul(), EscAnim.easeOutCubic(f) * Math.max(0.2f, motion));
        float alpha = EscAnim.lerp(0.45f, phase.alphaMul(), f);

        int cx = area.x() + area.width() / 2;
        int cy = area.y() + area.height() / 2;
        int w = Math.min(area.width(), Math.max(40, Math.round(area.width() * scale)));
        int h = Math.min(area.height(), Math.max(32, Math.round(area.height() * scale)));
        EscRect box = EscLayoutBands.clampInto(new EscRect(cx - w / 2, cy - h / 2, w, h), area);

        EscTheme theme = style.theme();
        float shadow = EscVisualBudget.shadow(theme) * phase.shadowMul();
        float glow = EscVisualBudget.glow(theme) * phase.glowMul();

        EscPanel.dropShadow(g, box, shadow);
        int fill = EscFocusVisual.applyAlpha(
            EscPanel.withOpacity(style.panelFillColor(), style.panelOpacity()), alpha);
        EscPanel.fill(g, box, fill);
        EscPanel.outerGlow(g, box, style.focusBorderColor(), glow);
        EscPanel.edgeHighlight(g, box, shadow * 0.6f);

        int borderBase = f > 0.55f ? style.focusBorderColor() : style.panelBorderColor();
        int border = EscFocusVisual.applyAlpha(
            f > 0.55f ? EscFocusVisual.brighten(borderBase, 0.15f) : borderBase, alpha);
        EscPanel.renderFrame(g, box, border, Math.max(1, style.panelBorderWidth()), style.frameStyle());

        if (EscVisualBudget.animatedBorders() && f > 0.7f) {
            float sweep = (System.currentTimeMillis() % 2000L) / 2000f;
            EscPanel.animatedBorderSweep(g, box, EscFocusVisual.applyAlpha(style.accentColor() | 0xFF000000, 0.85f), sweep, glow);
        }

        int titleColor = EscFocusVisual.applyAlpha(style.textColorTitle() | 0xFF000000, alpha);
        int bodyColor = EscFocusVisual.applyAlpha(style.textColorBody() | 0xFF000000, alpha);
        int accentColor = EscFocusVisual.applyAlpha(style.accentColor() | 0xFF000000, alpha);

        int textX = box.x() + 10;
        if (icon != null) {
            int iconSize = Math.min(20, box.height() / 3);
            EscImage.drawAuto(g, icon, box.x() + 8, box.y() + 8, iconSize, iconSize, 0xFFFFFFFF, alpha);
            textX = box.x() + 8 + iconSize + 6;
        }

        // On a short card the bottom-right badge chip sits level with the text, so those
        // rows have to give up its column; on a tall card they can use the full width.
        boolean hasBadge = badge != null && !badge.isBlank();
        int chipH = font.lineHeight + 4;
        int chipTop = box.bottom() - chipH - 8;
        int chipLeft = box.right() - 10 - (EscText.width(font, badge == null ? "" : badge) + 10);
        int ty = box.y() + 10;

        g.enableScissor(box.x() + 1, box.y() + 1, box.right() - 1, box.bottom() - 1);
        try {
            EscText.drawScrollingString(g, font, title == null ? "" : title, textX, ty,
                rowWidth(textX, ty, font, box, hasBadge, chipTop, chipLeft), titleColor);
            if (subtitle != null && !subtitle.isBlank()) {
                int subY = ty + font.lineHeight + 4;
                if (subY + font.lineHeight <= box.bottom() - 2) {
                    EscText.drawScrollingString(g, font, subtitle, textX, subY,
                        rowWidth(textX, subY, font, box, hasBadge, chipTop, chipLeft), bodyColor);
                }
            }
        } finally {
            g.disableScissor();
        }

        if (badge != null && !badge.isBlank()) {
            drawBadgeChip(g, font, box, badge, accentColor, alpha, style);
        }
    }

    /** Width available to a text row at {@code rowY}, minus the badge chip if they collide. */
    private static int rowWidth(int textX, int rowY, Font font, EscRect box,
                               boolean hasBadge, int chipTop, int chipLeft) {
        int right = box.right() - 8;
        if (hasBadge && rowY + font.lineHeight > chipTop) {
            right = Math.min(right, chipLeft - 4);
        }
        return Math.max(0, right - textX);
    }

    private static void drawBadgeChip(
        GuiGraphics g,
        Font font,
        EscRect box,
        String badge,
        int accentColor,
        float alpha,
        EscUiStyle style
    ) {
        int chipW = EscText.width(font, badge) + 10;
        int chipH = font.lineHeight + 4;
        int cx = box.right() - 10 - chipW;
        int cy = box.bottom() - chipH - 8;
        if (chipW <= 0 || cx < box.x() || cy < box.y()) {
            return;
        }
        int fill = EscFocusVisual.applyAlpha(EscPanel.withOpacity(style.panelFillColor(), 0.95f), alpha);
        EscPanel.fill(g, new EscRect(cx, cy, chipW, chipH), fill);
        EscPanel.border(g, new EscRect(cx, cy, chipW, chipH), accentColor, 1);
        EscText.drawString(g, font, badge, cx + 5, cy + 2, accentColor, false, EscFonts.DEFAULT);
    }

    public static boolean hit(EscRect area, double mx, double my, float focus01) {
        float f = EscAnim.clamp01(focus01);
        EscFocusPhase phase = EscFocusPhase.fromFocus01(f);
        float scale = EscAnim.lerp(0.86f, phase.scaleMul(), EscAnim.easeOutCubic(f));
        int cx = area.x() + area.width() / 2;
        int cy = area.y() + area.height() / 2;
        int w = Math.min(area.width(), Math.max(40, Math.round(area.width() * scale)));
        int h = Math.min(area.height(), Math.max(32, Math.round(area.height() * scale)));
        return EscLayoutBands.clampInto(new EscRect(cx - w / 2, cy - h / 2, w, h), area).contains(mx, my);
    }
}
