package net.minecraft.util.monitoring.jmx;

import net.minecraft.server.MinecraftServer;

/**
 * Browser shadow of MC's JMX statistics bean. The real class implements
 * javax.management.DynamicMBean and drags the entire javax.management API into
 * the link; JMX monitoring is a dedicated-server-only feature that never runs in
 * the browser. Reducing it to a no-op registerJmxMonitoring() removes MBeanServer,
 * ObjectName, Attribute, MBeanInfo, ... from the reachable set in one stroke.
 */
public final class MinecraftServerStatistics {
	private MinecraftServerStatistics() {
	}

	// ponytail: JMX is server-only; no-op. If a browser-side integrated server ever
	// wants live stats, expose them over the debug overlay, not JMX.
	public static void registerJmxMonitoring(MinecraftServer server) {
	}
}
