package org.teavm.classlib.java.util;

/** Subset of java.util.HexFormat (Java 17) used by Minecraft. */
public final class THexFormat {
	private static final THexFormat LOWER = new THexFormat(false);
	private static final THexFormat UPPER = new THexFormat(true);

	private final boolean upperCase;

	private THexFormat(boolean upperCase) {
		this.upperCase = upperCase;
	}

	public static THexFormat of() {
		return LOWER;
	}

	public THexFormat withUpperCase() {
		return UPPER;
	}

	public THexFormat withLowerCase() {
		return LOWER;
	}

	public THexFormat withPrefix(String prefix) {
		// ponytail: prefix not stored — MC only needs the digit-formatting paths (Stage D
		// if a prefixed format is ever actually emitted).
		return this;
	}

	public boolean isUpperCase() {
		return upperCase;
	}

	public String toHexDigits(int value) {
		return pad(Integer.toHexString(value), 8);
	}

	public String toHexDigits(long value) {
		return pad(Long.toHexString(value), 16);
	}

	public String toHexDigits(long value, int digits) {
		String hex = pad(Long.toHexString(value), 16);
		return hex.substring(hex.length() - digits);
	}

	public String toHexDigits(byte value) {
		return pad(Integer.toHexString(value & 0xFF), 2);
	}

	public String formatHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		for (byte b : bytes) {
			sb.append(toHexDigits(b));
		}
		return sb.toString();
	}

	public byte[] parseHex(CharSequence string) {
		int len = string.length();
		if ((len & 1) != 0) {
			throw new IllegalArgumentException("string length not even: " + len);
		}
		byte[] out = new byte[len / 2];
		for (int i = 0; i < out.length; ++i) {
			int hi = fromHexDigit(string.charAt(i * 2));
			int lo = fromHexDigit(string.charAt(i * 2 + 1));
			out[i] = (byte) ((hi << 4) | lo);
		}
		return out;
	}

	public static int fromHexDigit(int ch) {
		int v = Character.digit(ch, 16);
		if (v < 0) {
			throw new NumberFormatException("not a hexadecimal digit: " + (char) ch);
		}
		return v;
	}

	public static int fromHexDigitsToLong(CharSequence string) {
		return (int) fromHexDigitsToLong(string, 0, string.length());
	}

	public static long fromHexDigitsToLong(CharSequence string, int fromIndex, int toIndex) {
		long value = 0;
		for (int i = fromIndex; i < toIndex; ++i) {
			value = (value << 4) | fromHexDigit(string.charAt(i));
		}
		return value;
	}

	public static int fromHexDigits(CharSequence string) {
		return (int) fromHexDigitsToLong(string, 0, string.length());
	}

	public static int fromHexDigits(CharSequence string, int fromIndex, int toIndex) {
		return (int) fromHexDigitsToLong(string, fromIndex, toIndex);
	}

	private String pad(String hex, int width) {
		StringBuilder sb = new StringBuilder(width);
		for (int i = hex.length(); i < width; ++i) {
			sb.append('0');
		}
		sb.append(hex);
		return upperCase ? sb.toString().toUpperCase() : sb.toString();
	}
}
