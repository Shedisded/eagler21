package org.lwjgl.opengl;

/** TeaVM shim: inert. GL_ARB_vertex_attrib_binding is false; MC uses glVertexAttribPointer. */
public final class ARBVertexAttribBinding {
	public static void glBindVertexBuffer(int bindingIndex, int buffer, long offset, int stride) {
	}

	public static void glVertexAttribBinding(int attribIndex, int bindingIndex) {
	}

	public static void glVertexAttribFormat(int attribIndex, int size, int type, boolean normalized, int relativeOffset) {
	}

	public static void glVertexAttribIFormat(int attribIndex, int size, int type, int relativeOffset) {
	}
}
