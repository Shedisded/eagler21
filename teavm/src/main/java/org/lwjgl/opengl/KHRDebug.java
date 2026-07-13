package org.lwjgl.opengl;

/** TeaVM shim: inert. GL_KHR_debug is false, so MC never calls these. */
public final class KHRDebug {
	public static void glDebugMessageCallback(GLDebugMessageCallbackI callback, long userParam) {
	}

	public static void glDebugMessageControl(int source, int type, int severity, int[] ids, boolean enabled) {
	}

	public static void glObjectLabel(int identifier, int name, CharSequence label) {
	}

	public static void glPopDebugGroup() {
	}

	public static void glPushDebugGroup(int source, int id, CharSequence message) {
	}
}
