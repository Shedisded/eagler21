package net.minecraft.util;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.ListBuilder;
import java.util.function.UnaryOperator;

abstract class AbstractListBuilder<T, B> implements ListBuilder<T> {
    private final DynamicOps<T> ops;
    protected DataResult<B> builder = DataResult.success(this.initBuilder(), Lifecycle.stable());

    protected AbstractListBuilder(DynamicOps<T> p_412538_) {
        this.ops = p_412538_;
    }

    @Override
    public DynamicOps<T> ops() {
        return this.ops;
    }

    protected abstract B initBuilder();

    protected abstract B append(B p_412298_, T p_412204_);

    protected abstract DataResult<T> build(B p_412354_, T p_412668_);

    @Override
    public ListBuilder<T> add(T p_412702_) {
        this.builder = this.builder.map(p_412090_ -> this.append((B)p_412090_, p_412702_));
        return this;
    }

    @Override
    public ListBuilder<T> add(DataResult<T> p_412235_) {
        this.builder = this.builder.apply2stable(this::append, p_412235_);
        return this;
    }

    @Override
    public ListBuilder<T> withErrorsFrom(DataResult<?> p_412170_) {
        this.builder = this.builder.flatMap(p_412423_ -> p_412170_.map(p_412737_ -> p_412423_));
        return this;
    }

    @Override
    public ListBuilder<T> mapError(UnaryOperator<String> p_412578_) {
        this.builder = this.builder.mapError(p_412578_);
        return this;
    }

    @Override
    public DataResult<T> build(T p_412270_) {
        DataResult<T> dataresult = this.builder.flatMap(p_412422_ -> this.build((B)p_412422_, p_412270_));
        this.builder = DataResult.success(this.initBuilder(), Lifecycle.stable());
        return dataresult;
    }
}
