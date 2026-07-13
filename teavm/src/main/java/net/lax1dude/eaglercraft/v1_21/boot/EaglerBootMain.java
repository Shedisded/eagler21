package net.lax1dude.eaglercraft.v1_21.boot;

import net.minecraft.SharedConstants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;

/**
 * Milestone 1 entry point: boot the core data layer (registries, blocks,
 * items, codecs) under TeaVM. No rendering, input, audio or networking —
 * those migrate onto the Eaglercraft platform layer in later milestones.
 */
public class EaglerBootMain {

	public static void main(String[] args) {
		// Bootstrap.wrapStreams() redirects System.err into slf4j; without a
		// cached target slf4j-simple writes back to System.err -> infinite
		// recursion. Cache the original stream before the first log line.
		System.setProperty("org.slf4j.simpleLogger.cacheOutputStream", "true");
		SharedConstants.tryDetectVersion();
		Bootstrap.bootStrap();
		System.out.println("EaglerBoot: " + BuiltInRegistries.BLOCK.size() + " blocks, "
				+ BuiltInRegistries.ITEM.size() + " items registered");
	}
}
