package org.lwjgl.opengl;

import net.lax1dude.eaglercraft.v1_21.platform.webgl.GLContext;

public class GL11C {
	public static void glBindTexture(int target, int texture) {
		GLContext.gl().bindTexture(target, GLContext.CURRENT.textures.get(texture));
	}
}
