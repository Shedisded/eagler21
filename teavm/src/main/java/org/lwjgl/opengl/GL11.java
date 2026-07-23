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
		if (isWebGlCapability(cap)) {
			GLContext.gl().disable(cap);
		}
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
		if (isWebGlCapability(cap)) {
			GLContext.gl().enable(cap);
		}
	}

	// WebGL2 accepts only a fixed set of glEnable/glDisable capabilities. MC also toggles
	// desktop-only caps that WebGL2 either has always-on or lacks — e.g. GL_TEXTURE_CUBE_MAP_SEAMLESS
	// (0x884F) and GL_PROGRAM_POINT_SIZE (0x8642), both seen at GlDevice init. Passing those to
	// gl.enable/disable throws INVALID_ENUM, so filter to the WebGL2-legal set and no-op the rest.
	private static boolean isWebGlCapability(int cap) {
		switch (cap) {
			case 0x0BE2: // BLEND
			case 0x0B44: // CULL_FACE
			case 0x0B71: // DEPTH_TEST
			case 0x0BD0: // DITHER
			case 0x8037: // POLYGON_OFFSET_FILL
			case 0x809E: // SAMPLE_ALPHA_TO_COVERAGE
			case 0x80A0: // SAMPLE_COVERAGE
			case 0x0C11: // SCISSOR_TEST
			case 0x0B90: // STENCIL_TEST
			case 0x8C89: // RASTERIZER_DISCARD
				return true;
			default:
				return false;
		}
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

	// ponytail: track UNPACK pixel-store state so glTexSubImage2D(long) can size its heap view
	// exactly like WebGL2 reads it. MC uploads atlas sub-rectangles from a larger source buffer with
	// UNPACK_ROW_LENGTH=fullWidth + SKIP_ROWS/SKIP_PIXELS; sizing the view as width*height*bpp is too
	// small -> "texSubImage2D: ArrayBufferView not big enough" -> fatal.
	private static int unpackRowLength;
	private static int unpackSkipRows;
	private static int unpackSkipPixels;
	private static int unpackAlignment = 4;

	public static void glPixelStorei(int pname, int param) {
		switch (pname) {
			case 0xCF2: unpackRowLength = param; break;  // GL_UNPACK_ROW_LENGTH
			case 0xCF3: unpackSkipRows = param; break;   // GL_UNPACK_SKIP_ROWS
			case 0xCF4: unpackSkipPixels = param; break; // GL_UNPACK_SKIP_PIXELS
			case 0xCF5: unpackAlignment = param > 0 ? param : 4; break; // GL_UNPACK_ALIGNMENT
			default: break;
		}
		GLContext.gl().pixelStorei(pname, param);
	}

	/** Bytes WebGL2 reads from the source pointer for a width x height upload under the current UNPACK state. */
	private static int unpackViewBytes(int format, int type, int width, int height) {
		int bpp = GLShim.pixelBytes(format, type, 1, 1);
		int rowLength = unpackRowLength > 0 ? unpackRowLength : width;
		int align = unpackAlignment > 0 ? unpackAlignment : 4;
		int rowStride = ((rowLength * bpp + align - 1) / align) * align;
		// (skipRows+height-1) full rows from the base pointer, then the last row's (skipPixels+width) pixels.
		return rowStride * (unpackSkipRows + height - 1) + (unpackSkipPixels + width) * bpp;
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
		// WebGL2 has no proxy textures. MC uses GL_PROXY_TEXTURE_2D (0x8064) only to probe the
		// max texture size; our glGetTexLevelParameteri returns 0 so MC's probe already falls back
		// to GL_MAX_TEXTURE_SIZE cleanly. Skip the call so it doesn't spam INVALID_ENUM.
		if (target == 0x8064) {
			return;
		}
		GLContext.gl().texImage2D(target, level, mapInternalFormat(internalformat), width, height, border, format, type,
				pixels == null ? (ArrayBufferView) null : GLShim.view(pixels));
	}

	// WebGL2 rejects GL_DEPTH_COMPONENT32 (0x81A7); a FLOAT depth texture (MC's MainTarget depth
	// attachment) must use GL_DEPTH_COMPONENT32F (0x8CAC). Map it so the framebuffer texture links.
	private static int mapInternalFormat(int internalformat) {
		if (internalformat == 0x81A7) {
			return 0x8CAC;
		}
		return internalformat;
	}

	public static void glTexParameteri(int target, int pname, int param) {
		GLContext.gl().texParameteri(target, pname, param);
	}

	public static void glTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height,
			int format, int type, long pixels) {
		GLContext.gl().texSubImage2D(target, level, xoffset, yoffset, width, height, format, type,
				GLShim.view(pixels, unpackViewBytes(format, type, width, height)));
	}

	public static void glTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height,
			int format, int type, ByteBuffer pixels) {
		GLContext.gl().texSubImage2D(target, level, xoffset, yoffset, width, height, format, type, GLShim.view(pixels));
	}

	public static void glViewport(int x, int y, int width, int height) {
		GLContext.gl().viewport(x, y, width, height);
	}
}
