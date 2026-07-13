package org.teavm.classlib.java.security;

import java.io.ByteArrayOutputStream;

/**
 * Minimal MessageDigest (MD5 / SHA-1 / SHA-256) — TeaVM ships no
 * java.security. Buffers input and hashes on digest(); plenty for guava
 * Hashing and offline-UUID use.
 */
public abstract class TMessageDigest {
	private final String algorithm;
	final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

	TMessageDigest(String algorithm) {
		this.algorithm = algorithm;
	}

	public static TMessageDigest getInstance(String algorithm) throws TNoSuchAlgorithmException {
		return switch (algorithm.toUpperCase().replace("-", "")) {
			case "MD5" -> new Md5Digest();
			case "SHA1" -> new Sha1Digest();
			case "SHA256" -> new Sha256Digest();
			default -> throw new TNoSuchAlgorithmException(algorithm);
		};
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public void update(byte input) {
		buffer.write(input);
	}

	public void update(byte[] input) {
		buffer.write(input, 0, input.length);
	}

	public void update(byte[] input, int offset, int len) {
		buffer.write(input, offset, len);
	}

	public byte[] digest() {
		byte[] result = engineDigest(buffer.toByteArray());
		reset();
		return result;
	}

	public byte[] digest(byte[] input) {
		update(input);
		return digest();
	}

	public void reset() {
		buffer.reset();
	}

	public int getDigestLength() {
		return engineDigestLength();
	}

	abstract byte[] engineDigest(byte[] message);

	abstract int engineDigestLength();

	static final class Md5Digest extends TMessageDigest {
		Md5Digest() {
			super("MD5");
		}

		@Override
		byte[] engineDigest(byte[] message) {
			return Digests.md5(message);
		}

		@Override
		int engineDigestLength() {
			return 16;
		}
	}

	static final class Sha1Digest extends TMessageDigest {
		Sha1Digest() {
			super("SHA-1");
		}

		@Override
		byte[] engineDigest(byte[] message) {
			return Digests.sha1(message);
		}

		@Override
		int engineDigestLength() {
			return 20;
		}
	}

	static final class Sha256Digest extends TMessageDigest {
		Sha256Digest() {
			super("SHA-256");
		}

		@Override
		byte[] engineDigest(byte[] message) {
			return Digests.sha256(message);
		}

		@Override
		int engineDigestLength() {
			return 32;
		}
	}
}
