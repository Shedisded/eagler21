package org.teavm.classlib.java.net;

/**
 * Link stub for java.net.NetworkInterface. No real NICs in the browser; this is
 * a Stage D concern. Type-only so MC/netty bytecode links.
 */
public class TNetworkInterface {
	public static TNetworkInterface getByInetAddress(TInetAddress addr) {
		return null;
	}

	public static TNetworkInterface getByName(String name) {
		return null;
	}

	public java.util.Enumeration getInetAddresses() {
		return java.util.Collections.emptyEnumeration();
	}

	public String getName() {
		return "eth0";
	}

	public static java.util.Enumeration getNetworkInterfaces() {
		return java.util.Collections.emptyEnumeration();
	}

	public boolean isLoopback() {
		return false;
	}

	public boolean isVirtual() {
		return false;
	}

	public String getDisplayName() {
		return "eth0";
	}

	public byte[] getHardwareAddress() {
		return new byte[0];
	}
}
