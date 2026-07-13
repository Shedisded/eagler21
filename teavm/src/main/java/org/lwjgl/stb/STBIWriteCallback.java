package org.lwjgl.stb;

import java.nio.ByteBuffer;
import org.lwjgl.system.MemoryUtil;

/**
 * TeaVM shim. MC subclasses this to receive PNG bytes from stbi_write. Our
 * {@link STBImageWrite#nstbi_write_png_to_func} never invokes the callback
 * (screenshot writing is stubbed), so {@link #invoke} is dead; {@link #getData}
 * and {@link #address} must still resolve for the linker.
 */
public abstract class STBIWriteCallback {
	protected STBIWriteCallback() {
	}

	public static ByteBuffer getData(long address, int size) {
		return MemoryUtil.memByteBuffer(address, size);
	}

	public abstract void invoke(long context, long data, int size);

	public long address() {
		return 0L;
	}

	public void free() {
		// ponytail: no native callback to release (invoke is dead)
	}
}
