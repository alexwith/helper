package me.hyfe.helper.event.functional.single;

import com.google.common.base.Preconditions;
import me.hyfe.helper.event.SingleSubscription;
import me.hyfe.helper.event.functional.ExpiryTestStage;
import me.hyfe.helper.event.functional.SubscriptionBuilder;
import me.hyfe.helper.utils.Delegates;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface SingleSubscriptionBuilder<T extends Event> extends SubscriptionBuilder<T> {

    static <T extends Event> SingleSubscriptionBuilder<T> newBuilder(Class<T> eventClass) {
        return newBuilder(eventClass, EventPriority.NORMAL);
    }

    static <T extends Event> SingleSubscriptionBuilder<T> newBuilder(Class<T> eventClass, EventPriority priority) {
        Objects.requireNonNull(eventClass, "eventClass");
        Objects.requireNonNull(priority, "priority");
        return new SingleSubscriptionBuilderImpl<>(eventClass, priority);
    }

    @Override
    default SingleSubscriptionBuilder<T> expireIf(Predicate<T> predicate) {
        return expireIf(Delegates.predicateToBiPredicateSecond(predicate), ExpiryTestStage.PRE, ExpiryTestStage.POST_HANDLE);
    }

    @Override
    default SingleSubscriptionBuilder<T> expireAfter(long duration, TimeUnit unit) {
        Objects.requireNonNull(unit, "unit");
        Preconditions.checkArgument(duration >= 1, "duration < 1");
        long expiry = Math.addExact(System.currentTimeMillis(), unit.toMillis(duration));
        return expireIf((handler, event) -> System.currentTimeMillis() > expiry, ExpiryTestStage.PRE);
    }

    @Override
    default SingleSubscriptionBuilder<T> expireAfter(long maxCalls) {
        Preconditions.checkArgument(maxCalls >= 1, "maxCalls < 1");
        return expireIf((handler, event) -> handler.getCallCounter() >= maxCalls, ExpiryTestStage.PRE, ExpiryTestStage.POST_HANDLE);
    }

    @Override
    SingleSubscriptionBuilder<T> filter(Predicate<T> predicate);

    SingleSubscriptionBuilder<T> expireIf(BiPredicate<SingleSubscription<T>, T> predicate, ExpiryTestStage... testPoints);

    SingleSubscriptionBuilder<T> exceptionConsumer(BiConsumer<? super T, Throwable> consumer);

    SingleSubscriptionBuilder<T> handleSubclasses();

    SingleHandlerList<T> handlers();

    default SingleSubscription<T> handler(Consumer<? super T> handler) {
        return handlers().consumer(handler).register();
    }

    default SingleSubscription<T> biHandler(BiConsumer<SingleSubscription<T>, ? super T> handler) {
        return handlers().biConsumer(handler).register();
    }
}