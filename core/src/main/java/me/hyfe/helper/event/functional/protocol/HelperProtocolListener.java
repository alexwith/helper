package me.hyfe.helper.event.functional.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import me.hyfe.helper.event.ProtocolSubscription;
import me.hyfe.helper.internal.LoaderUtils;
import me.hyfe.helper.protocol.Protocol;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

class HelperProtocolListener extends PacketAdapter implements ProtocolSubscription {
    private final Set<PacketType> types;

    private final BiConsumer<? super PacketEvent, Throwable> exceptionConsumer;

    private final Predicate<PacketEvent>[] filters;
    private final BiPredicate<ProtocolSubscription, PacketEvent>[] preExpiryTests;
    private final BiPredicate<ProtocolSubscription, PacketEvent>[] midExpiryTests;
    private final BiPredicate<ProtocolSubscription, PacketEvent>[] postExpiryTests;
    private final BiConsumer<ProtocolSubscription, ? super PacketEvent>[] handlers;

    private final AtomicLong callCount = new AtomicLong(0);
    private final AtomicBoolean active = new AtomicBoolean(true);

    @SuppressWarnings("unchecked")
    HelperProtocolListener(ProtocolSubscriptionBuilderImpl builder, List<BiConsumer<ProtocolSubscription, ? super PacketEvent>> handlers) {
        super(LoaderUtils.getPlugin(), builder.priority, builder.types);

        this.types = builder.types;
        this.exceptionConsumer = builder.exceptionConsumer;

        this.filters = builder.filters.toArray(new Predicate[builder.filters.size()]);
        this.preExpiryTests = builder.preExpiryTests.toArray(new BiPredicate[builder.preExpiryTests.size()]);
        this.midExpiryTests = builder.midExpiryTests.toArray(new BiPredicate[builder.midExpiryTests.size()]);
        this.postExpiryTests = builder.postExpiryTests.toArray(new BiPredicate[builder.postExpiryTests.size()]);
        this.handlers = handlers.toArray(new BiConsumer[handlers.size()]);

        Protocol.manager().addPacketListener(this);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        onPacket(event);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        onPacket(event);
    }

    private void onPacket(PacketEvent event) {
        if (!this.types.contains(event.getPacketType())) {
            return;
        }
        if (!this.active.get()) {
            return;
        }
        for (BiPredicate<ProtocolSubscription, PacketEvent> test : this.preExpiryTests) {
            if (test.test(this, event)) {
                unregister();
                return;
            }
        }
        try {
            for (Predicate<PacketEvent> filter : this.filters) {
                if (!filter.test(event)) {
                    return;
                }
            }
            for (BiPredicate<ProtocolSubscription, PacketEvent> test : this.midExpiryTests) {
                if (test.test(this, event)) {
                    unregister();
                    return;
                }
            }
            for (BiConsumer<ProtocolSubscription, ? super PacketEvent> handler : this.handlers) {
                handler.accept(this, event);
            }
            this.callCount.incrementAndGet();
        } catch (Throwable t) {
            this.exceptionConsumer.accept(event, t);
        }
        for (BiPredicate<ProtocolSubscription, PacketEvent> test : this.postExpiryTests) {
            if (test.test(this, event)) {
                unregister();
                return;
            }
        }
    }

    @Override
    public Set<PacketType> getPackets() {
        return this.types;
    }

    @Override
    public boolean isActive() {
        return this.active.get();
    }

    @Override
    public boolean isClosed() {
        return !this.active.get();
    }

    @Override
    public long getCallCounter() {
        return this.callCount.get();
    }

    @Override
    public boolean unregister() {
        if (!this.active.getAndSet(false)) {
            return false;
        }
        Protocol.manager().removePacketListener(this);
        return true;
    }

    @Override
    public Collection<Object> getFunctions() {
        List<Object> functions = new ArrayList<>();
        Collections.addAll(functions, this.filters);
        Collections.addAll(functions, this.preExpiryTests);
        Collections.addAll(functions, this.midExpiryTests);
        Collections.addAll(functions, this.postExpiryTests);
        Collections.addAll(functions, this.handlers);
        return functions;
    }
}