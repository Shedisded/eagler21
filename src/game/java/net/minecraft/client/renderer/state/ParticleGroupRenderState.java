package net.minecraft.client.renderer.state;

import net.minecraft.client.renderer.SubmitNodeCollector;

public interface ParticleGroupRenderState {
    void submit(SubmitNodeCollector p_451317_, CameraRenderState p_451314_);

    default void clear() {
    }
}
