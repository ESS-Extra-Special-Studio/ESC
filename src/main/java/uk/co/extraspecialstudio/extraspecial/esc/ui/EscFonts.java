package uk.co.extraspecialstudio.extraspecial.esc.ui;

import net.minecraft.resources.ResourceLocation;
import uk.co.extraspecialstudio.extraspecial.Extraspecialcore;

import java.util.List;

/**
 * Built-in ESC font identifiers for {@link net.minecraft.network.chat.Style#withFont(ResourceLocation)}.
 * Each id maps to assets under {@code extraspecialcore:font/&lt;id&gt;.json}. See {@code FONT_LICENSES.txt}.
 */
public final class EscFonts {

    private EscFonts() {
    }

    public static final ResourceLocation FIRA_CODE = ns("fira_code");

    /** Monocraft — monospace, Minecraft-inspired. SIL-OFL (Idrees Hassan). */
    public static final ResourceLocation MONOCRAFT = ns("monocraft");

    /**
     * Default face for ESC helpers ({@link EscText}, buttons, {@link EscSearchBox}, etc.).
     * Set to {@link #MONOCRAFT} for Monocraft-wide testing; switch back to {@link #FIRA_CODE} when done.
     */
    public static final ResourceLocation DEFAULT = MONOCRAFT;

    /** GNU Unifont — full-plane coverage; GPLv2+-licensed glyph data (large file). */
    public static final ResourceLocation GNU_UNIFONT = ns("gnu_unifont");

    /**
     * Blocky pixel look for voxel/arcade UIs. Glyph file is Tiny5 (SIL-OFL via Google Fonts), not HipFonts Block Craft,
     * which is often demo-licensed on free portals and is not shipped here.
     */
    public static final ResourceLocation PIXEL_BLOCK = ns("pixel_block");

    /** Press Start 2P — chunky arcade / retro console captions. SIL-OFL. */
    public static final ResourceLocation PRESS_START_2P = ns("press_start_2p");

    /** VT323 — terminal / readable retro monospace. SIL-OFL. */
    public static final ResourceLocation VT323 = ns("vt323");

    /** All built-in ESC faces (excluding vanilla default). Useful for previews or settings enums. */
    public static final List<ResourceLocation> BUNDLED_IDS = List.of(
        FIRA_CODE,
        MONOCRAFT,
        GNU_UNIFONT,
        PIXEL_BLOCK,
        PRESS_START_2P,
        VT323
    );

    private static ResourceLocation ns(String path) {
        return ResourceLocation.fromNamespaceAndPath(Extraspecialcore.MODID, path);
    }
}
