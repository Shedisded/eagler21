package org.lwjgl.glfw;

// ponytail: single-abstract-method shim; no system.CallbackI in the browser build.
public interface GLFWCursorPosCallbackI {
	void invoke(long window, double xpos, double ypos);
}
