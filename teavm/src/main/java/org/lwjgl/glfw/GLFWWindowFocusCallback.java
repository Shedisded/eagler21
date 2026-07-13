package org.lwjgl.glfw;

// ponytail: thin holder of the GLFWWindowFocusCallbackI delegate; no native trampoline.
public class GLFWWindowFocusCallback implements GLFWWindowFocusCallbackI {
	protected GLFWWindowFocusCallbackI delegate;

	protected GLFWWindowFocusCallback() {
	}

	protected GLFWWindowFocusCallback(GLFWWindowFocusCallbackI delegate) {
		this.delegate = delegate;
	}

	public static GLFWWindowFocusCallback create(GLFWWindowFocusCallbackI delegate) {
		return new GLFWWindowFocusCallback(delegate);
	}

	@Override
	public void invoke(long window, boolean focused) {
		if (delegate != null) {
			delegate.invoke(window, focused);
		}
	}

	public void free() {
	}

	public void close() {
	}
}
