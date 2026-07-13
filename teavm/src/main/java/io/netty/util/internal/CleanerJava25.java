package io.netty.util.internal;

import java.nio.ByteBuffer;

/**
 * Shadow of netty's CleanerJava25. See {@link CleanerJava6}. The original used the
 * JDK 25 {@code Arena}/{@code MemorySegment} allocator through
 * {@code MethodHandle.invokeExact} (signature-polymorphic, unlinkable by TeaVM).
 * Reported unsupported; allocation throws, freeing is a no-op.
 */
final class CleanerJava25 implements Cleaner {

    static boolean isSupported() {
        return false;
    }

    @Override
    public CleanableDirectBuffer allocate(int capacity) {
        throw new UnsupportedOperationException("direct buffers unavailable in browser");
    }

    @Override
    public void freeDirectBuffer(ByteBuffer buffer) {
        // no-op
    }
}
