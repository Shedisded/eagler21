package net.lax1dude.eaglercraft.v1_21.netty;

import java.util.ArrayDeque;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSByRef;
import org.teavm.jso.dom.events.MessageEvent;
import org.teavm.jso.typedarrays.ArrayBuffer;
import org.teavm.jso.websocket.WebSocket;

/**
 * Thin wrapper over the browser {@link WebSocket} for the multiplayer transport. Owns the socket, tracks
 * open/closed/failed state, converts binary frames to/from {@code byte[]}, and buffers inbound frames until
 * the netty channel drains them (the channel pumps on its event loop, not the JS callback thread).
 *
 * P1: raw byte round-trip only. Framing/handshake live above this in the netty pipeline.
 */
public final class EaglerWebSocketTransport {

	public enum State { CONNECTING, OPEN, CLOSED, FAILED }

	private final WebSocket sock;
	private volatile State state = State.CONNECTING;
	// Inbound binary frames, oldest first. Drained by the channel's doBeginRead/read pump.
	private final ArrayDeque<byte[]> inbound = new ArrayDeque<>();
	private Runnable onReadable;   // fired (best-effort) when a frame arrives, to wake the channel
	private Runnable onOpen;
	private Runnable onClosed;

	public EaglerWebSocketTransport(String url) {
		this.sock = WebSocket.create(url);
		this.sock.setBinaryType("arraybuffer");
		this.sock.onOpen(e -> {
			state = State.OPEN;
			if (onOpen != null) {
				onOpen.run();
			}
		});
		this.sock.onError(e -> {
			if (state == State.CONNECTING) {
				state = State.FAILED;
			}
			if (onClosed != null) {
				onClosed.run();
			}
		});
		this.sock.onClose(e -> {
			state = (state == State.CONNECTING) ? State.FAILED : State.CLOSED;
			if (onClosed != null) {
				onClosed.run();
			}
		});
		this.sock.onMessage((MessageEvent e) -> {
			ArrayBuffer buf = asArrayBuffer(e);
			if (buf == null) {
				return; // ignore text frames on the game socket
			}
			int len = buf.getByteLength();
			byte[] dst = new byte[len];
			if (len > 0) {
				copyIn(buf, dst);
			}
			synchronized (inbound) {
				inbound.add(dst);
			}
			if (onReadable != null) {
				onReadable.run();
			}
		});
	}

	public State state() {
		return state;
	}

	public boolean isOpen() {
		return state == State.OPEN;
	}

	public void setOnOpen(Runnable r) {
		this.onOpen = r;
	}

	public void setOnClosed(Runnable r) {
		this.onClosed = r;
	}

	public void setOnReadable(Runnable r) {
		this.onReadable = r;
	}

	/** Next inbound frame, or null if none buffered. */
	public byte[] poll() {
		synchronized (inbound) {
			return inbound.poll();
		}
	}

	public boolean hasInbound() {
		synchronized (inbound) {
			return !inbound.isEmpty();
		}
	}

	/** Send one binary frame. {@code data} must be exactly the bytes to send. */
	public void send(byte[] data) {
		if (state == State.OPEN) {
			sendBytes(sock, data);
		}
	}

	public void close() {
		if (state != State.CLOSED && state != State.FAILED) {
			try {
				sock.close();
			} catch (RuntimeException ignored) {
			}
		}
		state = State.CLOSED;
	}

	// --- P1 self-test -----------------------------------------------------------------------------
	// If the page sets window.__EAGLER_WS_TEST = "<wss url>", open a raw transport to it and log the
	// state transitions + first inbound frame. Isolates the browser-WebSocket + server URL + binary
	// framing from netty/UI before trusting the full connect path. Returns true if it ran (caller skips
	// normal boot). ponytail: dev-only harness hook, gated off by default.
	public static boolean runSelfTestIfRequested() {
		String url = selfTestUrl();
		if (url == null || url.isEmpty()) {
			return false;
		}
		System.out.println("[EaglerWS] self-test connecting to " + url);
		EaglerWebSocketTransport t = new EaglerWebSocketTransport(url);
		t.setOnOpen(() -> System.out.println("[EaglerWS] OPEN"));
		t.setOnClosed(() -> System.out.println("[EaglerWS] CLOSED/FAILED state=" + t.state()));
		t.setOnReadable(() -> {
			byte[] f;
			while ((f = t.poll()) != null) {
				System.out.println("[EaglerWS] RECV " + f.length + " bytes; first=" + describe(f));
			}
		});
		return true;
	}

	private static String describe(byte[] f) {
		StringBuilder sb = new StringBuilder();
		int n = Math.min(f.length, 16);
		for (int i = 0; i < n; i++) {
			sb.append(Integer.toHexString(f[i] & 0xFF)).append(' ');
		}
		return sb.toString();
	}

	@JSBody(params = {}, script = "return (typeof window !== 'undefined' && window.__EAGLER_WS_TEST) ? window.__EAGLER_WS_TEST : null;")
	private static native String selfTestUrl();

	// --- JS interop -------------------------------------------------------------------------------
	// The message payload is an ArrayBuffer (binaryType=arraybuffer); text frames have a string .data.
	@JSBody(params = "e", script = "return (e.data instanceof ArrayBuffer) ? e.data : null;")
	private static native ArrayBuffer asArrayBuffer(MessageEvent e);

	// Copy the ArrayBuffer into the java byte[] (its underlying Int8Array, passed by-ref) in one bulk set.
	@JSBody(params = { "src", "dst" }, script = "dst.set(new Int8Array(src));")
	private static native void copyIn(ArrayBuffer src, @JSByRef byte[] dst);

	// Send the byte[]'s underlying Int8Array directly (zero-copy view); WebSocket.send accepts an ArrayBufferView.
	@JSBody(params = { "sock", "data" }, script = "sock.send(data);")
	private static native void sendBytes(WebSocket sock, @JSByRef byte[] data);
}
