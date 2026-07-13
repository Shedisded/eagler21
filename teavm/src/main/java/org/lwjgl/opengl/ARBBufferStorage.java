package org.lwjgl.opengl;

import java.nio.ByteBuffer;

/** TeaVM shim: inert. GL_ARB_buffer_storage is false; MC uses glBufferData instead. */
public final class ARBBufferStorage {
	public static void glBufferStorage(int target, long size, int flags) {
	}

	public static void glBufferStorage(int target, ByteBuffer data, int flags) {
	}
}
