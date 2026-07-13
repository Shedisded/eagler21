package org.lwjgl.opengl;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_21.platform.webgl.GLContext;
import net.lax1dude.eaglercraft.v1_21.platform.webgl.NativeHeap;
import org.lwjgl.system.MemoryUtil;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;
import org.teavm.jso.typedarrays.ArrayBufferView;

/**
 * Package-private helpers shared by the {@code org.lwjgl.opengl} WebGL2 shims:
 * heap-view construction, JS number coercion, pixel-rect sizing, and the shadow
 * buffer-mapping registry used by glMapBufferRange/glFlushMappedBufferRange/
 * glUnmapBuffer.
 */
final class GLShim {
	private GLShim() {
	}

	/** Zero-copy typed-array view over a heap ByteBuffer's remaining bytes. */
	static ArrayBufferView view(ByteBuffer b) {
		return NativeHeap.view(MemoryUtil.memAddress(b), b.remaining());
	}

	/** Zero-copy typed-array view over a raw heap pointer of the given length. */
	static ArrayBufferView view(long addr, int len) {
		return NativeHeap.view(addr, len);
	}

	/** Coerce a WebGL getXxxParameter JS result to int (bool -> 0/1, number -> truncated). */
	@JSBody(params = "o", script = "return (o == null) ? 0 : (o | 0);")
	static native int jsToInt(JSObject o);

	/**
	 * Conservative byte size of a width x height pixel rectangle, used only to
	 * size heap views for texture uploads / readPixels. A slightly large view is
	 * harmless; a too-small one is not, so unknown formats fall back to 4 bpp.
	 */
	static int pixelBytes(int format, int type, int width, int height) {
		int channels;
		switch (format) {
			case 0x1903: // RED
			case 0x1906: // ALPHA
			case 0x1902: // DEPTH_COMPONENT
			case 0x1901: // STENCIL_INDEX
			case 0x1909: // LUMINANCE
			case 0x8D94: // RED_INTEGER
				channels = 1;
				break;
			case 0x8227: // RG
			case 0x8228: // RG_INTEGER
			case 0x190A: // LUMINANCE_ALPHA
			case 0x84F9: // DEPTH_STENCIL
				channels = 2;
				break;
			case 0x1907: // RGB
			case 0x8D98: // RGB_INTEGER
				channels = 3;
				break;
			default: // RGBA (0x1908), RGBA_INTEGER (0x8D99), BGRA, ...
				channels = 4;
				break;
		}
		int channelBytes;
		switch (type) {
			case 0x1400: // BYTE
			case 0x1401: // UNSIGNED_BYTE
				channelBytes = 1;
				break;
			case 0x1402: // SHORT
			case 0x1403: // UNSIGNED_SHORT
			case 0x140B: // HALF_FLOAT
			case 0x8033: // UNSIGNED_SHORT_4_4_4_4
			case 0x8034: // UNSIGNED_SHORT_5_5_5_1
			case 0x8363: // UNSIGNED_SHORT_5_6_5
				return width * height * 2; // packed: 2 bytes per whole pixel
			case 0x1404: // INT
			case 0x1405: // UNSIGNED_INT
			case 0x1406: // FLOAT
				channelBytes = 4;
				break;
			case 0x8368: // UNSIGNED_INT_2_10_10_10_REV
			case 0x84FA: // UNSIGNED_INT_24_8
			case 0x8C3B: // UNSIGNED_INT_10F_11F_11F_REV
			case 0x8C3E: // UNSIGNED_INT_5_9_9_9_REV
				return width * height * 4; // packed: 4 bytes per whole pixel
			default:
				channelBytes = 4;
				break;
		}
		return width * height * channels * channelBytes;
	}

	// --- shadow buffer mapping ---
	// ponytail: WebGL2 has no glMapBufferRange. We hand MC a scratch heap ByteBuffer
	// and replay its writes with bufferSubData on flush/unmap. Read-back of existing
	// buffer contents and the access-flag bits are ignored (MC's mapped path is
	// write-only streaming). One mapping per buffer target at a time, per GL.

	private static final class Mapping {
		final int offset;
		final long base;
		final int length;
		final ByteBuffer buffer;
		boolean flushed;

		Mapping(int offset, long base, int length, ByteBuffer buffer) {
			this.offset = offset;
			this.base = base;
			this.length = length;
			this.buffer = buffer;
		}
	}

	private static final Map<Integer, Mapping> MAPPINGS = new HashMap<>();

	static ByteBuffer mapBufferRange(int target, long offset, long length, int access) {
		ByteBuffer bb = MemoryUtil.memAlloc((int) length);
		MAPPINGS.put(target, new Mapping((int) offset, MemoryUtil.memAddress(bb), (int) length, bb));
		return bb;
	}

	static void flushMappedBufferRange(int target, long offset, long length) {
		Mapping m = MAPPINGS.get(target);
		if (m == null) {
			return;
		}
		m.flushed = true;
		GLContext.gl().bufferSubData(target, (int) (m.offset + offset), NativeHeap.view(m.base + offset, (int) length));
	}

	static boolean unmapBuffer(int target) {
		Mapping m = MAPPINGS.remove(target);
		if (m == null) {
			return true;
		}
		// If the app used explicit flushes we already uploaded exactly what changed;
		// otherwise upload the whole mapped range (write-only mapping semantics).
		if (!m.flushed) {
			GLContext.gl().bufferSubData(target, m.offset, NativeHeap.view(m.base, m.length));
		}
		MemoryUtil.memFree(m.buffer);
		return true;
	}
}
