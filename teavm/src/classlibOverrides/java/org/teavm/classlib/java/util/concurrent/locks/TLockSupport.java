package org.teavm.classlib.java.util.concurrent.locks;

/** Single-threaded: parking would deadlock, so it throws; unpark is a no-op. */
public final class TLockSupport {
	private TLockSupport() {
	}

	public static void park() {
		throw new IllegalStateException("Cannot park the only thread");
	}

	public static void park(Object blocker) {
		park();
	}

	public static void parkNanos(long nanos) {
	}

	public static void parkNanos(Object blocker, long nanos) {
	}

	public static void parkUntil(long deadline) {
	}

	public static void parkUntil(Object blocker, long deadline) {
	}

	public static void unpark(Thread thread) {
	}
}
