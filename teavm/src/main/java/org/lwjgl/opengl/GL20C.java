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
		GLContext.gl().shaderSource(GLContext.CURRENT.shaders.get(shader), translateToEs300(sb.toString()));
	}

	/**
	 * MC ships desktop GLSL ("#version 330 core"); WebGL2 requires GLSL ES 3.00. Strip the
	 * original #version line (wherever it landed after moj_import expansion) and prepend
	 * "#version 300 es" plus the default precision qualifiers that ES fragment shaders
	 * require (desktop GLSL has implicit highp). MC's core-shader syntax — in/out,
	 * layout(std140) uniform blocks, texture() — is already ES-300 compatible.
	 */
	static String translateToEs300(String src) {
		int v = src.indexOf("#version");
		if (v >= 0) {
			int eol = src.indexOf('\n', v);
			if (eol < 0) {
				eol = src.length() - 1;
			}
			src = src.substring(0, v) + src.substring(Math.min(eol + 1, src.length()));
		}
		return "#version 300 es\n"
			+ "precision highp float;\n"
			+ "precision highp int;\n"
			+ "precision highp sampler2D;\n"
			+ src;
	}
}
