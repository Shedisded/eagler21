package org.teavm.classlib.java.util.concurrent;

import java.util.concurrent.TimeUnit;

/**
 * Shim for java.util.concurrent.CountDownLatch. The browser build runs on a
 * single thread, so the latch can never be counted down mid-await: await never
 * blocks and simply reports whether the count has already reached zero. Enough
 * for MC's bootstrap, which counts down and then awaits on the same thread.
 */
public class TCountDownLatch {
	private int count;

	public TCountDownLatch(int count) {
		this.count = count;
	}

	public void await() throws InterruptedException {
		// single-threaded: nothing else can count down, so never block
	}

	public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
		return count <= 0;
	}

	public void countDown() {
		if (count > 0) {
			count--;
		}
	}

	public long getCount() {
		return count;
	}
}
