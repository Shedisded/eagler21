package org.lwjgl.glfw;

import java.nio.ByteBuffer;
import org.lwjgl.system.CustomBuffer;
import org.lwjgl.system.MemoryStack;

/**
 * TeaVM shim for org.lwjgl.glfw.GLFWImage. MC only builds a GLFWImage.Buffer to
 * hand window icons to glfwSetWindowIcon — which the browser ignores — so this
 * is a fields-only builder with no native struct.
 */
public class GLFWImage {
	// ponytail: window icons are a no-op in the browser; malloc just hands back a
	// buffer whose setters are discarded. MemoryStack arg is unused.
	public static Buffer malloc(int count, MemoryStack stack) {
		return new Buffer(count);
	}

	/**
	 * TeaVM shim for GLFWImage.Buffer — a builder of ignored icon images.
	 */
	public static class Buffer extends CustomBuffer {
		private int width;
		private int height;
		private ByteBuffer pixels;

		public Buffer(int count) {
		}

		public Buffer width(int value) {
			this.width = value;
			return this;
		}

		public Buffer height(int value) {
			this.height = value;
			return this;
		}

		public Buffer pixels(ByteBuffer value) {
			this.pixels = value;
			return this;
		}

		// position(int) return type must be CustomBuffer to match MC's descriptor.
		@Override
		public CustomBuffer position(int newPosition) {
			this.position = newPosition;
			return this;
		}
	}
}
