package org.teavm.classlib.java.security;

/**
 * Link stub for java.security.PublicKey. IS-A Key the way MC/auth expect; real
 * crypto is a Stage D concern. TeaVM ships no TKey, so extend java.security.Key
 * directly (TeaVM maps it at link time).
 */
public interface TPublicKey extends java.security.Key {
	String getAlgorithm();

	byte[] getEncoded();
}
