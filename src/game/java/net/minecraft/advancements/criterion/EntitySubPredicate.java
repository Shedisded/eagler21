package net.minecraft.advancements.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public interface EntitySubPredicate {
    Codec<EntitySubPredicate> CODEC = BuiltInRegistries.ENTITY_SUB_PREDICATE_TYPE.byNameCodec().dispatch(EntitySubPredicate::codec, Function.identity());

    MapCodec<? extends EntitySubPredicate> codec();

    boolean matches(Entity p_469385_, ServerLevel p_469777_, @Nullable Vec3 p_468681_);
}
