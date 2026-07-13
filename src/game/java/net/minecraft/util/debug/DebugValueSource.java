package net.minecraft.util.debug;

import net.minecraft.server.level.ServerLevel;
import org.jspecify.annotations.Nullable;

public interface DebugValueSource {
    void registerDebugValues(ServerLevel p_449706_, DebugValueSource.Registration p_449064_);

    public interface Registration {
        <T> void register(DebugSubscription<T> p_449255_, DebugValueSource.ValueGetter<T> p_449796_);
    }

    public interface ValueGetter<T> {
        @Nullable T get();
    }
}
