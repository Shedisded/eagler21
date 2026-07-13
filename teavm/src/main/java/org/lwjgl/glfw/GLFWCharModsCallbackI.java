package org.lwjgl.glfw;

// ponytail: single-abstract-method shim; no system.CallbackI in the browser build.
public interface GLFWCharModsCallbackI {
	void invoke(long window, int codepoint, int mods);
}
