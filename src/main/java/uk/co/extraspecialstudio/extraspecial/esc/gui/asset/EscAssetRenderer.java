package uk.co.extraspecialstudio.extraspecial.esc.gui.asset;

/**
 * Draw hook for a registered {@link EscAssetDef}. Interactive widgets are added separately via
 * {@link uk.co.extraspecialstudio.extraspecial.esc.gui.EscGuiRuntime#buildWidgets}.
 */
@FunctionalInterface
public interface EscAssetRenderer {
    void render(EscAssetRenderContext ctx, EscAssetInstance instance);
}
