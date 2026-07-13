package net.minecraft.client.resources.model;

import net.minecraft.client.renderer.block.model.TextureSlots;

@FunctionalInterface
public interface UnbakedGeometry {
    UnbakedGeometry EMPTY = (p_405112_, p_405354_, p_405672_, p_405151_) -> QuadCollection.EMPTY;

    QuadCollection bake(TextureSlots p_405831_, ModelBaker p_405026_, ModelState p_405122_, ModelDebugName p_405635_);
}
