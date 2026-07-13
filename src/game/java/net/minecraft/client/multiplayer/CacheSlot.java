package net.minecraft.client.multiplayer;

import java.util.function.Function;
import org.jspecify.annotations.Nullable;

public class CacheSlot<C extends CacheSlot.Cleaner<C>, D> {
    private final Function<C, D> operation;
    private @Nullable C context;
    private @Nullable D value;

    public CacheSlot(Function<C, D> p_399634_) {
        this.operation = p_399634_;
    }

    public D compute(C p_399903_) {
        if (p_399903_ == this.context && this.value != null) {
            return this.value;
        } else {
            D d = this.operation.apply(p_399903_);
            this.value = d;
            this.context = p_399903_;
            p_399903_.registerForCleaning(this);
            return d;
        }
    }

    public void clear() {
        this.value = null;
        this.context = null;
    }

    @FunctionalInterface
    public interface Cleaner<C extends CacheSlot.Cleaner<C>> {
        void registerForCleaning(CacheSlot<C, ?> p_400255_);
    }
}
