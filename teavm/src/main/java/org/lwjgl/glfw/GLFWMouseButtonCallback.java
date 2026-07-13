package org.lwjgl.glfw;

// ponytail: thin holder of the GLFWMouseButtonCallbackI delegate; no native trampoline.
public class GLFWMouseButtonCallback implements GLFWMouseButtonCallbackI {
	protected GLFWMouseButtonCallbackI delegate;

	protected GLFWMouseButtonCallback() {
	}

	protected GLFWMouseButtonCallback(GLFWMouseButtonCallbackI delegate) {
		this.delegate = delegate;
	}

	public static GLFWMouseButtonCallback create(GLFWMouseButtonCallbackI delegate) {
		return new GLFWMouseButtonCallback(delegate);
	}

	@Override
	public void invoke(long window, int button, int action, int mods) {
		if (delegate != null) {
			delegate.invoke(window, button, action, mods);
		}
	}

	public void free() {
	}

	public void close() {
	}
}
