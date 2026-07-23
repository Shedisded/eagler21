package org.teavm.classlib.java.security;

/**
 * Link stub for java.security.KeyPair. The browser Eagler build never generates a real key pair (offline;
 * MinecraftServer.keyPair stays null), so the getters return null and are never dereferenced on the
 * multiplayer/singleplayer paths.
 */
public class TKeyPair {

	private final TPublicKey publicKey;
	private final TPrivateKey privateKey;

	public TKeyPair(TPublicKey publicKey, TPrivateKey privateKey) {
		this.publicKey = publicKey;
		this.privateKey = privateKey;
	}

	public TPublicKey getPublic() {
		return publicKey;
	}

	public TPrivateKey getPrivate() {
		return privateKey;
	}
}
