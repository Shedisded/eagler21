package org.lwjgl.stb;

/**
 * TeaVM shim: screenshot/PNG-write path. Stubbed to fail (returns 0); MC turns
 * that into an IOException on the screenshot, which is acceptable for bring-up.
 */
public final class STBImageWrite {
	private STBImageWrite() {
	}

	public static int nstbi_write_png_to_func(long func, long context, int w, int h, int comp, long data, int stride) {
		return 0;
	}
}
