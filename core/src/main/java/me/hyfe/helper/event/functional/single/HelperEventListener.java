package me.hyfe.helper.event.functional.single;

import me.hyfe.helper.Helper;
import me.hyfe.helper.event.SingleSubscription;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

class HelperEventListener<T extends Event> implements SingleSubscription<T>, EventExecutor, Listener {
    private final Class<T> eventClass;
    private final EventPriority priority;

    private final BiConsumer<? super T, Throwable> exceptionConsumer;
    private final boolean handleSubclasses;

    private final Predicate<T>[] filters;
    private final BiPredicate<SingleSubscription<T>, T>[] preExpiryTests;
    private final BiPredicate<SingleSubscription<T>, T>[] midExpiryTests;
    private final BiPredicate<SingleSubscription<T>, T>[] postExpiryTests;
    private final BiConsumer<SingleSubscription<T>, ? super T>[] handlers;

    private final AtomicLong callCount = new AtomicLong(0);
    private final AtomicBoolean active = new AtomicBoolean(true);

    @SuppressWarnings("unchecked")
    HelperEventListener(SingleSubscriptionBuilderImpl<T> builder, List<BiConsumer<SingleSubscription<T>, ? super T>> handlers) {
        this.eventClass = builder.eventClass;
        this.priority = builder.priority;
        this.exceptionConsumer = builder.exceptionConsumer;
        this.handleSubclasses = builder.handleSubclasses;

        this.filters = builder.filters.toArray(new Predicate[builder.filters.size()]);
        this.preExpiryTests = builder.preExpiryTests.toArray(new BiPredicate[builder.preExpiryTests.size()]);
        this.midExpiryTests = builder.midExpiryTests.toArray(new BiPredicate[builder.midExpiryTests.size()]);
        this.postExpiryTests = builder.postExpiryTests.toArray(new BiPredicate[builder.postExpiryTests.size()]);
        this.handlers = handlers.toArray(new BiConsumer[handlers.size()]);
    }

    void register(Plugin plugin) {
        Helper.plugins().registerEvent(this.eventClass, this, this.priority, this, plugin, false);
    }

    @Override
    public void execute(Listener listener, Event event) {
        if (this.handleSubclasses) {
            if (!this.eventClass.isInstance(event)) {
                return;
            }
        } else {
            if (event.getClass() != this.eventClass) {
                return;
            }
        }
        if (!this.active.get()) {
            event.getHandlers().unregister(listener);
            return;
        }
        T eventInstance = this.eventClass.cast(event);
        for (BiPredicate<SingleSubscription<T>, T> test : this.preExpiryTests) {
            if (test.test(this, eventInstance)) {
                event.getHandlers().unregister(listener);
                this.active.set(false);
                return;
            }
        }
        try {
            for (Predicate<T> filter : this.filters) {
                if (!filter.test(eventInstance)) {
                    return;
                }
            }
            for (BiPredicate<SingleSubscription<T>, T> test : this.midExpiryTests) {
                if (test.test(this, eventInstance)) {
                    event.getHandlers().unregister(listener);
                    this.active.set(false);
                    return;
                }
            }
            for (BiConsumer<SingleSubscription<T>, ? super T> handler : this.handlers) {
                handler.accept(this, eventInstance);
            }
            this.callCount.incrementAndGet();
        } catch (Throwable t) {
            this.exceptionConsumer.accept(eventInstance, t);
        }
        for (BiPredicate<SingleSubscription<T>, T> test : this.postExpiryTests) {
            if (test.test(this, eventInstance)) {
                event.getHandlers().unregister(listener);
                this.active.set(false);
                return;
            }
        }
    }

    @Override
    public boolean isActive() {
        return this.active.get();
    }

    @Override
    public Class<T> getEventClass() {
        return this.eventClass;
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
        unregisterListener(this.eventClass, this);
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

    private static void unregisterListener(Class<? extends Event> eventClass, Listener listener) {
        try {
            Method getHandlerListMethod = eventClass.getMethod("getHandlerList");
            HandlerList handlerList = (HandlerList) getHandlerListMethod.invoke(null);
            handlerList.unregister(listener);
        } catch (Throwable ignore) {

        }
    }
}