package uk.co.extraspecialstudio.extraspecial.esc.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

/**
 * 1.19.2 stand-in for the 1.20 GuiGraphics helper. Callers that override
 * {@code Screen#render} still receive a {@link PoseStack} and wrap it here.
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

    public void enableScissor(int x1, int y1, int x2, int y2) {
        GuiComponent.enableScissor(x1, y1, x2, y2);
    }

    public void disableScissor() {
        GuiComponent.disableScissor();
    }

    public int drawString(Font font, String text, int x, int y, int color) {
        return drawString(font, text, x, y, color, true);
    }

    public int drawString(Font font, String text, int x, int y, int color, boolean shadow) {
        return shadow ? font.drawShadow(pose, text, x, y, color) : font.draw(pose, text, x, y, color);
    }

    public int drawString(Font font, Component text, int x, int y, int color) {
        return drawString(font, text, x, y, color, true);
    }

    public int drawString(Font font, Component text, int x, int y, int color, boolean shadow) {
        return shadow ? font.drawShadow(pose, text, x, y, color) : font.draw(pose, text, x, y, color);
    }

    public int drawString(Font font, FormattedCharSequence text, int x, int y, int color) {
        return drawString(font, text, x, y, color, true);
    }

    public int drawString(Font font, FormattedCharSequence text, int x, int y, int color, boolean shadow) {
        return shadow ? font.drawShadow(pose, text, x, y, color) : font.draw(pose, text, x, y, color);
    }

    public void blit(ResourceLocation texture, int x, int y, int u, int v, int width, int height, int texW, int texH) {
        RenderSystem.setShaderTexture(0, texture);
        GuiComponent.blit(pose, x, y, 0, u, v, width, height, texW, texH);
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
        RenderSystem.setShaderTexture(0, texture);
        GuiComponent.blit(pose, x, y, width, height, u, v, uWidth, vHeight, texW, texH);
    }
}
