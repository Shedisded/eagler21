package net.minecraft.client.animation;

import org.joml.Vector3fc;

public record Keyframe(float timestamp, Vector3fc preTarget, Vector3fc postTarget, AnimationChannel.Interpolation interpolation) {
    public Keyframe(float p_433721_, Vector3fc p_447348_, AnimationChannel.Interpolation p_435093_) {
        this(p_433721_, p_447348_, p_447348_, p_435093_);
    }
}
