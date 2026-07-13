package net.minecraft.world.phys.shapes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jspecify.annotations.Nullable;

public interface CollisionContext {
    static CollisionContext empty() {
        return EntityCollisionContext.Empty.WITHOUT_FLUID_COLLISIONS;
    }

    static CollisionContext emptyWithFluidCollisions() {
        return EntityCollisionContext.Empty.WITH_FLUID_COLLISIONS;
    }

    static CollisionContext of(Entity p_82751_) {
        return (CollisionContext)(switch (p_82751_) {
            case AbstractMinecart abstractminecart -> AbstractMinecart.useExperimentalMovement(abstractminecart.level())
                ? new MinecartCollisionContext(abstractminecart, false)
                : new EntityCollisionContext(p_82751_, false, false);
            default -> new EntityCollisionContext(p_82751_, false, false);
        });
    }

    static CollisionContext of(Entity p_363326_, boolean p_364344_) {
        return new EntityCollisionContext(p_363326_, p_364344_, false);
    }

    static CollisionContext placementContext(@Nullable Player p_422168_) {
        return new EntityCollisionContext(
            p_422168_ != null ? p_422168_.isDescending() : false,
            true,
            p_422168_ != null ? p_422168_.getY() : -Double.MAX_VALUE,
            p_422168_ instanceof LivingEntity ? p_422168_.getMainHandItem() : ItemStack.EMPTY,
            false,
            p_422168_
        );
    }

    static CollisionContext withPosition(@Nullable Entity p_405836_, double p_422096_) {
        return new EntityCollisionContext(
            p_405836_ != null ? p_405836_.isDescending() : false,
            true,
            p_405836_ != null ? p_422096_ : -Double.MAX_VALUE,
            p_405836_ instanceof LivingEntity livingentity ? livingentity.getMainHandItem() : ItemStack.EMPTY,
            false,
            p_405836_
        );
    }

    boolean isDescending();

    boolean isAbove(VoxelShape p_82755_, BlockPos p_82756_, boolean p_82757_);

    boolean isHoldingItem(Item p_82752_);

    boolean alwaysCollideWithFluid();

    boolean canStandOnFluid(FluidState p_205110_, FluidState p_205111_);

    VoxelShape getCollisionShape(BlockState p_366647_, CollisionGetter p_366783_, BlockPos p_366848_);

    default boolean isPlacement() {
        return false;
    }
}
