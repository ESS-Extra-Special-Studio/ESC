package uk.co.extraspecialstudio.extraspecial.esc.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import uk.co.extraspecialstudio.extraspecial.esc.theme.EscColor;
import uk.co.extraspecialstudio.extraspecial.esc.theme.EscTheme;
import uk.co.extraspecialstudio.extraspecial.esc.theme.EscThemeConfigs;
import uk.co.extraspecialstudio.extraspecial.esc.theme.EscVisualBudget;

/**
 * Optional animated hub backdrop. Several styles for testing — cycle via hub footer {@code BG:}.
 * Draws when Quality is HIGH ({@link EscVisualBudget#showDynamicBackground()}).
 * Motion OFF freezes the current frame; it does not hide the backdrop.
 * <p>
 * Particles are clipped by coordinate clamping only — no {@link GuiGraphics#enableScissor}.
 * Nested/leftover scissor state previously corrupted later screens (seen when cycling animations).
 */
public final class EscBackground {
    private static final char[] MATRIX_GLYPHS = {'0', '1'};
    private static final long GROWTH_CYCLE_MS = 16_000L;
    /** Captured on first frame after Motion OFF — keeps backdrop visible but static. */
    private static long pausedBackdropMs = -1L;
    private static final GrowthTreeStyle[] GROWTH_TREES = {
        growthTree("oak"),
        growthTree("birch"),
        growthTree("spruce"),
        growthTree("jungle"),
        growthTree("acacia")
    };

    private EscBackground() {
    }

    public static void render(GuiGraphics g, EscRect area, EscTheme theme) {
        if (theme == null || area.width() <= 0 || area.height() <= 0) {
            return;
        }
        if (!EscVisualBudget.showDynamicBackground()) {
            EscPanel.fill(g, area, 0x66000000);
            return;
        }
        long ms = backdropTimeMs();
        int accent = backdropTint(theme);
        switch (EscThemeConfigs.backdropStyle()) {
            case MATRIX -> {
                EscPanel.fill(g, area, 0xEE020802);
                drawMatrix(g, area, ms);
            }
            case OCEAN -> {
                EscPanel.fill(g, area, 0xEE041428);
                drawOcean(g, area, ms);
            }
            case CLOUDS -> {
                EscPanel.fill(g, area, 0xEE3A78C8);
                drawClouds(g, area, ms);
            }
            case AURORA -> {
                EscPanel.fill(g, area, 0xEE040812);
                drawAurora(g, area, ms);
            }
            case EMBER -> {
                EscPanel.fill(g, area, 0xEE100604);
                drawEmber(g, area, ms);
            }
            case STARFIELD -> {
                EscPanel.fill(g, area, 0xEE02040E);
                drawStarfield(g, area, ms);
            }
            case LIGHTNING -> {
                EscPanel.fill(g, area, 0xEE060810);
                drawLightning(g, area, ms);
            }
            case GROWTH -> {
                EscPanel.fill(g, area, 0xEE06160A);
                drawGrowth(g, area, ms);
            }
            case SCAN -> {
                EscPanel.fill(g, area, accentBaseFill(accent));
                drawScan(g, area, accent, ms);
            }
            case RAIN -> {
                EscPanel.fill(g, area, accentBaseFill(accent));
                drawRain(g, area, accent, ms);
            }
            case PULSE -> {
                EscPanel.fill(g, area, accentBaseFill(accent));
                drawPulse(g, area, accent, ms);
            }
            case SPARKS -> {
                EscPanel.fill(g, area, accentBaseFill(accent));
                drawSparks(g, area, accent, ms);
            }
            case GRID -> {
                EscPanel.fill(g, area, accentBaseFill(accent));
                drawGrid(g, area, accent, ms);
            }
        }
    }

    private static long backdropTimeMs() {
        if (!EscVisualBudget.reducedMotion()) {
            pausedBackdropMs = -1L;
            return System.currentTimeMillis();
        }
        if (pausedBackdropMs < 0L) {
            pausedBackdropMs = System.currentTimeMillis();
        }
        return pausedBackdropMs;
    }

    /**
     * Accent-tinted backdrops use the theme accent when it is visible on a dark panel.
     * Black / near-black accents fall back to border, title, then a default blue tint.
     */
    private static int backdropTint(EscTheme theme) {
        int accent = EscColor.rgb(theme.accentArgb());
        if (luminance(accent) >= 48) {
            return accent;
        }
        int border = EscColor.rgb(theme.borderArgb());
        if (luminance(border) >= 48) {
            return border;
        }
        int title = EscColor.rgb(theme.textTitleRgb());
        if (luminance(title) >= 48) {
            return title;
        }
        return 0x66A0FF;
    }

    private static int accentBaseFill(int tintRgb) {
        return EscColor.withAlpha(EscColor.darken(tintRgb, 0.06f), 0xEE);
    }

    private static int luminance(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        return (r * 299 + g * 587 + b * 114) / 1000;
    }

    /** Fill only the intersection of a rect with {@code area} (no GL scissor). */
    private static void fillClipped(GuiGraphics g, EscRect area, int x0, int y0, int x1, int y1, int color) {
        int cx0 = Math.max(area.x(), x0);
        int cy0 = Math.max(area.y(), y0);
        int cx1 = Math.min(area.right(), x1);
        int cy1 = Math.min(area.bottom(), y1);
        if (cx1 > cx0 && cy1 > cy0) {
            g.fill(cx0, cy0, cx1, cy1, color);
        }
    }

    private static void drawGrid(GuiGraphics g, EscRect area, int accent, long ms) {
        int grid = EscColor.withAlpha(accent, 0x66);
        int step = 28;
        int drift = (int) ((ms / 40) % step);
        int thick = 2;
        for (int x = area.x() - drift; x < area.right(); x += step) {
            fillClipped(g, area, x, area.y(), x + thick, area.bottom(), grid);
        }
        for (int y = area.y() - drift; y < area.bottom(); y += step) {
            fillClipped(g, area, area.x(), y, area.right(), y + thick, grid);
        }
    }

    private static void drawScan(GuiGraphics g, EscRect area, int accent, long ms) {
        int line = EscColor.withAlpha(accent, 0x44);
        int bright = EscColor.withAlpha(accent, 0x99);
        int spacing = 10;
        int drift = (int) ((ms / 30) % spacing);
        int thick = 2;
        for (int y = area.y() - drift; y < area.bottom(); y += spacing) {
            fillClipped(g, area, area.x(), y, area.right(), y + thick, line);
        }
        int band = area.y() + (int) ((ms / 18) % Math.max(1, area.height()));
        int h = Math.min(10, area.height() / 8);
        fillClipped(g, area, area.x(), band, area.right(), band + h, bright);
    }

    private static void drawRain(GuiGraphics g, EscRect area, int accent, long ms) {
        int drop = EscColor.withAlpha(accent, 0x88);
        int cols = Math.max(8, area.width() / 14);
        int gap = Math.max(8, area.width() / cols);
        for (int i = 0; i < cols; i++) {
            int x = area.x() + i * gap + (i * 3 % 5);
            int speed = 18 + (i * 7 % 17);
            int len = 6 + (i * 5 % 10);
            int y = area.y() + (int) ((ms / speed + i * 37) % Math.max(1, area.height() + len)) - len;
            fillClipped(g, area, x, y, x + 1, y + len, drop);
        }
    }

    private static void drawPulse(GuiGraphics g, EscRect area, int accent, long ms) {
        double phase = (ms % 2400) / 2400.0;
        float wave = (float) (0.5 + 0.5 * Math.sin(phase * Math.PI * 2.0));
        int rings = 5;
        int cx = area.x() + area.width() / 2;
        int cy = area.y() + area.height() / 2;
        int maxR = Math.max(area.width(), area.height());
        for (int i = 0; i < rings; i++) {
            float t = (i + wave) / rings;
            int r = Math.round(maxR * t * 0.55f);
            int a = Math.max(0x28, Math.round(0x70 * (1f - t)));
            int color = EscColor.withAlpha(accent, a);
            int x0 = cx - r;
            int y0 = cy - r;
            int x1 = cx + r;
            int y1 = cy + r;
            fillClipped(g, area, x0, y0, x1, y0 + 1, color);
            fillClipped(g, area, x0, y1 - 1, x1, y1, color);
            fillClipped(g, area, x0, y0, x0 + 1, y1, color);
            fillClipped(g, area, x1 - 1, y0, x1, y1, color);
        }
    }

    private static void drawSparks(GuiGraphics g, EscRect area, int accent, long ms) {
        int count = Math.min(48, Math.max(16, (area.width() * area.height()) / 2800));
        long seed = (ms / 80);
        for (int i = 0; i < count; i++) {
            int n = hash(i * 73856093 ^ (int) seed);
            int x = area.x() + Math.floorMod(n, Math.max(1, area.width()));
            int y = area.y() + Math.floorMod(n >> 8, Math.max(1, area.height()));
            int twinkle = 0x50 + Math.floorMod(n >> 16, 0x50);
            int size = 1 + Math.floorMod(n >> 24, 2);
            int color = EscColor.withAlpha(accent, twinkle);
            fillClipped(g, area, x, y, x + size, y + size, color);
        }
    }

    /** Cascading binary rain (0 / 1 only). */
    private static void drawMatrix(GuiGraphics g, EscRect area, long ms) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) {
            return;
        }
        Font font = mc.font;
        int rowH = Math.max(8, font.lineHeight);
        int cols = Math.max(18, area.width() / 7);
        int gap = Math.max(6, area.width() / cols);
        for (int i = 0; i < cols; i++) {
            int x = area.x() + i * gap;
            if (x >= area.right() - 4) {
                continue;
            }
            // Higher divisor = slower fall (was ~10–28ms per cell).
            int speed = 55 + (i * 17 % 45);
            int trail = 10 + (i * 3 % 8);
            int headCell = (int) ((ms / speed + i * 53) % Math.max(1, area.height() / rowH + trail));
            for (int t = 0; t < trail; t++) {
                int cell = headCell - t;
                int y = area.y() + cell * rowH;
                if (y < area.y() || y + rowH > area.bottom()) {
                    continue;
                }
                // Flip glyphs slowly so rain reads as drift, not strobe.
                int glyphIdx = Math.floorMod(hash(i * 131 + cell * 17 + (int) (ms / 450)), MATRIX_GLYPHS.length);
                char ch = MATRIX_GLYPHS[glyphIdx];
                int rgb;
                if (t == 0) {
                    rgb = 0xCCFFCC;
                } else if (t < 3) {
                    rgb = 0x33FF66;
                } else {
                    int fade = Math.max(0x22, 0x88 - t * 8);
                    rgb = (Math.min(0xAA, fade + 0x33) << 8) | 0x001100;
                }
                g.drawString(font, String.valueOf(ch), x, y, rgb, false);
            }
        }
    }

    /** Scrolling wave crests — thin ribbons so motion stays obvious. */
    private static void drawOcean(GuiGraphics g, EscRect area, long ms) {
        double wash = (ms % 8000) / 8000.0;
        int washY = area.y() + (int) (area.height() * (0.35 + 0.1 * Math.sin(wash * Math.PI * 2)));
        fillClipped(g, area, area.x(), washY, area.right(), area.bottom(), 0x33062858);

        int layers = 4;
        for (int layer = 0; layer < layers; layer++) {
            double phase = ms / (360.0 + layer * 80.0) + layer * 1.7;
            int baseY = area.y() + area.height() * (42 + layer * 12) / 100;
            int amp = 8 + layer * 3;
            int fill = layer % 2 == 0 ? 0x660A3868 : 0x55062850;
            int crest = 0xAAE8F4FF;
            int step = 6;
            int prevY = baseY;
            for (int x = area.x(); x <= area.right(); x += step) {
                double nx = (x - area.x()) / 14.0 + phase;
                int y = baseY + (int) (Math.sin(nx) * amp + Math.sin(nx * 0.45 + phase) * (amp * 0.45));
                int x1 = Math.min(area.right(), x + step);
                int bandH = 10 + layer * 3;
                fillClipped(g, area, x, y, x1, y + bandH, fill);
                int yTop = Math.min(prevY, y);
                int yBot = Math.max(prevY, y);
                fillClipped(g, area, x, yTop - 1, x1, yBot + 2, crest);
                prevY = y;
            }
        }
    }

    /**
     * Vanilla saplings grow into block-built vanilla trees along the bottom edge.
     * Each tree is staggered so the backdrop always contains several growth stages.
     */
    private static void drawGrowth(GuiGraphics g, EscRect area, long ms) {
        int groundY = area.bottom() - 3;
        fillClipped(g, area, area.x(), groundY, area.right(), area.bottom(), 0xAA163A12);

        int count = Math.max(3, Math.min(7, area.width() / 100));
        int spacing = Math.max(1, area.width() / count);
        int block = Math.max(8, Math.min(13, area.height() / 11));
        for (int i = 0; i < count; i++) {
            GrowthTreeStyle style = GROWTH_TREES[i % GROWTH_TREES.length];
            long stagger = (GROWTH_CYCLE_MS * i) / count;
            float progress = ((ms + stagger) % GROWTH_CYCLE_MS) / (float) GROWTH_CYCLE_MS;
            int x = area.x() + spacing * i + spacing / 2;
            drawGrowingTree(g, area, style, x, groundY, block, progress);
        }
    }

    private static void drawGrowingTree(
        GuiGraphics g,
        EscRect area,
        GrowthTreeStyle style,
        int centerX,
        int groundY,
        int block,
        float progress
    ) {
        if (progress < 0.20f) {
            int saplingSize = Math.max(10, block + 3);
            drawTextureInside(g, area, style.sapling(), centerX - saplingSize / 2,
                groundY - saplingSize, saplingSize);
            return;
        }

        float grow = Math.min(1f, (progress - 0.20f) / 0.55f);
        int targetTrunk = style.tall() ? 5 : 4;
        int trunkBlocks = Math.max(1, Math.round(targetTrunk * Math.min(1f, grow * 1.35f)));
        for (int y = 0; y < trunkBlocks; y++) {
            drawTextureInside(g, area, style.log(), centerX - block / 2,
                groundY - (y + 1) * block, block);
        }

        float canopyGrow = Math.max(0f, (grow - 0.35f) / 0.65f);
        if (canopyGrow <= 0f) {
            return;
        }
        int crownY = groundY - trunkBlocks * block;
        int radius = canopyGrow < 0.45f ? 0 : (canopyGrow < 0.78f ? 1 : 2);
        for (int dy = -radius; dy <= 0; dy++) {
            int rowRadius = dy == -radius && radius > 1 ? radius - 1 : radius;
            for (int dx = -rowRadius; dx <= rowRadius; dx++) {
                if (radius > 1 && dy == 0 && Math.abs(dx) == radius) {
                    continue;
                }
                drawTextureInside(g, area, style.leaves(), centerX + dx * block - block / 2,
                    crownY + dy * block - block, block);
            }
        }
        if (radius > 0) {
            drawTextureInside(g, area, style.leaves(), centerX - block / 2,
                crownY - (radius + 1) * block, block);
        }
    }

    /** Draw only complete vanilla texture tiles; no GL scissor state is introduced. */
    private static void drawTextureInside(
        GuiGraphics g,
        EscRect area,
        ResourceLocation texture,
        int x,
        int y,
        int size
    ) {
        if (x < area.x() || y < area.y() || x + size > area.right() || y + size > area.bottom()) {
            return;
        }
        EscImage.draw(g, texture, x, y, size, size, 0xFFFFFFFF, 1f);
    }

    private static GrowthTreeStyle growthTree(String wood) {
        return new GrowthTreeStyle(
            vanillaBlock(wood + "_sapling"),
            vanillaBlock(wood + "_log"),
            vanillaBlock(wood + "_leaves"),
            wood.equals("spruce") || wood.equals("jungle")
        );
    }

    private static ResourceLocation vanillaBlock(String name) {
        return ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/" + name + ".png");
    }

    private record GrowthTreeStyle(
        ResourceLocation sapling,
        ResourceLocation log,
        ResourceLocation leaves,
        boolean tall
    ) {
    }

    /** Blue sky with white clouds drifting L/R (clipped to panel). */
    private static void drawClouds(GuiGraphics g, EscRect area, long ms) {
        int mid = area.y() + area.height() * 2 / 3;
        fillClipped(g, area, area.x(), mid, area.right(), area.bottom(), 0x330E4A8C);
        int clouds = Math.max(6, area.width() / 60);
        for (int i = 0; i < clouds; i++) {
            int n = hash(i * 9137 + 17);
            int w = 36 + Math.floorMod(n, 50);
            int h = 10 + Math.floorMod(n >> 8, 12);
            int baseY = area.y() + 10 + Math.floorMod(n >> 16, Math.max(1, Math.max(1, area.height() / 2) - h - 4));
            int dir = (i % 2 == 0) ? 1 : -1;
            int speed = 28 + Math.floorMod(n >> 4, 36);
            int span = area.width() + w * 2;
            int travel = Math.floorMod((int) (ms / speed) + i * 73, span);
            int x = dir > 0
                ? area.x() - w + travel
                : area.right() + w - travel;
            drawCloudBlob(g, area, x, baseY, w, h);
        }
    }

    private static void drawCloudBlob(GuiGraphics g, EscRect area, int x, int y, int w, int h) {
        int body = 0xBBFFFFFF;
        int soft = 0x66E8F4FF;
        fillClipped(g, area, x + w / 6, y + h / 3, x + w - w / 6, y + h, body);
        fillClipped(g, area, x, y + h / 2, x + w / 3, y + h, soft);
        fillClipped(g, area, x + w / 2, y, x + w - w / 8, y + h * 2 / 3, body);
        fillClipped(g, area, x + w / 4, y + h / 5, x + w / 2, y + h * 3 / 4, soft);
    }

    /** Flowing aurora curtains with clear lateral drift. */
    private static void drawAurora(GuiGraphics g, EscRect area, long ms) {
        double drift = ms / 900.0;
        int bands = 5;
        for (int b = 0; b < bands; b++) {
            double phase = drift * (1.1 + b * 0.15) + b * 1.3;
            int rgb = switch (b % 3) {
                case 0 -> 0x44FFAA;
                case 1 -> 0x66FFDD;
                default -> 0x8866FF;
            };
            int step = 8;
            for (int x = area.x(); x < area.right(); x += step) {
                double nx = (x - area.x()) / 26.0 + phase;
                double wave = Math.sin(nx) * 0.5 + 0.5;
                double wave2 = Math.sin(nx * 0.55 + drift * 2.0) * 0.5 + 0.5;
                int top = area.y() + 6 + (int) (wave * area.height() * 0.28 + wave2 * 10);
                int height = 22 + b * 5 + (int) (wave2 * 14);
                int bot = Math.min(area.bottom() - 4, top + height);
                int a = 0x22 + b * 6;
                fillClipped(g, area, x, top, x + step, bot, (a << 24) | rgb);
                fillClipped(g, area, x, top, x + step, top + 3, ((a + 0x20) << 24) | 0xEEFFEE);
            }
        }
    }

    /** Embers rise inside the panel and wrap — never escape the top edge. */
    private static void drawEmber(GuiGraphics g, EscRect area, long ms) {
        int count = Math.min(42, Math.max(14, area.width() / 12));
        int travel = Math.max(8, area.height() - 6);
        for (int i = 0; i < count; i++) {
            int n = hash(i * 5021 + 3);
            int x = area.x() + 2 + Math.floorMod(n, Math.max(1, area.width() - 4));
            int speed = 18 + Math.floorMod(n >> 6, 24);
            int y = area.bottom() - 3 - Math.floorMod((int) (ms / speed) + i * 19, travel);
            int wobble = (int) (Math.sin((ms / 160.0) + i) * 2);
            int size = 1 + Math.floorMod(n >> 20, 3);
            int hot = Math.floorMod(n, 3) == 0 ? 0xFFCC44 : 0xFF6622;
            float topFade = (y - area.y()) / (float) Math.max(1, area.height());
            int a = Math.max(0x18, (int) ((0x38 + Math.floorMod(n >> 10, 0x40)) * Math.min(1f, topFade * 1.4f)));
            int px = x + wobble;
            fillClipped(g, area, px, y, px + size, y + size + 1, (a << 24) | hot);
        }
    }

    /** Night sky twinkle + occasional streaks. */
    private static void drawStarfield(GuiGraphics g, EscRect area, long ms) {
        int count = Math.min(64, Math.max(24, (area.width() * area.height()) / 2200));
        long bucket = ms / 90;
        for (int i = 0; i < count; i++) {
            int n = hash(i * 19349663 ^ (int) (bucket / 8));
            int x = area.x() + Math.floorMod(n, Math.max(1, area.width()));
            int y = area.y() + Math.floorMod(n >> 9, Math.max(1, area.height()));
            int twinkle = 0x30 + Math.floorMod((int) (bucket + i) * 13, 0x70);
            int size = Math.floorMod(n >> 22, 5) == 0 ? 2 : 1;
            fillClipped(g, area, x, y, x + size, y + size, (twinkle << 24) | 0xDDEEFF);
        }
        for (int s = 0; s < 2; s++) {
            int n = hash((int) (bucket / 3) * 9176 + s * 99);
            if (Math.floorMod(n, 7) != 0) {
                continue;
            }
            int x = area.x() + Math.floorMod(n, Math.max(1, area.width()));
            int y = area.y() + Math.floorMod(n >> 8, Math.max(1, area.height() / 2));
            fillClipped(g, area, x, y, x + 14, y + 1, 0x88FFFFFF);
            fillClipped(g, area, x + 2, y + 1, x + 10, y + 2, 0x44AACCFF);
        }
    }

    /** Storm sky with soft flash + forked bolts (flash kept gentle). */
    private static void drawLightning(GuiGraphics g, EscRect area, long ms) {
        int drops = Math.max(10, area.width() / 18);
        for (int i = 0; i < drops; i++) {
            int n = hash(i * 4219 + 5);
            int x = area.x() + Math.floorMod(n, Math.max(1, area.width()));
            int speed = 12 + Math.floorMod(n >> 5, 18);
            int len = 8 + Math.floorMod(n >> 12, 10);
            int y = area.y() + (int) ((ms / speed + i * 29) % Math.max(1, area.height() + len)) - len;
            fillClipped(g, area, x, y, x + 1, y + len, 0x28AACCFF);
        }

        long bucket = ms / 90;
        if ((bucket % 61) == 0) {
            fillClipped(g, area, area.x(), area.y(), area.right(), area.bottom(), 0x18B8C8E8);
        } else if ((bucket % 61) == 1) {
            fillClipped(g, area, area.x(), area.y(), area.right(), area.bottom(), 0x0CB0C0E0);
        }

        boolean boltOn = (bucket % 61) <= 2 || (bucket % 89) <= 1;
        if (!boltOn) {
            return;
        }
        int seed = hash((int) (bucket / 3) * 9173);
        int x = area.x() + Math.floorMod(seed, Math.max(1, area.width()));
        int y = area.y();
        int segs = 7 + Math.floorMod(seed >> 4, 5);
        int segH = Math.max(6, area.height() / segs);
        for (int s = 0; s < segs; s++) {
            int n = hash(seed ^ (s * 7919));
            int nx = x + Math.floorMod(n, 25) - 12;
            int ny = Math.min(area.bottom(), y + segH);
            int x0 = Math.min(x, nx);
            int x1 = Math.max(x, nx) + 2;
            fillClipped(g, area, x0 - 1, y, x1 + 1, ny, 0x33AACCFF);
            fillClipped(g, area, x0, y, x1, ny, 0xCCEEF6FF);
            if (Math.floorMod(n >> 8, 4) == 0) {
                int fx = nx + (Math.floorMod(n >> 12, 2) == 0 ? 10 : -10);
                int fy = y + segH / 2;
                fillClipped(g, area, Math.min(nx, fx), fy, Math.max(nx, fx) + 1, fy + 2, 0x99DDEEFF);
            }
            x = nx;
            y = ny;
        }
    }

    private static int hash(int x) {
        x ^= (x >>> 16);
        x *= 0x7feb352d;
        x ^= (x >>> 15);
        x *= 0x846ca68b;
        x ^= (x >>> 16);
        return x;
    }
}
