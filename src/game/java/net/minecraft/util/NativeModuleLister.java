package net.minecraft.util;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import net.minecraft.CrashReportCategory;

/**
 * ponytail: JNA-based Windows DLL enumeration removed for the browser port.
 * Crash reports simply list no native modules on every platform now.
 */
public class NativeModuleLister {

    public static List<NativeModuleLister.NativeModuleInfo> listModules() {
        return ImmutableList.of();
    }

    public static void addCrashSection(CrashReportCategory p_184680_) {
        p_184680_.setDetail("Modules", () -> "<none>");
    }

    public static class NativeModuleInfo {
        public final String name;
        public final Optional<NativeModuleLister.NativeModuleVersion> version;

        public NativeModuleInfo(String p_184693_, Optional<NativeModuleLister.NativeModuleVersion> p_184694_) {
            this.name = p_184693_;
            this.version = p_184694_;
        }

        @Override
        public String toString() {
            return this.version.<String>map(p_339487_ -> this.name + ":" + p_339487_).orElse(this.name);
        }
    }

    public static class NativeModuleVersion {
        public final String description;
        public final String version;
        public final String company;

        public NativeModuleVersion(String p_184702_, String p_184703_, String p_184704_) {
            this.description = p_184702_;
            this.version = p_184703_;
            this.company = p_184704_;
        }

        @Override
        public String toString() {
            return this.description + ":" + this.version + ":" + this.company;
        }
    }
}
