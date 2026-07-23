package org.teavm.classlib.java.security;

/**
 * Link stub for java.security.Signature. MC chat-message signing (1.19+) is unused in the browser Eagler
 * build (offline mode; EaglerXServer handles identity). Methods throw if reached.
 */
public abstract class TSignature {

	public static TSignature getInstance(String algorithm) {
		throw new UnsupportedOperationException("java.security.Signature (browser build)");
	}

	public final void initVerify(TPublicKey publicKey) {
		throw new UnsupportedOperationException("Signature.initVerify (browser build)");
	}

	public final void initSign(TPrivateKey privateKey) {
		throw new UnsupportedOperationException("Signature.initSign (browser build)");
	}

	public final void update(byte[] data) {
		throw new UnsupportedOperationException("Signature.update (browser build)");
	}

	public final void update(byte[] data, int off, int len) {
		throw new UnsupportedOperationException("Signature.update (browser build)");
	}

	public final boolean verify(byte[] signature) {
		throw new UnsupportedOperationException("Signature.verify (browser build)");
	}

	public final byte[] sign() {
		throw new UnsupportedOperationException("Signature.sign (browser build)");
	}
}
