package org.teavm.classlib.java.util.concurrent;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public interface TExecutorService extends Executor {
	void shutdown();

	List<Runnable> shutdownNow();

	boolean isShutdown();

	boolean isTerminated();

	boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException;

	<T> TFuture<T> submit(Callable<T> task);

	<T> TFuture<T> submit(Runnable task, T result);

	TFuture<?> submit(Runnable task);
}
