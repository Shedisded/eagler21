package org.lwjgl.opengl;

import java.nio.ByteBuffer;
import net.lax1dude.eaglercraft.v1_21.platform.webgl.GLContext;
import org.teavm.jso.typedarrays.ArrayBufferView;

public class GL11 {
	public static void glBindTexture(int target, int texture) {
		GL11C.glBindTexture(target, texture);
	}

	public static void glClearColor(float red, float green, float blue, float alpha) {
		GLContext.gl().clearColor(red, green, blue, alpha);
	}

	public static void glClearDepth(double depth) {
		GLContext.gl().clearDepth((float) depth);
	}

	public static void glClear(int mask) {
		GLContext.gl().clear(mask);
	}

	public static void glColorMask(boolean red, boolean green, boolean blue, boolean alpha) {
		GLContext.gl().colorMask(red, green, blue, alpha);
	}

	public static void glDeleteTextures(int texture) {
		GLContext.gl().deleteTexture(GLContext.CURRENT.textures.get(texture));
		GLContext.CURRENT.textures.free(texture);
	}

	public static void glDepthFunc(int func) {
		GLContext.gl().depthFunc(func);
	}

	public static void glDepthMask(boolean flag) {
		GLContext.gl().depthMask(flag);
	}

	public static void glDisable(int cap) {
		GLContext.gl().disable(cap);
	}

	public static void glDrawArrays(int mode, int first, int count) {
		GLContext.gl().drawArrays(mode, first, count);
	}

	public static void glDrawBuffer(int buf) {
		// ponytail: unsupported in WebGL2 (single default draw buffer)
	}

	public static void glDrawElements(int mode, int count, int type, long indices) {
		GLContext.gl().drawElements(mode, count, type, (int) indices);
	}

	public static void glEnable(int cap) {
		GLContext.gl().enable(cap);
	}

	public static int glGenTextures() {
		return GLContext.CURRENT.textures.register(GLContext.gl().createTexture());
	}

	public static int glGetError() {
		return GLContext.gl().getError();
	}

	public static float glGetFloat(int pname) {
		return GLContext.gl().getParameterf(pname);
	}

	public static int glGetInteger(int pname) {
		return GLContext.gl().getParameteri(pname);
	}

	public static String glGetString(int pname) {
		String s = GLContext.gl().getParameterString(pname);
		return s == null ? "" : s;
	}

	public static int glGetTexLevelParameteri(int target, int level, int pname) {
		// ponytail: no getTexLevelParameter in WebGL2
		return 0;
	}

	public static void glLogicOp(int opcode) {
		// ponytail: unsupported in WebGL2
	}

	public static void glPixelStorei(int pname, int param) {
		GLContext.gl().pixelStorei(pname, param);
	}

	public static void glPolygonMode(int face, int mode) {
		// ponytail: unsupported in WebGL2 (fill only)
	}

	public static void glPolygonOffset(float factor, float units) {
		GLContext.gl().polygonOffset(factor, units);
	}

	public static void glReadPixels(int x, int y, int width, int height, int format, int type, long pixels) {
		GLContext.gl().readPixels(x, y, width, height, format, type,
				GLShim.view(pixels, GLShim.pixelBytes(format, type, width, height)));
	}

	public static void glTexImage2D(int target, int level, int internalformat, int width, int height, int border,
			int format, int type, ByteBuffer pixels) {
		GLContext.gl().texImage2D(target, level, internalformat, width, height, border, format, type,
				pixels == null ? (ArrayBufferView) null : GLShim.view(pixels));
	}

	public static void glTexParameteri(int target, int pname, int param) {
		GLContext.gl().texParameteri(target, pname, param);
	}

	public static void glTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height,
			int format, int type, long pixels) {
		GLContext.gl().texSubImage2D(target, level, xoffset, yoffset, width, height, format, type,
				GLShim.view(pixels, GLShim.pixelBytes(format, type, width, height)));
	}

	public static void glTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height,
			int format, int type, ByteBuffer pixels) {
		GLContext.gl().texSubImage2D(target, level, xoffset, yoffset, width, height, format, type, GLShim.view(pixels));
	}

	public static void glViewport(int x, int y, int width, int height) {
		GLContext.gl().viewport(x, y, width, height);
	}
}
