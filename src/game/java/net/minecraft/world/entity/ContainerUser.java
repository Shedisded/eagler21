package net.minecraft.world.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;

public interface ContainerUser {
    boolean hasContainerOpen(ContainerOpenersCounter p_435084_, BlockPos p_435116_);

    double getContainerInteractionRange();

    default LivingEntity getLivingEntity() {
        if (this instanceof LivingEntity) {
            return (LivingEntity)this;
        } else {
            throw new IllegalStateException("A container user must be a LivingEntity");
        }
    }
}
