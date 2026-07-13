package org.lwjgl.openal;

import java.nio.ByteBuffer;

/** TeaVM shim: silent OpenAL. Sound plays into the void; the game logic is unaffected. */
public final class AL10 {
	private AL10() {
	}

	private static int nextId = 1;

	public static int alGetError() {
		return 0;
	}

	public static void alEnable(int target) {
	}

	public static void alGenBuffers(int[] buffers) {
		for (int i = 0; i < buffers.length; i++) {
			buffers[i] = nextId++;
		}
	}

	public static void alGenSources(int[] sources) {
		for (int i = 0; i < sources.length; i++) {
			sources[i] = nextId++;
		}
	}

	public static void alDeleteBuffers(int[] buffers) {
	}

	public static void alDeleteSources(int[] sources) {
	}

	public static void alBufferData(int buffer, int format, ByteBuffer data, int frequency) {
	}

	public static int alGetSourcei(int source, int param) {
		return 0;
	}

	public static void alListener3f(int param, float v1, float v2, float v3) {
	}

	public static void alListenerfv(int param, float[] values) {
	}

	public static void alSourcef(int source, int param, float value) {
	}

	public static void alSourcefv(int source, int param, float[] values) {
	}

	public static void alSourcei(int source, int param, int value) {
	}

	public static void alSourcePause(int source) {
	}

	public static void alSourcePlay(int source) {
	}

	public static void alSourceStop(int source) {
	}

	public static void alSourceQueueBuffers(int source, int[] buffers) {
	}

	public static void alSourceUnqueueBuffers(int source, int[] buffers) {
	}
}
