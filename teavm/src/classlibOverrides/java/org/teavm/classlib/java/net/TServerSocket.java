package org.teavm.classlib.java.net;

/**
 * Link stub for java.net.ServerSocket. No listening sockets in the browser;
 * real transport is a Stage D concern. Type-only so MC/netty bytecode links.
 */
public class TServerSocket implements java.io.Closeable {
	public TServerSocket(int port) {
	}

	public TServerSocket(int port, int backlog, TInetAddress bindAddr) {
	}

	public int getLocalPort() {
		return -1;
	}

	public boolean isClosed() {
		return true;
	}

	public TSocket accept() {
		throw new UnsupportedOperationException("Stage D");
	}

	@Override
	public void close() {
	}
}
