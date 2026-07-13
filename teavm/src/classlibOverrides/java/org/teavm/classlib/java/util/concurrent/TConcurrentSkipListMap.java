package org.teavm.classlib.java.util.concurrent;

import org.teavm.classlib.java.util.TComparator;
import org.teavm.classlib.java.util.TMap;
import org.teavm.classlib.java.util.TSortedMap;
import org.teavm.classlib.java.util.TTreeMap;

// ponytail: single-threaded JS, TreeMap semantics cover ConcurrentSkipListMap
public class TConcurrentSkipListMap<K, V> extends TTreeMap<K, V> {
	public TConcurrentSkipListMap() {
	}

	public TConcurrentSkipListMap(TComparator<? super K> comparator) {
		super(comparator);
	}

	public TConcurrentSkipListMap(TMap<? extends K, ? extends V> m) {
		super(m);
	}

	public TConcurrentSkipListMap(TSortedMap<K, ? extends V> m) {
		super(m);
	}
}
