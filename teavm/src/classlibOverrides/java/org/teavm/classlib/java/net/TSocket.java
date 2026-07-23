package org.teavm.classlib.java.net;

/**
 * Link stub for java.net.Socket. Real TCP is a Stage D concern (WebSocket
 * transport); the type only needs to exist so MC/netty bytecode links.
 */
public class TSocket implements java.io.Closeable {
	public TSocket(String host, int port) {
	}

	public java.io.InputStream getInputStream() {
		throw new UnsupportedOperationException("Stage D");
	}

	public boolean isClosed() {
		return true;
	}

	public java.io.OutputStream getOutputStream() {
		throw new UnsupportedOperationException("Stage D");
	}

	public int getPort() {
		return 0;
	}

	public void connect(TSocketAddress endpoint, int timeout) {
		// ponytail: no TCP in the browser (Stage D)
	}

	@Override
	public void close() {
	}
}
