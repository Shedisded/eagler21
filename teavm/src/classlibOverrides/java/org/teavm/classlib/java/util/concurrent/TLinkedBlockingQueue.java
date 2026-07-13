package org.teavm.classlib.java.util.concurrent;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

/** Single-threaded environment: a plain list queue with throwing "blocking" ops. */
public class TLinkedBlockingQueue<E> extends LinkedList<E> {
	public TLinkedBlockingQueue() {
	}

	public TLinkedBlockingQueue(int capacity) {
	}

	public TLinkedBlockingQueue(Collection<? extends E> c) {
		super(c);
	}

	public void put(E e) {
		add(e);
	}

	public boolean offer(E e, long timeout, TimeUnit unit) {
		return offer(e);
	}

	public E take() throws InterruptedException {
		E e = poll();
		if (e == null) {
			throw new IllegalStateException("Blocking take() on an empty queue in a single-threaded environment");
		}
		return e;
	}

	public E poll(long timeout, TimeUnit unit) {
		return poll();
	}

	public int remainingCapacity() {
		return Integer.MAX_VALUE;
	}

	public int drainTo(Collection<? super E> c) {
		return drainTo(c, Integer.MAX_VALUE);
	}

	public int drainTo(Collection<? super E> c, int maxElements) {
		int n = 0;
		E e;
		while (n < maxElements && (e = poll()) != null) {
			c.add(e);
			++n;
		}
		return n;
	}
}
