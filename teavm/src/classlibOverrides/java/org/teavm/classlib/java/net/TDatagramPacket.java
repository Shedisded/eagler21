package org.teavm.classlib.java.net;

/**
 * Link stub for java.net.DatagramPacket. No UDP in the browser; real transport
 * is a Stage D concern. Type-only so MC/netty bytecode links.
 */
public class TDatagramPacket {
	public TDatagramPacket(byte[] buf, int length, TInetAddress address, int port) {
	}

	public TDatagramPacket(byte[] buf, int length) {
	}

	public byte[] getData() {
		return new byte[0];
	}

	public int getOffset() {
		return 0;
	}

	public int getLength() {
		return 0;
	}

	public TInetAddress getAddress() {
		return null;
	}
}
