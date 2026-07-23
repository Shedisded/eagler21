package javax.crypto.spec;

import java.security.spec.AlgorithmParameterSpec;

// Link stub for MC's Crypt cipher IV (encryption unused in the browser Eagler build).
public class IvParameterSpec implements AlgorithmParameterSpec {

	private final byte[] iv;

	public IvParameterSpec(byte[] iv) {
		this.iv = iv != null ? iv.clone() : null;
	}

	public byte[] getIV() {
		return iv != null ? iv.clone() : null;
	}
}
