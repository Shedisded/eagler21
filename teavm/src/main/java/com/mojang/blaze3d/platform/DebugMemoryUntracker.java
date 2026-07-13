package com.mojang.blaze3d.platform;

import org.lwjgl.system.Pointer;

/**
 * TeaVM shadow: the real class probes LWJGL's debug allocator via reflection
 * and MethodHandles, neither of which TeaVM supports. Memory tracking is a
 * desktop-debug feature; no-op here.
 */
public class DebugMemoryUntracker {
	public static void untrack(long address) {
	}

	public static void untrack(Pointer pointer) {
	}
}
