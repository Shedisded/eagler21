package org.teavm.classlib.java.nio.channels;

import org.teavm.classlib.java.nio.TByteBuffer;
import org.teavm.classlib.java.nio.TMappedByteBuffer;
import org.teavm.classlib.java.nio.file.TOpenOption;
import org.teavm.classlib.java.nio.file.TPath;

/**
 * In-memory FileChannel for the browser build. TeaVM's default FS is an in-memory VFS
 * and there is no OS file handle, so this backs each channel with a growable byte[] held
 * for the channel's lifetime (NOT yet synced back to the VFS on close — a Stage-D save
 * concern; write-only logs like DownloadQueue's JsonEventLog and read-back-within-session
 * work fine). map() still throws (no mmap in a browser). Only the method signatures MC
 * actually references exist here (verified by the green TeaVM link); the base-class bodies
 * throw so any new ref surfaces at link time rather than silently no-oping.
 */
public abstract class TFileChannel {

	public static TFileChannel open(TPath path, TOpenOption... options) {
		return new InMemoryFileChannel();
	}

	public int read(TByteBuffer dst, long position) {
		throw new UnsupportedOperationException("FileChannel.read (Stage D)");
	}

	public int write(TByteBuffer src) {
		throw new UnsupportedOperationException("FileChannel.write (Stage D)");
	}

	public int write(TByteBuffer src, long position) {
		throw new UnsupportedOperationException("FileChannel.write (Stage D)");
	}

	public long size() {
		throw new UnsupportedOperationException("FileChannel.size (Stage D)");
	}

	public TFileChannel truncate(long size) {
		throw new UnsupportedOperationException("FileChannel.truncate (Stage D)");
	}

	public void force(boolean metaData) {
		throw new UnsupportedOperationException("FileChannel.force (Stage D)");
	}

	public TFileLock tryLock() {
		throw new UnsupportedOperationException("FileChannel.tryLock (Stage D)");
	}

	public void close() {
		throw new UnsupportedOperationException("FileChannel.close (Stage D)");
	}

	public boolean isOpen() {
		return false;
	}

	public TFileChannel position(long newPosition) {
		throw new UnsupportedOperationException("FileChannel.position (Stage D)");
	}

	public TMappedByteBuffer map(MapMode mode, long position, long size) {
		throw new UnsupportedOperationException("FileChannel.map — no mmap in the browser (Stage D)");
	}

	/** Growable-byte[]-backed concrete channel. Single-session, not VFS-persistent yet. */
	static final class InMemoryFileChannel extends TFileChannel {
		private byte[] data = new byte[32];
		private int length;
		private long position;
		private boolean open = true;

		private void ensure(long end) {
			if (end > data.length) {
				int cap = data.length;
				while (cap < end) {
					cap <<= 1;
				}
				byte[] grown = new byte[cap];
				System.arraycopy(data, 0, grown, 0, length);
				data = grown;
			}
		}

		private int writeAt(TByteBuffer src, long at) {
			int n = src.remaining();
			ensure(at + n);
			byte[] tmp = new byte[n];
			src.get(tmp, 0, n);
			System.arraycopy(tmp, 0, data, (int) at, n);
			if (at + n > length) {
				length = (int) (at + n);
			}
			return n;
		}

		@Override
		public int write(TByteBuffer src) {
			int n = writeAt(src, position);
			position += n;
			return n;
		}

		@Override
		public int write(TByteBuffer src, long at) {
			return writeAt(src, at);
		}

		@Override
		public int read(TByteBuffer dst, long at) {
			if (at >= length) {
				return -1;
			}
			int n = Math.min(dst.remaining(), (int) (length - at));
			dst.put(data, (int) at, n);
			return n;
		}

		@Override
		public long size() {
			return length;
		}

		@Override
		public TFileChannel truncate(long size) {
			if (size < length) {
				length = (int) size;
			}
			if (position > length) {
				position = length;
			}
			return this;
		}

		@Override
		public void force(boolean metaData) {
			// no-op: nothing to flush for an in-memory channel
		}

		@Override
		public TFileLock tryLock() {
			return new InMemoryFileLock();
		}

		@Override
		public void close() {
			open = false;
		}

		@Override
		public boolean isOpen() {
			return open;
		}

		@Override
		public TFileChannel position(long newPosition) {
			position = newPosition;
			return this;
		}
	}

	/** Always-valid dummy lock — there is no concurrent OS process in the browser. */
	static final class InMemoryFileLock extends TFileLock {
		@Override
		public boolean isValid() {
			return true;
		}

		@Override
		public void release() {
			// no-op
		}

		@Override
		public void close() {
			// no-op
		}
	}

	public static final class MapMode {
		public static final MapMode READ_ONLY = new MapMode("READ_ONLY");
		public static final MapMode READ_WRITE = new MapMode("READ_WRITE");
		public static final MapMode PRIVATE = new MapMode("PRIVATE");

		private final String name;

		private MapMode(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}
}
