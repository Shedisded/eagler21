package org.lwjgl.system;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import org.lwjgl.PointerBuffer;
import net.lax1dude.eaglercraft.v1_21.platform.webgl.NativeHeap;

/**
 * TeaVM shim for org.lwjgl.system.MemoryStack. JS is single-threaded, so one
 * global stack. Allocations come from {@link NativeHeap} and are freed en masse
 * when the frame is popped (try-with-resources close()).
 *
 * ponytail: not a real contiguous stack arena — just frame-scoped alloc/free.
 * Semantics MC relies on (scratch buffers valid until close) hold.
 */
public final class MemoryStack implements AutoCloseable {
	private static final MemoryStack INSTANCE = new MemoryStack();

	private final ArrayList<ArrayList<Long>> frameStack = new ArrayList<>();
	private ArrayList<Long> current;

	private MemoryStack() {
	}

	public static MemoryStack stackPush() {
		INSTANCE.pushFrame();
		return INSTANCE;
	}

	public static MemoryStack stackGet() {
		return INSTANCE;
	}

	private void pushFrame() {
		current = new ArrayList<>();
		frameStack.add(current);
	}

	@Override
	public void close() {
		if (frameStack.isEmpty()) {
			return;
		}
		ArrayList<Long> frame = frameStack.remove(frameStack.size() - 1);
		for (Long addr : frame) {
			NativeHeap.free(addr);
		}
		current = frameStack.isEmpty() ? null : frameStack.get(frameStack.size() - 1);
	}

	private long track(long addr) {
		current.add(addr);
		return addr;
	}

	public ByteBuffer malloc(int size) {
		return MemoryUtil.newByteBuffer(track(NativeHeap.alloc(size)), size);
	}

	public ByteBuffer calloc(int size) {
		return MemoryUtil.newByteBuffer(track(NativeHeap.calloc(1, size)), size);
	}

	public IntBuffer mallocInt(int size) {
		long addr = track(NativeHeap.alloc(size * 4L));
		ByteBuffer bb = MemoryUtil.newByteBuffer(addr, size * 4);
		IntBuffer ib = bb.asIntBuffer();
		MemoryUtil.register(ib, addr, size * 4);
		return ib;
	}

	public IntBuffer callocInt(int size) {
		long addr = track(NativeHeap.calloc(size, 4));
		ByteBuffer bb = MemoryUtil.newByteBuffer(addr, size * 4);
		IntBuffer ib = bb.asIntBuffer();
		MemoryUtil.register(ib, addr, size * 4);
		return ib;
	}

	public PointerBuffer mallocPointer(int size) {
		long addr = track(NativeHeap.alloc(size * 8L));
		return new PointerBuffer(addr, size);
	}

	// LWJGL's getPointer() is an int stack cursor, not an address. Our fake stack
	// has no linear buffer, so the live allocation count is a stand-in cursor (MC
	// never pairs it with setPointer here, so the exact value is inert).
	public int getPointer() {
		return current == null ? 0 : current.size();
	}

	public long nmalloc(int alignment, int size) {
		return track(NativeHeap.alloc(size));
	}
}
