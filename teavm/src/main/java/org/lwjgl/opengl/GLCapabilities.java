package org.lwjgl.opengl;

/**
 * TeaVM shim for org.lwjgl.opengl.GLCapabilities. WebGL2 exposes a fixed core
 * profile with none of these extensions, so every advertised capability is
 * false — MC then takes its portable core/compat path (buffer subdata instead
 * of buffer storage, no DSA, no debug output, bitmap fonts, etc.).
 *
 * Only the seven fields MC actually reads (see teavm/lwjgl-refs/gl-refs.txt)
 * exist here; all default to false.
 */
public final class GLCapabilities {
	public final boolean GL_ARB_buffer_storage = false;
	public final boolean GL_ARB_debug_output = false;
	public final boolean GL_ARB_direct_state_access = false;
	public final boolean GL_ARB_vertex_attrib_binding = false;
	public final boolean GL_EXT_debug_label = false;
	public final boolean GL_EXT_texture_filter_anisotropic = false;
	public final boolean GL_KHR_debug = false;
}
