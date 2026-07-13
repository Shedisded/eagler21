package org.lwjgl.glfw;

// ponytail: single-abstract-method shim; no system.CallbackI in the browser build.
public interface GLFWCursorEnterCallbackI {
	void invoke(long window, boolean entered);
}
