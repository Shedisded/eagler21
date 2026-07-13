package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

public interface VillagerLikeModel<T extends EntityRenderState> {
    void translateToArms(T p_434998_, PoseStack p_383014_);
}
