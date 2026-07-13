package io.netty.util.internal;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

/**
 * Shadow of netty's VarHandleFactory (teavm/src/main wins the TeaVM task classpath).
 *
 * ponytail: the original built byte-array/ByteBuffer VarHandle views in its clinit via
 * MethodHandles.lookup() + findVarHandle — both unlinkable by TeaVM (signature-polymorphic /
 * reflective). Report unsupported so netty never takes the VarHandle byte-access path; the view
 * accessors throw (never reached — VarHandleByteBufferAccess is shadowed to use manual byte ops).
 * No MethodHandle/VarHandle is ever constructed here.
 */
final class VarHandleFactory {

    private static final Throwable UNAVAILABLE =
            new UnsupportedOperationException("VarHandle unavailable on TeaVM");

    private VarHandleFactory() {
    }

    public static boolean isSupported() {
        return false;
    }

    public static Throwable unavailableCause() {
        return UNAVAILABLE;
    }

    public static VarHandle privateFindVarHandle(MethodHandles.Lookup lookup, Class<?> declaringClass,
            String fieldName, Class<?> fieldType) {
        throw new UnsupportedOperationException("VarHandle unavailable on TeaVM");
    }

    public static VarHandle longLeArrayView() {
        throw new UnsupportedOperationException();
    }

    public static VarHandle longBeArrayView() {
        throw new UnsupportedOperationException();
    }

    public static VarHandle intLeArrayView() {
        throw new UnsupportedOperationException();
    }

    public static VarHandle intBeArrayView() {
        throw new UnsupportedOperationException();
    }

    public static VarHandle shortLeArrayView() {
        throw new UnsupportedOperationException();
    }

    public static VarHandle shortBeArrayView() {
        throw new UnsupportedOperationException();
    }

    public static VarHandle longLeByteBufferView() {
        throw new UnsupportedOperationException();
    }

    public static VarHandle longBeByteBufferView() {
        throw new UnsupportedOperationException();
    }

    public static VarHandle intLeByteBufferView() {
        throw new UnsupportedOperationException();
    }

    public static VarHandle intBeByteBufferView() {
        throw new UnsupportedOperationException();
    }

    public static VarHandle shortLeByteBufferView() {
        throw new UnsupportedOperationException();
    }

    public static VarHandle shortBeByteBufferView() {
        throw new UnsupportedOperationException();
    }
}
