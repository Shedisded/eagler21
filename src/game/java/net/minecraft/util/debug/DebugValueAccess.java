package net.minecraft.util.debug;

import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import org.jspecify.annotations.Nullable;

public interface DebugValueAccess {
    <T> void forEachChunk(DebugSubscription<T> p_449042_, BiConsumer<ChunkPos, T> p_449283_);

    <T> @Nullable T getChunkValue(DebugSubscription<T> p_449231_, ChunkPos p_449428_);

    <T> void forEachBlock(DebugSubscription<T> p_449190_, BiConsumer<BlockPos, T> p_449391_);

    <T> @Nullable T getBlockValue(DebugSubscription<T> p_449891_, BlockPos p_449563_);

    <T> void forEachEntity(DebugSubscription<T> p_449907_, BiConsumer<Entity, T> p_449055_);

    <T> @Nullable T getEntityValue(DebugSubscription<T> p_449720_, Entity p_449780_);

    <T> void forEachEvent(DebugSubscription<T> p_449441_, DebugValueAccess.EventVisitor<T> p_449759_);

    @FunctionalInterface
    public interface EventVisitor<T> {
        void accept(T p_449386_, int p_449777_, int p_449013_);
    }
}
