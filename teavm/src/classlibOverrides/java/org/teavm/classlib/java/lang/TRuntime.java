package org.teavm.classlib.java.lang;

import org.teavm.jso.browser.Navigator;

/**
 * Override of TeaVM's TRuntime that adds no-op shutdown-hook methods (the
 * browser has no JVM shutdown event) so MC's Main can register its client
 * shutdown thread. Memory figures are the same dummies TeaVM's stock TRuntime
 * reports.
 */
public class TRuntime {
	private static final TRuntime instance = new TRuntime();

	public void exit(int status) {
	}

	public TProcess exec(TString command) {
		throw new UnsupportedOperationException("no subprocesses in the browser (Stage D)");
	}

	public void addShutdownHook(TThread hook) {
	}

	public boolean removeShutdownHook(TThread hook) {
		return false;
	}

	public long freeMemory() {
		return Integer.MAX_VALUE;
	}

	public long totalMemory() {
		return Integer.MAX_VALUE;
	}

	public long maxMemory() {
		return Integer.MAX_VALUE;
	}

	public void gc() {
		System.gc();
	}

	public int availableProcessors() {
		int n = Navigator.hardwareConcurrency();
		return n > 0 ? n : 1;
	}

	public static TRuntime getRuntime() {
		return instance;
	}

	public static Version version() {
		if (version == null) {
			version = new Version();
		}
		return version;
	}

	private static Version version;

	public static final class Version implements Comparable<Version> {
		private Version() {
		}

		public int feature() {
			return 21;
		}

		@Override
		public int compareTo(Version o) {
			return 0;
		}
	}
}
