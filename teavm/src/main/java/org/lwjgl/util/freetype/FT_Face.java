package org.lwjgl.util.freetype;

/** TeaVM shim: dead (FreeType faces never open in the browser). Links only. */
public final class FT_Face {
	public static FT_Face create(long address) {
		return new FT_Face();
	}

	public FT_GlyphSlot glyph() {
		return null;
	}
}
