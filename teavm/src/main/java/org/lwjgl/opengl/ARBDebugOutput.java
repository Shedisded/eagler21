package org.lwjgl.opengl;

/** TeaVM shim: inert. GL_ARB_debug_output is false. */
public final class ARBDebugOutput {
	public static void glDebugMessageCallbackARB(GLDebugMessageARBCallbackI callback, long userParam) {
	}

	public static void glDebugMessageControlARB(int source, int type, int severity, int[] ids, boolean enabled) {
	}
}
