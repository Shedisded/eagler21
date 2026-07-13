package org.teavm.classlib.java.util;

/**
 * Link stub for java.util.StringJoiner. Type-only shim so MC bytecode linking
 * it resolves; join logic surfaces in a later wave.
 */
public class TStringJoiner {

	public TStringJoiner(org.teavm.classlib.java.lang.TCharSequence delimiter,
			org.teavm.classlib.java.lang.TCharSequence prefix,
			org.teavm.classlib.java.lang.TCharSequence suffix) {
		// ponytail: join logic (toString) surfaces in a later wave if MC needs it
	}

	public TStringJoiner add(org.teavm.classlib.java.lang.TCharSequence newElement) {
		return this;
	}
}
