package net.minecraft.resources;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.ListBuilder;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public abstract class DelegatingOps<T> implements DynamicOps<T> {
    protected final DynamicOps<T> delegate;

    protected DelegatingOps(DynamicOps<T> p_135467_) {
        this.delegate = p_135467_;
    }

    @Override
    public T empty() {
        return this.delegate.empty();
    }

    @Override
    public T emptyMap() {
        return this.delegate.emptyMap();
    }

    @Override
    public T emptyList() {
        return this.delegate.emptyList();
    }

    @Override
    public <U> U convertTo(DynamicOps<U> p_135470_, T p_135471_) {
        return (U)(Objects.equals(p_135470_, this.delegate) ? p_135471_ : this.delegate.convertTo(p_135470_, p_135471_));
    }

    @Override
    public DataResult<Number> getNumberValue(T p_135518_) {
        return this.delegate.getNumberValue(p_135518_);
    }

    @Override
    public T createNumeric(Number p_135495_) {
        return this.delegate.createNumeric(p_135495_);
    }

    @Override
    public T createByte(byte p_135475_) {
        return this.delegate.createByte(p_135475_);
    }

    @Override
    public T createShort(short p_135497_) {
        return this.delegate.createShort(p_135497_);
    }

    @Override
    public T createInt(int p_135483_) {
        return this.delegate.createInt(p_135483_);
    }

    @Override
    public T createLong(long p_135489_) {
        return this.delegate.createLong(p_135489_);
    }

    @Override
    public T createFloat(float p_135481_) {
        return this.delegate.createFloat(p_135481_);
    }

    @Override
    public T createDouble(double p_135479_) {
        return this.delegate.createDouble(p_135479_);
    }

    @Override
    public DataResult<Boolean> getBooleanValue(T p_135502_) {
        return this.delegate.getBooleanValue(p_135502_);
    }

    @Override
    public T createBoolean(boolean p_135473_) {
        return this.delegate.createBoolean(p_135473_);
    }

    @Override
    public DataResult<String> getStringValue(T p_135522_) {
        return this.delegate.getStringValue(p_135522_);
    }

    @Override
    public T createString(String p_135499_) {
        return this.delegate.createString(p_135499_);
    }

    @Override
    public DataResult<T> mergeToList(T p_135526_, T p_135527_) {
        return this.delegate.mergeToList(p_135526_, p_135527_);
    }

    @Override
    public DataResult<T> mergeToList(T p_135529_, List<T> p_135530_) {
        return this.delegate.mergeToList(p_135529_, p_135530_);
    }

    @Override
    public DataResult<T> mergeToMap(T p_135535_, T p_135536_, T p_135537_) {
        return this.delegate.mergeToMap(p_135535_, p_135536_, p_135537_);
    }

    @Override
    public DataResult<T> mergeToMap(T p_135532_, MapLike<T> p_135533_) {
        return this.delegate.mergeToMap(p_135532_, p_135533_);
    }

    @Override
    public DataResult<T> mergeToMap(T p_341903_, Map<T, T> p_341909_) {
        return this.delegate.mergeToMap(p_341903_, p_341909_);
    }

    @Override
    public DataResult<T> mergeToPrimitive(T p_341890_, T p_341887_) {
        return this.delegate.mergeToPrimitive(p_341890_, p_341887_);
    }

    @Override
    public DataResult<Stream<Pair<T, T>>> getMapValues(T p_135516_) {
        return this.delegate.getMapValues(p_135516_);
    }

    @Override
    public DataResult<Consumer<BiConsumer<T, T>>> getMapEntries(T p_135514_) {
        return this.delegate.getMapEntries(p_135514_);
    }

    @Override
    public T createMap(Map<T, T> p_341927_) {
        return this.delegate.createMap(p_341927_);
    }

    @Override
    public T createMap(Stream<Pair<T, T>> p_135493_) {
        return this.delegate.createMap(p_135493_);
    }

    @Override
    public DataResult<MapLike<T>> getMap(T p_135512_) {
        return this.delegate.getMap(p_135512_);
    }

    @Override
    public DataResult<Stream<T>> getStream(T p_135520_) {
        return this.delegate.getStream(p_135520_);
    }

    @Override
    public DataResult<Consumer<Consumer<T>>> getList(T p_135508_) {
        return this.delegate.getList(p_135508_);
    }

    @Override
    public T createList(Stream<T> p_135487_) {
        return this.delegate.createList(p_135487_);
    }

    @Override
    public DataResult<ByteBuffer> getByteBuffer(T p_135504_) {
        return this.delegate.getByteBuffer(p_135504_);
    }

    @Override
    public T createByteList(ByteBuffer p_135477_) {
        return this.delegate.createByteList(p_135477_);
    }

    @Override
    public DataResult<IntStream> getIntStream(T p_135506_) {
        return this.delegate.getIntStream(p_135506_);
    }

    @Override
    public T createIntList(IntStream p_135485_) {
        return this.delegate.createIntList(p_135485_);
    }

    @Override
    public DataResult<LongStream> getLongStream(T p_135510_) {
        return this.delegate.getLongStream(p_135510_);
    }

    @Override
    public T createLongList(LongStream p_135491_) {
        return this.delegate.createLongList(p_135491_);
    }

    @Override
    public T remove(T p_135539_, String p_135540_) {
        return this.delegate.remove(p_135539_, p_135540_);
    }

    @Override
    public boolean compressMaps() {
        return this.delegate.compressMaps();
    }

    @Override
    public ListBuilder<T> listBuilder() {
        return new DelegatingOps.DelegateListBuilder(this.delegate.listBuilder());
    }

    @Override
    public RecordBuilder<T> mapBuilder() {
        return new DelegatingOps.DelegateRecordBuilder(this.delegate.mapBuilder());
    }

    protected class DelegateListBuilder implements ListBuilder<T> {
        private final ListBuilder<T> original;

        protected DelegateListBuilder(ListBuilder<T> p_412307_) {
            this.original = p_412307_;
        }

        @Override
        public DynamicOps<T> ops() {
            return DelegatingOps.this;
        }

        @Override
        public DataResult<T> build(T p_412237_) {
            return this.original.build(p_412237_);
        }

        @Override
        public ListBuilder<T> add(T p_412532_) {
            this.original.add(p_412532_);
            return this;
        }

        @Override
        public ListBuilder<T> add(DataResult<T> p_412378_) {
            this.original.add(p_412378_);
            return this;
        }

        @Override
        public <E> ListBuilder<T> add(E p_412579_, Encoder<E> p_412566_) {
            this.original.add(p_412566_.encodeStart(this.ops(), p_412579_));
            return this;
        }

        @Override
        public <E> ListBuilder<T> addAll(Iterable<E> p_412167_, Encoder<E> p_412458_) {
            p_412167_.forEach(p_412442_ -> this.original.add(p_412458_.encode((E)p_412442_, this.ops(), (T)this.ops().empty())));
            return this;
        }

        @Override
        public ListBuilder<T> withErrorsFrom(DataResult<?> p_412058_) {
            this.original.withErrorsFrom(p_412058_);
            return this;
        }

        @Override
        public ListBuilder<T> mapError(UnaryOperator<String> p_412382_) {
            this.original.mapError(p_412382_);
            return this;
        }

        @Override
        public DataResult<T> build(DataResult<T> p_412048_) {
            return this.original.build(p_412048_);
        }
    }

    protected class DelegateRecordBuilder implements RecordBuilder<T> {
        private final RecordBuilder<T> original;

        protected DelegateRecordBuilder(RecordBuilder<T> p_412697_) {
            this.original = p_412697_;
        }

        @Override
        public DynamicOps<T> ops() {
            return DelegatingOps.this;
        }

        @Override
        public RecordBuilder<T> add(T p_412088_, T p_412147_) {
            this.original.add(p_412088_, p_412147_);
            return this;
        }

        @Override
        public RecordBuilder<T> add(T p_412590_, DataResult<T> p_412110_) {
            this.original.add(p_412590_, p_412110_);
            return this;
        }

        @Override
        public RecordBuilder<T> add(DataResult<T> p_412341_, DataResult<T> p_412398_) {
            this.original.add(p_412341_, p_412398_);
            return this;
        }

        @Override
        public RecordBuilder<T> add(String p_412210_, T p_412450_) {
            this.original.add(p_412210_, p_412450_);
            return this;
        }

        @Override
        public RecordBuilder<T> add(String p_412601_, DataResult<T> p_412079_) {
            this.original.add(p_412601_, p_412079_);
            return this;
        }

        @Override
        public <E> RecordBuilder<T> add(String p_412459_, E p_412139_, Encoder<E> p_412219_) {
            return this.original.add(p_412459_, p_412219_.encodeStart(this.ops(), p_412139_));
        }

        @Override
        public RecordBuilder<T> withErrorsFrom(DataResult<?> p_412634_) {
            this.original.withErrorsFrom(p_412634_);
            return this;
        }

        @Override
        public RecordBuilder<T> setLifecycle(Lifecycle p_412125_) {
            this.original.setLifecycle(p_412125_);
            return this;
        }

        @Override
        public RecordBuilder<T> mapError(UnaryOperator<String> p_412209_) {
            this.original.mapError(p_412209_);
            return this;
        }

        @Override
        public DataResult<T> build(T p_412377_) {
            return this.original.build(p_412377_);
        }

        @Override
        public DataResult<T> build(DataResult<T> p_412414_) {
            return this.original.build(p_412414_);
        }
    }
}
