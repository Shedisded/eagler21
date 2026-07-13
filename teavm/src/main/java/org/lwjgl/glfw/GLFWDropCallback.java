package org.lwjgl.glfw;

// ponytail: thin holder of the GLFWDropCallbackI delegate; no native trampoline.
public class GLFWDropCallback implements GLFWDropCallbackI {
	protected GLFWDropCallbackI delegate;

	protected GLFWDropCallback() {
	}

	protected GLFWDropCallback(GLFWDropCallbackI delegate) {
		this.delegate = delegate;
	}

	public static GLFWDropCallback create(GLFWDropCallbackI delegate) {
		return new GLFWDropCallback(delegate);
	}

	// ponytail: drag-and-drop file paths are not wired for the browser; MC decodes
	// the i-th dropped name here — nothing is ever dropped, so return "".
	public static String getName(long names, int index) {
		return "";
	}

	@Override
	public void invoke(long window, int count, long names) {
		if (delegate != null) {
			delegate.invoke(window, count, names);
		}
	}

	public void free() {
	}

	public void close() {
	}
}
