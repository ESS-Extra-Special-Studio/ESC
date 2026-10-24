package uk.co.extraspecialstudio.extraspecial.esc.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.IReorderingProcessor;

/**
 * Draw surface for 1.18.2, where {@code GuiGraphics} does not exist.
 * Screen and widget overrides still receive a {@link MatrixStack} and wrap it here.
 */
public final class EscGraphics {
    private final MatrixStack pose;

    public EscGraphics(MatrixStack pose) {
        this.pose = pose;
    }

    public MatrixStack pose() {
        return pose;
    }

    public void fill(int x1, int y1, int x2, int y2, int color) {
        AbstractGui.fill(pose, x1, y1, x2, y2, color);
    }

    public int drawString(FontRenderer font, String text, int x, int y, int color) {
        return drawString(font, text, x, y, color, true);
    }

    public int drawString(FontRenderer font, String text, int x, int y, int color, boolean dropShadow) {
        if (font == null || text == null) {
            return 0;
        }
        return dropShadow ? font.drawShadow(pose, text, x, y, color) : font.draw(pose, text, x, y, color);
    }

    public int drawString(FontRenderer font, IReorderingProcessor text, int x, int y, int color) {
        return drawString(font, text, x, y, color, true);
    }

    public int drawString(FontRenderer font, IReorderingProcessor text, int x, int y, int color, boolean dropShadow) {
        if (font == null || text == null) {
            return 0;
        }
        return dropShadow ? font.drawShadow(pose, text, x, y, color) : font.draw(pose, text, x, y, color);
    }

    public int drawString(FontRenderer font, ITextComponent text, int x, int y, int color) {
        return drawString(font, text, x, y, color, true);
    }

    public int drawString(FontRenderer font, ITextComponent text, int x, int y, int color, boolean dropShadow) {
        if (text == null) {
            return 0;
        }
        return drawString(font, text.getVisualOrderText(), x, y, color, dropShadow);
    }

    public void enableScissor(int x1, int y1, int x2, int y2) {
        Minecraft mc = Minecraft.getInstance();
        double scale = mc.getWindow().getGuiScale();
        int fbHeight = mc.getWindow().getHeight();
        int left = Math.min(x1, x2);
        int right = Math.max(x1, x2);
        int top = Math.min(y1, y2);
        int bottom = Math.max(y1, y2);
        int sx = (int) Math.floor(left * scale);
        int sy = (int) Math.floor(fbHeight - bottom * scale);
        int sw = (int) Math.ceil((right - left) * scale);
        int sh = (int) Math.ceil((bottom - top) * scale);
        RenderSystem.enableScissor(sx, sy, Math.max(0, sw), Math.max(0, sh));
    }

    public void disableScissor() {
        RenderSystem.disableScissor();
    }

    public void blit(ResourceLocation texture, int x, int y, int u, int v, int width, int height, int texW, int texH) {
        blit(texture, x, y, width, height, u, v, width, height, texW, texH);
    }

    public void blit(
        ResourceLocation texture,
        int x,
        int y,
        int width,
        int height,
        float u,
        float v,
        int uWidth,
        int vHeight,
        int texW,
        int texH
    ) {
        if (texture == null || width <= 0 || height <= 0) {
            return;
        }
        Minecraft.getInstance().getTextureManager().bind(texture);
        AbstractGui.blit(pose, x, y, width, height, u, v, uWidth, vHeight, texW, texH);
    }
}
