package io.netty.buffer;

import java.nio.ByteBuffer;

/**
 * Shadow of netty's VarHandleByteBufferAccess (teavm/src/main wins the TeaVM task
 * classpath). The original routed every accessor through a {@code VarHandle}
 * array/buffer view obtained from {@code PlatformDependent}; those get/set calls
 * are signature-polymorphic and cannot be linked by TeaVM.
 *
 * ponytail: reimplement each accessor with plain manual byte assembly (netty's own
 * slow path). This also makes {@code PlatformDependent.*ArrayView()/*ByteBufferView()}
 * unreachable, which in turn drops {@code VarHandleFactory} (and its MethodHandle
 * clinit) from the link entirely. Browser byte access is not perf-critical.
 */
final class VarHandleByteBufferAccess {

    private VarHandleByteBufferAccess() {
    }

    private static int u(ByteBuffer b, int i) {
        return b.get(i) & 0xff;
    }

    private static int u(byte[] m, int i) {
        return m[i] & 0xff;
    }

    // ---- ByteBuffer accessors ------------------------------------------------

    static short getShortBE(ByteBuffer b, int i) {
        return (short) (u(b, i) << 8 | u(b, i + 1));
    }

    static void setShortBE(ByteBuffer b, int i, int v) {
        b.put(i, (byte) (v >>> 8));
        b.put(i + 1, (byte) v);
    }

    static short getShortLE(ByteBuffer b, int i) {
        return (short) (u(b, i) | u(b, i + 1) << 8);
    }

    static void setShortLE(ByteBuffer b, int i, int v) {
        b.put(i, (byte) v);
        b.put(i + 1, (byte) (v >>> 8));
    }

    static int getIntBE(ByteBuffer b, int i) {
        return u(b, i) << 24 | u(b, i + 1) << 16 | u(b, i + 2) << 8 | u(b, i + 3);
    }

    static void setIntBE(ByteBuffer b, int i, int v) {
        b.put(i, (byte) (v >>> 24));
        b.put(i + 1, (byte) (v >>> 16));
        b.put(i + 2, (byte) (v >>> 8));
        b.put(i + 3, (byte) v);
    }

    static int getIntLE(ByteBuffer b, int i) {
        return u(b, i) | u(b, i + 1) << 8 | u(b, i + 2) << 16 | u(b, i + 3) << 24;
    }

    static void setIntLE(ByteBuffer b, int i, int v) {
        b.put(i, (byte) v);
        b.put(i + 1, (byte) (v >>> 8));
        b.put(i + 2, (byte) (v >>> 16));
        b.put(i + 3, (byte) (v >>> 24));
    }

    static long getLongBE(ByteBuffer b, int i) {
        return (long) getIntBE(b, i) << 32 | getIntBE(b, i + 4) & 0xffffffffL;
    }

    static void setLongBE(ByteBuffer b, int i, long v) {
        setIntBE(b, i, (int) (v >>> 32));
        setIntBE(b, i + 4, (int) v);
    }

    static long getLongLE(ByteBuffer b, int i) {
        return getIntLE(b, i) & 0xffffffffL | (long) getIntLE(b, i + 4) << 32;
    }

    static void setLongLE(ByteBuffer b, int i, long v) {
        setIntLE(b, i, (int) v);
        setIntLE(b, i + 4, (int) (v >>> 32));
    }

    // ---- byte[] accessors ----------------------------------------------------

    static short getShortBE(byte[] m, int i) {
        return (short) (u(m, i) << 8 | u(m, i + 1));
    }

    static void setShortBE(byte[] m, int i, int v) {
        m[i] = (byte) (v >>> 8);
        m[i + 1] = (byte) v;
    }

    static short getShortLE(byte[] m, int i) {
        return (short) (u(m, i) | u(m, i + 1) << 8);
    }

    static void setShortLE(byte[] m, int i, int v) {
        m[i] = (byte) v;
        m[i + 1] = (byte) (v >>> 8);
    }

    static int getIntBE(byte[] m, int i) {
        return u(m, i) << 24 | u(m, i + 1) << 16 | u(m, i + 2) << 8 | u(m, i + 3);
    }

    static void setIntBE(byte[] m, int i, int v) {
        m[i] = (byte) (v >>> 24);
        m[i + 1] = (byte) (v >>> 16);
        m[i + 2] = (byte) (v >>> 8);
        m[i + 3] = (byte) v;
    }

    static int getIntLE(byte[] m, int i) {
        return u(m, i) | u(m, i + 1) << 8 | u(m, i + 2) << 16 | u(m, i + 3) << 24;
    }

    static void setIntLE(byte[] m, int i, int v) {
        m[i] = (byte) v;
        m[i + 1] = (byte) (v >>> 8);
        m[i + 2] = (byte) (v >>> 16);
        m[i + 3] = (byte) (v >>> 24);
    }

    static long getLongBE(byte[] m, int i) {
        return (long) getIntBE(m, i) << 32 | getIntBE(m, i + 4) & 0xffffffffL;
    }

    static void setLongBE(byte[] m, int i, long v) {
        setIntBE(m, i, (int) (v >>> 32));
        setIntBE(m, i + 4, (int) v);
    }

    static long getLongLE(byte[] m, int i) {
        return getIntLE(m, i) & 0xffffffffL | (long) getIntLE(m, i + 4) << 32;
    }

    static void setLongLE(byte[] m, int i, long v) {
        setIntLE(m, i, (int) v);
        setIntLE(m, i + 4, (int) (v >>> 32));
    }
}
