package org.lwjgl.glfw;

// ponytail: single-abstract-method shim; no system.CallbackI in the browser build.
public interface GLFWKeyCallbackI {
	void invoke(long window, int key, int scancode, int action, int mods);
}
