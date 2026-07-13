package org.lwjgl.openal;

/** TeaVM shim. OpenALC11 must be true or Library.init() throws; HRTF unsupported. */
public final class ALCCapabilities {
	public final boolean OpenALC11 = true;
	public final boolean ALC_SOFT_HRTF = false;
}
