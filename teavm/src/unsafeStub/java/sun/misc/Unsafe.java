package sun.misc;

import java.lang.reflect.Field;

/**
 * Compile-time stub for TeaVM. joml, netty and guava probe for Unsafe inside
 * try/catch and fall back to safe paths when it is unusable; every method
 * here throws so those probes fail cleanly in the browser.
 */
@SuppressWarnings("unused")
public final class Unsafe {
	private Unsafe() {
	}

	public static Unsafe getUnsafe() {
		throw uoe();
	}

	private static UnsupportedOperationException uoe() {
		return new UnsupportedOperationException("sun.misc.Unsafe is not available under TeaVM");
	}

	public long objectFieldOffset(Field f) { throw uoe(); }
	public long staticFieldOffset(Field f) { throw uoe(); }
	public Object staticFieldBase(Field f) { throw uoe(); }
	public int arrayBaseOffset(Class<?> arrayClass) { throw uoe(); }
	public int arrayIndexScale(Class<?> arrayClass) { throw uoe(); }
	public int addressSize() { throw uoe(); }
	public int pageSize() { throw uoe(); }
	public Object allocateInstance(Class<?> cls) { throw uoe(); }

	public boolean compareAndSwapObject(Object o, long offset, Object expected, Object x) { throw uoe(); }
	public boolean compareAndSwapInt(Object o, long offset, int expected, int x) { throw uoe(); }
	public boolean compareAndSwapLong(Object o, long offset, long expected, long x) { throw uoe(); }
	public Object getAndSetObject(Object o, long offset, Object newValue) { throw uoe(); }
	public int getAndSetInt(Object o, long offset, int newValue) { throw uoe(); }
	public long getAndSetLong(Object o, long offset, long newValue) { throw uoe(); }
	public int getAndAddInt(Object o, long offset, int delta) { throw uoe(); }
	public long getAndAddLong(Object o, long offset, long delta) { throw uoe(); }

	public Object getObject(Object o, long offset) { throw uoe(); }
	public void putObject(Object o, long offset, Object x) { throw uoe(); }
	public Object getObjectVolatile(Object o, long offset) { throw uoe(); }
	public void putObjectVolatile(Object o, long offset, Object x) { throw uoe(); }
	public void putOrderedObject(Object o, long offset, Object x) { throw uoe(); }
	public boolean getBoolean(Object o, long offset) { throw uoe(); }
	public void putBoolean(Object o, long offset, boolean x) { throw uoe(); }
	public byte getByte(Object o, long offset) { throw uoe(); }
	public void putByte(Object o, long offset, byte x) { throw uoe(); }
	public short getShort(Object o, long offset) { throw uoe(); }
	public void putShort(Object o, long offset, short x) { throw uoe(); }
	public char getChar(Object o, long offset) { throw uoe(); }
	public void putChar(Object o, long offset, char x) { throw uoe(); }
	public int getInt(Object o, long offset) { throw uoe(); }
	public void putInt(Object o, long offset, int x) { throw uoe(); }
	public int getIntVolatile(Object o, long offset) { throw uoe(); }
	public void putIntVolatile(Object o, long offset, int x) { throw uoe(); }
	public void putOrderedInt(Object o, long offset, int x) { throw uoe(); }
	public long getLong(Object o, long offset) { throw uoe(); }
	public void putLong(Object o, long offset, long x) { throw uoe(); }
	public long getLongVolatile(Object o, long offset) { throw uoe(); }
	public void putLongVolatile(Object o, long offset, long x) { throw uoe(); }
	public void putOrderedLong(Object o, long offset, long x) { throw uoe(); }
	public float getFloat(Object o, long offset) { throw uoe(); }
	public void putFloat(Object o, long offset, float x) { throw uoe(); }
	public double getDouble(Object o, long offset) { throw uoe(); }
	public void putDouble(Object o, long offset, double x) { throw uoe(); }

	public byte getByte(long address) { throw uoe(); }
	public void putByte(long address, byte x) { throw uoe(); }
	public short getShort(long address) { throw uoe(); }
	public void putShort(long address, short x) { throw uoe(); }
	public char getChar(long address) { throw uoe(); }
	public void putChar(long address, char x) { throw uoe(); }
	public int getInt(long address) { throw uoe(); }
	public void putInt(long address, int x) { throw uoe(); }
	public long getLong(long address) { throw uoe(); }
	public void putLong(long address, long x) { throw uoe(); }
	public float getFloat(long address) { throw uoe(); }
	public void putFloat(long address, float x) { throw uoe(); }
	public double getDouble(long address) { throw uoe(); }
	public void putDouble(long address, double x) { throw uoe(); }
	public long getAddress(long address) { throw uoe(); }
	public void putAddress(long address, long x) { throw uoe(); }

	public long allocateMemory(long bytes) { throw uoe(); }
	public long reallocateMemory(long address, long bytes) { throw uoe(); }
	public void freeMemory(long address) { throw uoe(); }
	public void setMemory(Object o, long offset, long bytes, byte value) { throw uoe(); }
	public void setMemory(long address, long bytes, byte value) { throw uoe(); }
	public void copyMemory(Object srcBase, long srcOffset, Object destBase, long destOffset, long bytes) { throw uoe(); }
	public void copyMemory(long srcAddress, long destAddress, long bytes) { throw uoe(); }

	public void park(boolean isAbsolute, long time) { throw uoe(); }
	public void unpark(Object thread) { throw uoe(); }
	public void loadFence() { throw uoe(); }
	public void storeFence() { throw uoe(); }
	public void fullFence() { throw uoe(); }
	public void throwException(Throwable ee) { throw uoe(); }
}
