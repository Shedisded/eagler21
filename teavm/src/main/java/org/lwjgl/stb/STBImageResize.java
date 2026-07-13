package org.lwjgl.stb;

import net.lax1dude.eaglercraft.v1_21.platform.webgl.NativeHeap;

/**
 * TeaVM shim for org.lwjgl.stb.STBImageResize. MC uses this to build mipmaps
 * from a NativeImage. Straight bilinear resample over heap bytes.
 */
public final class STBImageResize {
	private STBImageResize() {
	}

	public static int nstbir_resize_uint8(long input, int inW, int inH, int inStride, long output, int outW, int outH,
			int outStride, int channels) {
		if (inStride == 0) {
			inStride = inW * channels;
		}
		if (outStride == 0) {
			outStride = outW * channels;
		}
		for (int oy = 0; oy < outH; oy++) {
			float sy = (oy + 0.5f) * inH / outH - 0.5f;
			int y0 = clamp((int) Math.floor(sy), 0, inH - 1);
			int y1 = clamp(y0 + 1, 0, inH - 1);
			float wy = sy - (float) Math.floor(sy);
			for (int ox = 0; ox < outW; ox++) {
				float sx = (ox + 0.5f) * inW / outW - 0.5f;
				int x0 = clamp((int) Math.floor(sx), 0, inW - 1);
				int x1 = clamp(x0 + 1, 0, inW - 1);
				float wx = sx - (float) Math.floor(sx);
				for (int c = 0; c < channels; c++) {
					int p00 = NativeHeap.getByte(input + (long) y0 * inStride + (long) x0 * channels + c) & 0xFF;
					int p10 = NativeHeap.getByte(input + (long) y0 * inStride + (long) x1 * channels + c) & 0xFF;
					int p01 = NativeHeap.getByte(input + (long) y1 * inStride + (long) x0 * channels + c) & 0xFF;
					int p11 = NativeHeap.getByte(input + (long) y1 * inStride + (long) x1 * channels + c) & 0xFF;
					float top = p00 + (p10 - p00) * wx;
					float bot = p01 + (p11 - p01) * wx;
					int v = Math.round(top + (bot - top) * wy);
					NativeHeap.putByte(output + (long) oy * outStride + (long) ox * channels + c, (byte) v);
				}
			}
		}
		return 1;
	}

	private static int clamp(int v, int lo, int hi) {
		return v < lo ? lo : (v > hi ? hi : v);
	}
}
