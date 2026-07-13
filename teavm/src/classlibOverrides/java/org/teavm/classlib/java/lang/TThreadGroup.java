package org.teavm.classlib.java.lang;

/**
 * Link stub for java.lang.ThreadGroup. Thread groups are inert in the browser
 * (single event-loop "thread"); this just carries a name so the Thread(ThreadGroup,
 * Runnable, String) constructor and any group plumbing links.
 */
public class TThreadGroup {
	private final String name;

	public TThreadGroup() {
		this("main");
	}

	public TThreadGroup(String name) {
		this.name = name;
	}

	public TThreadGroup(TThreadGroup parent, String name) {
		this.name = name;
	}

	public final String getName() {
		return name;
	}
}
