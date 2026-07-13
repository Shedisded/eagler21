package org.teavm.classlib.java.net;

/**
 * Link stub for java.net.IDN. No punycode in the browser build; hostnames flow
 * through unchanged (MC only feeds already-ASCII hosts here at boot).
 */
public final class TIDN {
	public static final int ALLOW_UNASSIGNED = 0x01;
	public static final int USE_STD3_ASCII_RULES = 0x02;

	private TIDN() {
	}

	public static String toASCII(String input) {
		return input;
	}

	public static String toASCII(String input, int flag) {
		return input;
	}

	public static String toUnicode(String input) {
		return input;
	}

	public static String toUnicode(String input, int flag) {
		return input;
	}
}
