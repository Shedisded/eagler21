package org.lwjgl.openal;

/** TeaVM shim. */
public final class ALC {
	private ALC() {
	}

	public static ALCCapabilities createCapabilities(long device) {
		return new ALCCapabilities();
	}
}
