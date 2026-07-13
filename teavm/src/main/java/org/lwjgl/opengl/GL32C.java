package org.lwjgl.opengl;

import net.lax1dude.eaglercraft.v1_21.platform.webgl.GLContext;

public class GL32C {
	public static void glBeginQuery(int target, int id) {
		GLContext.gl().beginQuery(target, GLContext.CURRENT.queries.get(id));
	}

	public static void glDeleteQueries(int id) {
		GLContext.gl().deleteQuery(GLContext.CURRENT.queries.get(id));
		GLContext.CURRENT.queries.free(id);
	}

	public static void glEndQuery(int target) {
		GLContext.gl().endQuery(target);
	}

	public static int glGenQueries() {
		return GLContext.CURRENT.queries.register(GLContext.gl().createQuery());
	}

	public static int glGetQueryObjecti(int id, int pname) {
		return GLShim.jsToInt(GLContext.gl().getQueryParameter(GLContext.CURRENT.queries.get(id), pname));
	}
}
