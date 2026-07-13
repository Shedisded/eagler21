package net.minecraft.world.level.storage.loot;

import com.mojang.serialization.Codec;
import java.util.stream.Stream;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public record LootDataType<T>(ResourceKey<Registry<T>> registryKey, Codec<T> codec, LootDataType.Validator<T> validator) {
    public static final LootDataType<LootItemCondition> PREDICATE = new LootDataType<>(
        Registries.PREDICATE, LootItemCondition.DIRECT_CODEC, createSimpleValidator()
    );
    public static final LootDataType<LootItemFunction> MODIFIER = new LootDataType<>(
        Registries.ITEM_MODIFIER, LootItemFunctions.ROOT_CODEC, createSimpleValidator()
    );
    public static final LootDataType<LootTable> TABLE = new LootDataType<>(Registries.LOOT_TABLE, LootTable.DIRECT_CODEC, createLootTableValidator());

    public void runValidation(ValidationContext p_279366_, ResourceKey<T> p_336149_, T p_279124_) {
        this.validator.run(p_279366_, p_336149_, p_279124_);
    }

    public static Stream<LootDataType<?>> values() {
        return Stream.of(PREDICATE, MODIFIER, TABLE);
    }

    private static <T extends LootContextUser> LootDataType.Validator<T> createSimpleValidator() {
        return (p_421454_, p_421455_, p_421456_) -> p_421456_.validate(p_421454_.enterElement(new ProblemReporter.RootElementPathElement(p_421455_), p_421455_));
    }

    private static LootDataType.Validator<LootTable> createLootTableValidator() {
        return (p_421457_, p_421458_, p_421459_) -> p_421459_.validate(
            p_421457_.setContextKeySet(p_421459_.getParamSet()).enterElement(new ProblemReporter.RootElementPathElement(p_421458_), p_421458_)
        );
    }

    @FunctionalInterface
    public interface Validator<T> {
        void run(ValidationContext p_279419_, ResourceKey<T> p_335916_, T p_279326_);
    }
}
