package org.lwjgl.system;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.IdentityHashMap;
import net.lax1dude.eaglercraft.v1_21.platform.webgl.NativeHeap;

/**
 * TeaVM shim for org.lwjgl.system.MemoryUtil. LWJGL hands MC raw {@code long}
 * addresses and "direct" NIO buffers; we back both with {@link NativeHeap} and
 * keep a buffer→address registry so {@link #memAddress} round-trips.
 *
 * Only the subset MC 1.21 actually calls is implemented; descriptors match the
 * real LWJGL 3.3.3 so precompiled MC bytecode links against this class.
 */
public final class MemoryUtil {
	private MemoryUtil() {
	}

	// Identity, not content: ByteBuffer.hashCode mutates with position.
	private static final IdentityHashMap<Buffer, Long> ADDR = new IdentityHashMap<>();
	private static final IdentityHashMap<Buffer, Integer> BYTES = new IdentityHashMap<>();

	public interface MemoryAllocator {
		long malloc(long size);

		long calloc(long num, long size);

		long realloc(long ptr, long size);

		void free(long ptr);

		long aligned_alloc(long alignment, long size);

		void aligned_free(long ptr);
	}

	private static final MemoryAllocator ALLOCATOR = new MemoryAllocator() {
		public long malloc(long size) {
			return NativeHeap.alloc(size);
		}

		public long calloc(long num, long size) {
			return NativeHeap.calloc(num, size);
		}

		public long realloc(long ptr, long size) {
			return NativeHeap.realloc(ptr, size);
		}

		public void free(long ptr) {
			NativeHeap.free(ptr);
		}

		public long aligned_alloc(long alignment, long size) {
			return NativeHeap.alloc(size);
		}

		public void aligned_free(long ptr) {
			NativeHeap.free(ptr);
		}
	};

	public static MemoryAllocator getAllocator() {
		return ALLOCATOR;
	}

	public static MemoryAllocator getAllocator(boolean debug) {
		return ALLOCATOR;
	}

	// --- raw native memory ---

	public static long nmemAlloc(long size) {
		return NativeHeap.alloc(size);
	}

	public static long nmemCalloc(long num, long size) {
		return NativeHeap.calloc(num, size);
	}

	public static void nmemFree(long ptr) {
		NativeHeap.free(ptr);
	}

	public static byte memGetByte(long ptr) {
		return NativeHeap.getByte(ptr);
	}

	public static int memGetInt(long ptr) {
		return NativeHeap.getInt(ptr);
	}

	public static void memPutByte(long ptr, byte value) {
		NativeHeap.putByte(ptr, value);
	}

	public static void memPutShort(long ptr, short value) {
		NativeHeap.putShort(ptr, value);
	}

	public static void memPutInt(long ptr, int value) {
		NativeHeap.putInt(ptr, value);
	}

	public static void memPutFloat(long ptr, float value) {
		NativeHeap.putFloat(ptr, value);
	}

	public static void memSet(long ptr, int value, long bytes) {
		NativeHeap.set(ptr, value, bytes);
	}

	public static void memCopy(long src, long dst, long bytes) {
		NativeHeap.copy(src, dst, bytes);
	}

	// --- NIO buffer views over native memory ---

	public static ByteBuffer memAlloc(int size) {
		return newByteBuffer(NativeHeap.alloc(size), size);
	}

	public static IntBuffer memAllocInt(int size) {
		ByteBuffer bb = memAlloc(size * 4);
		IntBuffer ib = bb.asIntBuffer();
		ADDR.put(ib, ADDR.get(bb));
		BYTES.put(ib, size * 4);
		return ib;
	}

	public static ByteBuffer memRealloc(ByteBuffer old, int newSize) {
		// ponytail: preserve position/limit like real LWJGL. TextureUtil.readResource grows the
		// buffer mid-fill (channel.read then memRealloc when full) and keeps reading into the
		// RETURNED buffer — a position-0 buffer would overwrite the PNG signature at index 0 with
		// later bytes -> "Bad PNG Signature" for any image bigger than the initial capacity.
		int oldPos = old.position();
		long oldAddr = addressOf(old);
		long newAddr = NativeHeap.realloc(oldAddr, newSize);
		unregister(old);
		ByteBuffer bb = newByteBuffer(newAddr, newSize); // capacity==limit==newSize, room to keep filling
		bb.position(Math.min(oldPos, newSize));
		return bb;
	}

	public static void memFree(Buffer buffer) {
		if (buffer == null) {
			return;
		}
		Long addr = ADDR.get(buffer);
		if (addr != null) {
			NativeHeap.free(addr);
			unregister(buffer);
		}
	}

	public static long memAddress(ByteBuffer buffer) {
		return addressOf(buffer) + buffer.position();
	}

	public static long memAddress(IntBuffer buffer) {
		return addressOf(buffer) + (long) buffer.position() * 4L;
	}

	public static long memAddress0(Buffer buffer) {
		return addressOf(buffer);
	}

	public static ByteBuffer memByteBuffer(long address, int capacity) {
		return newByteBuffer(address, capacity);
	}

	public static ByteBuffer memByteBuffer(IntBuffer buffer) {
		return newByteBuffer(addressOf(buffer), BYTES.getOrDefault(buffer, buffer.capacity() * 4));
	}

	public static IntBuffer memIntBuffer(long address, int capacity) {
		ByteBuffer bb = newByteBuffer(address, capacity * 4);
		IntBuffer ib = bb.asIntBuffer();
		ADDR.put(ib, address);
		BYTES.put(ib, capacity * 4);
		return ib;
	}

	public static ByteBuffer memSlice(ByteBuffer buffer, int offset, int length) {
		return newByteBuffer(addressOf(buffer) + offset, length);
	}

	public static void memCopy(ByteBuffer src, ByteBuffer dst) {
		int n = Math.min(src.remaining(), dst.remaining());
		Long s = ADDR.get(src);
		Long d = ADDR.get(dst);
		if (s != null && d != null) {
			NativeHeap.copy(s + src.position(), d + dst.position(), n);
		} else {
			int sp = src.position();
			int dp = dst.position();
			for (int i = 0; i < n; i++) {
				dst.put(dp + i, src.get(sp + i));
			}
		}
	}

	public static String memUTF8(long address) {
		if (address == 0L) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		// UTF-8 decode until NUL. Good enough for GLFW/GL diagnostic strings.
		long p = address;
		int b;
		while ((b = NativeHeap.getByte(p++) & 0xFF) != 0) {
			if (b < 0x80) {
				sb.append((char) b);
			} else if (b < 0xE0) {
				int b2 = NativeHeap.getByte(p++) & 0x3F;
				sb.append((char) ((b & 0x1F) << 6 | b2));
			} else if (b < 0xF0) {
				int b2 = NativeHeap.getByte(p++) & 0x3F;
				int b3 = NativeHeap.getByte(p++) & 0x3F;
				sb.append((char) ((b & 0x0F) << 12 | b2 << 6 | b3));
			} else {
				int b2 = NativeHeap.getByte(p++) & 0x3F;
				int b3 = NativeHeap.getByte(p++) & 0x3F;
				int b4 = NativeHeap.getByte(p++) & 0x3F;
				int cp = (b & 0x07) << 18 | b2 << 12 | b3 << 6 | b4;
				sb.appendCodePoint(cp);
			}
		}
		return sb.toString();
	}

	// --- registry helpers, shared with BufferUtils/MemoryStack/PointerBuffer ---

	public static ByteBuffer newByteBuffer(long address, int capacity) {
		ByteBuffer bb = ByteBuffer.wrap(NativeHeap.HEAP, (int) address, capacity).slice().order(ByteOrder.LITTLE_ENDIAN);
		ADDR.put(bb, address);
		BYTES.put(bb, capacity);
		return bb;
	}

	public static long addressOf(Buffer buffer) {
		Long a = ADDR.get(buffer);
		return a != null ? a : 0L;
	}

	public static void register(Buffer buffer, long address, int capacityBytes) {
		ADDR.put(buffer, address);
		BYTES.put(buffer, capacityBytes);
	}

	private static void unregister(Buffer buffer) {
		ADDR.remove(buffer);
		BYTES.remove(buffer);
	}
}
