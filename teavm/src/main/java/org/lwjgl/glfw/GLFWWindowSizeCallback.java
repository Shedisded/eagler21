package org.lwjgl.glfw;

// ponytail: thin holder of the GLFWWindowSizeCallbackI delegate; no native trampoline.
public class GLFWWindowSizeCallback implements GLFWWindowSizeCallbackI {
	protected GLFWWindowSizeCallbackI delegate;

	protected GLFWWindowSizeCallback() {
	}

	protected GLFWWindowSizeCallback(GLFWWindowSizeCallbackI delegate) {
		this.delegate = delegate;
	}

	public static GLFWWindowSizeCallback create(GLFWWindowSizeCallbackI delegate) {
		return new GLFWWindowSizeCallback(delegate);
	}

	@Override
	public void invoke(long window, int width, int height) {
		if (delegate != null) {
			delegate.invoke(window, width, height);
		}
	}

	public void free() {
	}

	public void close() {
	}
}
