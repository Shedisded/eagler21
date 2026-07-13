package net.minecraft.client.renderer.block.model;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.util.RandomSource;

public class SingleVariant implements BlockStateModel {
    private final BlockModelPart model;

    public SingleVariant(BlockModelPart p_410307_) {
        this.model = p_410307_;
    }

    @Override
    public void collectParts(RandomSource p_410554_, List<BlockModelPart> p_410007_) {
        p_410007_.add(this.model);
    }

    @Override
    public TextureAtlasSprite particleIcon() {
        return this.model.particleIcon();
    }

    public record Unbaked(Variant variant) implements BlockStateModel.Unbaked {
        public static final Codec<SingleVariant.Unbaked> CODEC = Variant.CODEC.xmap(SingleVariant.Unbaked::new, SingleVariant.Unbaked::variant);

        @Override
        public BlockStateModel bake(ModelBaker p_410856_) {
            return new SingleVariant(this.variant.bake(p_410856_));
        }

        @Override
        public void resolveDependencies(ResolvableModel.Resolver p_410724_) {
            this.variant.resolveDependencies(p_410724_);
        }
    }
}
