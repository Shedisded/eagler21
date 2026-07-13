package org.teavm.classlib.java.lang;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Minimal StackWalker: TeaVM has no stack reflection, so the frame stream is
 * always empty and the caller class is unknowable. LogUtils.getLogger() and
 * MC's crash-frame walk are the users in this port; they get an empty walk.
 */
public final class TStackWalker {
	public enum Option {
		RETAIN_CLASS_REFERENCE,
		SHOW_HIDDEN_FRAMES,
		SHOW_REFLECT_FRAMES
	}

	public interface StackFrame {
		Class<?> getDeclaringClass();

		String getFileName();

		int getLineNumber();

		String getMethodName();
	}

	private static final TStackWalker INSTANCE = new TStackWalker();

	private TStackWalker() {
	}

	public static TStackWalker getInstance() {
		return INSTANCE;
	}

	public static TStackWalker getInstance(Option option) {
		return INSTANCE;
	}

	public static TStackWalker getInstance(Set<Option> options) {
		return INSTANCE;
	}

	public static TStackWalker getInstance(Set<Option> options, int estimateDepth) {
		return INSTANCE;
	}

	public Class<?> getCallerClass() {
		return Object.class;
	}

	// ponytail: no stack reflection on TeaVM → walk an empty frame stream. Callers
	// (crash-report frame formatting) then produce empty output rather than crash.
	public <T> T walk(Function<? super Stream<StackFrame>, ? extends T> function) {
		return function.apply(Stream.<StackFrame>empty());
	}
}
