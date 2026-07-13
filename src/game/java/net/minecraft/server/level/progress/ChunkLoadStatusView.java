package net.minecraft.server.level.progress;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.jspecify.annotations.Nullable;

public interface ChunkLoadStatusView {
    void moveTo(ResourceKey<Level> p_433845_, ChunkPos p_435642_);

    @Nullable ChunkStatus get(int p_434162_, int p_435494_);

    int radius();
}
