package me.hyfe.helper.event.functional;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface FunctionalHandlerList<T, R> {

    FunctionalHandlerList<T, R> consumer(Consumer<? super T> handler);

    FunctionalHandlerList<T, R> biConsumer(BiConsumer<R, ? super T> handler);

    R register();
}