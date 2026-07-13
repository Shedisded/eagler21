package org.teavm.classlib.java.security;

/**
 * Link stub for java.security.KeyPairGenerator. Real key generation is a Stage D
 * concern; kept as a plain class (the JDK's KeyPairGeneratorSpi base is not
 * needed to clear the link ref). Type-only so MC bytecode links.
 */
public class TKeyPairGenerator {
	public static TKeyPairGenerator getInstance(String algorithm) {
		return null;
	}

	public void initialize(int keysize) {
		// ponytail: no key generation in the browser (Stage D)
	}

	public TKeyPair generateKeyPair() {
		return null;
	}
}
