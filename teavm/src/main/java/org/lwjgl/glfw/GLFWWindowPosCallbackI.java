package org.lwjgl.glfw;

// ponytail: single-abstract-method shim; no system.CallbackI in the browser build.
public interface GLFWWindowPosCallbackI {
	void invoke(long window, int xpos, int ypos);
}
