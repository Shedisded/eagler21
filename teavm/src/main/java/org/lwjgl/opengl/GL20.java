package org.lwjgl.opengl;

import net.lax1dude.eaglercraft.v1_21.platform.webgl.GLContext;
import org.teavm.jso.webgl.WebGLUniformLocation;

public class GL20 {
	public static void glAttachShader(int program, int shader) {
		GLContext.gl().attachShader(GLContext.CURRENT.programs.get(program), GLContext.CURRENT.shaders.get(shader));
	}

	public static void glBindAttribLocation(int program, int index, CharSequence name) {
		GLContext.gl().bindAttribLocation(GLContext.CURRENT.programs.get(program), index, name.toString());
	}

	public static void glCompileShader(int shader) {
		GLContext.gl().compileShader(GLContext.CURRENT.shaders.get(shader));
	}

	public static int glCreateProgram() {
		return GLContext.CURRENT.programs.register(GLContext.gl().createProgram());
	}

	public static int glCreateShader(int type) {
		return GLContext.CURRENT.shaders.register(GLContext.gl().createShader(type));
	}

	public static void glDeleteProgram(int program) {
		GLContext.gl().deleteProgram(GLContext.CURRENT.programs.get(program));
		GLContext.CURRENT.programs.free(program);
	}

	public static void glDeleteShader(int shader) {
		GLContext.gl().deleteShader(GLContext.CURRENT.shaders.get(shader));
		GLContext.CURRENT.shaders.free(shader);
	}

	public static void glEnableVertexAttribArray(int index) {
		GLContext.gl().enableVertexAttribArray(index);
	}

	public static int glGetProgrami(int program, int pname) {
		return GLContext.gl().getProgramParameteri(GLContext.CURRENT.programs.get(program), pname);
	}

	public static String glGetProgramInfoLog(int program, int maxLength) {
		String s = GLContext.gl().getProgramInfoLog(GLContext.CURRENT.programs.get(program));
		return s == null ? "" : s;
	}

	public static int glGetShaderi(int shader, int pname) {
		return GLContext.gl().getShaderParameteri(GLContext.CURRENT.shaders.get(shader), pname);
	}

	public static String glGetShaderInfoLog(int shader, int maxLength) {
		String s = GLContext.gl().getShaderInfoLog(GLContext.CURRENT.shaders.get(shader));
		return s == null ? "" : s;
	}

	public static int glGetUniformLocation(int program, CharSequence name) {
		WebGLUniformLocation loc = GLContext.gl().getUniformLocation(GLContext.CURRENT.programs.get(program),
				name.toString());
		return loc == null ? -1 : GLContext.CURRENT.uniforms.register(loc);
	}

	public static void glLinkProgram(int program) {
		GLContext.gl().linkProgram(GLContext.CURRENT.programs.get(program));
	}

	public static void glScissor(int x, int y, int width, int height) {
		GLContext.gl().scissor(x, y, width, height);
	}

	public static void glUniform1i(int location, int v0) {
		GLContext.gl().uniform1i(GLContext.CURRENT.uniforms.get(location), v0);
	}

	public static void glUseProgram(int program) {
		GLContext.gl().useProgram(GLContext.CURRENT.programs.get(program));
	}

	public static void glVertexAttribPointer(int index, int size, int type, boolean normalized, int stride,
			long pointer) {
		GLContext.gl().vertexAttribPointer(index, size, type, normalized, stride, (int) pointer);
	}
}
