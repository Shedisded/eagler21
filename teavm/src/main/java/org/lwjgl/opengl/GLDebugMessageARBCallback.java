package org.lwjgl.opengl;

import org.lwjgl.system.Pointer;

/** TeaVM shim; see {@link GLDebugMessageCallback}. Dead in browser (GL_ARB_debug_output false). */
public final class GLDebugMessageARBCallback implements GLDebugMessageARBCallbackI, Pointer {
	private final GLDebugMessageARBCallbackI delegate;

	private GLDebugMessageARBCallback(GLDebugMessageARBCallbackI delegate) {
		this.delegate = delegate;
	}

	public static GLDebugMessageARBCallback create(GLDebugMessageARBCallbackI i) {
		return new GLDebugMessageARBCallback(i);
	}

	@Override
	public void invoke(int source, int type, int id, int severity, int length, long message, long userParam) {
		delegate.invoke(source, type, id, severity, length, message, userParam);
	}

	@Override
	public long address() {
		return 0L;
	}
}
