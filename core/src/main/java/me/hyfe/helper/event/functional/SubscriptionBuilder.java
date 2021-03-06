package me.hyfe.helper.event.functional;

import me.hyfe.helper.utils.Log;

import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public interface SubscriptionBuilder<T> {

    BiConsumer<Object, Throwable> DEFAULT_EXCEPTION_CONSUMER = (event, throwable) -> {
        Log.severe("[EVENTS] Exception thrown whilst handling event: " + event.getClass().getName());
        throwable.printStackTrace();
    };

    SubscriptionBuilder<T> expireIf(Predicate<T> predicate);

    SubscriptionBuilder<T> expireAfter(long duration, TimeUnit unit);

    SubscriptionBuilder<T> expireAfter(long maxCalls);

    SubscriptionBuilder<T> filter(Predicate<T> predicate);
}