package net.minecraft.client.resources.model;

import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public interface SpriteGetter {
    TextureAtlasSprite get(Material p_387809_, ModelDebugName p_404904_);

    TextureAtlasSprite reportMissingReference(String p_387031_, ModelDebugName p_405621_);

    default TextureAtlasSprite resolveSlot(TextureSlots p_404867_, String p_405322_, ModelDebugName p_405457_) {
        Material material = p_404867_.getMaterial(p_405322_);
        return material != null ? this.get(material, p_405457_) : this.reportMissingReference(p_405322_, p_405457_);
    }
}
