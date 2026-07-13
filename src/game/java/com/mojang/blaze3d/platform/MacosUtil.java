package com.mojang.blaze3d.platform;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import net.minecraft.server.packs.resources.IoSupplier;

/**
 * ponytail: macOS Cocoa/ObjC-bridge integration removed for the browser port.
 * This port targets Linux desktop (debug) and the browser; the Cocoa
 * fullscreen/dock-icon tweaks were the only java-objc-bridge/JNA users here.
 */
public class MacosUtil {
    public static final boolean IS_MACOS = System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("mac");

    public static void exitNativeFullscreen(Window p_446195_) {
    }

    public static void clearResizableBit(Window p_446343_) {
    }

    public static void loadIcon(IoSupplier<InputStream> p_250929_) throws IOException {
    }
}
