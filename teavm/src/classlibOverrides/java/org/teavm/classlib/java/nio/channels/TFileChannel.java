package org.teavm.classlib.java.nio.channels;

import org.teavm.classlib.java.nio.TByteBuffer;
import org.teavm.classlib.java.nio.TMappedByteBuffer;
import org.teavm.classlib.java.nio.file.TOpenOption;
import org.teavm.classlib.java.nio.file.TPath;

/**
 * Link-only stub. Real file channels are unused in the browser (ICU's mmap path
 * is dead at the title screen); only MapMode.READ_ONLY is referenced as a
 * constant. FileInputStream.getChannel() throws rather than returning one.
 *
 * ponytail: the instance/open methods exist purely to satisfy MC's region-file
 * (LevelStorageSource) link refs and all throw — real file I/O is Stage D (back
 * it with the TeaVM in-memory VFS then).
 */
public abstract class TFileChannel {

	public static TFileChannel open(TPath path, TOpenOption... options) {
		throw new UnsupportedOperationException("FileChannel.open unavailable in browser (Stage D)");
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
