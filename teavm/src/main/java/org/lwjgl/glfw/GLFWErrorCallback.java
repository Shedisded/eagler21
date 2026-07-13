package org.lwjgl.glfw;

/**
 * TeaVM shim for the concrete GLFWErrorCallback. Real LWJGL extends
 * system.Callback (native trampoline); here it is a thin holder of the
 * {@link GLFWErrorCallbackI} delegate that MC registers.
 */
public class GLFWErrorCallback implements GLFWErrorCallbackI {
	protected GLFWErrorCallbackI delegate;

	protected GLFWErrorCallback() {
	}

	protected GLFWErrorCallback(GLFWErrorCallbackI delegate) {
		this.delegate = delegate;
	}

	public static GLFWErrorCallback create(GLFWErrorCallbackI delegate) {
		return new GLFWErrorCallback(delegate);
	}

	@Override
	public void invoke(int error, long description) {
		if (delegate != null) {
			delegate.invoke(error, description);
		}
	}

	// ponytail: no native handle to release in the browser.
	public void free() {
	}

	public void close() {
	}
}
