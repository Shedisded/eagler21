package org.teavm.classlib.java.nio.channels;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.teavm.classlib.java.nio.TByteBuffer;

/**
 * java.nio.channels.Channels factory. Real stream<->channel adapters: MC's
 * TextureUtil.readResource(InputStream) does Channels.newChannel(in) and then
 * channel.read(buf) — a null channel here NPEs on the first texture load
 * (mojangstudios.png). Back each channel by the underlying stream.
 */
public final class TChannels {
	private TChannels() {
	}

	public static TReadableByteChannel newChannel(InputStream in) {
		return new InputStreamChannel(in);
	}

	public static TWritableByteChannel newChannel(OutputStream out) {
		return new OutputStreamChannel(out);
	}

	public static java.io.Writer newWriter(TWritableByteChannel ch, java.nio.charset.Charset charset) {
		return null; // ponytail: unused by MC; add when a caller surfaces at link time
	}

	static final class InputStreamChannel implements TReadableByteChannel {
		private final InputStream in;
		private boolean open = true;
		private byte[] tmp = new byte[8192];

		InputStreamChannel(InputStream in) {
			this.in = in;
		}

		@Override
		public int read(TByteBuffer dst) throws IOException {
			int want = dst.remaining();
			if (want == 0) {
				return 0;
			}
			if (want > tmp.length) {
				tmp = new byte[want];
			}
			int n = in.read(tmp, 0, want);
			if (n > 0) {
				dst.put(tmp, 0, n);
			}
			return n; // -1 at EOF, matching InputStream contract
		}

		@Override
		public boolean isOpen() {
			return open;
		}

		@Override
		public void close() throws IOException {
			open = false;
			in.close();
		}
	}

	static final class OutputStreamChannel implements TWritableByteChannel {
		private final OutputStream out;
		private boolean open = true;

		OutputStreamChannel(OutputStream out) {
			this.out = out;
		}

		@Override
		public int write(TByteBuffer src) throws IOException {
			int n = src.remaining();
			byte[] buf = new byte[n];
			src.get(buf, 0, n);
			out.write(buf, 0, n);
			return n;
		}

		@Override
		public boolean isOpen() {
			return open;
		}

		@Override
		public void close() throws IOException {
			open = false;
			out.close();
		}
	}
}
