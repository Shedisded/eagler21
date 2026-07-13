package net.minecraft.client.renderer.chunk;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import org.jspecify.annotations.Nullable;

public class RenderSectionRegion implements BlockAndTintGetter {
    public static final int RADIUS = 1;
    public static final int SIZE = 3;
    private final int minSectionX;
    private final int minSectionY;
    private final int minSectionZ;
    private final SectionCopy[] sections;
    private final Level level;

    RenderSectionRegion(Level p_427508_, int p_427409_, int p_427310_, int p_427483_, SectionCopy[] p_427286_) {
        this.level = p_427508_;
        this.minSectionX = p_427409_;
        this.minSectionY = p_427310_;
        this.minSectionZ = p_427483_;
        this.sections = p_427286_;
    }

    @Override
    public BlockState getBlockState(BlockPos p_427463_) {
        return this.getSection(
                SectionPos.blockToSectionCoord(p_427463_.getX()),
                SectionPos.blockToSectionCoord(p_427463_.getY()),
                SectionPos.blockToSectionCoord(p_427463_.getZ())
            )
            .getBlockState(p_427463_);
    }

    @Override
    public FluidState getFluidState(BlockPos p_427285_) {
        return this.getSection(
                SectionPos.blockToSectionCoord(p_427285_.getX()),
                SectionPos.blockToSectionCoord(p_427285_.getY()),
                SectionPos.blockToSectionCoord(p_427285_.getZ())
            )
            .getBlockState(p_427285_)
            .getFluidState();
    }

    @Override
    public float getShade(Direction p_427438_, boolean p_427509_) {
        return this.level.getShade(p_427438_, p_427509_);
    }

    @Override
    public LevelLightEngine getLightEngine() {
        return this.level.getLightEngine();
    }

    @Override
    public @Nullable BlockEntity getBlockEntity(BlockPos p_427443_) {
        return this.getSection(
                SectionPos.blockToSectionCoord(p_427443_.getX()),
                SectionPos.blockToSectionCoord(p_427443_.getY()),
                SectionPos.blockToSectionCoord(p_427443_.getZ())
            )
            .getBlockEntity(p_427443_);
    }

    private SectionCopy getSection(int p_427464_, int p_427322_, int p_427454_) {
        return this.sections[index(this.minSectionX, this.minSectionY, this.minSectionZ, p_427464_, p_427322_, p_427454_)];
    }

    @Override
    public int getBlockTint(BlockPos p_427329_, ColorResolver p_427340_) {
        return this.level.getBlockTint(p_427329_, p_427340_);
    }

    @Override
    public int getMinY() {
        return this.level.getMinY();
    }

    @Override
    public int getHeight() {
        return this.level.getHeight();
    }

    public static int index(int p_427335_, int p_427442_, int p_427497_, int p_427301_, int p_427248_, int p_427503_) {
        return p_427301_ - p_427335_ + (p_427248_ - p_427442_) * 3 + (p_427503_ - p_427497_) * 3 * 3;
    }
}
