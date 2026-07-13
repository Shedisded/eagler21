package org.lwjgl;

/** TeaVM shim for org.lwjgl.Version. */
public final class Version {
	private Version() {
	}

	public static String getVersion() {
		return "3.3.3-teavm";
	}
}
