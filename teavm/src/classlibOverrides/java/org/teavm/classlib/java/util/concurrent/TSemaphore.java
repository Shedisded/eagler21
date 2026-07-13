package org.teavm.classlib.java.util.concurrent;

import java.io.Serializable;

/**
 * Shim for java.util.concurrent.Semaphore. The browser build is single-threaded,
 * so permits are tracked but acquire never blocks (nothing else can be holding
 * them). Simple correct single-threaded accounting.
 */
public class TSemaphore implements Serializable {
	private int permits;

	public TSemaphore(int permits) {
		this.permits = permits;
	}

	public void acquire() throws InterruptedException {
		permits--;
	}

	public void release() {
		permits++;
	}

	public boolean tryAcquire() {
		if (permits > 0) {
			permits--;
			return true;
		}
		return false;
	}
}
