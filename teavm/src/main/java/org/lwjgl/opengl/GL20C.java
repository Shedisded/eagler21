package org.lwjgl.opengl;

import net.lax1dude.eaglercraft.v1_21.platform.webgl.GLContext;
import net.lax1dude.eaglercraft.v1_21.platform.webgl.NativeHeap;
import org.lwjgl.system.MemoryUtil;

public class GL20C {
	/**
	 * {@code strings} is an array of {@code count} pointers to NUL-terminated
	 * UTF-8 C strings in the heap; {@code lengths} (an int[] pointer or 0 for
	 * NUL-terminated) is ignored — MC always passes NUL-terminated source.
	 */
	public static void nglShaderSource(int shader, int count, long strings, long lengths) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < count; i++) {
			long strPtr = NativeHeap.getLong(strings + (long) i * 8L);
			sb.append(MemoryUtil.memUTF8(strPtr));
		}
		GLContext.gl().shaderSource(GLContext.CURRENT.shaders.get(shader), sb.toString());
	}
}
