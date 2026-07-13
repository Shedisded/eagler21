package net.lax1dude.eaglercraft.v1_21.boot;

/**
 * Stage C entry point: boot the full Minecraft client through its normal
 * {@code net.minecraft.client.main.Main} path with synthetic launch args. This
 * pulls the entire render/window/input stack into the reachable set so the
 * TeaVM linker exercises the org.lwjgl WebGL/GLFW shims.
 *
 * The desktop launcher (runClient) passes equivalent args; here they are fixed
 * for the offline browser build.
 */
public class EaglerClientMain {

	public static void main(String[] args) {
		// slf4j must cache the real System.err before Bootstrap.wrapStreams()
		// redirects it (see EaglerBootMain / milestone 1).
		System.setProperty("org.slf4j.simpleLogger.cacheOutputStream", "true");

		String[] launchArgs = {
			"--version", "1.21.11-port",
			"--gameDir", "/game",
			"--assetsDir", "/game/assets",
			"--assetIndex", "29",
			"--username", "Player",
			"--accessToken", "0",
			"--userType", "legacy",
			"--uuid", "00000000000000000000000000000000",
		};

		net.minecraft.client.main.Main.main(launchArgs);
	}
}
