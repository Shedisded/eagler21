package io.netty.util.internal;

import java.nio.ByteBuffer;

/**
 * Shadow of netty's CleanerJava6 (teavm/src/main wins the TeaVM task classpath).
 *
 * ponytail: the browser has no {@code sun.misc.Cleaner} and no direct buffers.
 * The original probed for the cleaner via {@code MethodHandle.invokeExact}, which
 * is signature-polymorphic and cannot be linked by TeaVM. We report unsupported
 * so {@code PlatformDependent} never selects this cleaner; {@link #allocate} throws
 * (never reached at the title screen) and the inner CleanableDirectBufferImpl is
 * dropped by never being constructed. No MethodHandle, no Unsafe.
 */
final class CleanerJava6 implements Cleaner {

    static boolean isSupported() {
        return false;
    }

    @Override
    public CleanableDirectBuffer allocate(int capacity) {
        throw new UnsupportedOperationException("direct buffers unavailable in browser");
    }

    @Override
    public void freeDirectBuffer(ByteBuffer buffer) {
        // no-op: nothing to free without native direct memory
    }
}
