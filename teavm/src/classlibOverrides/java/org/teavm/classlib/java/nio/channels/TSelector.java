package org.teavm.classlib.java.nio.channels;

import java.io.Closeable;
import java.io.IOException;

/** Link stub for java.nio.channels.Selector. */
public abstract class TSelector implements Closeable {
	public static TSelector open() throws IOException {
		return null; // ponytail: Stage D
	}

	// ponytail: no real NIO selector in the browser (netty's event loop never actually
	// selects — Stage D replaces the transport with WebSockets). All inert.
	@SuppressWarnings("rawtypes")
	public java.util.Set keys() {
		return java.util.Collections.emptySet();
	}

	@SuppressWarnings("rawtypes")
	public java.util.Set selectedKeys() {
		return java.util.Collections.emptySet();
	}

	public int select(long timeout) throws IOException {
		return 0;
	}

	public int selectNow() throws IOException {
		return 0;
	}

	public TSelector wakeup() {
		return this;
	}
}
