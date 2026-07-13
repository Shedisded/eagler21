package org.lwjgl.glfw;

/**
 * TeaVM shim for org.lwjgl.glfw.Callbacks. Real LWJGL frees the native callback
 * trampolines bound to a window; there are none in the browser.
 */
public final class Callbacks {
	private Callbacks() {
	}

	// ponytail: no native callbacks to release.
	public static void glfwFreeCallbacks(long window) {
	}
}
