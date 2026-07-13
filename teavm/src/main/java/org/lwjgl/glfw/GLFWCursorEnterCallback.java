package org.lwjgl.glfw;

// ponytail: thin holder of the GLFWCursorEnterCallbackI delegate; no native trampoline.
public class GLFWCursorEnterCallback implements GLFWCursorEnterCallbackI {
	protected GLFWCursorEnterCallbackI delegate;

	protected GLFWCursorEnterCallback() {
	}

	protected GLFWCursorEnterCallback(GLFWCursorEnterCallbackI delegate) {
		this.delegate = delegate;
	}

	public static GLFWCursorEnterCallback create(GLFWCursorEnterCallbackI delegate) {
		return new GLFWCursorEnterCallback(delegate);
	}

	@Override
	public void invoke(long window, boolean entered) {
		if (delegate != null) {
			delegate.invoke(window, entered);
		}
	}

	public void free() {
	}

	public void close() {
	}
}
