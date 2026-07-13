package org.lwjgl;

import java.nio.ByteBuffer;
import net.lax1dude.eaglercraft.v1_21.platform.webgl.NativeHeap;
import org.lwjgl.system.MemoryUtil;

/**
 * TeaVM shim for org.lwjgl.PointerBuffer: an array of 8-byte pointers living in
 * {@link NativeHeap}, so {@link #address0()} can be handed to native-style GL
 * entry points (e.g. nglShaderSource).
 */
public final class PointerBuffer {
	private final long base;
	private final int capacity;
	private int position;

	public PointerBuffer(long base, int capacity) {
		this.base = base;
		this.capacity = capacity;
	}

	public long address0() {
		return base;
	}

	public long address() {
		return base;
	}

	public int position() {
		return position;
	}

	public PointerBuffer position(int newPosition) {
		this.position = newPosition;
		return this;
	}

	public PointerBuffer flip() {
		this.position = 0;
		return this;
	}

	public PointerBuffer rewind() {
		this.position = 0;
		return this;
	}

	public long get() {
		return NativeHeap.getLong(base + (long) (position++) * 8L);
	}

	public long get(int index) {
		return NativeHeap.getLong(base + (long) index * 8L);
	}

	public PointerBuffer put(long value) {
		NativeHeap.putLong(base + (long) (position++) * 8L, value);
		return this;
	}

	public PointerBuffer put(int index, long value) {
		NativeHeap.putLong(base + (long) index * 8L, value);
		return this;
	}

	public PointerBuffer put(ByteBuffer buffer) {
		return put(MemoryUtil.memAddress(buffer));
	}

	public int capacity() {
		return capacity;
	}

	public int remaining() {
		return capacity - position;
	}

	// ponytail: no independent limit tracked; this buffer is capacity-bounded.
	public int limit() {
		return capacity;
	}
}
