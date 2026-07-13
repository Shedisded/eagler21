package net.minecraft.client.renderer.chunk;

import java.util.Collections;
import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jspecify.annotations.Nullable;

public interface SectionMesh extends AutoCloseable {
    default boolean isDifferentPointOfView(TranslucencyPointOfView p_427309_) {
        return false;
    }

    default boolean hasRenderableLayers() {
        return false;
    }

    default boolean hasTranslucentGeometry() {
        return false;
    }

    default boolean isEmpty(ChunkSectionLayer p_427270_) {
        return true;
    }

    default List<BlockEntity> getRenderableBlockEntities() {
        return Collections.emptyList();
    }

    boolean facesCanSeeEachother(Direction p_427518_, Direction p_427512_);

    default @Nullable SectionBuffers getBuffers(ChunkSectionLayer p_427238_) {
        return null;
    }

    @Override
    default void close() {
    }
}
