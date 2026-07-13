package org.teavm.classlib.java.net;

/**
 * Link-time stub for java.net.Proxy. The browser build never routes through a
 * JDK proxy (networking is reworked onto WebSocket in Stage D); MC only needs
 * NO_PROXY to flow through GameConfig into Minecraft.
 */
public class TProxy {
	public enum Type {
		DIRECT, HTTP, SOCKS
	}

	public static final TProxy NO_PROXY = new TProxy();

	private TProxy() {
	}

	public Type type() {
		return Type.DIRECT;
	}
}
