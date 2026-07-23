package net.lax1dude.eaglercraft.v1_21.platform.webgl;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSByRef;
import org.teavm.jso.typedarrays.Int8Array;

/**
 * Emulated off-heap memory for the LWJGL MemoryUtil shim. LWJGL hands MC raw
 * {@code long} addresses; we back them with one fixed {@code byte[]} and a
 * first-fit free list. Address 0 is reserved as NULL.
 *
 * ponytail: fixed 256MB arena, first-fit + coalesce. Never grows — a growable
 * arena would invalidate the byte[] that ByteBuffer.wrap handed out and MC
 * holds NativeImage buffers across frames. Bump the size constant if a big
 * world needs more; switch to segregated free lists if alloc churn shows up.
 */
public final class NativeHeap {
	private NativeHeap() {
	}

	static final int HEAP_SIZE = 256 * 1024 * 1024;
	public static final byte[] HEAP = new byte[HEAP_SIZE];

	// Free list as parallel arrays of (offset,size), kept sorted by offset for
	// O(n) coalescing. n stays small (tens of live blocks at the title screen).
	private static int[] freeOff = new int[1024];
	private static int[] freeLen = new int[1024];
	private static int freeCount;
	// Live allocation sizes keyed by address, for free()/realloc().
	private static long[] liveAddr = new long[1024];
	private static int[] liveLen = new int[1024];
	private static int liveCount;

	static {
		freeOff[0] = 8; // reserve [0,8) so address 0 == NULL
		freeLen[0] = HEAP_SIZE - 8;
		freeCount = 1;
	}

	public static synchronized long alloc(long size) {
		int need = (int) ((size + 7L) & ~7L); // 8-byte align
		if (need <= 0) {
			need = 8;
		}
		for (int i = 0; i < freeCount; i++) {
			if (freeLen[i] >= need) {
				int off = freeOff[i];
				if (freeLen[i] == need) {
					removeFree(i);
				} else {
					freeOff[i] += need;
					freeLen[i] -= need;
				}
				recordLive(off, need);
				return off;
			}
		}
		throw new OutOfMemoryError("NativeHeap exhausted, wanted " + size + " bytes");
	}

	public static long calloc(long count, long size) {
		long total = count * size;
		long addr = alloc(total);
		int off = (int) addr;
		int n = (int) ((total + 7L) & ~7L);
		for (int i = 0; i < n; i++) {
			HEAP[off + i] = 0;
		}
		return addr;
	}

	public static synchronized void free(long addr) {
		if (addr == 0L) {
			return;
		}
		int len = takeLive(addr);
		if (len < 0) {
			return; // double free / foreign pointer — ignore
		}
		insertFree((int) addr, len);
	}

	public static long realloc(long addr, long newSize) {
		if (addr == 0L) {
			return alloc(newSize);
		}
		int oldLen = liveLenOf(addr);
		long fresh = alloc(newSize);
		if (oldLen > 0) {
			int copy = (int) Math.min(oldLen, newSize);
			System.arraycopy(HEAP, (int) addr, HEAP, (int) fresh, copy);
		}
		free(addr);
		return fresh;
	}

	// --- scalar accessors (little-endian, browser native order) ---

	public static byte getByte(long a) {
		return HEAP[(int) a];
	}

	public static void putByte(long a, byte v) {
		HEAP[(int) a] = v;
	}

	public static short getShort(long a) {
		int i = (int) a;
		return (short) ((HEAP[i] & 0xFF) | (HEAP[i + 1] << 8));
	}

	public static void putShort(long a, short v) {
		int i = (int) a;
		HEAP[i] = (byte) v;
		HEAP[i + 1] = (byte) (v >> 8);
	}

	public static int getInt(long a) {
		int i = (int) a;
		return (HEAP[i] & 0xFF) | ((HEAP[i + 1] & 0xFF) << 8) | ((HEAP[i + 2] & 0xFF) << 16) | (HEAP[i + 3] << 24);
	}

	public static void putInt(long a, int v) {
		int i = (int) a;
		HEAP[i] = (byte) v;
		HEAP[i + 1] = (byte) (v >> 8);
		HEAP[i + 2] = (byte) (v >> 16);
		HEAP[i + 3] = (byte) (v >> 24);
	}

	public static long getLong(long a) {
		return (getInt(a) & 0xFFFFFFFFL) | ((long) getInt(a + 4) << 32);
	}

	public static void putLong(long a, long v) {
		putInt(a, (int) v);
		putInt(a + 4, (int) (v >> 32));
	}

	public static float getFloat(long a) {
		return Float.intBitsToFloat(getInt(a));
	}

	public static void putFloat(long a, float v) {
		putInt(a, Float.floatToRawIntBits(v));
	}

	public static void set(long addr, int value, long bytes) {
		int off = (int) addr;
		int n = (int) bytes;
		byte b = (byte) value;
		for (int i = 0; i < n; i++) {
			HEAP[off + i] = b;
		}
	}

	public static void copy(long src, long dst, long bytes) {
		System.arraycopy(HEAP, (int) src, HEAP, (int) dst, (int) bytes);
	}

	/** Zero-copy JS typed-array view over a heap slice — for WebGL uploads. */
	public static Int8Array view(long addr, int len) {
		return subarray(HEAP, (int) addr, len);
	}

	// @JSByRef passes the byte[]'s underlying Int8Array by reference (zero-copy). WITHOUT it,
	// TeaVM marshals a byte[] param by allocating a fresh Int8Array and copying every byte —
	// here that meant a full 256MB copy per upload AND the script's `array.data` read hit
	// `undefined` (the copy is a raw Int8Array, no `.data`) → crash on the first real texture
	// upload (DynamicTexture.upload → writeToTexture → view). With @JSByRef, `array` IS the
	// Int8Array, so subarray() returns a live zero-copy view over HEAP (writes back for readPixels).
	// Return a Uint8Array (not the byte[]'s native Int8Array): WebGL2 texImage2D/texSubImage2D
	// and readPixels with type UNSIGNED_BYTE reject a signed Int8Array ("ArrayBufferView not
	// Uint8Array or Uint8ClampedArray"); bufferData accepts either. The Uint8Array is a view over
	// the SAME ArrayBuffer (zero-copy, writes land back in HEAP for readPixels).
	@JSBody(params = { "array", "begin", "len" }, script = "return new Uint8Array(array.buffer, array.byteOffset + begin, len);")
	static native Int8Array subarray(@JSByRef byte[] array, int begin, int len);

	// --- internal bookkeeping ---

	private static void recordLive(int off, int len) {
		if (liveCount == liveAddr.length) {
			long[] na = new long[liveCount * 2];
			int[] nl = new int[liveCount * 2];
			System.arraycopy(liveAddr, 0, na, 0, liveCount);
			System.arraycopy(liveLen, 0, nl, 0, liveCount);
			liveAddr = na;
			liveLen = nl;
		}
		liveAddr[liveCount] = off;
		liveLen[liveCount] = len;
		liveCount++;
	}

	private static int liveLenOf(long addr) {
		for (int i = 0; i < liveCount; i++) {
			if (liveAddr[i] == addr) {
				return liveLen[i];
			}
		}
		return -1;
	}

	private static int takeLive(long addr) {
		for (int i = 0; i < liveCount; i++) {
			if (liveAddr[i] == addr) {
				int len = liveLen[i];
				liveCount--;
				liveAddr[i] = liveAddr[liveCount];
				liveLen[i] = liveLen[liveCount];
				return len;
			}
		}
		return -1;
	}

	private static void removeFree(int i) {
		freeCount--;
		freeOff[i] = freeOff[freeCount];
		freeLen[i] = freeLen[freeCount];
		sortFree();
	}

	private static void insertFree(int off, int len) {
		if (freeCount == freeOff.length) {
			int[] no = new int[freeCount * 2];
			int[] nl = new int[freeCount * 2];
			System.arraycopy(freeOff, 0, no, 0, freeCount);
			System.arraycopy(freeLen, 0, nl, 0, freeCount);
			freeOff = no;
			freeLen = nl;
		}
		freeOff[freeCount] = off;
		freeLen[freeCount] = len;
		freeCount++;
		sortFree();
		coalesce();
	}

	private static void sortFree() {
		// insertion sort by offset; freeCount is small
		for (int i = 1; i < freeCount; i++) {
			int o = freeOff[i];
			int l = freeLen[i];
			int j = i - 1;
			while (j >= 0 && freeOff[j] > o) {
				freeOff[j + 1] = freeOff[j];
				freeLen[j + 1] = freeLen[j];
				j--;
			}
			freeOff[j + 1] = o;
			freeLen[j + 1] = l;
		}
	}

	private static void coalesce() {
		int w = 0;
		for (int r = 1; r < freeCount; r++) {
			if (freeOff[w] + freeLen[w] == freeOff[r]) {
				freeLen[w] += freeLen[r];
			} else {
				w++;
				freeOff[w] = freeOff[r];
				freeLen[w] = freeLen[r];
			}
		}
		freeCount = w + 1;
	}
}
