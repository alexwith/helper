package me.hyfe.helper.event.functional.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketEvent;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import me.hyfe.helper.event.ProtocolSubscription;
import me.hyfe.helper.event.functional.ExpiryTestStage;
import me.hyfe.helper.event.functional.SubscriptionBuilder;
import me.hyfe.helper.utils.Delegates;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface ProtocolSubscriptionBuilder extends SubscriptionBuilder<PacketEvent> {

    static ProtocolSubscriptionBuilder newBuilder(PacketType... packets) {
        return newBuilder(ListenerPriority.NORMAL, packets);
    }

    static ProtocolSubscriptionBuilder newBuilder(ListenerPriority priority, PacketType... packets) {
        Objects.requireNonNull(priority, "priority");
        Objects.requireNonNull(packets, "packets");
        return new ProtocolSubscriptionBuilderImpl(ImmutableSet.copyOf(packets), priority);
    }

    @Override
    default ProtocolSubscriptionBuilder expireIf(Predicate<PacketEvent> predicate) {
        return expireIf(Delegates.predicateToBiPredicateSecond(predicate), ExpiryTestStage.PRE, ExpiryTestStage.POST_HANDLE);
    }

    @Override
    default ProtocolSubscriptionBuilder expireAfter(long duration, TimeUnit unit) {
        Objects.requireNonNull(unit, "unit");
        Preconditions.checkArgument(duration >= 1, "duration < 1");
        long expiry = Math.addExact(System.currentTimeMillis(), unit.toMillis(duration));
        return expireIf((handler, event) -> System.currentTimeMillis() > expiry, ExpiryTestStage.PRE);
    }

    @Override
    default ProtocolSubscriptionBuilder expireAfter(long maxCalls) {
        Preconditions.checkArgument(maxCalls >= 1, "maxCalls < 1");
        return expireIf((handler, event) -> handler.getCallCounter() >= maxCalls, ExpiryTestStage.PRE, ExpiryTestStage.POST_HANDLE);
    }

    @Override
    ProtocolSubscriptionBuilder filter(Predicate<PacketEvent> predicate);

    ProtocolSubscriptionBuilder expireIf(BiPredicate<ProtocolSubscription, PacketEvent> predicate, ExpiryTestStage... testPoints);

    ProtocolSubscriptionBuilder exceptionConsumer(BiConsumer<? super PacketEvent, Throwable> consumer);

    ProtocolHandlerList handlers();

    default ProtocolSubscription handler(Consumer<? super PacketEvent> handler) {
        return handlers().consumer(handler).register();
    }

    default ProtocolSubscription biHandler(BiConsumer<ProtocolSubscription, ? super PacketEvent> handler) {
        return handlers().biConsumer(handler).register();
    }
}