package org.lwjgl.opengl;

/** TeaVM shim: functional interface MC implements via lambda for GL debug output. */
@FunctionalInterface
public interface GLDebugMessageCallbackI {
	void invoke(int source, int type, int id, int severity, int length, long message, long userParam);
}
