package me.hyfe.helper.event.functional.merged;

import me.hyfe.helper.event.MergedSubscription;
import me.hyfe.helper.event.functional.FunctionalHandlerList;
import me.hyfe.helper.utils.Delegates;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface MergedHandlerList<T> extends FunctionalHandlerList<T, MergedSubscription<T>> {

    @Override
    default MergedHandlerList<T> consumer( Consumer<? super T> handler) {
        Objects.requireNonNull(handler, "handler");
        return biConsumer(Delegates.consumerToBiConsumerSecond(handler));
    }

    @Override
    MergedHandlerList<T> biConsumer( BiConsumer<MergedSubscription<T>, ? super T> handler);
}