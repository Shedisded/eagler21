package org.lwjgl.glfw;

import org.lwjgl.system.CustomBuffer;

/**
 * TeaVM shim for org.lwjgl.glfw.GLFWVidMode. Real LWJGL is a struct view over
 * native memory; here it is a plain immutable record of the fields MC reads.
 *
 * ponytail: no native struct — the browser has exactly one "monitor" (the
 * canvas), so a single mode with fixed 8/8/8 bits @ 60Hz is enough.
 */
public class GLFWVidMode {
	private final int width;
	private final int height;
	private final int redBits;
	private final int greenBits;
	private final int blueBits;
	private final int refreshRate;

	public GLFWVidMode(int width, int height, int redBits, int greenBits, int blueBits, int refreshRate) {
		this.width = width;
		this.height = height;
		this.redBits = redBits;
		this.greenBits = greenBits;
		this.blueBits = blueBits;
		this.refreshRate = refreshRate;
	}

	public int width() {
		return width;
	}

	public int height() {
		return height;
	}

	public int redBits() {
		return redBits;
	}

	public int greenBits() {
		return greenBits;
	}

	public int blueBits() {
		return blueBits;
	}

	public int refreshRate() {
		return refreshRate;
	}

	/**
	 * TeaVM shim for GLFWVidMode.Buffer — a one-element list of modes. MC calls
	 * glfwGetVideoModes and iterates; we only ever expose the single mode.
	 */
	public static class Buffer extends CustomBuffer {
		private final GLFWVidMode mode;

		public Buffer(GLFWVidMode mode) {
			this.mode = mode;
		}

		public int limit() {
			return 1;
		}

		// position(int) return type must be CustomBuffer to match MC's descriptor.
		@Override
		public CustomBuffer position(int newPosition) {
			this.position = newPosition;
			return this;
		}

		public int width() {
			return mode.width;
		}

		public int height() {
			return mode.height;
		}

		public int redBits() {
			return mode.redBits;
		}

		public int greenBits() {
			return mode.greenBits;
		}

		public int blueBits() {
			return mode.blueBits;
		}

		public int refreshRate() {
			return mode.refreshRate;
		}
	}
}
