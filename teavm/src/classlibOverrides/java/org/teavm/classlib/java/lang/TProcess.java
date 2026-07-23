package org.teavm.classlib.java.lang;

/**
 * Link stub for java.lang.Process. No subprocesses in the browser; this is a
 * Stage D concern. Abstract to mirror the JDK type; type-only so MC bytecode links.
 */
public abstract class TProcess {

	public void destroy() {
		// ponytail: no subprocesses in the browser
	}

	public org.teavm.classlib.java.io.TInputStream getInputStream() {
		return null;
	}

	public org.teavm.classlib.java.io.TInputStream getErrorStream() {
		return null;
	}

	public org.teavm.classlib.java.io.TOutputStream getOutputStream() {
		return null;
	}

	public int waitFor() {
		return 0;
	}
}
