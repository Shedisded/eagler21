package io.netty.util.internal;

import java.nio.ByteBuffer;

/**
 * Shadow of netty's CleanerJava24Linker (a {@code public class}, so kept public).
 * See {@link CleanerJava6}. The original bound the JDK 24 FFM linker
 * (malloc/free/createByteBuffer) through {@code MethodHandle.invokeExact}
 * (signature-polymorphic, unlinkable by TeaVM). Reported unsupported; the package
 * static malloc/free helpers are dropped (nothing outside this class referenced them).
 */
public class CleanerJava24Linker implements Cleaner {

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
