package net.minecraft.core.component;

import org.jspecify.annotations.Nullable;

public interface DataComponentGetter {
    <T> @Nullable T get(DataComponentType<? extends T> p_397310_);

    default <T> T getOrDefault(DataComponentType<? extends T> p_397089_, T p_397491_) {
        T t = this.get(p_397089_);
        return t != null ? t : p_397491_;
    }

    default <T> @Nullable TypedDataComponent<T> getTyped(DataComponentType<T> p_397753_) {
        T t = this.get(p_397753_);
        return t != null ? new TypedDataComponent<>(p_397753_, t) : null;
    }
}
