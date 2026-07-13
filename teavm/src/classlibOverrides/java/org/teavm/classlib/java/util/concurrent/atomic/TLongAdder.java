package org.teavm.classlib.java.util.concurrent.atomic;

import org.teavm.classlib.java.lang.TNumber;

/**
 * Shim for java.util.concurrent.atomic.LongAdder. The real one stripes its sum
 * across cells (Striped64) for contention; the single-threaded browser build has
 * no contention, so a single running long is correct. Extends TNumber and reads
 * the accumulated sum back through the Number accessors.
 */
public class TLongAdder extends TNumber {
	private long sum;

	public void add(long x) {
		sum += x;
	}

	public void increment() {
		add(1);
	}

	public long sum() {
		return sum;
	}

	@Override
	public int intValue() {
		return (int) sum;
	}

	@Override
	public long longValue() {
		return sum;
	}

	@Override
	public float floatValue() {
		return sum;
	}

	@Override
	public double doubleValue() {
		return sum;
	}
}
