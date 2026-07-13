package org.teavm.classlib.java.lang.management;

/**
 * Link stub for java.lang.management.ThreadInfo. No real threads to introspect;
 * ThreadMXBean.dumpAllThreads() returns an empty array, so these accessors are
 * referenced but never actually invoked.
 */
public class TThreadInfo {
	public long getThreadId() {
		return 0L;
	}

	public String getThreadName() {
		return "main";
	}

	public StackTraceElement[] getStackTrace() {
		return new StackTraceElement[0];
	}

	@Override
	public String toString() {
		return "\"unknown\" (no thread introspection in the browser build)";
	}
}
