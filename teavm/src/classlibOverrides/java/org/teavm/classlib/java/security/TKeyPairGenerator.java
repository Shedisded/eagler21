package org.teavm.classlib.java.security;

/**
 * Link stub for java.security.KeyPairGenerator. Real key generation is a Stage D
 * concern; kept as a plain class (the JDK's KeyPairGeneratorSpi base is not
 * needed to clear the link ref). Type-only so MC bytecode links.
 */
public class TKeyPairGenerator {
	public static TKeyPairGenerator getInstance(String algorithm) {
		// Return a real (no-op) instance, not null: MinecraftServer.initializeKeyPair does
		// getInstance("RSA").initialize(1024).generateKeyPair(); a null here NPE'd on .initialize
		// and crashed the integrated-server tick loop. With this, generateKeyPair() returns null and
		// the server's keyPair stays null -- fine for singleplayer (the local/memory connection skips
		// the encryption handshake; getKeyPair() is only used by ServerLoginPacketListenerImpl for
		// real network logins). No RSA in the browser.
		return new TKeyPairGenerator();
	}

	public void initialize(int keysize) {
		// ponytail: no key generation in the browser (Stage D)
	}

	public TKeyPair generateKeyPair() {
		return null;
	}
}
