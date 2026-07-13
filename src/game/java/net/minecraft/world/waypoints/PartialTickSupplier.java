package net.minecraft.world.waypoints;

import net.minecraft.world.entity.Entity;

@FunctionalInterface
public interface PartialTickSupplier {
    float apply(Entity p_437380_);
}
