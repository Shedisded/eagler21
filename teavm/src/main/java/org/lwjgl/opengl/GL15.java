package org.lwjgl.opengl;

import java.nio.ByteBuffer;
import net.lax1dude.eaglercraft.v1_21.platform.webgl.GLContext;

public class GL15 {
	public static void glBindBuffer(int target, int buffer) {
		GLContext.gl().bindBuffer(target, GLContext.CURRENT.buffers.get(buffer));
	}

	public static void glBufferData(int target, long size, int usage) {
		GLContext.gl().bufferData(target, (int) size, usage);
	}

	public static void glBufferData(int target, ByteBuffer data, int usage) {
		GLContext.gl().bufferData(target, GLShim.view(data), usage);
	}

	public static void glBufferSubData(int target, long offset, ByteBuffer data) {
		GLContext.gl().bufferSubData(target, (int) offset, GLShim.view(data));
	}

	public static void glDeleteBuffers(int buffer) {
		GLContext.gl().deleteBuffer(GLContext.CURRENT.buffers.get(buffer));
		GLContext.CURRENT.buffers.free(buffer);
	}

	public static int glGenBuffers() {
		return GLContext.CURRENT.buffers.register(GLContext.gl().createBuffer());
	}

	public static boolean glUnmapBuffer(int target) {
		return GLShim.unmapBuffer(target);
	}
}
