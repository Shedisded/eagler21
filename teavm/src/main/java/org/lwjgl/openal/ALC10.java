package org.lwjgl.openal;

import java.nio.IntBuffer;

/** TeaVM shim: fake OpenAL context/device so Library.init() succeeds silently. */
public final class ALC10 {
	private ALC10() {
	}

	public static long alcOpenDevice(CharSequence deviceName) {
		return 1L;
	}

	public static boolean alcCloseDevice(long device) {
		return true;
	}

	public static long alcCreateContext(long device, IntBuffer attrList) {
		return 1L;
	}

	public static void alcDestroyContext(long context) {
	}

	public static boolean alcMakeContextCurrent(long context) {
		return true;
	}

	public static int alcGetError(long device) {
		return 0;
	}

	public static int alcGetInteger(long device, int param) {
		return 0;
	}

	public static void alcGetIntegerv(long device, int param, IntBuffer values) {
	}

	public static String alcGetString(long device, int param) {
		return "";
	}

	public static boolean alcIsExtensionPresent(long device, CharSequence extName) {
		return false;
	}
}
