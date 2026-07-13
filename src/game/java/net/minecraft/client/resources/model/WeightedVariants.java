package net.minecraft.client.resources.model;

import java.util.List;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;

public class WeightedVariants implements BlockStateModel {
    private final WeightedList<BlockStateModel> list;
    private final TextureAtlasSprite particleIcon;

    public WeightedVariants(WeightedList<BlockStateModel> p_410272_) {
        this.list = p_410272_;
        BlockStateModel blockstatemodel = p_410272_.unwrap().getFirst().value();
        this.particleIcon = blockstatemodel.particleIcon();
    }

    @Override
    public TextureAtlasSprite particleIcon() {
        return this.particleIcon;
    }

    @Override
    public void collectParts(RandomSource p_409649_, List<BlockModelPart> p_410123_) {
        this.list.getRandomOrThrow(p_409649_).collectParts(p_409649_, p_410123_);
    }

    public record Unbaked(WeightedList<BlockStateModel.Unbaked> entries) implements BlockStateModel.Unbaked {
        @Override
        public BlockStateModel bake(ModelBaker p_410875_) {
            return new WeightedVariants(this.entries.map(p_409707_ -> p_409707_.bake(p_410875_)));
        }

        @Override
        public void resolveDependencies(ResolvableModel.Resolver p_409900_) {
            this.entries.unwrap().forEach(p_409719_ -> p_409719_.value().resolveDependencies(p_409900_));
        }
    }
}
