package org.lwjgl.glfw;

// ponytail: thin holder of the GLFWCharModsCallbackI delegate; no native trampoline.
public class GLFWCharModsCallback implements GLFWCharModsCallbackI {
	protected GLFWCharModsCallbackI delegate;

	protected GLFWCharModsCallback() {
	}

	protected GLFWCharModsCallback(GLFWCharModsCallbackI delegate) {
		this.delegate = delegate;
	}

	public static GLFWCharModsCallback create(GLFWCharModsCallbackI delegate) {
		return new GLFWCharModsCallback(delegate);
	}

	@Override
	public void invoke(long window, int codepoint, int mods) {
		if (delegate != null) {
			delegate.invoke(window, codepoint, mods);
		}
	}

	public void free() {
	}

	public void close() {
	}
}
