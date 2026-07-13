package org.lwjgl.opengl;

import net.lax1dude.eaglercraft.v1_21.platform.webgl.GLContext;

public class GL32 {
	public static void glBindBufferRange(int target, int index, int buffer, long offset, long size) {
		GLContext.gl().bindBufferRange(target, index, GLContext.CURRENT.buffers.get(buffer), (int) offset, (int) size);
	}

	public static int glClientWaitSync(long sync, int flags, long timeout) {
		return GLContext.gl().clientWaitSync(GLContext.CURRENT.syncs.get((int) sync), flags, (int) timeout);
	}

	public static void glDeleteSync(long sync) {
		GLContext.gl().deleteSync(GLContext.CURRENT.syncs.get((int) sync));
		GLContext.CURRENT.syncs.free((int) sync);
	}

	public static void glDrawElementsBaseVertex(int mode, int count, int type, long indices, int basevertex) {
		// ponytail: WebGL2 has no glDrawElementsBaseVertex; baseVertex ignored
		// (correct only when basevertex == 0, which is MC's common case)
		GLContext.gl().drawElements(mode, count, type, (int) indices);
	}

	public static void glDrawElementsInstancedBaseVertex(int mode, int count, int type, long indices, int instancecount,
			int basevertex) {
		// ponytail: baseVertex ignored (see glDrawElementsBaseVertex)
		GLContext.gl().drawElementsInstanced(mode, count, type, (int) indices, instancecount);
	}

	public static long glFenceSync(int condition, int flags) {
		return GLContext.CURRENT.syncs.register(GLContext.gl().fenceSync(condition, flags));
	}
}
