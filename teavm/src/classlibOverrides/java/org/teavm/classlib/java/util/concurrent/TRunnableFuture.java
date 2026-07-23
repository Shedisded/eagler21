package org.teavm.classlib.java.util.concurrent;

import org.teavm.classlib.java.lang.TRunnable;

/**
 * Link stub for java.util.concurrent.RunnableFuture. TeaVM ships none, so netty's
 * io.netty.util.concurrent.PromiseTask (which implements it) had no mapped supertype →
 * "missing in the classpath". Combines Runnable + Future exactly like the real one.
 */
public interface TRunnableFuture<V> extends TRunnable, TFuture<V> {
}
