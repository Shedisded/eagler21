package org.teavm.classlib.java.nio.channels;

import java.io.IOException;

/** Link stub for java.nio.channels.FileLock. */
public abstract class TFileLock implements AutoCloseable {
	public boolean isValid() {
		return false; // ponytail: Stage D
	}

	public void release() throws IOException {
		// ponytail: Stage D no-op
	}
}
