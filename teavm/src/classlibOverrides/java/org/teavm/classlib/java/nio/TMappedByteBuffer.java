package org.teavm.classlib.java.nio;

/**
 * Link stub for java.nio.MappedByteBuffer. Memory-mapped files do not exist in the
 * browser; FileChannel.map() throws, so this is never instantiated. Abstract so it
 * inherits (and leaves abstract) TByteBuffer's operations without needing bodies.
 */
public abstract class TMappedByteBuffer extends TByteBuffer {
}
