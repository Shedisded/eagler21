package org.lwjgl.opengl;

import net.lax1dude.eaglercraft.v1_21.platform.webgl.GLContext;

public class GL31 {
	public static void glCopyBufferSubData(int readTarget, int writeTarget, long readOffset, long writeOffset,
			long size) {
		GLContext.gl().copyBufferSubData(readTarget, writeTarget, (int) readOffset, (int) writeOffset, (int) size);
	}

	public static void glDrawArraysInstanced(int mode, int first, int count, int primcount) {
		GLContext.gl().drawArraysInstanced(mode, first, count, primcount);
	}

	public static void glDrawElementsInstanced(int mode, int count, int type, long indices, int primcount) {
		GLContext.gl().drawElementsInstanced(mode, count, type, (int) indices, primcount);
	}

	public static String glGetActiveUniformBlockName(int program, int uniformBlockIndex) {
		String s = GLContext.gl().getActiveUniformBlockName(GLContext.CURRENT.programs.get(program), uniformBlockIndex);
		return s == null ? "" : s;
	}

	public static int glGetUniformBlockIndex(int program, CharSequence uniformBlockName) {
		return GLContext.gl().getUniformBlockIndex(GLContext.CURRENT.programs.get(program),
				uniformBlockName.toString());
	}

	public static void glTexBuffer(int target, int internalformat, int buffer) {
		// ponytail: no texture buffer objects in WebGL2
	}

	public static void glUniformBlockBinding(int program, int uniformBlockIndex, int uniformBlockBinding) {
		GLContext.gl().uniformBlockBinding(GLContext.CURRENT.programs.get(program), uniformBlockIndex,
				uniformBlockBinding);
	}
}
