package net.lax1dude.eaglercraft.v1_21.platform.webgl;

import org.teavm.jso.webgl.WebGL2RenderingContext;
import org.teavm.jso.webgl.WebGLBuffer;
import org.teavm.jso.webgl.WebGLFramebuffer;
import org.teavm.jso.webgl.WebGLProgram;
import org.teavm.jso.webgl.WebGLQuery;
import org.teavm.jso.webgl.WebGLRenderbuffer;
import org.teavm.jso.webgl.WebGLSampler;
import org.teavm.jso.webgl.WebGLShader;
import org.teavm.jso.webgl.WebGLSync;
import org.teavm.jso.webgl.WebGLTexture;
import org.teavm.jso.webgl.WebGLUniformLocation;
import org.teavm.jso.webgl.WebGLVertexArrayObject;

/**
 * Holds the live WebGL2 context and the int-name registries the org.lwjgl.opengl
 * shims translate through. One context per page; {@link #CURRENT} is set by the
 * GLFW shim after canvas creation.
 */
public final class GLContext {
	public static GLContext CURRENT;

	public final WebGL2RenderingContext gl;

	public final GLObjectMap<WebGLBuffer> buffers = new GLObjectMap<>();
	public final GLObjectMap<WebGLTexture> textures = new GLObjectMap<>();
	public final GLObjectMap<WebGLFramebuffer> framebuffers = new GLObjectMap<>();
	public final GLObjectMap<WebGLRenderbuffer> renderbuffers = new GLObjectMap<>();
	public final GLObjectMap<WebGLProgram> programs = new GLObjectMap<>();
	public final GLObjectMap<WebGLShader> shaders = new GLObjectMap<>();
	public final GLObjectMap<WebGLVertexArrayObject> vertexArrays = new GLObjectMap<>();
	public final GLObjectMap<WebGLSampler> samplers = new GLObjectMap<>();
	public final GLObjectMap<WebGLQuery> queries = new GLObjectMap<>();
	public final GLObjectMap<WebGLSync> syncs = new GLObjectMap<>();
	public final GLObjectMap<WebGLUniformLocation> uniforms = new GLObjectMap<>();

	public GLContext(WebGL2RenderingContext gl) {
		this.gl = gl;
	}

	public static WebGL2RenderingContext gl() {
		return CURRENT.gl;
	}
}
