package com.mojang.realmsclient.client;

import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.dto.UploadInfo;
import com.mojang.realmsclient.gui.screens.UploadResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.minecraft.client.User;
import net.minecraft.util.LenientJsonParser;
import net.minecraft.util.Util;
import org.apache.commons.io.input.CountingInputStream;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class FileUpload implements AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int MAX_RETRIES = 5;
    private static final String UPLOAD_PATH = "/upload";
    private final File file;
    private final long realmId;
    private final int slotId;
    private final UploadInfo uploadInfo;
    private final String sessionId;
    private final String username;
    private final String clientVersion;
    private final String worldVersion;
    private final UploadStatus uploadStatus;

    public FileUpload(File p_87071_, long p_87072_, int p_87073_, UploadInfo p_87074_, User p_87075_, String p_87076_, String p_325917_, UploadStatus p_87077_) {
        this.file = p_87071_;
        this.realmId = p_87072_;
        this.slotId = p_87073_;
        this.uploadInfo = p_87074_;
        this.sessionId = p_87075_.getSessionId();
        this.username = p_87075_.getName();
        this.clientVersion = p_87076_;
        this.worldVersion = p_325917_;
        this.uploadStatus = p_87077_;
    }

    @Override
    public void close() {
        // ponytail: no HttpClient in the browser build; nothing to close.
    }

    // ponytail: Realms world upload used java.net.http (HttpClient) — no browser transport,
    // and stubbing that API cascades into HttpHeaders/Flow/BodyPublisher generics. Gut the
    // HTTP path so java.net.http stays UNREACHABLE at TeaVM link time. Realms is disabled
    // in the browser port; return a synthetic 503 so callers complete cleanly.
    public CompletableFuture<UploadResult> startUpload() {
        LOGGER.warn("Realms world upload is not supported in the browser build");
        return CompletableFuture.completedFuture(new UploadResult(503, "Realms disabled in browser port"));
    }

    static class UploadCountingInputStream extends CountingInputStream {
        private final UploadStatus uploadStatus;

        UploadCountingInputStream(InputStream p_460825_, UploadStatus p_461078_) {
            super(p_460825_);
            this.uploadStatus = p_461078_;
        }

        @Override
        protected void afterRead(int p_460665_) throws IOException {
            super.afterRead(p_460665_);
            this.uploadStatus.onWrite(this.getByteCount());
        }
    }
}
