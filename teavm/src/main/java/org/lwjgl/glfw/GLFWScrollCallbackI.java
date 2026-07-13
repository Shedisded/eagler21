package org.lwjgl.glfw;

// ponytail: single-abstract-method shim; no system.CallbackI in the browser build.
public interface GLFWScrollCallbackI {
	void invoke(long window, double xoffset, double yoffset);
}
