package org.teavm.classlib.java.util.concurrent.atomic;

public class TAtomicReferenceArray<E> {
	private final Object[] array;

	public TAtomicReferenceArray(int length) {
		this.array = new Object[length];
	}

	public TAtomicReferenceArray(E[] source) {
		this.array = new Object[source.length];
		System.arraycopy(source, 0, this.array, 0, source.length);
	}

	public int length() {
		return array.length;
	}

	@SuppressWarnings("unchecked")
	public E get(int i) {
		return (E) array[i];
	}

	public void set(int i, E newValue) {
		array[i] = newValue;
	}

	public void lazySet(int i, E newValue) {
		array[i] = newValue;
	}

	@SuppressWarnings("unchecked")
	public E getAndSet(int i, E newValue) {
		Object old = array[i];
		array[i] = newValue;
		return (E) old;
	}

	public boolean compareAndSet(int i, E expect, E update) {
		if (array[i] == expect) {
			array[i] = update;
			return true;
		}
		return false;
	}

	public boolean weakCompareAndSet(int i, E expect, E update) {
		return compareAndSet(i, expect, update);
	}

	// single-threaded: return the witness (current) value, swapping only on match
	@SuppressWarnings("unchecked")
	public E compareAndExchange(int i, E expect, E update) {
		E current = (E) array[i];
		if (current == expect) {
			array[i] = update;
		}
		return current;
	}

	@Override
	public String toString() {
		return java.util.Arrays.toString(array);
	}
}
