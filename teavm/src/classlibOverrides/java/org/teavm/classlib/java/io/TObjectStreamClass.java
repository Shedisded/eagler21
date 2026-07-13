package org.teavm.classlib.java.io;

/** Link-time stub: JDK object serialization is unsupported on TeaVM. */
public class TObjectStreamClass {
	private TObjectStreamClass() {
	}

	public static TObjectStreamClass lookup(Class<?> cl) {
		return null;
	}

	public static TObjectStreamClass lookupAny(Class<?> cl) {
		return null;
	}

	public String getName() {
		return "";
	}

	public long getSerialVersionUID() {
		return 0L;
	}
}
