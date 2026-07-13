package org.lwjgl.glfw;

// ponytail: real LWJGL extends system.CallbackI (libffi glue) — unneeded in the
// browser. A single-abstract-method interface is all the lambda metafactory needs.
public interface GLFWErrorCallbackI {
	void invoke(int error, long description);
}
