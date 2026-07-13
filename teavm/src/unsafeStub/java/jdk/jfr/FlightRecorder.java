package jdk.jfr;

/**
 * Link stub for jdk.jfr.FlightRecorder. No recorder exists in the browser;
 * event registration is a no-op and availability probes report false.
 */
@SuppressWarnings("unused")
public final class FlightRecorder {
	private FlightRecorder() {
	}

	public static boolean isAvailable() {
		return false;
	}

	public static boolean isInitialized() {
		return false;
	}

	public static void register(Class<? extends Event> eventClass) {
	}

	public static void unregister(Class<? extends Event> eventClass) {
	}
}
