package org.teavm.classlib.java.lang.invoke;

/**
 * Minimal VarHandle so code that only touches the non-polymorphic static fence
 * methods links. netty's {@code PlatformDependent.initializeVarHandle} calls
 * {@link #storeStoreFence()} unconditionally in its clinit.
 *
 * ponytail: fences are no-ops — the browser JS runtime is single-threaded, so
 * there is nothing to order. INSTANCE access methods (get/set/compareAndSet) are
 * signature-polymorphic and cannot be linked by TeaVM; any reachable instance use
 * must be removed by shadowing the netty class that holds it (e.g. the
 * io.netty.buffer.VarHandleByteBufferAccess shadow in teavm/src/main).
 */
public class TVarHandle {
    public static void fullFence() {
    }

    public static void acquireFence() {
    }

    public static void releaseFence() {
    }

    public static void loadLoadFence() {
    }

    public static void storeStoreFence() {
    }
}
