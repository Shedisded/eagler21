package org.lwjgl.glfw;

// ponytail: single-abstract-method shim; no system.CallbackI in the browser build.
public interface GLFWFramebufferSizeCallbackI {
	void invoke(long window, int width, int height);
}
