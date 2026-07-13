package org.teavm.classlib.java.net;

/**
 * Link stub for java.net.DatagramSocket. No UDP in the browser; real transport
 * is a Stage D concern. Type-only so MC/netty bytecode links.
 */
public class TDatagramSocket implements java.io.Closeable {
	public void send(TDatagramPacket packet) {
		// ponytail: no UDP in the browser (Stage D)
	}

	@Override
	public void close() {
	}
}
