package org.lwjgl.util.tinyfd;

/** TeaVM shim: no native dialogs in the browser. */
public final class TinyFileDialogs {
	private TinyFileDialogs() {
	}

	public static boolean tinyfd_messageBox(CharSequence title, CharSequence message, CharSequence dialogType,
			CharSequence iconType, boolean defaultButton) {
		return false;
	}
}
