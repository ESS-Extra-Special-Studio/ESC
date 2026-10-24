package uk.co.extraspecialstudio.extraspecial.esc.ui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import uk.co.extraspecialstudio.extraspecial.esc.ui.anim.EscFocusVisual;

/**
 * Image / icon draw helpers for ESC cards and badges.
 */
public final class EscImage {

    private EscImage() {
    }

    /** Draw assuming a 16×16 source (vanilla item icons). */
    public static void draw(
        GuiGraphics g,
        ResourceLocation texture,
        int x,
        int y,
        int w,
        int h,
        int tintArgb,
        float alpha
    ) {
        draw(g, texture, x, y, w, h, 16, 16, tintArgb, alpha);
    }

    public static void draw(
        GuiGraphics g,
        ResourceLocation texture,
        int x,
        int y,
        int w,
        int h,
        int texW,
        int texH,
        int tintArgb,
        float alpha
    ) {
        if (texture == null || w <= 0 || h <= 0 || texW <= 0 || texH <= 0) {
            return;
        }
        int color = EscFocusVisual.applyAlpha(tintArgb | 0xFF000000, alpha);
        g.blit(RenderType.GUI_TEXTURED, texture, x, y, 0f, 0f, w, h, texW, texH, color);
    }

    /** Fit texture into a box preserving aspect (letterbox). */
    public static void drawContained(
        GuiGraphics g,
        ResourceLocation texture,
        EscRect box,
        int texW,
        int texH,
        int tintArgb,
        float alpha
    ) {
        if (texture == null || box.width() <= 0 || box.height() <= 0) {
            return;
        }
        float sx = box.width() / (float) Math.max(1, texW);
        float sy = box.height() / (float) Math.max(1, texH);
        float s = Math.min(sx, sy);
        int w = Math.max(1, Math.round(texW * s));
        int h = Math.max(1, Math.round(texH * s));
        int x = box.x() + (box.width() - w) / 2;
        int y = box.y() + (box.height() - h) / 2;
        draw(g, texture, x, y, w, h, texW, texH, tintArgb, alpha);
    }

    /** Crop-to-fill (cover). Assumes square UV for simplicity. */
    public static void drawCover(GuiGraphics g, ResourceLocation texture, EscRect box, int tintArgb, float alpha) {
        if (texture == null) return;
        draw(g, texture, box.x(), box.y(), box.width(), box.height(), box.width(), box.height(), tintArgb, alpha);
    }

    /**
     * Auto-pick source size: vanilla item/block files are 16×16; other textures
     * (mod logos, section art, dynamic uploads) stretch the full image into the dest box.
     */
    public static void drawAuto(
        GuiGraphics g,
        ResourceLocation texture,
        int x,
        int y,
        int w,
        int h,
        int tintArgb,
        float alpha
    ) {
        if (texture == null) return;
        String path = texture.getPath();
        if (path.startsWith("textures/item/") || path.startsWith("textures/block/")) {
            draw(g, texture, x, y, w, h, 16, 16, tintArgb, alpha);
        } else {
            // UV space = dest size → maps entire GL texture into the box regardless of PNG pixels.
            int color = EscFocusVisual.applyAlpha(tintArgb | 0xFF000000, alpha);
            g.blit(RenderType.GUI_TEXTURED, texture, x, y, 0f, 0f, w, h, w, h, color);
        }
    }
}
