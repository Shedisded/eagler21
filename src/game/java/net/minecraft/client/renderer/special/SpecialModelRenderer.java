package net.minecraft.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import java.util.function.Consumer;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

public interface SpecialModelRenderer<T> {
    void submit(
        @Nullable T p_387335_,
        ItemDisplayContext p_388635_,
        PoseStack p_387508_,
        SubmitNodeCollector p_440488_,
        int p_386748_,
        int p_388858_,
        boolean p_387642_,
        int p_451675_
    );

    void getExtents(Consumer<Vector3fc> p_470829_);

    @Nullable T extractArgument(ItemStack p_387212_);

    public interface BakingContext {
        EntityModelSet entityModelSet();

        MaterialSet materials();

        PlayerSkinRenderCache playerSkinRenderCache();

        public record Simple(EntityModelSet entityModelSet, MaterialSet materials, PlayerSkinRenderCache playerSkinRenderCache)
            implements SpecialModelRenderer.BakingContext {
        }
    }

    public interface Unbaked {
        @Nullable SpecialModelRenderer<?> bake(SpecialModelRenderer.BakingContext p_433472_);

        MapCodec<? extends SpecialModelRenderer.Unbaked> type();
    }
}
