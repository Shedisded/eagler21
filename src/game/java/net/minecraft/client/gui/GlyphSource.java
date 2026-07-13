package net.minecraft.client.gui;

import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.util.RandomSource;

public interface GlyphSource {
    BakedGlyph getGlyph(int p_432794_);

    BakedGlyph getRandomGlyph(RandomSource p_433037_, int p_434268_);
}
