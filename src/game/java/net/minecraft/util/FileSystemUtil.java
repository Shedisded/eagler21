package net.minecraft.util;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import org.slf4j.Logger;

public class FileSystemUtil {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static Path safeGetPath(URI p_397564_) throws IOException {
        try {
            return Paths.get(p_397564_);
        } catch (FileSystemNotFoundException filesystemnotfoundexception) {
        } catch (Throwable throwable) {
            LOGGER.warn("Unable to get path for: {}", p_397564_, throwable);
        }

        try {
            FileSystems.newFileSystem(p_397564_, Collections.emptyMap());
        } catch (FileSystemAlreadyExistsException filesystemalreadyexistsexception) {
        }

        return Paths.get(p_397564_);
    }
}
