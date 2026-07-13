package org.teavm.classlib.java.net;

/** Link-time stub: no real socket networking in the browser. */
public class TInetAddress {
	private final String hostName;

	TInetAddress(String hostName) {
		this.hostName = hostName;
	}

	public static TInetAddress getByName(String host) {
		return new TInetAddress(host == null ? "localhost" : host);
	}

	public static TInetAddress[] getAllByName(String host) {
		return new TInetAddress[] { getByName(host) };
	}

	public static TInetAddress getLocalHost() {
		return new TInetAddress("localhost");
	}

	public static TInetAddress getLoopbackAddress() {
		return new TInetAddress("localhost");
	}

	public static TInetAddress getByAddress(byte[] addr) {
		return new TInetAddress("0.0.0.0");
	}

	public static TInetAddress getByAddress(String host, byte[] addr) {
		return new TInetAddress(host);
	}

	public byte[] getAddress() {
		return new byte[] { 127, 0, 0, 1 };
	}

	public String getHostAddress() {
		return "127.0.0.1";
	}

	public String getHostName() {
		return hostName;
	}

	public String getCanonicalHostName() {
		return hostName;
	}

	public boolean isLoopbackAddress() {
		return true;
	}

	public boolean isAnyLocalAddress() {
		return false;
	}

	public boolean isMulticastAddress() {
		return false;
	}

	public boolean isLinkLocalAddress() {
		return false;
	}

	public boolean isSiteLocalAddress() {
		return false;
	}

	public boolean isReachable(int timeout) {
		return false;
	}

	@Override
	public String toString() {
		return hostName + "/127.0.0.1";
	}
}
