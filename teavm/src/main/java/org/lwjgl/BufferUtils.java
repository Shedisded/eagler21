package org.lwjgl;

import java.nio.ByteBuffer;
import net.lax1dude.eaglercraft.v1_21.platform.webgl.NativeHeap;
import org.lwjgl.system.MemoryUtil;

/** TeaVM shim for org.lwjgl.BufferUtils: heap-backed direct buffers. */
public final class BufferUtils {
	private BufferUtils() {
	}

	public static ByteBuffer createByteBuffer(int capacity) {
		return MemoryUtil.newByteBuffer(NativeHeap.calloc(1, capacity), capacity);
	}
}
