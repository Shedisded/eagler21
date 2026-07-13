package org.teavm.classlib.java.util.concurrent.locks;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/** Single-threaded environment: locking always succeeds instantly. */
public class TReentrantLock implements TLock {
	private int holdCount;

	public TReentrantLock() {
	}

	public TReentrantLock(boolean fair) {
	}

	@Override
	public void lock() {
		++holdCount;
	}

	@Override
	public void lockInterruptibly() {
		++holdCount;
	}

	@Override
	public boolean tryLock() {
		++holdCount;
		return true;
	}

	@Override
	public boolean tryLock(long time, TimeUnit unit) {
		++holdCount;
		return true;
	}

	@Override
	public void unlock() {
		--holdCount;
	}

	@Override
	public TCondition newCondition() {
		return new SingleThreadCondition();
	}

	public boolean isLocked() {
		return holdCount > 0;
	}

	public boolean isHeldByCurrentThread() {
		return holdCount > 0;
	}

	public int getHoldCount() {
		return holdCount;
	}

	static final class SingleThreadCondition implements TCondition {
		@Override
		public void await() {
			throw new IllegalStateException("Cannot block on a condition in a single-threaded environment");
		}

		@Override
		public void awaitUninterruptibly() {
			await();
		}

		@Override
		public long awaitNanos(long nanosTimeout) {
			await();
			return 0L;
		}

		@Override
		public boolean await(long time, TimeUnit unit) {
			await();
			return false;
		}

		@Override
		public boolean awaitUntil(Date deadline) {
			await();
			return false;
		}

		@Override
		public void signal() {
		}

		@Override
		public void signalAll() {
		}
	}
}
