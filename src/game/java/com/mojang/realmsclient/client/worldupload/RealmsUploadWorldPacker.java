package com.mojang.realmsclient.client.worldupload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.function.BooleanSupplier;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

public class RealmsUploadWorldPacker {
    // ponytail: Realms world-upload packing used commons-compress TarArchiveOutputStream,
    // which references FileOutputStream.getChannel() (no NIO FileChannel from a browser FOS).
    // Realms is disabled in the browser port — throw instead of packing a tarball. Keeps the
    // TarArchiveOutputStream/getChannel dependency out of the TeaVM reachable set.
    public static File pack(Path p_374057_, BooleanSupplier p_374422_) throws IOException {
        throw new UnsupportedOperationException("Realms world upload is not supported in the browser build");
    }
}
