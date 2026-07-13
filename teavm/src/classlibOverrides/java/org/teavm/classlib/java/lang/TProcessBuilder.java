package org.teavm.classlib.java.lang;

/**
 * Link stub for java.lang.ProcessBuilder. No subprocesses in the browser; this
 * is a Stage D concern. Type-only so MC bytecode links.
 */
public class TProcessBuilder {

	public TProcessBuilder(TString... command) {
		// ponytail: no subprocesses in the browser
	}

	public TProcess start() {
		throw new UnsupportedOperationException("no subprocesses in the browser (Stage D)");
	}
}
