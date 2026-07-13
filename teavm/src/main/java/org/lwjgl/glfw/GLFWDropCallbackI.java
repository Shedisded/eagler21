package org.lwjgl.glfw;

// ponytail: single-abstract-method shim; no system.CallbackI in the browser build.
public interface GLFWDropCallbackI {
	void invoke(long window, int count, long names);
}
