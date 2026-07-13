package org.teavm.classlib.java.util.concurrent.locks;

import java.io.Serializable;

/**
 * Shim for java.util.concurrent.locks.StampedLock. Locking is a no-op in the
 * single-threaded browser build; the write-lock methods hand back a fixed
 * nonzero stamp so callers that check "did I get the lock?" see success.
 */
public class TStampedLock implements Serializable {
	public long writeLock() {
		return 1L;
	}

	public long tryWriteLock() {
		return 1L;
	}

	public void unlockWrite(long stamp) {
		// no-op: single-threaded, nothing to release
	}
}
