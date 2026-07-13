package org.lwjgl.glfw;

// ponytail: thin holder of the GLFWMonitorCallbackI delegate; no native trampoline.
public class GLFWMonitorCallback implements GLFWMonitorCallbackI {
	protected GLFWMonitorCallbackI delegate;

	protected GLFWMonitorCallback() {
	}

	protected GLFWMonitorCallback(GLFWMonitorCallbackI delegate) {
		this.delegate = delegate;
	}

	public static GLFWMonitorCallback create(GLFWMonitorCallbackI delegate) {
		return new GLFWMonitorCallback(delegate);
	}

	@Override
	public void invoke(long monitor, int event) {
		if (delegate != null) {
			delegate.invoke(monitor, event);
		}
	}

	public void free() {
	}

	public void close() {
	}
}
