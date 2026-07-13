package org.teavm.classlib.java.util.concurrent;

import java.util.ArrayDeque;
import java.util.Collection;

/** Single-threaded environment: a plain deque is a valid CLQ. */
public class TConcurrentLinkedQueue<E> extends ArrayDeque<E> {
	public TConcurrentLinkedQueue() {
	}

	public TConcurrentLinkedQueue(Collection<? extends E> c) {
		super(c);
	}
}
