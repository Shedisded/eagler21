package org.lwjgl.opengl;

import java.nio.ByteBuffer;

/** TeaVM shim: inert. GL_ARB_direct_state_access is false; MC uses the bind-then-edit path. */
public final class ARBDirectStateAccess {
	public static void glBlitNamedFramebuffer(int readFb, int drawFb, int srcX0, int srcY0, int srcX1, int srcY1,
			int dstX0, int dstY0, int dstX1, int dstY1, int mask, int filter) {
	}

	public static void glCopyNamedBufferSubData(int readBuffer, int writeBuffer, long readOffset, long writeOffset,
			long size) {
	}

	public static int glCreateBuffers() {
		return 0;
	}

	public static int glCreateFramebuffers() {
		return 0;
	}

	public static void glFlushMappedNamedBufferRange(int buffer, long offset, long length) {
	}

	public static ByteBuffer glMapNamedBufferRange(int buffer, long offset, long length, int access) {
		return null;
	}

	public static void glNamedBufferData(int buffer, long size, int usage) {
	}

	public static void glNamedBufferData(int buffer, ByteBuffer data, int usage) {
	}

	public static void glNamedBufferStorage(int buffer, long size, int flags) {
	}

	public static void glNamedBufferStorage(int buffer, ByteBuffer data, int flags) {
	}

	public static void glNamedBufferSubData(int buffer, long offset, ByteBuffer data) {
	}

	public static void glNamedFramebufferTexture(int framebuffer, int attachment, int texture, int level) {
	}

	public static boolean glUnmapNamedBuffer(int buffer) {
		return true;
	}
}
