package org.lwjgl.glfw;

// ponytail: thin holder of the GLFWFramebufferSizeCallbackI delegate; no native trampoline.
public class GLFWFramebufferSizeCallback implements GLFWFramebufferSizeCallbackI {
	protected GLFWFramebufferSizeCallbackI delegate;

	protected GLFWFramebufferSizeCallback() {
	}

	protected GLFWFramebufferSizeCallback(GLFWFramebufferSizeCallbackI delegate) {
		this.delegate = delegate;
	}

	public static GLFWFramebufferSizeCallback create(GLFWFramebufferSizeCallbackI delegate) {
		return new GLFWFramebufferSizeCallback(delegate);
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
