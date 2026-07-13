package net.minecraft.client.renderer.item.properties.select;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public interface SelectItemModelProperty<T> {
    @Nullable T get(ItemStack p_387845_, @Nullable ClientLevel p_387945_, @Nullable LivingEntity p_388349_, int p_388630_, ItemDisplayContext p_388902_);

    Codec<T> valueCodec();

    SelectItemModelProperty.Type<? extends SelectItemModelProperty<T>, T> type();

    public record Type<P extends SelectItemModelProperty<T>, T>(MapCodec<SelectItemModel.UnbakedSwitch<P, T>> switchCodec) {
        public static <P extends SelectItemModelProperty<T>, T> SelectItemModelProperty.Type<P, T> create(MapCodec<P> p_386816_, Codec<T> p_387850_) {
            MapCodec<SelectItemModel.UnbakedSwitch<P, T>> mapcodec = RecordCodecBuilder.mapCodec(
                p_396319_ -> p_396319_.group(
                        p_386816_.forGetter(SelectItemModel.UnbakedSwitch::property),
                        createCasesFieldCodec(p_387850_).forGetter(SelectItemModel.UnbakedSwitch::cases)
                    )
                    .apply(p_396319_, SelectItemModel.UnbakedSwitch::new)
            );
            return new SelectItemModelProperty.Type<>(mapcodec);
        }

        public static <T> MapCodec<List<SelectItemModel.SwitchCase<T>>> createCasesFieldCodec(Codec<T> p_397527_) {
            return SelectItemModel.SwitchCase.codec(p_397527_).listOf().validate(SelectItemModelProperty.Type::validateCases).fieldOf("cases");
        }

        private static <T> DataResult<List<SelectItemModel.SwitchCase<T>>> validateCases(List<SelectItemModel.SwitchCase<T>> p_397904_) {
            if (p_397904_.isEmpty()) {
                return DataResult.error(() -> "Empty case list");
            } else {
                Multiset<T> multiset = HashMultiset.create();

                for (SelectItemModel.SwitchCase<T> switchcase : p_397904_) {
                    multiset.addAll(switchcase.values());
                }

                return multiset.size() != multiset.entrySet().size()
                    ? DataResult.error(
                        () -> "Duplicate case conditions: "
                            + multiset.entrySet()
                                .stream()
                                .filter(p_388701_ -> p_388701_.getCount() > 1)
                                .map(p_387521_ -> p_387521_.getElement().toString())
                                .collect(Collectors.joining(", "))
                    )
                    : DataResult.success(p_397904_);
            }
        }
    }
}
