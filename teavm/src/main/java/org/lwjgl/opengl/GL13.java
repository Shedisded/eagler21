package org.lwjgl.opengl;

import net.lax1dude.eaglercraft.v1_21.platform.webgl.GLContext;

public class GL13 {
	public static void glActiveTexture(int texture) {
		GLContext.gl().activeTexture(texture);
	}
}
