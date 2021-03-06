package me.hyfe.helper.event.functional.single;

import me.hyfe.helper.event.SingleSubscription;
import me.hyfe.helper.event.functional.FunctionalHandlerList;
import me.hyfe.helper.utils.Delegates;
import org.bukkit.event.Event;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface SingleHandlerList<T extends Event> extends FunctionalHandlerList<T, SingleSubscription<T>> {

    @Override
    default SingleHandlerList<T> consumer(Consumer<? super T> handler) {
        Objects.requireNonNull(handler, "handler");
        return biConsumer(Delegates.consumerToBiConsumerSecond(handler));
    }

    @Override
    SingleHandlerList<T> biConsumer(BiConsumer<SingleSubscription<T>, ? super T> handler);
}