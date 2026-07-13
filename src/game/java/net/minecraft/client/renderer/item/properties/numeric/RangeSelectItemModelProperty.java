package net.minecraft.client.renderer.item.properties.numeric;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public interface RangeSelectItemModelProperty {
    float get(ItemStack p_388695_, @Nullable ClientLevel p_388363_, @Nullable ItemOwner p_432809_, int p_386614_);

    MapCodec<? extends RangeSelectItemModelProperty> type();
}
