package org.lwjgl.opengl;

import java.nio.ByteBuffer;
import net.lax1dude.eaglercraft.v1_21.platform.webgl.GLContext;

public class GL30 {
	public static void glBindFramebuffer(int target, int framebuffer) {
		GLContext.gl().bindFramebuffer(target, GLContext.CURRENT.framebuffers.get(framebuffer));
	}

	public static void glBindVertexArray(int array) {
		GLContext.gl().bindVertexArray(GLContext.CURRENT.vertexArrays.get(array));
	}

	public static void glBlitFramebuffer(int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1,
			int dstY1, int mask, int filter) {
		GLContext.gl().blitFramebuffer(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
	}

	public static void glDeleteFramebuffers(int framebuffer) {
		GLContext.gl().deleteFramebuffer(GLContext.CURRENT.framebuffers.get(framebuffer));
		GLContext.CURRENT.framebuffers.free(framebuffer);
	}

	public static void glFlushMappedBufferRange(int target, long offset, long length) {
		GLShim.flushMappedBufferRange(target, offset, length);
	}

	public static void glFramebufferTexture2D(int target, int attachment, int textarget, int texture, int level) {
		GLContext.gl().framebufferTexture2D(target, attachment, textarget, GLContext.CURRENT.textures.get(texture),
				level);
	}

	public static int glGenFramebuffers() {
		return GLContext.CURRENT.framebuffers.register(GLContext.gl().createFramebuffer());
	}

	public static int glGenVertexArrays() {
		return GLContext.CURRENT.vertexArrays.register(GLContext.gl().createVertexArray());
	}

	public static ByteBuffer glMapBufferRange(int target, long offset, long length, int access) {
		return GLShim.mapBufferRange(target, offset, length, access);
	}

	public static void glVertexAttribIPointer(int index, int size, int type, int stride, long pointer) {
		GLContext.gl().vertexAttribIPointer(index, size, type, stride, (int) pointer);
	}
}
