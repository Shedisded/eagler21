package org.lwjgl.glfw;

// ponytail: single-abstract-method shim; no system.CallbackI in the browser build.
public interface GLFWWindowCloseCallbackI {
	void invoke(long window);
}
