package org.teavm.classlib.java.net;

/**
 * Link stub for java.net.MulticastSocket. LAN server discovery uses UDP
 * multicast, which the browser has no access to (Stage D); type-only so
 * LanServerDetection links.
 */
public class TMulticastSocket extends TDatagramSocket {
	public TMulticastSocket(int port) {
	}

	public void setSoTimeout(int t) {
		throw new UnsupportedOperationException("no UDP multicast in the browser (Stage D)");
	}

	public void joinGroup(TInetAddress g) {
		throw new UnsupportedOperationException("no UDP multicast in the browser (Stage D)");
	}

	public void receive(TDatagramPacket p) {
		throw new UnsupportedOperationException("no UDP multicast in the browser (Stage D)");
	}

	public void leaveGroup(TInetAddress g) {
		throw new UnsupportedOperationException("no UDP multicast in the browser (Stage D)");
	}
}
