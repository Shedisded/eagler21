package org.lwjgl.system;

/**
 * TeaVM shim for org.lwjgl.system.CustomBuffer. In real LWJGL this is the base
 * of every struct {@code $Buffer} view over native memory. MC only references
 * it as the return type of {@code GLFW*$Buffer.position(int)}, so this minimal
 * base (a plain position cursor) is all the linker needs.
 *
 * ponytail: no native backing; the two glfw {@code $Buffer} subclasses hold
 * their data in plain fields.
 */
public abstract class CustomBuffer {
	protected int position;

	public int position() {
		return position;
	}

	public CustomBuffer position(int newPosition) {
		this.position = newPosition;
		return this;
	}
}
