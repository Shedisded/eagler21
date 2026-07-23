package org.lwjgl.stb;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import net.lax1dude.eaglercraft.v1_21.platform.webgl.NativeHeap;
import org.lwjgl.system.MemoryUtil;

/**
 * TeaVM shim for org.lwjgl.stb.STBImage — the texture-decode path. MC decodes
 * every PNG (textures, GUI, font atlases) through {@code stbi_load_from_memory}.
 * TeaVM has {@link Inflater}, so we decode PNG in pure Java and hand MC an
 * RGBA/expanded buffer allocated on the same {@link NativeHeap} the real LWJGL
 * would have used.
 *
 * ponytail: 8-bit-per-channel, non-interlaced PNG only (every vanilla asset).
 * Sub-byte depths and Adam7 interlace fail with a reason string; add them if a
 * real asset needs them.
 */
public final class STBImage {
	private STBImage() {
	}

	private static String failureReason = "";

	public static String stbi_failure_reason() {
		return failureReason;
	}

	public static void nstbi_image_free(long address) {
		NativeHeap.free(address);
	}

	public static ByteBuffer stbi_load_from_memory(ByteBuffer buffer, IntBuffer w, IntBuffer h, IntBuffer channelsInFile,
			int desiredChannels) {
		ByteBuffer dup = buffer.duplicate();
		byte[] png = new byte[dup.remaining()];
		dup.get(png);
		try {
			return decode(png, w, h, channelsInFile, desiredChannels);
		} catch (Exception e) {
			failureReason = e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
			return null;
		}
	}

	// --- PNG decode ---

	private static ByteBuffer decode(byte[] p, IntBuffer w, IntBuffer h, IntBuffer channelsInFile, int desired)
			throws DataFormatException {
		if (p.length < 8 || (p[0] & 0xFF) != 0x89 || p[1] != 'P' || p[2] != 'N' || p[3] != 'G') {
			throw new IllegalArgumentException("not a PNG");
		}
		int width = 0, height = 0, bitDepth = 0, colorType = 0, interlace = 0;
		byte[] palette = null;
		byte[] paletteAlpha = null;
		ByteArrayOutputStream idat = new ByteArrayOutputStream();

		int pos = 8;
		while (pos + 8 <= p.length) {
			int clen = i32(p, pos);
			pos += 4;
			int type = i32(p, pos);
			pos += 4;
			int data = pos;
			pos += clen;
			pos += 4; // CRC
			switch (type) {
				case 0x49484452: // IHDR
					width = i32(p, data);
					height = i32(p, data + 4);
					bitDepth = p[data + 8] & 0xFF;
					colorType = p[data + 9] & 0xFF;
					interlace = p[data + 12] & 0xFF;
					break;
				case 0x504C5445: // PLTE
					palette = new byte[clen];
					System.arraycopy(p, data, palette, 0, clen);
					break;
				case 0x74524E53: // tRNS (palette alpha)
					paletteAlpha = new byte[clen];
					System.arraycopy(p, data, paletteAlpha, 0, clen);
					break;
				case 0x49444154: // IDAT
					idat.write(p, data, clen);
					break;
				case 0x49454E44: // IEND
					pos = p.length;
					break;
				default:
					break;
			}
		}

		if (interlace != 0) {
			throw new IllegalArgumentException("interlaced PNG unsupported");
		}
		// ponytail: bit depths 1/2/4 (grayscale + palette) are needed by the default font
		// (1-bit) and armor trims (2/4-bit) — a missing default font is a FATAL crash. 16-bit
		// unseen in vanilla; add if an asset needs it.
		if (bitDepth != 1 && bitDepth != 2 && bitDepth != 4 && bitDepth != 8) {
			throw new IllegalArgumentException("unsupported bit depth " + bitDepth);
		}
		if (bitDepth < 8 && colorType != 0 && colorType != 3) {
			throw new IllegalArgumentException("sub-8-bit only valid for grayscale/palette, got color type " + colorType);
		}

		int srcCh; // channels (samples) per pixel in the raw scanlines
		switch (colorType) {
			case 0: srcCh = 1; break; // grayscale
			case 2: srcCh = 3; break; // truecolor
			case 3: srcCh = 1; break; // palette index
			case 4: srcCh = 2; break; // grayscale + alpha
			case 6: srcCh = 4; break; // truecolor + alpha
			default: throw new IllegalArgumentException("bad color type " + colorType);
		}

		byte[] zdata = idat.toByteArray();
		int stride = width * srcCh; // one byte per sample after expansion (what the colour loop reads)
		byte[] raw;
		if (bitDepth == 8) {
			raw = new byte[height * stride];
			inflateAndUnfilter(zdata, raw, height, stride, srcCh);
		} else {
			// Packed scanline: bitDepth bits per sample, MSB-first, byte-aligned per row.
			// PNG filter "bpp" rounds up to 1 byte for sub-8-bit samples.
			int packedStride = (width * srcCh * bitDepth + 7) / 8;
			byte[] packed = new byte[height * packedStride];
			inflateAndUnfilter(zdata, packed, height, packedStride, 1);
			raw = new byte[height * stride];
			int maxVal = (1 << bitDepth) - 1;
			int scale = colorType == 0 ? 255 / maxVal : 1; // grayscale -> 0..255; palette -> keep index
			for (int y = 0; y < height; y++) {
				int prow = y * packedStride;
				int orow = y * stride;
				int bit = 0;
				for (int s = 0; s < width * srcCh; s++) {
					int val = (packed[prow + (bit >> 3)] >> (8 - bitDepth - (bit & 7))) & maxVal;
					raw[orow + s] = (byte) (val * scale);
					bit += bitDepth;
				}
			}
		}

		// report file channel count (used only when desired==0)
		int fileCh = colorType == 3 ? 3 : (colorType == 4 ? 2 : (colorType == 0 ? 1 : srcCh));
		if (colorType == 6) fileCh = 4;
		int outCh = desired != 0 ? desired : fileCh;

		ByteBuffer out = MemoryUtil.memAlloc(width * height * outCh);
		byte[] o = new byte[width * height * outCh];
		int oi = 0;
		for (int y = 0; y < height; y++) {
			int row = y * stride;
			for (int x = 0; x < width; x++) {
				int r, g, b, a;
				switch (colorType) {
					case 0: { // gray
						r = g = b = raw[row + x] & 0xFF;
						a = 255;
						break;
					}
					case 2: { // rgb
						int s = row + x * 3;
						r = raw[s] & 0xFF; g = raw[s + 1] & 0xFF; b = raw[s + 2] & 0xFF; a = 255;
						break;
					}
					case 3: { // palette
						int idx = raw[row + x] & 0xFF;
						r = palette[idx * 3] & 0xFF; g = palette[idx * 3 + 1] & 0xFF; b = palette[idx * 3 + 2] & 0xFF;
						a = (paletteAlpha != null && idx < paletteAlpha.length) ? paletteAlpha[idx] & 0xFF : 255;
						break;
					}
					case 4: { // gray + alpha
						int s = row + x * 2;
						r = g = b = raw[s] & 0xFF; a = raw[s + 1] & 0xFF;
						break;
					}
					default: { // rgba
						int s = row + x * 4;
						r = raw[s] & 0xFF; g = raw[s + 1] & 0xFF; b = raw[s + 2] & 0xFF; a = raw[s + 3] & 0xFF;
						break;
					}
				}
				oi = pack(o, oi, r, g, b, a, outCh);
			}
		}
		out.put(o);
		out.position(0);

		w.put(0, width);
		h.put(0, height);
		channelsInFile.put(0, fileCh);
		failureReason = "";
		return out;
	}

	private static int pack(byte[] o, int oi, int r, int g, int b, int a, int outCh) {
		switch (outCh) {
			case 1:
				o[oi++] = (byte) gray(r, g, b);
				break;
			case 2:
				o[oi++] = (byte) gray(r, g, b);
				o[oi++] = (byte) a;
				break;
			case 3:
				o[oi++] = (byte) r; o[oi++] = (byte) g; o[oi++] = (byte) b;
				break;
			default:
				o[oi++] = (byte) r; o[oi++] = (byte) g; o[oi++] = (byte) b; o[oi++] = (byte) a;
				break;
		}
		return oi;
	}

	private static int gray(int r, int g, int b) {
		return (r * 77 + g * 150 + b * 29) >> 8; // stbi luminance weights
	}

	private static void inflateAndUnfilter(byte[] zdata, byte[] raw, int height, int stride, int bpp)
			throws DataFormatException {
		Inflater inf = new Inflater();
		inf.setInput(zdata);
		byte[] line = new byte[stride];
		byte[] prev = new byte[stride];
		byte[] filterByte = new byte[1];
		try {
			for (int y = 0; y < height; y++) {
				readFully(inf, filterByte, 1);
				readFully(inf, line, stride);
				int filter = filterByte[0] & 0xFF;
				unfilter(filter, line, prev, bpp, stride);
				System.arraycopy(line, 0, raw, y * stride, stride);
				byte[] t = prev; prev = line; line = t;
			}
		} finally {
			inf.end();
		}
	}

	private static void readFully(Inflater inf, byte[] dst, int len) throws DataFormatException {
		int off = 0;
		while (off < len) {
			int n = inf.inflate(dst, off, len - off);
			if (n == 0) {
				if (inf.finished() || inf.needsInput()) {
					throw new DataFormatException("truncated IDAT");
				}
			}
			off += n;
		}
	}

	private static void unfilter(int filter, byte[] cur, byte[] prev, int bpp, int stride) {
		switch (filter) {
			case 0:
				break;
			case 1: // Sub
				for (int i = bpp; i < stride; i++) {
					cur[i] = (byte) (cur[i] + cur[i - bpp]);
				}
				break;
			case 2: // Up
				for (int i = 0; i < stride; i++) {
					cur[i] = (byte) (cur[i] + prev[i]);
				}
				break;
			case 3: // Average
				for (int i = 0; i < stride; i++) {
					int a = i >= bpp ? cur[i - bpp] & 0xFF : 0;
					int b = prev[i] & 0xFF;
					cur[i] = (byte) (cur[i] + ((a + b) >> 1));
				}
				break;
			case 4: // Paeth
				for (int i = 0; i < stride; i++) {
					int a = i >= bpp ? cur[i - bpp] & 0xFF : 0;
					int b = prev[i] & 0xFF;
					int c = i >= bpp ? prev[i - bpp] & 0xFF : 0;
					cur[i] = (byte) (cur[i] + paeth(a, b, c));
				}
				break;
			default:
				break;
		}
	}

	private static int paeth(int a, int b, int c) {
		int p = a + b - c;
		int pa = Math.abs(p - a);
		int pb = Math.abs(p - b);
		int pc = Math.abs(p - c);
		if (pa <= pb && pa <= pc) {
			return a;
		}
		return pb <= pc ? b : c;
	}

	private static int i32(byte[] b, int o) {
		return (b[o] & 0xFF) << 24 | (b[o + 1] & 0xFF) << 16 | (b[o + 2] & 0xFF) << 8 | (b[o + 3] & 0xFF);
	}
}
