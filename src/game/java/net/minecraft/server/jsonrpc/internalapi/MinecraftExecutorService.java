package net.minecraft.server.jsonrpc.internalapi;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public interface MinecraftExecutorService {
    <V> CompletableFuture<V> submit(Supplier<V> p_449179_);

    CompletableFuture<Void> submit(Runnable p_449335_);
}
