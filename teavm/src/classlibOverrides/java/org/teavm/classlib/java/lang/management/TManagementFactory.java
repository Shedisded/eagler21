package org.teavm.classlib.java.lang.management;

import java.util.ArrayList;
import java.util.List;

/**
 * Link stub for java.lang.management.ManagementFactory. getPlatformMBeanServer()
 * is deliberately absent — its only caller (MinecraftServerStatistics, dedicated-
 * server JMX) is shadowed to a no-op, which keeps the whole javax.management
 * surface out of the browser link.
 */
public final class TManagementFactory {
	private static final long START = System.currentTimeMillis();

	private TManagementFactory() {
	}

	public static TRuntimeMXBean getRuntimeMXBean() {
		return () -> System.currentTimeMillis() - START;
	}

	public static TThreadMXBean getThreadMXBean() {
		return (lockedMonitors, lockedSynchronizers) -> new TThreadInfo[0];
	}

	public static List<TGarbageCollectorMXBean> getGarbageCollectorMXBeans() {
		return new ArrayList<>();
	}
}
