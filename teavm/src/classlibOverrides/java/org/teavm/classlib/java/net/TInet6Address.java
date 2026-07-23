package org.teavm.classlib.java.net;

/**
 * Link stub for java.net.Inet6Address. IS-A InetAddress the way MC expects;
 * real name resolution is a Stage D concern. TInetAddress exposes only a
 * package-private (String) constructor, so mirror it here.
 */
public class TInet6Address extends TInetAddress {
	TInet6Address(String hostName) {
		super(hostName);
	}

	public static TInet6Address getByAddress(String host, byte[] addr, int scopeId) {
		return null;
	}

	public static TInet6Address getByAddress(String host, byte[] addr, TNetworkInterface nif) {
		return null;
	}

	// Referenced by netty's address handling (Bootstrap connect / resolver). No IPv6 scope in the browser.
	public TNetworkInterface getScopedInterface() {
		return null;
	}

	public int getScopeId() {
		return 0;
	}
}
