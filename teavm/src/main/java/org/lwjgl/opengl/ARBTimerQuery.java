package org.lwjgl.opengl;

/** TeaVM shim: inert. No GPU timer queries in WebGL2; report 0 ns. */
public final class ARBTimerQuery {
	public static long glGetQueryObjecti64(int id, int pname) {
		return 0L;
	}
}
