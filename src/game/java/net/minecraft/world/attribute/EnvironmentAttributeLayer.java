package net.minecraft.world.attribute;

import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public sealed interface EnvironmentAttributeLayer<Value>
    permits EnvironmentAttributeLayer.Constant,
    EnvironmentAttributeLayer.TimeBased,
    EnvironmentAttributeLayer.Positional {
    @FunctionalInterface
    public non-sealed interface Constant<Value> extends EnvironmentAttributeLayer<Value> {
        Value applyConstant(Value p_467322_);
    }

    @FunctionalInterface
    public non-sealed interface Positional<Value> extends EnvironmentAttributeLayer<Value> {
        Value applyPositional(Value p_467263_, Vec3 p_467181_, @Nullable SpatialAttributeInterpolator p_469507_);
    }

    @FunctionalInterface
    public non-sealed interface TimeBased<Value> extends EnvironmentAttributeLayer<Value> {
        Value applyTimeBased(Value p_469372_, int p_467895_);
    }
}
