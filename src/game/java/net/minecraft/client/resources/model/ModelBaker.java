package net.minecraft.client.resources.model;

import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.resources.Identifier;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public interface ModelBaker {
    ResolvedModel getModel(Identifier p_468655_);

    BlockModelPart missingBlockModelPart();

    SpriteGetter sprites();

    ModelBaker.PartCache parts();

    <T> T compute(ModelBaker.SharedOperationKey<T> p_410340_);

    public interface PartCache {
        default Vector3fc vector(float p_470811_, float p_470686_, float p_470742_) {
            return this.vector(new Vector3f(p_470811_, p_470686_, p_470742_));
        }

        Vector3fc vector(Vector3fc p_470736_);
    }

    @FunctionalInterface
    public interface SharedOperationKey<T> {
        T compute(ModelBaker p_410681_);
    }
}
