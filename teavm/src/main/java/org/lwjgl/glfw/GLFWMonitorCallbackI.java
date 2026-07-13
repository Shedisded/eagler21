package org.lwjgl.glfw;

// ponytail: single-abstract-method shim; no system.CallbackI in the browser build.
public interface GLFWMonitorCallbackI {
	void invoke(long monitor, int event);
}
