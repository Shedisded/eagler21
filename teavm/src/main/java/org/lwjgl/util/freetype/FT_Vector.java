package org.lwjgl.util.freetype;

import org.lwjgl.system.MemoryStack;

/**
 * TeaVM shim. Only reached via FreeTypeUtil.setVector during glyph rendering,
 * which is dead (no faces), but keep it functional — it's a trivial x/y pair.
 */
public final class FT_Vector {
	private long x;
	private long y;

	public static FT_Vector malloc(MemoryStack stack) {
		return new FT_Vector();
	}

	public FT_Vector set(long x, long y) {
		this.x = x;
		this.y = y;
		return this;
	}

	public long x() {
		return x;
	}
}
