package org.lwjgl.openal;

/**
 * TeaVM shim. The two EXT flags Library.init() checks must be true or it throws;
 * they gate distance-model math MC runs the same either way.
 */
public final class ALCapabilities {
	public final boolean AL_EXT_LINEAR_DISTANCE = true;
	public final boolean AL_EXT_source_distance_model = true;
}
