package org.lwjgl.openal;

/** TeaVM shim. */
public final class AL {
	private AL() {
	}

	public static ALCapabilities createCapabilities(ALCCapabilities alcCaps) {
		return new ALCapabilities();
	}
}
