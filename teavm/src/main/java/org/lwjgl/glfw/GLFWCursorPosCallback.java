package org.lwjgl.glfw;

// ponytail: thin holder of the GLFWCursorPosCallbackI delegate; no native trampoline.
public class GLFWCursorPosCallback implements GLFWCursorPosCallbackI {
	protected GLFWCursorPosCallbackI delegate;

	protected GLFWCursorPosCallback() {
	}

	protected GLFWCursorPosCallback(GLFWCursorPosCallbackI delegate) {
		this.delegate = delegate;
	}

	public static GLFWCursorPosCallback create(GLFWCursorPosCallbackI delegate) {
		return new GLFWCursorPosCallback(delegate);
	}

	@Override
	public void invoke(long window, double xpos, double ypos) {
		if (delegate != null) {
			delegate.invoke(window, xpos, ypos);
		}
	}

	public void free() {
	}

	public void close() {
	}
}
