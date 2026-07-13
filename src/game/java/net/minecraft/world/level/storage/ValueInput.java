package net.minecraft.world.level.storage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.HolderLookup;

public interface ValueInput {
    <T> Optional<T> read(String p_421885_, Codec<T> p_421485_);

    @Deprecated
    <T> Optional<T> read(MapCodec<T> p_421486_);

    Optional<ValueInput> child(String p_422384_);

    ValueInput childOrEmpty(String p_421939_);

    Optional<ValueInput.ValueInputList> childrenList(String p_422428_);

    ValueInput.ValueInputList childrenListOrEmpty(String p_422350_);

    <T> Optional<ValueInput.TypedInputList<T>> list(String p_421592_, Codec<T> p_421666_);

    <T> ValueInput.TypedInputList<T> listOrEmpty(String p_422095_, Codec<T> p_421514_);

    boolean getBooleanOr(String p_421656_, boolean p_421647_);

    byte getByteOr(String p_422560_, byte p_422526_);

    int getShortOr(String p_422515_, short p_422601_);

    Optional<Integer> getInt(String p_421880_);

    int getIntOr(String p_422499_, int p_421752_);

    long getLongOr(String p_422553_, long p_422169_);

    Optional<Long> getLong(String p_421618_);

    float getFloatOr(String p_421972_, float p_422012_);

    double getDoubleOr(String p_422517_, double p_422205_);

    Optional<String> getString(String p_422005_);

    String getStringOr(String p_422315_, String p_422429_);

    Optional<int[]> getIntArray(String p_422112_);

    @Deprecated
    HolderLookup.Provider lookup();

    public interface TypedInputList<T> extends Iterable<T> {
        boolean isEmpty();

        Stream<T> stream();
    }

    public interface ValueInputList extends Iterable<ValueInput> {
        boolean isEmpty();

        Stream<ValueInput> stream();
    }
}
