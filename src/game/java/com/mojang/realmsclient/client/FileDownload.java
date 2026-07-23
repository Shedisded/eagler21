package com.mojang.realmsclient.client;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.exception.RealmsDefaultUncaughtExceptionHandler;
import com.mojang.realmsclient.gui.screens.RealmsDownloadLatestWorldScreen;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Locale;
import java.util.OptionalLong;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.CheckReturnValue;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NbtException;
import net.minecraft.nbt.ReportedNbtException;
import net.minecraft.util.Util;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.validation.ContentValidationException;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class FileDownload {
    private static final Logger LOGGER = LogUtils.getLogger();
    private volatile boolean cancelled;
    private volatile boolean finished;
    private volatile boolean error;
    private volatile boolean extracting;
    private volatile @Nullable File tempFile;
    private volatile File resourcePackPath;
    private volatile @Nullable CompletableFuture<?> pendingRequest;
    private @Nullable Thread currentThread;
    private static final String[] INVALID_FILE_NAMES = new String[]{
        "CON",
        "COM",
        "PRN",
        "AUX",
        "CLOCK$",
        "NUL",
        "COM1",
        "COM2",
        "COM3",
        "COM4",
        "COM5",
        "COM6",
        "COM7",
        "COM8",
        "COM9",
        "LPT1",
        "LPT2",
        "LPT3",
        "LPT4",
        "LPT5",
        "LPT6",
        "LPT7",
        "LPT8",
        "LPT9"
    };

    private <T> @Nullable T joinCancellableRequest(CompletableFuture<T> p_460856_) throws Throwable {
        this.pendingRequest = p_460856_;
        if (this.cancelled) {
            p_460856_.cancel(true);
            return null;
        } else {
            try {
                try {
                    return p_460856_.join();
                } catch (CompletionException completionexception) {
                    throw completionexception.getCause();
                }
            } catch (CancellationException cancellationexception) {
                return null;
            }
        }
    }

    // ponytail: Realms world download used java.net.http (HttpClient), which has no browser
    // transport. createClient/createRequest and the HTTP download paths are gutted so
    // java.net.http stays UNREACHABLE at TeaVM link time (stubbing that API cascades into
    // HttpHeaders/Flow/generics). Realms is disabled in the browser port. Non-HTTP helpers
    // (untar, folder naming) are kept but are now dead code.
    @CheckReturnValue
    public static OptionalLong contentLength(String p_86990_) {
        return OptionalLong.empty();
    }

    public void download(WorldDownload p_86983_, String p_86984_, RealmsDownloadLatestWorldScreen.DownloadStatus p_86985_, LevelStorageSource p_86986_) {
        LOGGER.warn("Realms world download is not supported in the browser build");
        this.error = true;
    }

    public void cancel() {
        if (this.tempFile != null) {
            this.tempFile.delete();
            this.tempFile = null;
        }

        this.cancelled = true;
        CompletableFuture<?> completablefuture = this.pendingRequest;
        if (completablefuture != null) {
            completablefuture.cancel(true);
        }
    }

    public boolean isFinished() {
        return this.finished;
    }

    public boolean isError() {
        return this.error;
    }

    public boolean isExtracting() {
        return this.extracting;
    }

    public static String findAvailableFolderName(String p_87002_) {
        p_87002_ = p_87002_.replaceAll("[\\./\"]", "_");

        for (String s : INVALID_FILE_NAMES) {
            if (p_87002_.equalsIgnoreCase(s)) {
                p_87002_ = "_" + p_87002_ + "_";
            }
        }

        return p_87002_;
    }

    private void untarGzipArchive(String p_86992_, @Nullable File p_86993_, LevelStorageSource p_86994_) throws IOException {
        Pattern pattern = Pattern.compile(".*-([0-9]+)$");
        int i = 1;

        for (char c0 : SharedConstants.ILLEGAL_FILE_CHARACTERS) {
            p_86992_ = p_86992_.replace(c0, '_');
        }

        if (StringUtils.isEmpty(p_86992_)) {
            p_86992_ = "Realm";
        }

        p_86992_ = findAvailableFolderName(p_86992_);

        try {
            for (LevelStorageSource.LevelDirectory levelstoragesource$leveldirectory : p_86994_.findLevelCandidates()) {
                String s1 = levelstoragesource$leveldirectory.directoryName();
                if (s1.toLowerCase(Locale.ROOT).startsWith(p_86992_.toLowerCase(Locale.ROOT))) {
                    Matcher matcher = pattern.matcher(s1);
                    if (matcher.matches()) {
                        int j = Integer.parseInt(matcher.group(1));
                        if (j > i) {
                            i = j;
                        }
                    } else {
                        i++;
                    }
                }
            }
        } catch (Exception exception1) {
            LOGGER.error("Error getting level list", (Throwable)exception1);
            this.error = true;
            return;
        }

        String s;
        if (p_86994_.isNewLevelIdAcceptable(p_86992_) && i <= 1) {
            s = p_86992_;
        } else {
            s = p_86992_ + (i == 1 ? "" : "-" + i);
            if (!p_86994_.isNewLevelIdAcceptable(s)) {
                boolean flag = false;

                while (!flag) {
                    i++;
                    s = p_86992_ + (i == 1 ? "" : "-" + i);
                    if (p_86994_.isNewLevelIdAcceptable(s)) {
                        flag = true;
                    }
                }
            }
        }

        TarArchiveInputStream tararchiveinputstream = null;
        File file1 = new File(Minecraft.getInstance().gameDirectory.getAbsolutePath(), "saves");

        try {
            file1.mkdir();
            tararchiveinputstream = new TarArchiveInputStream(new GzipCompressorInputStream(new BufferedInputStream(new FileInputStream(p_86993_))));

            for (TarArchiveEntry tararchiveentry = tararchiveinputstream.getNextTarEntry();
                tararchiveentry != null;
                tararchiveentry = tararchiveinputstream.getNextTarEntry()
            ) {
                File file2 = new File(file1, tararchiveentry.getName().replace("world", s));
                if (tararchiveentry.isDirectory()) {
                    file2.mkdirs();
                } else {
                    file2.createNewFile();

                    try (FileOutputStream fileoutputstream = new FileOutputStream(file2)) {
                        IOUtils.copy(tararchiveinputstream, fileoutputstream);
                    }
                }
            }
        } catch (Exception exception) {
            LOGGER.error("Error extracting world", (Throwable)exception);
            this.error = true;
        } finally {
            if (tararchiveinputstream != null) {
                tararchiveinputstream.close();
            }

            if (p_86993_ != null) {
                p_86993_.delete();
            }

            try (LevelStorageSource.LevelStorageAccess levelstoragesource$levelstorageaccess = p_86994_.validateAndCreateAccess(s)) {
                levelstoragesource$levelstorageaccess.renameAndDropPlayer(s);
            } catch (NbtException | ReportedNbtException | IOException ioexception) {
                LOGGER.error("Failed to modify unpacked realms level {}", s, ioexception);
            } catch (ContentValidationException contentvalidationexception) {
                LOGGER.warn("Failed to download file", (Throwable)contentvalidationexception);
            }

            this.resourcePackPath = new File(file1, s + File.separator + "resources.zip");
        }
    }

    private void finishWorldDownload(String p_460966_, File p_460827_, LevelStorageSource p_460878_, RealmsDownloadLatestWorldScreen.DownloadStatus p_461121_) {
        if (p_461121_.bytesWritten >= p_461121_.totalBytes && !this.cancelled && !this.error) {
            try {
                this.extracting = true;
                this.untarGzipArchive(p_460966_, p_460827_, p_460878_);
            } catch (IOException ioexception) {
                LOGGER.error("Error extracting archive", (Throwable)ioexception);
                this.error = true;
            }
        }
    }

    private void finishResourcePackDownload(RealmsDownloadLatestWorldScreen.DownloadStatus p_461171_, File p_461158_, WorldDownload p_460924_) {
        if (p_461171_.bytesWritten >= p_461171_.totalBytes && !this.cancelled) {
            try {
                String s = Hashing.sha1().hashBytes(Files.toByteArray(p_461158_)).toString();
                if (s.equals(p_460924_.resourcePackHash())) {
                    FileUtils.copyFile(p_461158_, this.resourcePackPath);
                    this.finished = true;
                } else {
                    LOGGER.error("Resourcepack had wrong hash (expected {}, found {}). Deleting it.", p_460924_.resourcePackHash(), s);
                    FileUtils.deleteQuietly(p_461158_);
                    this.error = true;
                }
            } catch (IOException ioexception) {
                LOGGER.error("Error copying resourcepack file: {}", ioexception.getMessage());
                this.error = true;
            }
        }
    }

    static class DownloadCountingOutputStream extends CountingOutputStream {
        private final RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus;

        public DownloadCountingOutputStream(OutputStream p_193509_, RealmsDownloadLatestWorldScreen.DownloadStatus p_461166_) {
            super(p_193509_);
            this.downloadStatus = p_461166_;
        }

        @Override
        protected void afterWrite(int p_87019_) throws IOException {
            super.afterWrite(p_87019_);
            this.downloadStatus.bytesWritten = this.getByteCount();
        }
    }
}
