package io.netty.util.internal;

import java.nio.ByteBuffer;

/**
 * Shadow of netty's CleanerJava9. See {@link CleanerJava6} for the rationale —
 * the original used {@code Unsafe.invokeCleaner} via {@code MethodHandle.invokeExact}
 * (signature-polymorphic, unlinkable by TeaVM). Reported unsupported; direct-buffer
 * allocation throws, freeing is a no-op.
 */
final class CleanerJava9 implements Cleaner {

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
