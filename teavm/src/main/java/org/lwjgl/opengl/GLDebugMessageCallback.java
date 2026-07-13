package org.lwjgl.opengl;

import org.lwjgl.system.Pointer;

/**
 * TeaVM shim. The debug path is dead code in the browser (GL_KHR_debug is
 * always false), but the linker still needs this type and its exact
 * descriptors. Implements {@link Pointer} so it satisfies
 * {@code GLX.make(T, Consumer<T>)} with {@code DebugMemoryUntracker::untrack}.
 */
public final class GLDebugMessageCallback implements GLDebugMessageCallbackI, Pointer {
	private final GLDebugMessageCallbackI delegate;

	private GLDebugMessageCallback(GLDebugMessageCallbackI delegate) {
		this.delegate = delegate;
	}

	public static GLDebugMessageCallback create(GLDebugMessageCallbackI i) {
		return new GLDebugMessageCallback(i);
	}

	public static String getMessage(int length, long message) {
		return "";
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
