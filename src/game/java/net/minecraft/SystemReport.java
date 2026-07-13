package net.minecraft;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.slf4j.Logger;

/**
 * ponytail: hardware/JVM probing (oshi, ManagementFactory, FileStore) stripped
 * for the browser port — none of it exists under TeaVM and it only fed crash
 * report details. Restore from vanilla if desktop diagnostics are ever needed.
 */
public class SystemReport {
    public static final long BYTES_PER_MEBIBYTE = 1048576L;
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String OPERATING_SYSTEM = System.getProperty("os.name")
        + " ("
        + System.getProperty("os.arch")
        + ") version "
        + System.getProperty("os.version");
    private static final String JAVA_VERSION = System.getProperty("java.version") + ", " + System.getProperty("java.vendor");
    private final Map<String, String> entries = Maps.newLinkedHashMap();

    public SystemReport() {
        this.setDetail("Minecraft Version", SharedConstants.getCurrentVersion().name());
        this.setDetail("Minecraft Version ID", SharedConstants.getCurrentVersion().id());
        this.setDetail("Operating System", OPERATING_SYSTEM);
        this.setDetail("Java Version", JAVA_VERSION);
        this.setDetail("CPUs", () -> String.valueOf(Runtime.getRuntime().availableProcessors()));
    }

    public void setDetail(String p_143520_, String p_143521_) {
        this.entries.put(p_143520_, p_143521_);
    }

    public void setDetail(String p_143523_, Supplier<String> p_143524_) {
        try {
            this.setDetail(p_143523_, p_143524_.get());
        } catch (Exception exception) {
            LOGGER.warn("Failed to get system info for {}", p_143523_, exception);
            this.setDetail(p_143523_, "ERR");
        }
    }

    public static float sizeInMiB(long p_345366_) {
        return (float)p_345366_ / 1048576.0F;
    }

    public void appendToCrashReportString(StringBuilder p_143526_) {
        p_143526_.append("-- ").append("System Details").append(" --\n");
        p_143526_.append("Details:");
        this.entries.forEach((p_143529_, p_143530_) -> {
            p_143526_.append("\n\t");
            p_143526_.append(p_143529_);
            p_143526_.append(": ");
            p_143526_.append(p_143530_);
        });
    }

    public String toLineSeparatedString() {
        return this.entries
            .entrySet()
            .stream()
            .map(p_143534_ -> p_143534_.getKey() + ": " + p_143534_.getValue())
            .collect(Collectors.joining(System.lineSeparator()));
    }
}
