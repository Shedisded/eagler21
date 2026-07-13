package org.teavm.classlib.java.util;

import java.util.Random;

/**
 * Replacement for TeaVM's string-based UUID stub. Minecraft relies on the
 * two-long representation (network codecs, entity ids), so this mirrors the
 * real java.util.UUID semantics. Loaded ahead of teavm-classlib on the
 * compiler classpath, which makes it the implementation of java.util.UUID.
 */
public class TUUID implements Comparable<TUUID> {
	private static final Random RANDOM = new Random();

	private final long mostSigBits;
	private final long leastSigBits;

	public TUUID(long mostSigBits, long leastSigBits) {
		this.mostSigBits = mostSigBits;
		this.leastSigBits = leastSigBits;
	}

	public long getMostSignificantBits() {
		return mostSigBits;
	}

	public long getLeastSignificantBits() {
		return leastSigBits;
	}

	public int version() {
		return (int) ((mostSigBits >> 12) & 0xF);
	}

	public int variant() {
		return (int) ((leastSigBits >>> (64 - (leastSigBits >>> 62))) & (leastSigBits >> 63));
	}

	public static TUUID randomUUID() {
		long most = RANDOM.nextLong();
		long least = RANDOM.nextLong();
		most = (most & ~0xF000L) | 0x4000L;
		least = (least & ~(0xC000000000000000L)) | 0x8000000000000000L;
		return new TUUID(most, least);
	}

	public static TUUID nameUUIDFromBytes(byte[] name) {
		byte[] md5;
		try {
			md5 = org.teavm.classlib.java.security.TMessageDigest.getInstance("MD5").digest(name);
		} catch (org.teavm.classlib.java.security.TNoSuchAlgorithmException e) {
			throw new InternalError("MD5 not supported", e);
		}
		md5[6] = (byte) ((md5[6] & 0x0F) | 0x30);
		md5[8] = (byte) ((md5[8] & 0x3F) | 0x80);
		long most = 0L;
		long least = 0L;
		for (int i = 0; i < 8; ++i) {
			most = (most << 8) | (md5[i] & 0xFF);
		}
		for (int i = 8; i < 16; ++i) {
			least = (least << 8) | (md5[i] & 0xFF);
		}
		return new TUUID(most, least);
	}

	public static TUUID fromString(String name) {
		String[] parts = name.split("-");
		if (parts.length != 5) {
			throw new IllegalArgumentException("Invalid UUID string: " + name);
		}
		long most = Long.parseLong(parts[0], 16) << 32;
		most |= Long.parseLong(parts[1], 16) << 16;
		most |= Long.parseLong(parts[2], 16);
		long least = Long.parseLong(parts[3], 16) << 48;
		least |= Long.parseLong(parts[4], 16);
		return new TUUID(most, least);
	}

	@Override
	public String toString() {
		return digits(mostSigBits >> 32, 8) + "-"
				+ digits(mostSigBits >> 16, 4) + "-"
				+ digits(mostSigBits, 4) + "-"
				+ digits(leastSigBits >> 48, 4) + "-"
				+ digits(leastSigBits, 12);
	}

	private static String digits(long val, int digits) {
		long hi = 1L << (digits * 4);
		return Long.toHexString(hi | (val & (hi - 1))).substring(1);
	}

	@Override
	public int hashCode() {
		long hilo = mostSigBits ^ leastSigBits;
		return ((int) (hilo >> 32)) ^ (int) hilo;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TUUID other)) {
			return false;
		}
		return mostSigBits == other.mostSigBits && leastSigBits == other.leastSigBits;
	}

	@Override
	public int compareTo(TUUID val) {
		int c = Long.compare(this.mostSigBits, val.mostSigBits);
		return c != 0 ? c : Long.compare(this.leastSigBits, val.leastSigBits);
	}

}
