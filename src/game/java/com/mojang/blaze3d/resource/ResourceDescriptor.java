package com.mojang.blaze3d.resource;

public interface ResourceDescriptor<T> {
    T allocate();

    default void prepare(T p_394130_) {
    }

    void free(T p_363676_);

    default boolean canUsePhysicalResource(ResourceDescriptor<?> p_394583_) {
        return this.equals(p_394583_);
    }
}
