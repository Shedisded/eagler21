package com.mojang.jtracy;

import java.nio.ByteBuffer;
import java.util.function.Supplier;

/**
 * ponytail: no-op stub replacing Mojang's jtracy profiler bindings.
 * The real library loads a native Tracy agent, which neither the browser
 * target nor this port needs. Restore the maven dependency if native
 * profiling is ever wanted on desktop.
 */
public class TracyClient {
	private static final Zone ZONE = new Zone();

	public static boolean isAvailable() {
		return false;
	}

	public static synchronized void load() {
	}

	public static void markFrame() {
	}

	public static void frameImage(ByteBuffer buffer, int width, int height, int offset, boolean flip) {
	}

	public static Zone beginZone(String name, boolean verbose) {
		return ZONE;
	}

	public static Zone beginZone(String name, String function, String file, int line) {
		return ZONE;
	}

	public static void setThreadName(String name, int color) {
	}

	public static Plot createPlot(String name) {
		return new Plot();
	}

	public static DiscontinuousFrame createDiscontinuousFrame(String name) {
		return new DiscontinuousFrame();
	}

	public static ContinuousFrame createContinuousFrame(String name) {
		return new ContinuousFrame();
	}

	public static MemoryPool createMemoryPool(String name) {
		return new MemoryPool();
	}

	public static void reportAppInfo(String info) {
	}

	public static void message(String message) {
	}

	public static void message(String message, int color) {
	}

	public static void message(Supplier<String> message) {
	}

	public static void message(Supplier<String> message, int color) {
	}
}
