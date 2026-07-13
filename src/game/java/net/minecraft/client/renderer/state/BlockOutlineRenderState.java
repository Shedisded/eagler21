package net.minecraft.client.renderer.state;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public record BlockOutlineRenderState(
    BlockPos pos,
    boolean isTranslucent,
    boolean highContrast,
    VoxelShape shape,
    @Nullable VoxelShape collisionShape,
    @Nullable VoxelShape occlusionShape,
    @Nullable VoxelShape interactionShape
) {
    public BlockOutlineRenderState(BlockPos p_451260_, boolean p_451414_, boolean p_451550_, VoxelShape p_451218_) {
        this(p_451260_, p_451414_, p_451550_, p_451218_, null, null, null);
    }
}
