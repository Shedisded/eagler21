package javax.crypto;

// Link-only stub: no MC secret-key generation in the browser Eagler build.
public class KeyGenerator {

	private KeyGenerator() {
	}

	public static KeyGenerator getInstance(String algorithm) {
		throw new UnsupportedOperationException("javax.crypto.KeyGenerator (browser build)");
	}

	public void init(int keysize) {
	}

	public SecretKey generateKey() {
		throw new UnsupportedOperationException("javax.crypto.KeyGenerator.generateKey (browser build)");
	}
}
