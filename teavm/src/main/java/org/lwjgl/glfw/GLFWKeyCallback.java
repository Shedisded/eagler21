package org.lwjgl.glfw;

// ponytail: thin holder of the GLFWKeyCallbackI delegate; no native trampoline.
public class GLFWKeyCallback implements GLFWKeyCallbackI {
	protected GLFWKeyCallbackI delegate;

	protected GLFWKeyCallback() {
	}

	protected GLFWKeyCallback(GLFWKeyCallbackI delegate) {
		this.delegate = delegate;
	}

	public static GLFWKeyCallback create(GLFWKeyCallbackI delegate) {
		return new GLFWKeyCallback(delegate);
	}

	@Override
	public void invoke(long window, int key, int scancode, int action, int mods) {
		if (delegate != null) {
			delegate.invoke(window, key, scancode, action, mods);
		}
	}

	public void free() {
	}

	public void close() {
	}
}
