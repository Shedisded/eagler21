package org.lwjgl.glfw;

// ponytail: single-abstract-method shim; no system.CallbackI in the browser build.
public interface GLFWMouseButtonCallbackI {
	void invoke(long window, int button, int action, int mods);
}
