package me.hyfe.helper.event.functional.merged;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import me.hyfe.helper.event.MergedSubscription;
import me.hyfe.helper.event.functional.ExpiryTestStage;
import me.hyfe.helper.event.functional.SubscriptionBuilder;
import me.hyfe.helper.utils.Delegates;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.*;

public interface MergedSubscriptionBuilder<T> extends SubscriptionBuilder<T> {

    static <T> MergedSubscriptionBuilder<T> newBuilder(Class<T> handledClass) {
        Objects.requireNonNull(handledClass, "handledClass");
        return new MergedSubscriptionBuilderImpl<>(TypeToken.of(handledClass));
    }

    static <T> MergedSubscriptionBuilder<T> newBuilder(TypeToken<T> type) {
        Objects.requireNonNull(type, "type");
        return new MergedSubscriptionBuilderImpl<>(type);
    }

    @SafeVarargs
    static <S extends Event> MergedSubscriptionBuilder<S> newBuilder(Class<S> superClass, Class<? extends S>... eventClasses) {
        return newBuilder(superClass, EventPriority.NORMAL, eventClasses);
    }

    @SafeVarargs
    static <S extends Event> MergedSubscriptionBuilder<S> newBuilder(Class<S> superClass, EventPriority priority, Class<? extends S>... eventClasses) {
        Objects.requireNonNull(superClass, "superClass");
        Objects.requireNonNull(eventClasses, "eventClasses");
        Objects.requireNonNull(priority, "priority");
        if (eventClasses.length < 2) {
            throw new IllegalArgumentException("merge method used for only one subclass");
        }

        MergedSubscriptionBuilderImpl<S> h = new MergedSubscriptionBuilderImpl<>(TypeToken.of(superClass));
        for (Class<? extends S> clazz : eventClasses) {
            h.bindEvent(clazz, priority, e -> e);
        }
        return h;
    }

    @Override
    default MergedSubscriptionBuilder<T> expireIf(Predicate<T> predicate) {
        return expireIf(Delegates.predicateToBiPredicateSecond(predicate), ExpiryTestStage.PRE, ExpiryTestStage.POST_HANDLE);
    }

    @Override
    default MergedSubscriptionBuilder<T> expireAfter(long duration, TimeUnit unit) {
        Objects.requireNonNull(unit, "unit");
        Preconditions.checkArgument(duration >= 1, "duration < 1");
        long expiry = Math.addExact(System.currentTimeMillis(), unit.toMillis(duration));
        return expireIf((handler, event) -> System.currentTimeMillis() > expiry, ExpiryTestStage.PRE);
    }

    @Override
    default MergedSubscriptionBuilder<T> expireAfter(long maxCalls) {
        Preconditions.checkArgument(maxCalls >= 1, "maxCalls < 1");
        return expireIf((handler, event) -> handler.getCallCounter() >= maxCalls, ExpiryTestStage.PRE, ExpiryTestStage.POST_HANDLE);
    }

    @Override
    MergedSubscriptionBuilder<T> filter(Predicate<T> predicate);

    MergedSubscriptionBuilder<T> expireIf(BiPredicate<MergedSubscription<T>, T> predicate, ExpiryTestStage... testPoints);

    <E extends Event> MergedSubscriptionBuilder<T> bindEvent(Class<E> eventClass, Function<E, T> function);

    <E extends Event> MergedSubscriptionBuilder<T> bindEvent(Class<E> eventClass, EventPriority priority, Function<E, T> function);

    MergedSubscriptionBuilder<T> exceptionConsumer(BiConsumer<Event, Throwable> consumer);

    MergedHandlerList<T> handlers();

    default MergedSubscription<T> handler(Consumer<? super T> handler) {
        return handlers().consumer(handler).register();
    }

    default MergedSubscription<T> biHandler(BiConsumer<MergedSubscription<T>, ? super T> handler) {
        return handlers().biConsumer(handler).register();
    }
}