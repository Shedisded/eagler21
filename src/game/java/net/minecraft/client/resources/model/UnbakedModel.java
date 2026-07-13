package net.minecraft.client.resources.model;

import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public interface UnbakedModel {
    String PARTICLE_TEXTURE_REFERENCE = "particle";

    default @Nullable Boolean ambientOcclusion() {
        return null;
    }

    default UnbakedModel.@Nullable GuiLight guiLight() {
        return null;
    }

    default @Nullable ItemTransforms transforms() {
        return null;
    }

    default TextureSlots.Data textureSlots() {
        return TextureSlots.Data.EMPTY;
    }

    default @Nullable UnbakedGeometry geometry() {
        return null;
    }

    default @Nullable Identifier parent() {
        return null;
    }

    public static enum GuiLight {
        FRONT("front"),
        SIDE("side");

        private final String name;

        private GuiLight(String p_387953_) {
            this.name = p_387953_;
        }

        public static UnbakedModel.GuiLight getByName(String p_388510_) {
            for (UnbakedModel.GuiLight unbakedmodel$guilight : values()) {
                if (unbakedmodel$guilight.name.equals(p_388510_)) {
                    return unbakedmodel$guilight;
                }
            }

            throw new IllegalArgumentException("Invalid gui light: " + p_388510_);
        }

        public boolean lightLikeBlock() {
            return this == SIDE;
        }
    }
}
