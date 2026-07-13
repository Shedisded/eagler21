package org.teavm.classlib.java.net;

/**
 * Value holder for java.net.InetSocketAddress. Real name resolution/sockets are
 * Stage D (WebSocket transport); this just carries host + port so MC's
 * ServerAddress/ServerData plumbing links and reads back what it stored.
 */
public class TInetSocketAddress extends TSocketAddress {
	private final TInetAddress address;
	private final String hostname;
	private final int port;

	public TInetSocketAddress(String hostname, int port) {
		this.hostname = hostname;
		this.address = TInetAddress.getByName(hostname);
		this.port = port;
	}

	public TInetSocketAddress(TInetAddress address, int port) {
		this.address = address;
		this.hostname = address != null ? address.getHostName() : null;
		this.port = port;
	}

	public TInetSocketAddress(int port) {
		this("0.0.0.0", port);
	}

	public static TInetSocketAddress createUnresolved(String host, int port) {
		return new TInetSocketAddress(host, port);
	}

	public TInetAddress getAddress() {
		return address;
	}

	public String getHostName() {
		return hostname;
	}

	public String getHostString() {
		return hostname;
	}

	public int getPort() {
		return port;
	}

	public boolean isUnresolved() {
		return false;
	}

	@Override
	public String toString() {
		return hostname + ":" + port;
	}
}
