package org.teavm.classlib.java.util.concurrent.locks;

public class TReentrantReadWriteLock implements TReadWriteLock {
	private final ReadLock readLock = new ReadLock();
	private final WriteLock writeLock = new WriteLock();

	public TReentrantReadWriteLock() {
	}

	public TReentrantReadWriteLock(boolean fair) {
	}

	@Override
	public ReadLock readLock() {
		return readLock;
	}

	@Override
	public WriteLock writeLock() {
		return writeLock;
	}

	// Single-threaded in the browser; these are only referenced by ICU's
	// ICURWLock (reachable but never executed via the DecimalFormat path).
	public boolean isWriteLocked() {
		return false;
	}

	public int getReadLockCount() {
		return 0;
	}

	public static class ReadLock extends TReentrantLock {
	}

	public static class WriteLock extends TReentrantLock {
	}
}
