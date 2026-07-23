package io.netty.util;

/**
 * TeaVM shadow of netty's ResourceLeakDetector.
 *
 * The real class uses reflection heavily: {@code addExclusions} calls
 * {@code clz.getDeclaredMethods()} at clinit of AbstractByteBufAllocator and throws
 * IllegalArgumentException when the named method isn't found — and TeaVM does not emit
 * reflection metadata for those netty classes, so getDeclaredMethods returns empty and the
 * clinit blows up before any ByteBuf can be allocated. The leak tracker's track() path also
 * relies on Thread stack captures that are meaningless in a single-threaded browser.
 *
 * Leak detection is a dev-only diagnostic; hard-disable the whole thing. All entry points
 * become no-ops and track() returns null so callers skip tracking.
 */
public class ResourceLeakDetector<T> {

	public enum Level {
		DISABLED,
		SIMPLE,
		ADVANCED,
		PARANOID;
	}

	private static Level level = Level.DISABLED;

	// Read by the (jar) ResourceLeakDetectorFactory.newResourceLeakDetector as the ctor sampling arg.
	public static final int SAMPLING_INTERVAL = 128;

	public ResourceLeakDetector(Class<?> resourceType) {
	}

	public ResourceLeakDetector(Class<?> resourceType, int samplingInterval) {
	}

	@Deprecated
	public ResourceLeakDetector(Class<?> resourceType, int samplingInterval, long maxActive) {
	}

	public static void setLevel(Level newLevel) {
		if (newLevel != null) {
			level = newLevel;
		}
	}

	public static Level getLevel() {
		return level;
	}

	public static boolean isEnabled() {
		return false;
	}

	// Gate in AbstractByteBufAllocator.toLeakAwareBuffer; leak recording is off in the browser build.
	public static boolean isRecordEnabled() {
		return false;
	}

	public static void addExclusions(Class<?> clz, String... methodNames) {
		// no-op: reflection-based validation can't run under TeaVM and leak detection is disabled
	}

	public final ResourceLeakTracker<T> track(T obj) {
		return null;
	}

	@Deprecated
	public final ResourceLeak open(T obj) {
		return null;
	}
}
