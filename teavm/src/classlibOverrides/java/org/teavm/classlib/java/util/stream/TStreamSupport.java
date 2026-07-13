package org.teavm.classlib.java.util.stream;

import java.util.Spliterator;
import java.util.function.IntPredicate;
import java.util.function.LongPredicate;
import java.util.function.Supplier;
import org.teavm.classlib.java.util.TSpliterator;
import org.teavm.classlib.java.util.stream.impl.TStreamOverSpliterator;
import org.teavm.classlib.java.util.stream.impl.TStreamOverSpliteratorSupplier;
import org.teavm.classlib.java.util.stream.intimpl.TSimpleIntStreamImpl;
import org.teavm.classlib.java.util.stream.longimpl.TSimpleLongStreamImpl;

public final class TStreamSupport {
    private TStreamSupport() {
    }

    public static <T> TStream<T> stream(TSpliterator<T> spliterator, boolean parallel) {
        return new TStreamOverSpliterator<>(spliterator);
    }

    public static <T> TStream<T> stream(Supplier<Spliterator<T>> spliterator, int characteristics, boolean parallel) {
        return new TStreamOverSpliteratorSupplier<>(spliterator, characteristics);
    }

    // patched: fastutil links against the primitive factories

    public static TIntStream intStream(Spliterator.OfInt spliterator, boolean parallel) {
        return new IntStreamOverSpliterator(spliterator);
    }

    public static TLongStream longStream(Spliterator.OfLong spliterator, boolean parallel) {
        return new LongStreamOverSpliterator(spliterator);
    }

    static final class IntStreamOverSpliterator extends TSimpleIntStreamImpl {
        private final Spliterator.OfInt spliterator;

        IntStreamOverSpliterator(Spliterator.OfInt spliterator) {
            this.spliterator = spliterator;
        }

        @Override
        public boolean next(IntPredicate consumer) {
            boolean[] wantsMore = { true };
            while (spliterator.tryAdvance((int value) -> wantsMore[0] = consumer.test(value))) {
                if (!wantsMore[0]) {
                    return true;
                }
            }
            return false;
        }
    }

    static final class LongStreamOverSpliterator extends TSimpleLongStreamImpl {
        private final Spliterator.OfLong spliterator;

        LongStreamOverSpliterator(Spliterator.OfLong spliterator) {
            this.spliterator = spliterator;
        }

        @Override
        public boolean next(LongPredicate consumer) {
            boolean[] wantsMore = { true };
            while (spliterator.tryAdvance((long value) -> wantsMore[0] = consumer.test(value))) {
                if (!wantsMore[0]) {
                    return true;
                }
            }
            return false;
        }
    }
}
