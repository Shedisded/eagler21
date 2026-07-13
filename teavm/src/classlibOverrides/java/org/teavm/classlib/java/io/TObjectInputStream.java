package org.teavm.classlib.java.io;

/** Link-time stub: JDK object serialization is unsupported on TeaVM. */
public class TObjectInputStream extends TInputStream {
	public TObjectInputStream(TInputStream in) {
	}

	public final Object readObject() {
		throw new UnsupportedOperationException("Java serialization is not supported");
	}

	public void defaultReadObject() {
		throw new UnsupportedOperationException("Java serialization is not supported");
	}

	@Override
	public int read() {
		return -1;
	}
}
