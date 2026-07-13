package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.HumanoidArm;

public interface ArmedModel<T extends EntityRenderState> {
    void translateToHand(T p_435159_, HumanoidArm p_102108_, PoseStack p_102109_);
}
