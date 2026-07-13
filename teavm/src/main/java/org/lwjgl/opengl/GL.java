package org.lwjgl.opengl;

/**
 * TeaVM shim for org.lwjgl.opengl.GL. There is exactly one WebGL2 context per
 * page, so capabilities are a process-wide singleton created on first request.
 */
public final class GL {
	private static GLCapabilities capabilities;

	public static GLCapabilities createCapabilities() {
		capabilities = new GLCapabilities();
		return capabilities;
	}

	public static GLCapabilities getCapabilities() {
		if (capabilities == null) {
			capabilities = new GLCapabilities();
		}
		return capabilities;
	}
}
