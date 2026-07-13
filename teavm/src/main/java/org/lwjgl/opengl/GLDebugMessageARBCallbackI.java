package org.lwjgl.opengl;

/** TeaVM shim: functional interface MC implements via lambda for ARB debug output. */
@FunctionalInterface
public interface GLDebugMessageARBCallbackI {
	void invoke(int source, int type, int id, int severity, int length, long message, long userParam);
}
