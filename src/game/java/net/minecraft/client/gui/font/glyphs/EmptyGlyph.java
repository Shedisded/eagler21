package net.minecraft.client.gui.font.glyphs;

import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.font.UnbakedGlyph;
import net.minecraft.client.gui.font.TextRenderable;
import net.minecraft.network.chat.Style;
import org.jspecify.annotations.Nullable;

public class EmptyGlyph implements UnbakedGlyph {
    final GlyphInfo info;

    public EmptyGlyph(float p_439922_) {
        this.info = GlyphInfo.simple(p_439922_);
    }

    @Override
    public GlyphInfo info() {
        return this.info;
    }

    @Override
    public BakedGlyph bake(UnbakedGlyph.Stitcher p_439453_) {
        return new BakedGlyph() {
            @Override
            public GlyphInfo info() {
                return EmptyGlyph.this.info;
            }

            @Override
            public TextRenderable.@Nullable Styled createGlyph(
                float p_439459_, float p_440447_, int p_439996_, int p_440639_, Style p_439498_, float p_439464_, float p_439851_
            ) {
                return null;
            }
        };
    }
}
