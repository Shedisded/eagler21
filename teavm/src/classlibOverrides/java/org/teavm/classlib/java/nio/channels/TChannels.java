package org.teavm.classlib.java.nio.channels;

import java.io.InputStream;
import java.io.OutputStream;

/** Link stub for java.nio.channels.Channels (static factory holder; unused at runtime). */
public final class TChannels {
	private TChannels() {
	}

	public static TReadableByteChannel newChannel(InputStream in) {
		return null; // ponytail: Stage D
	}

	public static TWritableByteChannel newChannel(OutputStream out) {
		return null; // ponytail: Stage D
	}

	public static java.io.Writer newWriter(TWritableByteChannel ch, java.nio.charset.Charset charset) {
		return null; // ponytail: Stage D
	}
}
