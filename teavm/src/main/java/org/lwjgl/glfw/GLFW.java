package org.lwjgl.glfw;

import java.nio.ByteBuffer;

import net.lax1dude.eaglercraft.v1_21.platform.webgl.GLContext;
import net.lax1dude.eaglercraft.v1_21.platform.webgl.NativeHeap;
import org.lwjgl.PointerBuffer;
import org.teavm.jso.dom.html.HTMLCanvasElement;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLElement;
import org.teavm.jso.webgl.WebGL2RenderingContext;
import org.teavm.jso.webgl.WebGLContextAttributes;

/**
 * TeaVM shim for org.lwjgl.glfw.GLFW. Only the methods MC 1.21.11 references
 * (see /tmp/glfw-refs.txt) are implemented, with the exact descriptors so the
 * precompiled bytecode links. Window/monitor handles are opaque fixed fakes;
 * the one real job is wiring a WebGL2 canvas context into {@link GLContext}.
 *
 * The real GLFW's GLFW_* ints are compile-time constants, so javac inlines them
 * at MC's call sites — there are no field references to satisfy here, only methods.
 *
 * ponytail: every entry point is the laziest browser-correct stub; input
 * callbacks are merely stored (public static) for a later Stage D wiring pass.
 */
public final class GLFW {
	private GLFW() {
	}

	/** Fixed fake window/monitor/context handle. Values are never dereferenced. */
	private static final long HANDLE = 1L;

	/** Live canvas + context, set by {@link #glfwCreateWindow}; read by input wiring. */
	public static HTMLCanvasElement canvas;
	public static WebGL2RenderingContext gl;

	// Stored callback delegates (public for Stage D input dispatch).
	public static GLFWErrorCallbackI errorCallback;
	public static GLFWMonitorCallbackI monitorCallback;
	public static GLFWKeyCallbackI keyCallback;
	public static GLFWCharModsCallbackI charModsCallback;
	public static GLFWCursorPosCallbackI cursorPosCallback;
	public static GLFWCursorEnterCallbackI cursorEnterCallback;
	public static GLFWMouseButtonCallbackI mouseButtonCallback;
	public static GLFWScrollCallbackI scrollCallback;
	public static GLFWDropCallbackI dropCallback;
	public static GLFWFramebufferSizeCallbackI framebufferSizeCallback;
	public static GLFWWindowCloseCallbackI windowCloseCallback;
	public static GLFWWindowFocusCallbackI windowFocusCallback;
	public static GLFWWindowIconifyCallbackI windowIconifyCallback;
	public static GLFWWindowPosCallbackI windowPosCallback;
	public static GLFWWindowSizeCallbackI windowSizeCallback;

	// ---- init / lifecycle -------------------------------------------------

	public static boolean glfwInit() {
		return true;
	}

	// Referenced directly by the patched InputConstants (raw mouse motion is
	// unavailable in the browser).
	public static final int GLFW_RAW_MOUSE_MOTION = 0x33005;

	public static boolean glfwRawMouseMotionSupported() {
		return false;
	}

	public static void glfwTerminate() {
	}

	public static void glfwDefaultWindowHints() {
	}

	public static void glfwWindowHint(int hint, int value) {
	}

	public static void glfwSwapInterval(int interval) {
	}

	public static void glfwPollEvents() {
	}

	public static void glfwWaitEventsTimeout(double timeout) {
	}

	// ---- window creation --------------------------------------------------

	public static long glfwCreateWindow(int width, int height, CharSequence title, long monitor, long share) {
		HTMLDocument doc = HTMLDocument.current();
		HTMLElement el = doc.getElementById("gameCanvas");
		if (el == null) {
			el = doc.querySelector("canvas"); // ponytail: fall back to the first canvas
		}
		if (el == null) {
			throw new IllegalStateException("port-fable: no <canvas> (expected id 'gameCanvas') to bind the GL context");
		}
		HTMLCanvasElement c = (HTMLCanvasElement) el;
		c.setWidth(width);
		c.setHeight(height);

		WebGLContextAttributes attrs = WebGLContextAttributes.create();
		attrs.setAlpha(false);
		attrs.setDepth(true);
		attrs.setAntialias(false);
		attrs.setPremultipliedAlpha(false);

		WebGL2RenderingContext ctx = (WebGL2RenderingContext) c.getContext("webgl2", attrs);
		if (ctx == null) {
			throw new IllegalStateException("port-fable: WebGL2 is not available in this browser");
		}

		canvas = c;
		gl = ctx;
		GLContext.CURRENT = new GLContext(ctx);
		return HANDLE;
	}

	public static void glfwDestroyWindow(long window) {
	}

	public static void glfwMakeContextCurrent(long window) {
	}

	public static long glfwGetCurrentContext() {
		return HANDLE;
	}

	public static void glfwSwapBuffers(long window) {
		// ponytail: the browser composites the canvas; RAF yielding lands in a later milestone.
	}

	public static boolean glfwWindowShouldClose(long window) {
		return false;
	}

	public static void glfwSetWindowTitle(long window, CharSequence title) {
		// ponytail: page <title> is owned by the shell; ignore.
	}

	public static void glfwSetWindowSizeLimits(long window, int minWidth, int minHeight, int maxWidth, int maxHeight) {
	}

	public static void glfwSetWindowIcon(long window, GLFWImage.Buffer images) {
		// ponytail: no window chrome in the browser; icons are ignored.
	}

	public static void glfwSetWindowMonitor(long window, long monitor, int xpos, int ypos, int width, int height, int refreshRate) {
	}

	// ---- framebuffer / window geometry ------------------------------------

	public static void glfwGetFramebufferSize(long window, int[] width, int[] height) {
		width[0] = canvas != null ? canvas.getWidth() : 1;
		height[0] = canvas != null ? canvas.getHeight() : 1;
	}

	public static void glfwGetWindowPos(long window, int[] xpos, int[] ypos) {
		xpos[0] = 0;
		ypos[0] = 0;
	}

	// ---- monitors ---------------------------------------------------------

	public static long glfwGetPrimaryMonitor() {
		return HANDLE;
	}

	public static long glfwGetWindowMonitor(long window) {
		return 0L;
	}

	public static PointerBuffer glfwGetMonitors() {
		// ponytail: one virtual monitor (the canvas); back one 8-byte pointer slot.
		long addr = NativeHeap.calloc(1L, 8L);
		PointerBuffer pb = new PointerBuffer(addr, 1);
		pb.put(0, HANDLE);
		return pb;
	}

	public static void glfwGetMonitorPos(long monitor, int[] xpos, int[] ypos) {
		xpos[0] = 0;
		ypos[0] = 0;
	}

	public static int glfwGetPlatform() {
		return 0;
	}

	public static GLFWVidMode glfwGetVideoMode(long monitor) {
		int w = canvas != null ? canvas.getWidth() : 1920;
		int h = canvas != null ? canvas.getHeight() : 1080;
		return new GLFWVidMode(w, h, 8, 8, 8, 60);
	}

	public static GLFWVidMode.Buffer glfwGetVideoModes(long monitor) {
		return new GLFWVidMode.Buffer(glfwGetVideoMode(monitor));
	}

	// ---- time / input state ----------------------------------------------

	public static double glfwGetTime() {
		return System.nanoTime() / 1.0e9;
	}

	public static int glfwGetKey(long window, int key) {
		return 0; // GLFW_RELEASE
	}

	public static String glfwGetKeyName(int key, int scancode) {
		return null;
	}

	public static void glfwSetInputMode(long window, int mode, int value) {
	}

	public static void glfwSetCursorPos(long window, double xpos, double ypos) {
	}

	public static void glfwSetCursor(long window, long cursor) {
	}

	public static long glfwCreateStandardCursor(int shape) {
		return 0L;
	}

	// ---- clipboard --------------------------------------------------------

	public static String glfwGetClipboardString(long window) {
		return "";
	}

	public static void glfwSetClipboardString(long window, ByteBuffer string) {
	}

	// ---- error handling ---------------------------------------------------

	public static int glfwGetError(PointerBuffer description) {
		return 0; // GLFW_NO_ERROR
	}

	public static GLFWErrorCallback glfwSetErrorCallback(GLFWErrorCallbackI cbfun) {
		errorCallback = cbfun;
		return cbfun == null ? null : GLFWErrorCallback.create(cbfun);
	}

	// ---- callback registration (store delegate, return concrete wrapper) --

	public static GLFWMonitorCallback glfwSetMonitorCallback(GLFWMonitorCallbackI cbfun) {
		monitorCallback = cbfun;
		return cbfun == null ? null : GLFWMonitorCallback.create(cbfun);
	}

	public static GLFWKeyCallback glfwSetKeyCallback(long window, GLFWKeyCallbackI cbfun) {
		keyCallback = cbfun;
		return cbfun == null ? null : GLFWKeyCallback.create(cbfun);
	}

	public static GLFWCharModsCallback glfwSetCharModsCallback(long window, GLFWCharModsCallbackI cbfun) {
		charModsCallback = cbfun;
		return cbfun == null ? null : GLFWCharModsCallback.create(cbfun);
	}

	public static GLFWCursorPosCallback glfwSetCursorPosCallback(long window, GLFWCursorPosCallbackI cbfun) {
		cursorPosCallback = cbfun;
		return cbfun == null ? null : GLFWCursorPosCallback.create(cbfun);
	}

	public static GLFWCursorEnterCallback glfwSetCursorEnterCallback(long window, GLFWCursorEnterCallbackI cbfun) {
		cursorEnterCallback = cbfun;
		return cbfun == null ? null : GLFWCursorEnterCallback.create(cbfun);
	}

	public static GLFWMouseButtonCallback glfwSetMouseButtonCallback(long window, GLFWMouseButtonCallbackI cbfun) {
		mouseButtonCallback = cbfun;
		return cbfun == null ? null : GLFWMouseButtonCallback.create(cbfun);
	}

	public static GLFWScrollCallback glfwSetScrollCallback(long window, GLFWScrollCallbackI cbfun) {
		scrollCallback = cbfun;
		return cbfun == null ? null : GLFWScrollCallback.create(cbfun);
	}

	public static GLFWDropCallback glfwSetDropCallback(long window, GLFWDropCallbackI cbfun) {
		dropCallback = cbfun;
		return cbfun == null ? null : GLFWDropCallback.create(cbfun);
	}

	public static GLFWFramebufferSizeCallback glfwSetFramebufferSizeCallback(long window, GLFWFramebufferSizeCallbackI cbfun) {
		framebufferSizeCallback = cbfun;
		return cbfun == null ? null : GLFWFramebufferSizeCallback.create(cbfun);
	}

	public static GLFWWindowCloseCallback glfwSetWindowCloseCallback(long window, GLFWWindowCloseCallbackI cbfun) {
		windowCloseCallback = cbfun;
		return cbfun == null ? null : GLFWWindowCloseCallback.create(cbfun);
	}

	public static GLFWWindowFocusCallback glfwSetWindowFocusCallback(long window, GLFWWindowFocusCallbackI cbfun) {
		windowFocusCallback = cbfun;
		return cbfun == null ? null : GLFWWindowFocusCallback.create(cbfun);
	}

	public static GLFWWindowIconifyCallback glfwSetWindowIconifyCallback(long window, GLFWWindowIconifyCallbackI cbfun) {
		windowIconifyCallback = cbfun;
		return cbfun == null ? null : GLFWWindowIconifyCallback.create(cbfun);
	}

	public static GLFWWindowPosCallback glfwSetWindowPosCallback(long window, GLFWWindowPosCallbackI cbfun) {
		windowPosCallback = cbfun;
		return cbfun == null ? null : GLFWWindowPosCallback.create(cbfun);
	}

	public static GLFWWindowSizeCallback glfwSetWindowSizeCallback(long window, GLFWWindowSizeCallbackI cbfun) {
		windowSizeCallback = cbfun;
		return cbfun == null ? null : GLFWWindowSizeCallback.create(cbfun);
	}
}
