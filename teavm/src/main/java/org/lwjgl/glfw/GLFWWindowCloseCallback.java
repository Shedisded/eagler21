package org.lwjgl.glfw;

// ponytail: thin holder of the GLFWWindowCloseCallbackI delegate; no native trampoline.
public class GLFWWindowCloseCallback implements GLFWWindowCloseCallbackI {
	protected GLFWWindowCloseCallbackI delegate;

	protected GLFWWindowCloseCallback() {
	}

	protected GLFWWindowCloseCallback(GLFWWindowCloseCallbackI delegate) {
		this.delegate = delegate;
	}

	public static GLFWWindowCloseCallback create(GLFWWindowCloseCallbackI delegate) {
		return new GLFWWindowCloseCallback(delegate);
	}

	@Override
	public void invoke(long window) {
		if (delegate != null) {
			delegate.invoke(window);
		}
	}

	public void free() {
	}

	public void close() {
	}
}
