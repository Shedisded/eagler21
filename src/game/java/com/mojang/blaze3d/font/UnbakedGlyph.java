package com.mojang.blaze3d.font;

import net.minecraft.client.gui.font.glyphs.BakedGlyph;

public interface UnbakedGlyph {
    GlyphInfo info();

    BakedGlyph bake(UnbakedGlyph.Stitcher p_440351_);

    public interface Stitcher {
        BakedGlyph stitch(GlyphInfo p_439847_, GlyphBitmap p_440212_);

        BakedGlyph getMissing();
    }
}
