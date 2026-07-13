package org.lwjgl.glfw;

// ponytail: thin holder of the GLFWWindowPosCallbackI delegate; no native trampoline.
public class GLFWWindowPosCallback implements GLFWWindowPosCallbackI {
	protected GLFWWindowPosCallbackI delegate;

	protected GLFWWindowPosCallback() {
	}

	protected GLFWWindowPosCallback(GLFWWindowPosCallbackI delegate) {
		this.delegate = delegate;
	}

	public static GLFWWindowPosCallback create(GLFWWindowPosCallbackI delegate) {
		return new GLFWWindowPosCallback(delegate);
	}

	@Override
	public void invoke(long window, int xpos, int ypos) {
		if (delegate != null) {
			delegate.invoke(window, xpos, ypos);
		}
	}

	public void free() {
	}

	public void close() {
	}
}
