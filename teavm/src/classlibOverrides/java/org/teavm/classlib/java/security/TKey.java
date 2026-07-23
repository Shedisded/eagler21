package org.teavm.classlib.java.security;

/**
 * Link stub for java.security.Key. TeaVM ships no TKey, so java.security.PrivateKey /
 * PublicKey (which implement java.security.Key) had no mapped supertype → "missing in
 * the classpath". This provides it. Real crypto is a Stage D concern.
 */
public interface TKey extends java.io.Serializable {
	String getAlgorithm();

	String getFormat();

	byte[] getEncoded();
}
