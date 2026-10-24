package uk.co.extraspecialstudio.extraspecial.esc.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

/**
 * Draw surface for 1.18.2, where {@code GuiGraphics} does not exist.
 * Screen and widget overrides still receive a {@link PoseStack} and wrap it here.
 */
public final class EscGraphics {
    private final PoseStack pose;

    public EscGraphics(PoseStack pose) {
        this.pose = pose;
    }

    public PoseStack pose() {
        return pose;
    }

    public void fill(int x1, int y1, int x2, int y2, int color) {
        GuiComponent.fill(pose, x1, y1, x2, y2, color);
    }

    public int drawString(Font font, String text, int x, int y, int color) {
        return drawString(font, text, x, y, color, true);
    }

    public int drawString(Font font, String text, int x, int y, int color, boolean dropShadow) {
        if (font == null || text == null) {
            return 0;
        }
        return dropShadow ? font.drawShadow(pose, text, x, y, color) : font.draw(pose, text, x, y, color);
    }

    public int drawString(Font font, FormattedCharSequence text, int x, int y, int color) {
        return drawString(font, text, x, y, color, true);
    }

    public int drawString(Font font, FormattedCharSequence text, int x, int y, int color, boolean dropShadow) {
        if (font == null || text == null) {
            return 0;
        }
        return dropShadow ? font.drawShadow(pose, text, x, y, color) : font.draw(pose, text, x, y, color);
    }

    public int drawString(Font font, Component text, int x, int y, int color) {
        return drawString(font, text, x, y, color, true);
    }

    public int drawString(Font font, Component text, int x, int y, int color, boolean dropShadow) {
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
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, texture);
        GuiComponent.blit(pose, x, y, width, height, u, v, uWidth, vHeight, texW, texH);
    }
}
