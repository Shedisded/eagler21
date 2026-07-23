package org.lwjgl.glfw;

/**
 * Maps a browser {@code KeyboardEvent.code} (physical key, layout-independent)
 * to the standard GLFW key integer MC's precompiled bytecode expects. Returns
 * {@code GLFW_KEY_UNKNOWN} (-1) for anything unmapped.
 *
 * ponytail: the GLFW values are inlined constants in MC, so this is plain data —
 * one static switch, no map allocation. Letters/digits/numpad/F-keys collapse to
 * arithmetic since their GLFW codes are contiguous (GLFW_KEY_A == 'A' == 65 etc).
 */
final class KeyCodes {
	private KeyCodes() {
	}

	static int glfw(String c) {
		if (c == null || c.isEmpty()) {
			return -1;
		}
		int len = c.length();
		if (len == 4 && c.startsWith("Key")) {
			return c.charAt(3); // 'A'..'Z' == GLFW_KEY_A..GLFW_KEY_Z (65..90)
		}
		if (len == 6 && c.startsWith("Digit")) {
			return c.charAt(5); // '0'..'9' == GLFW_KEY_0..GLFW_KEY_9 (48..57)
		}
		if (len == 7 && c.startsWith("Numpad") && c.charAt(6) >= '0' && c.charAt(6) <= '9') {
			return 320 + (c.charAt(6) - '0'); // GLFW_KEY_KP_0..KP_9
		}
		if (c.charAt(0) == 'F' && len >= 2 && len <= 3 && Character.isDigit(c.charAt(1))) {
			int n = Integer.parseInt(c.substring(1));
			if (n >= 1 && n <= 25) {
				return 290 + (n - 1); // GLFW_KEY_F1..F25
			}
		}
		switch (c) {
			case "Space":         return 32;
			case "Quote":         return 39;  // apostrophe key
			case "Comma":         return 44;
			case "Minus":         return 45;
			case "Period":        return 46;
			case "Slash":         return 47;
			case "Semicolon":     return 59;
			case "Equal":         return 61;
			case "BracketLeft":   return 91;
			case "Backslash":     return 92;
			case "BracketRight":  return 93;
			case "Backquote":     return 96;  // grave accent
			case "Escape":        return 256;
			case "Enter":         return 257;
			case "Tab":           return 258;
			case "Backspace":     return 259;
			case "Insert":        return 260;
			case "Delete":        return 261;
			case "ArrowRight":    return 262;
			case "ArrowLeft":     return 263;
			case "ArrowDown":     return 264;
			case "ArrowUp":       return 265;
			case "PageUp":        return 266;
			case "PageDown":      return 267;
			case "Home":          return 268;
			case "End":           return 269;
			case "CapsLock":      return 280;
			case "ScrollLock":    return 281;
			case "NumLock":       return 282;
			case "PrintScreen":   return 283;
			case "Pause":         return 284;
			case "NumpadDecimal":  return 330;
			case "NumpadDivide":   return 331;
			case "NumpadMultiply": return 332;
			case "NumpadSubtract": return 333;
			case "NumpadAdd":      return 334;
			case "NumpadEnter":    return 335;
			case "NumpadEqual":    return 336;
			case "ShiftLeft":     return 340;
			case "ControlLeft":   return 341;
			case "AltLeft":       return 342;
			case "MetaLeft":
			case "OSLeft":        return 343; // left super
			case "ShiftRight":    return 344;
			case "ControlRight":  return 345;
			case "AltRight":      return 346;
			case "MetaRight":
			case "OSRight":       return 347; // right super
			case "ContextMenu":   return 348; // menu
			default:              return -1;  // GLFW_KEY_UNKNOWN
		}
	}
}
