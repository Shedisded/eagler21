package org.lwjgl.glfw;

// ponytail: thin holder of the GLFWScrollCallbackI delegate; no native trampoline.
public class GLFWScrollCallback implements GLFWScrollCallbackI {
	protected GLFWScrollCallbackI delegate;

	protected GLFWScrollCallback() {
	}

	protected GLFWScrollCallback(GLFWScrollCallbackI delegate) {
		this.delegate = delegate;
	}

	public static GLFWScrollCallback create(GLFWScrollCallbackI delegate) {
		return new GLFWScrollCallback(delegate);
	}

	@Override
	public void invoke(long window, double xoffset, double yoffset) {
		if (delegate != null) {
			delegate.invoke(window, xoffset, yoffset);
		}
	}

	public void free() {
	}

	public void close() {
	}
}
