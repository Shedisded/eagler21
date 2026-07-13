package org.teavm.classlib.java.util.concurrent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Single-threaded Executors: every factory returns an executor that runs
 * tasks immediately on the calling thread.
 */
public final class TExecutors {
	private TExecutors() {
	}

	public static TExecutorService newSingleThreadExecutor() {
		return new DirectExecutorService();
	}

	public static TExecutorService newSingleThreadExecutor(TThreadFactory factory) {
		return new DirectExecutorService();
	}

	public static TExecutorService newFixedThreadPool(int nThreads) {
		return new DirectExecutorService();
	}

	public static TExecutorService newFixedThreadPool(int nThreads, TThreadFactory factory) {
		return new DirectExecutorService();
	}

	public static TExecutorService newCachedThreadPool() {
		return new DirectExecutorService();
	}

	public static TExecutorService newCachedThreadPool(TThreadFactory factory) {
		return new DirectExecutorService();
	}

	public static TScheduledExecutorService newScheduledThreadPool(int corePoolSize, TThreadFactory threadFactory) {
		return new DirectScheduledExecutorService();
	}

	public static TScheduledExecutorService newScheduledThreadPool(int corePoolSize) {
		return new DirectScheduledExecutorService();
	}

	public static TThreadFactory defaultThreadFactory() {
		return Thread::new;
	}

	public static <T> Callable<T> callable(Runnable task, T result) {
		return () -> {
			task.run();
			return result;
		};
	}

	static class DirectExecutorService implements TExecutorService {
		private boolean shutdown;

		@Override
		public void execute(Runnable command) {
			command.run();
		}

		@Override
		public void shutdown() {
			shutdown = true;
		}

		@Override
		public List<Runnable> shutdownNow() {
			shutdown = true;
			return Collections.emptyList();
		}

		@Override
		public boolean isShutdown() {
			return shutdown;
		}

		@Override
		public boolean isTerminated() {
			return shutdown;
		}

		@Override
		public boolean awaitTermination(long timeout, TimeUnit unit) {
			return true;
		}

		@Override
		public <T> TFuture<T> submit(Callable<T> task) {
			try {
				return new CompletedTaskFuture<>(task.call(), null);
			} catch (Exception e) {
				return new CompletedTaskFuture<>(null, e);
			}
		}

		@Override
		public <T> TFuture<T> submit(Runnable task, T result) {
			return submit(TExecutors.callable(task, result));
		}

		@Override
		public TFuture<?> submit(Runnable task) {
			return submit(task, null);
		}
	}

	// ponytail: runs immediately like DirectExecutorService; the schedule* methods
	// are Stage D (no timers in the browser event loop yet) and return null.
	static final class DirectScheduledExecutorService extends DirectExecutorService
			implements TScheduledExecutorService {
		@Override
		public TScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
			command.run();
			return null;
		}

		@Override
		public <V> TScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
			return null;
		}

		@Override
		public TScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period,
				TimeUnit unit) {
			return null;
		}

		@Override
		public TScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay,
				TimeUnit unit) {
			return null;
		}
	}

	static final class CompletedTaskFuture<T> implements TFuture<T> {
		private final T value;
		private final Throwable exception;

		CompletedTaskFuture(T value, Throwable exception) {
			this.value = value;
			this.exception = exception;
		}

		@Override
		public boolean cancel(boolean mayInterruptIfRunning) {
			return false;
		}

		@Override
		public boolean isCancelled() {
			return false;
		}

		@Override
		public boolean isDone() {
			return true;
		}

		@Override
		public T get() throws ExecutionException {
			if (exception != null) {
				throw new ExecutionException(exception);
			}
			return value;
		}

		@Override
		public T get(long timeout, TimeUnit unit) throws ExecutionException {
			return get();
		}
	}
}
