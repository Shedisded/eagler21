package javax.crypto.spec;

import javax.crypto.SecretKey;

// Link stub for MC's Crypt (encryption unused in the browser Eagler build). Stores the bytes so the
// harmless getters work; the cipher ops that would consume it throw (Cipher stub).
public class SecretKeySpec implements SecretKey {

	private final byte[] key;
	private final String algorithm;

	public SecretKeySpec(byte[] key, String algorithm) {
		this.key = key != null ? key.clone() : null;
		this.algorithm = algorithm;
	}

	public SecretKeySpec(byte[] key, int offset, int len, String algorithm) {
		this.key = new byte[len];
		if (key != null) {
			System.arraycopy(key, offset, this.key, 0, len);
		}
		this.algorithm = algorithm;
	}

	@Override
	public String getAlgorithm() {
		return algorithm;
	}

	@Override
	public String getFormat() {
		return "RAW";
	}

	@Override
	public byte[] getEncoded() {
		return key != null ? key.clone() : null;
	}
}
