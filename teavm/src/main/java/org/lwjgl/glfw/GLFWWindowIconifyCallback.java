package org.lwjgl.glfw;

// ponytail: thin holder of the GLFWWindowIconifyCallbackI delegate; no native trampoline.
public class GLFWWindowIconifyCallback implements GLFWWindowIconifyCallbackI {
	protected GLFWWindowIconifyCallbackI delegate;

	protected GLFWWindowIconifyCallback() {
	}

	protected GLFWWindowIconifyCallback(GLFWWindowIconifyCallbackI delegate) {
		this.delegate = delegate;
	}

	public static GLFWWindowIconifyCallback create(GLFWWindowIconifyCallbackI delegate) {
		return new GLFWWindowIconifyCallback(delegate);
	}

	@Override
	public void invoke(long window, boolean iconified) {
		if (delegate != null) {
			delegate.invoke(window, iconified);
		}
	}

	public void free() {
	}

	public void close() {
	}
}
