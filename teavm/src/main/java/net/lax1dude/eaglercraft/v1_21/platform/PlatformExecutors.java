package net.lax1dude.eaglercraft.v1_21.platform;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * TeaVM implementation: JS is single-threaded, every task runs inline on the
 * calling thread. Avoids guava MoreExecutors so AbstractExecutorService never
 * becomes reachable.
 */
public final class PlatformExecutors {
	private PlatformExecutors() {
	}

	public static ExecutorService newBackgroundExecutor(String name, int parallelism, Thread.UncaughtExceptionHandler handler) {
		return new DirectExecutorService();
	}

	// IO pool: JS is single-threaded, so run inline like the background executor. The desktop cached thread
	// pool would map to a non-running TeaVM pool and IOWorker.join() would deadlock world gen (stall at ~16%).
	public static ExecutorService newIoExecutor(java.util.concurrent.ThreadFactory factory) {
		return new DirectExecutorService();
	}

	static final class DirectExecutorService implements ExecutorService {
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
		public <T> Future<T> submit(Callable<T> task) {
			try {
				return new CompletedFuture<>(task.call(), null);
			} catch (Exception e) {
				return new CompletedFuture<>(null, e);
			}
		}

		@Override
		public <T> Future<T> submit(Runnable task, T result) {
			return submit(() -> {
				task.run();
				return result;
			});
		}

		@Override
		public Future<?> submit(Runnable task) {
			return submit(task, null);
		}

		@Override
		public <T> List<Future<T>> invokeAll(java.util.Collection<? extends Callable<T>> tasks) {
			List<Future<T>> futures = new java.util.ArrayList<>(tasks.size());
			for (Callable<T> task : tasks) {
				futures.add(submit(task));
			}
			return futures;
		}

		@Override
		public <T> List<Future<T>> invokeAll(java.util.Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) {
			return invokeAll(tasks);
		}

		@Override
		public <T> T invokeAny(java.util.Collection<? extends Callable<T>> tasks) throws ExecutionException {
			Exception last = null;
			for (Callable<T> task : tasks) {
				try {
					return task.call();
				} catch (Exception e) {
					last = e;
				}
			}
			throw new ExecutionException(last);
		}

		@Override
		public <T> T invokeAny(java.util.Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws ExecutionException {
			return invokeAny(tasks);
		}
	}

	static final class CompletedFuture<T> implements Future<T> {
		private final T value;
		private final Throwable exception;

		CompletedFuture(T value, Throwable exception) {
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
		public T get(long timeout, TimeUnit unit) throws ExecutionException, TimeoutException {
			return get();
		}
	}
}
