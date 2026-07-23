package org.lwjgl.opengl;

import net.lax1dude.eaglercraft.v1_21.platform.webgl.GLContext;
import org.teavm.jso.JSObject;
import org.teavm.jso.core.JSBoolean;
import org.teavm.jso.core.JSNumber;
import org.teavm.jso.webgl.WebGL2RenderingContext;
import org.teavm.jso.webgl.WebGLBuffer;

public class GL32 {
	public static void glBindBufferRange(int target, int index, int buffer, long offset, long size) {
		GLContext.gl().bindBufferRange(target, index, GLContext.CURRENT.buffers.get(buffer), (int) offset, (int) size);
	}

	public static int glClientWaitSync(long sync, int flags, long timeout) {
		// ponytail: WebGL2's MAX_CLIENT_WAIT_TIMEOUT_WEBGL is 0 in Chromium -> any nonzero timeout
		// throws INVALID_OPERATION -> WAIT_FAILED -> MC's GlFence "Failed to complete GPU fence: 1282".
		// The main thread can't block-wait in WebGL anyway; always poll non-blocking (timeout 0).
		// MC's block-wait caller (MappableRingBuffer, Long.MAX_VALUE) ignores the result, and the
		// poll caller (RenderSystem) already passes 0. Ceiling: no true GPU wait -> possible ring-buffer
		// overwrite; upgrade to a bounded poll-loop if render artifacts appear.
		return GLContext.gl().clientWaitSync(GLContext.CURRENT.syncs.get((int) sync), flags, 0);
	}

	public static void glDeleteSync(long sync) {
		GLContext.gl().deleteSync(GLContext.CURRENT.syncs.get((int) sync));
		GLContext.CURRENT.syncs.free((int) sync);
	}

	public static void glDrawElementsBaseVertex(int mode, int count, int type, long indices, int basevertex) {
		if (basevertex == 0) {
			GLContext.gl().drawElements(mode, count, type, (int) indices);
			return;
		}
		int n = applyBaseVertex(basevertex);
		GLContext.gl().drawElements(mode, count, type, (int) indices);
		restoreBaseVertex(n);
	}

	public static void glDrawElementsInstancedBaseVertex(int mode, int count, int type, long indices, int instancecount,
			int basevertex) {
		if (basevertex == 0) {
			GLContext.gl().drawElementsInstanced(mode, count, type, (int) indices, instancecount);
			return;
		}
		int n = applyBaseVertex(basevertex);
		GLContext.gl().drawElementsInstanced(mode, count, type, (int) indices, instancecount);
		restoreBaseVertex(n);
	}

	public static long glFenceSync(int condition, int flags) {
		return GLContext.CURRENT.syncs.register(GLContext.gl().fenceSync(condition, flags));
	}

	// --- baseVertex emulation ---------------------------------------------------------------
	// WebGL2 has no glDrawElementsBaseVertex. MC's GUI renderer packs many meshes into one shared
	// vertex buffer and draws each with a nonzero baseVertex (blaze3d GlCommandEncoder.drawFromBuffers);
	// ignoring it makes every mesh after the first read the wrong vertices -> giant/garbled logo etc.
	// Emulate it: baseVertex is just a byte offset of baseVertex*stride added to every enabled vertex
	// attribute's pointer. We read the currently-bound VAO's attribute layout live via getVertexAttrib
	// (no per-VAO tracking needed), re-point each attribute at offset+baseVertex*stride, draw, then
	// restore. Reusable scratch arrays; GUI formats have ~3-4 attributes and few draws per frame.
	// ceiling: getVertexAttrib round-trips per draw are slow; cache per-format if this ever hot-paths.
	private static final int GL_MAX_VERTEX_ATTRIBS = 0x8869;
	private static final int GL_ARRAY_BUFFER = 0x8892;
	private static final int VAA_ENABLED = 0x8622;
	private static final int VAA_SIZE = 0x8623;
	private static final int VAA_STRIDE = 0x8624;
	private static final int VAA_TYPE = 0x8625;
	private static final int VAA_NORMALIZED = 0x886A;
	private static final int VAA_INTEGER = 0x88FD;
	private static final int VAA_BUFFER_BINDING = 0x889F;
	private static final int VAA_POINTER = 0x8645;

	private static int maxAttribs = -1;
	private static final int[] bavIdx = new int[32];
	private static final int[] bavSize = new int[32];
	private static final int[] bavType = new int[32];
	private static final int[] bavStride = new int[32];
	private static final int[] bavOffset = new int[32];
	private static final boolean[] bavNorm = new boolean[32];
	private static final boolean[] bavInt = new boolean[32];
	private static final WebGLBuffer[] bavBuf = new WebGLBuffer[32];

	private static int applyBaseVertex(int basevertex) {
		WebGL2RenderingContext gl = GLContext.gl();
		if (maxAttribs < 0) {
			maxAttribs = Math.min(32, gl.getParameteri(GL_MAX_VERTEX_ATTRIBS));
		}
		int n = 0;
		for (int i = 0; i < maxAttribs; i++) {
			if (!jsBool(gl.getVertexAttrib(i, VAA_ENABLED))) {
				continue;
			}
			int stride = jsInt(gl.getVertexAttrib(i, VAA_STRIDE));
			if (stride == 0) {
				// tightly-packed: baseVertex advance would need size*typeBytes; MC always sets an
				// explicit stride, so skip (leaving it un-offset) rather than guess.
				continue;
			}
			bavIdx[n] = i;
			bavStride[n] = stride;
			bavSize[n] = jsInt(gl.getVertexAttrib(i, VAA_SIZE));
			bavType[n] = jsInt(gl.getVertexAttrib(i, VAA_TYPE));
			bavNorm[n] = jsBool(gl.getVertexAttrib(i, VAA_NORMALIZED));
			bavInt[n] = jsBool(gl.getVertexAttrib(i, VAA_INTEGER));
			bavOffset[n] = gl.getVertexAttribOffset(i, VAA_POINTER);
			bavBuf[n] = (WebGLBuffer) gl.getVertexAttrib(i, VAA_BUFFER_BINDING);
			gl.bindBuffer(GL_ARRAY_BUFFER, bavBuf[n]);
			int off = bavOffset[n] + basevertex * stride;
			if (bavInt[n]) {
				gl.vertexAttribIPointer(i, bavSize[n], bavType[n], stride, off);
			} else {
				gl.vertexAttribPointer(i, bavSize[n], bavType[n], bavNorm[n], stride, off);
			}
			n++;
		}
		return n;
	}

	private static void restoreBaseVertex(int n) {
		WebGL2RenderingContext gl = GLContext.gl();
		for (int k = 0; k < n; k++) {
			gl.bindBuffer(GL_ARRAY_BUFFER, bavBuf[k]);
			if (bavInt[k]) {
				gl.vertexAttribIPointer(bavIdx[k], bavSize[k], bavType[k], bavStride[k], bavOffset[k]);
			} else {
				gl.vertexAttribPointer(bavIdx[k], bavSize[k], bavType[k], bavNorm[k], bavStride[k], bavOffset[k]);
			}
			bavBuf[k] = null;
		}
	}

	private static int jsInt(JSObject o) {
		return ((JSNumber) o).intValue();
	}

	private static boolean jsBool(JSObject o) {
		return ((JSBoolean) o).booleanValue();
	}
}
