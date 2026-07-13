package org.lwjgl.opengl;

import net.lax1dude.eaglercraft.v1_21.platform.webgl.GLContext;

public class GL14 {
	public static void glBlendFuncSeparate(int srcRGB, int dstRGB, int srcAlpha, int dstAlpha) {
		GLContext.gl().blendFuncSeparate(srcRGB, dstRGB, srcAlpha, dstAlpha);
	}
}
