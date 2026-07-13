package org.teavm.classlib.java.util;

/**
 * Link stub for java.util.Scanner. Type-only — MC references it (e.g. in some
 * debug/CLI path dead at the title screen); parsing methods surface later if needed.
 */
public class TScanner {

	public TScanner(org.teavm.classlib.java.lang.TReadable source) {
		// ponytail: parsing is Stage D
	}

	public TScanner useLocale(TLocale locale) {
		return this;
	}

	public void close() {
	}

	public String next() {
		return "";
	}

	public float nextFloat() {
		return 0f;
	}
}
