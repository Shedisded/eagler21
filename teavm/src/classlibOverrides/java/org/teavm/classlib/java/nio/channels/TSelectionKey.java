package org.teavm.classlib.java.nio.channels;

/** Link stub for java.nio.channels.SelectionKey. Inert — the browser has no real selector. */
public abstract class TSelectionKey {

	private Object attachment;

	public Object attachment() {
		return attachment;
	}

	public Object attach(Object ob) {
		Object prev = attachment;
		attachment = ob;
		return prev;
	}

	public void cancel() {
	}

	public TSelectableChannel channel() {
		return null;
	}

	public boolean isValid() {
		return false;
	}

	public int readyOps() {
		return 0;
	}

	public int interestOps() {
		return 0;
	}

	public TSelectionKey interestOps(int ops) {
		return this;
	}
}
