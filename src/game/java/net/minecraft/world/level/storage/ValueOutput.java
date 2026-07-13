package net.minecraft.world.level.storage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import org.jspecify.annotations.Nullable;

public interface ValueOutput {
    <T> void store(String p_422294_, Codec<T> p_422043_, T p_422218_);

    <T> void storeNullable(String p_421542_, Codec<T> p_422209_, @Nullable T p_422585_);

    @Deprecated
    <T> void store(MapCodec<T> p_421608_, T p_422614_);

    void putBoolean(String p_422349_, boolean p_422395_);

    void putByte(String p_422701_, byte p_421659_);

    void putShort(String p_421913_, short p_422330_);

    void putInt(String p_422187_, int p_421585_);

    void putLong(String p_421476_, long p_422717_);

    void putFloat(String p_422633_, float p_422074_);

    void putDouble(String p_422161_, double p_422544_);

    void putString(String p_421927_, String p_422134_);

    void putIntArray(String p_422132_, int[] p_422527_);

    ValueOutput child(String p_422171_);

    ValueOutput.ValueOutputList childrenList(String p_421920_);

    <T> ValueOutput.TypedOutputList<T> list(String p_421565_, Codec<T> p_422461_);

    void discard(String p_421763_);

    boolean isEmpty();

    public interface TypedOutputList<T> {
        void add(T p_421896_);

        boolean isEmpty();
    }

    public interface ValueOutputList {
        ValueOutput addChild();

        void discardLast();

        boolean isEmpty();
    }
}
