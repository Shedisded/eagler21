package net.lax1dude.eaglercraft.v1_21.platform;

import com.google.common.util.concurrent.MoreExecutors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Desktop implementation: real ForkJoinPool. The TeaVM target replaces this
 * class with a single-threaded direct executor (teavm/src/main).
 */
public final class PlatformExecutors {
	private PlatformExecutors() {
	}

	public static ExecutorService newBackgroundExecutor(String name, int parallelism, Thread.UncaughtExceptionHandler handler) {
		if (parallelism <= 0) {
			return MoreExecutors.newDirectExecutorService();
		}
		AtomicInteger counter = new AtomicInteger(1);
		return new ForkJoinPool(parallelism, pool -> {
			ForkJoinWorkerThread thread = new ForkJoinWorkerThread(pool) {
			};
			thread.setName("Worker-" + name + "-" + counter.getAndIncrement());
			return thread;
		}, handler, true);
	}

	// IO pool (chunk IOWorker etc.). Desktop: a real cached thread pool. TeaVM overrides this class with a
	// single-threaded direct executor -- IOWorker .join()s its tasks, so a non-running pool deadlocks world gen.
	public static ExecutorService newIoExecutor(ThreadFactory factory) {
		return Executors.newCachedThreadPool(factory);
	}
}
