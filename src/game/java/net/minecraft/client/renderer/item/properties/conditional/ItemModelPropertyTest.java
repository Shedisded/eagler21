package net.minecraft.client.renderer.item.properties.conditional;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface ItemModelPropertyTest {
    boolean get(ItemStack p_399831_, @Nullable ClientLevel p_399506_, @Nullable LivingEntity p_399675_, int p_400089_, ItemDisplayContext p_400225_);
}
