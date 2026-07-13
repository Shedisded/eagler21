package org.teavm.classlib.java.util.concurrent;

import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Single-threaded CompletionStage: only the surface Minecraft actually uses.
 */
public interface TCompletionStage<T> {
	<U> TCompletionStage<U> thenApply(Function<? super T, ? extends U> fn);

	<U> TCompletionStage<U> thenApplyAsync(Function<? super T, ? extends U> fn);

	<U> TCompletionStage<U> thenApplyAsync(Function<? super T, ? extends U> fn, Executor executor);

	TCompletionStage<Void> thenAccept(Consumer<? super T> action);

	TCompletionStage<Void> thenAcceptAsync(Consumer<? super T> action);

	TCompletionStage<Void> thenAcceptAsync(Consumer<? super T> action, Executor executor);

	TCompletionStage<Void> thenRun(Runnable action);

	TCompletionStage<Void> thenRunAsync(Runnable action);

	TCompletionStage<Void> thenRunAsync(Runnable action, Executor executor);

	<U> TCompletionStage<U> thenCompose(Function<? super T, ? extends TCompletionStage<U>> fn);

	<U> TCompletionStage<U> thenComposeAsync(Function<? super T, ? extends TCompletionStage<U>> fn);

	<U> TCompletionStage<U> thenComposeAsync(Function<? super T, ? extends TCompletionStage<U>> fn, Executor executor);

	<U, V> TCompletionStage<V> thenCombine(TCompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn);

	<U, V> TCompletionStage<V> thenCombineAsync(TCompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn, Executor executor);

	<U> TCompletionStage<U> applyToEither(TCompletionStage<? extends T> other, Function<? super T, U> fn);

	TCompletionStage<T> whenComplete(BiConsumer<? super T, ? super Throwable> action);

	TCompletionStage<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action, Executor executor);

	<U> TCompletionStage<U> handle(BiFunction<? super T, Throwable, ? extends U> fn);

	<U> TCompletionStage<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn);

	<U> TCompletionStage<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn, Executor executor);

	TCompletionStage<T> exceptionally(Function<Throwable, ? extends T> fn);

	TCompletionStage<T> exceptionallyAsync(Function<Throwable, ? extends T> fn, Executor executor);

	TCompletableFuture<T> toCompletableFuture();
}
