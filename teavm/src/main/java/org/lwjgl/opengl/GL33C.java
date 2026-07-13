package org.lwjgl.opengl;

import net.lax1dude.eaglercraft.v1_21.platform.webgl.GLContext;

public class GL33C {
	public static void glBindSampler(int unit, int sampler) {
		GLContext.gl().bindSampler(unit, GLContext.CURRENT.samplers.get(sampler));
	}

	public static void glDeleteSamplers(int sampler) {
		GLContext.gl().deleteSampler(GLContext.CURRENT.samplers.get(sampler));
		GLContext.CURRENT.samplers.free(sampler);
	}

	public static int glGenSamplers() {
		return GLContext.CURRENT.samplers.register(GLContext.gl().createSampler());
	}

	public static void glSamplerParameterf(int sampler, int pname, float param) {
		GLContext.gl().samplerParameterf(GLContext.CURRENT.samplers.get(sampler), pname, param);
	}

	public static void glSamplerParameteri(int sampler, int pname, int param) {
		GLContext.gl().samplerParameteri(GLContext.CURRENT.samplers.get(sampler), pname, param);
	}
}
