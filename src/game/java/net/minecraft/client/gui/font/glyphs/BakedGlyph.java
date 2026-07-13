package net.minecraft.client.gui.font.glyphs;

import com.mojang.blaze3d.font.GlyphInfo;
import net.minecraft.client.gui.font.TextRenderable;
import net.minecraft.network.chat.Style;
import org.jspecify.annotations.Nullable;

public interface BakedGlyph {
    GlyphInfo info();

    TextRenderable.@Nullable Styled createGlyph(
        float p_439446_, float p_440229_, int p_439010_, int p_440429_, Style p_439811_, float p_440144_, float p_439280_
    );
}
