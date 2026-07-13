package org.lwjgl.openal;

import java.util.Collections;
import java.util.List;

/** TeaVM shim: no enumerable audio devices. */
public final class ALUtil {
	private ALUtil() {
	}

	public static List<String> getStringList(long device, int type) {
		return Collections.emptyList();
	}
}
