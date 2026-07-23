package javax.crypto;

import java.security.Key;

// Link-only stub: MC packet encryption is never used in the browser Eagler build (no MC online-mode
// encryption over the wss transport). All methods throw if actually reached.
public class Cipher {

	public static final int ENCRYPT_MODE = 1;
	public static final int DECRYPT_MODE = 2;

	private Cipher() {
	}

	public static Cipher getInstance(String transformation) {
		throw new UnsupportedOperationException("javax.crypto.Cipher (browser build)");
	}

	public void init(int opmode, Key key) {
		throw new UnsupportedOperationException("javax.crypto.Cipher.init (browser build)");
	}

	public void init(int opmode, Key key, java.security.spec.AlgorithmParameterSpec params) {
		throw new UnsupportedOperationException("javax.crypto.Cipher.init (browser build)");
	}

	public int getBlockSize() {
		return 16;
	}

	public int getOutputSize(int inputLen) {
		return inputLen;
	}

	public byte[] update(byte[] input) {
		throw new UnsupportedOperationException("javax.crypto.Cipher.update (browser build)");
	}

	public int update(byte[] input, int inputOffset, int inputLen, byte[] output) {
		throw new UnsupportedOperationException("javax.crypto.Cipher.update (browser build)");
	}

	public int update(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset) {
		throw new UnsupportedOperationException("javax.crypto.Cipher.update (browser build)");
	}

	public byte[] doFinal() {
		throw new UnsupportedOperationException("javax.crypto.Cipher.doFinal (browser build)");
	}

	public byte[] doFinal(byte[] input) {
		throw new UnsupportedOperationException("javax.crypto.Cipher.doFinal (browser build)");
	}
}
