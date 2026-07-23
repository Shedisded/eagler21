package org.teavm.classlib.java.util.concurrent;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Link stub for java.util.concurrent.ThreadPoolExecutor. No real threads in the
 * browser; pings/background tasks are disabled (Stage D). execute() no-ops and
 * submit() returns null so callers link without running anything. The remaining
 * ExecutorService methods are throwing stubs — present only so this concrete
 * class satisfies the interface (they are never called on the browser path).
 */
public class TThreadPoolExecutor extends TAbstractExecutorService {
	public TThreadPoolExecutor() {
	}

	@Override
	public void execute(Runnable r) {
		// ponytail: no threads in the browser — background tasks disabled (Stage D)
	}

	@Override
	public Future<?> submit(Runnable r) {
		return null; // ponytail: no threads in the browser (Stage D)
	}

	@Override
	public <T> Future<T> submit(Runnable task, T result) {
		return null;
	}

	@Override
	public <T> Future<T> submit(Callable<T> task) {
		return null;
	}

	@Override
	public void shutdown() {
	}

	@Override
	public List<Runnable> shutdownNow() {
		return Collections.emptyList();
	}

	@Override
	public boolean isShutdown() {
		return true;
	}

	@Override
	public boolean isTerminated() {
		return true;
	}

	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit) {
		return true;
	}

	@Override
	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) {
		throw new UnsupportedOperationException("no threads in the browser (Stage D)");
	}

	@Override
	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) {
		throw new UnsupportedOperationException("no threads in the browser (Stage D)");
	}

	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws ExecutionException {
		throw new UnsupportedOperationException("no threads in the browser (Stage D)");
	}

	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws ExecutionException {
		throw new UnsupportedOperationException("no threads in the browser (Stage D)");
	}
}
