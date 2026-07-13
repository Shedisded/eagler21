package io.netty.util.internal.logging;

/**
 * TeaVM override of netty's logger factory: the vanilla version probes
 * log4j2/log4j/JDK logging, all unreachable under TeaVM. slf4j is the only
 * backend in this port.
 */
public abstract class InternalLoggerFactory {
	private static volatile InternalLoggerFactory defaultFactory;

	public static InternalLoggerFactory getDefaultFactory() {
		if (defaultFactory == null) {
			defaultFactory = Slf4JLoggerFactory.INSTANCE;
		}
		return defaultFactory;
	}

	public static void setDefaultFactory(InternalLoggerFactory defaultFactory) {
		if (defaultFactory == null) {
			throw new NullPointerException("defaultFactory");
		}
		InternalLoggerFactory.defaultFactory = defaultFactory;
	}

	public static InternalLogger getInstance(Class<?> clazz) {
		return getInstance(clazz.getName());
	}

	public static InternalLogger getInstance(String name) {
		return getDefaultFactory().newInstance(name);
	}

	protected abstract InternalLogger newInstance(String name);
}
