package jdk.jfr;

/**
 * Compile/link stub for jdk.jfr.Event. netty's buffer AllocateBufferEvent /
 * FreeChunkEvent / ... extend this and call the inherited commit()/isEnabled()/
 * shouldCommit(); with JFR disabled (no flight recorder in the browser) they all
 * report "not enabled" and commit nothing.
 */
@SuppressWarnings("unused")
public abstract class Event {
	protected Event() {
	}

	public void begin() {
	}

	public void end() {
	}

	public void commit() {
	}

	public boolean isEnabled() {
		return false;
	}

	public boolean shouldCommit() {
		return false;
	}

	public void set(int index, Object value) {
	}
}
