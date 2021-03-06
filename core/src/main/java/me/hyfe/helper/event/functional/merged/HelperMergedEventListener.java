package me.hyfe.helper.event.functional.merged;

import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import me.hyfe.helper.Helper;
import me.hyfe.helper.event.MergedSubscription;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

class HelperMergedEventListener<T> implements MergedSubscription<T>, EventExecutor, Listener {
    private final TypeToken<T> handledClass;
    private final Map<Class<? extends Event>, MergedHandlerMapping<T, ? extends Event>> mappings;

    private final BiConsumer<? super Event, Throwable> exceptionConsumer;

    private final Predicate<T>[] filters;
    private final BiPredicate<MergedSubscription<T>, T>[] preExpiryTests;
    private final BiPredicate<MergedSubscription<T>, T>[] midExpiryTests;
    private final BiPredicate<MergedSubscription<T>, T>[] postExpiryTests;
    private final BiConsumer<MergedSubscription<T>, ? super T>[] handlers;

    private final AtomicLong callCount = new AtomicLong(0);
    private final AtomicBoolean active = new AtomicBoolean(true);

    @SuppressWarnings("unchecked")
    HelperMergedEventListener(MergedSubscriptionBuilderImpl<T> builder, List<BiConsumer<MergedSubscription<T>, ? super T>> handlers) {
        this.handledClass = builder.handledClass;
        this.mappings = ImmutableMap.copyOf(builder.mappings);
        this.exceptionConsumer = builder.exceptionConsumer;

        this.filters = builder.filters.toArray(new Predicate[builder.filters.size()]);
        this.preExpiryTests = builder.preExpiryTests.toArray(new BiPredicate[builder.preExpiryTests.size()]);
        this.midExpiryTests = builder.midExpiryTests.toArray(new BiPredicate[builder.midExpiryTests.size()]);
        this.postExpiryTests = builder.postExpiryTests.toArray(new BiPredicate[builder.postExpiryTests.size()]);
        this.handlers = handlers.toArray(new BiConsumer[handlers.size()]);
    }

    void register(Plugin plugin) {
        Map<Class<?>, EventPriority> registered = new IdentityHashMap<>();

        for (Map.Entry<Class<? extends Event>, MergedHandlerMapping<T, ? extends Event>> ent : this.mappings.entrySet()) {
            Class<? extends Event> type = ent.getKey();
            Class<? extends Event> registrationType = getRegistrationClass(type);

            // only register once
            EventPriority existing = registered.put(registrationType, ent.getValue().getPriority());
            if (existing != null) {
                if (existing != ent.getValue().getPriority()) {
                    throw new RuntimeException("Unable to register the same event with different priorities: " + type + " --> " + registrationType);
                }
                continue;
            }

            Helper.plugins().registerEvent(registrationType, this, ent.getValue().getPriority(), this, plugin, false);
        }
    }

    @Override
    public void execute(Listener listener, Event event) {
        MergedHandlerMapping<T, ? extends Event> mapping = this.mappings.get(event.getClass());
        if (mapping == null) {
            return;
        }
        Function<Object, T> function = mapping.getFunction();
        if (!this.active.get()) {
            event.getHandlers().unregister(listener);
            return;
        }
        T handledInstance = function.apply(event);
        for (BiPredicate<MergedSubscription<T>, T> test : this.preExpiryTests) {
            if (test.test(this, handledInstance)) {
                event.getHandlers().unregister(listener);
                this.active.set(false);
                return;
            }
        }
        try {
            for (Predicate<T> filter : this.filters) {
                if (!filter.test(handledInstance)) {
                    return;
                }
            }
            for (BiPredicate<MergedSubscription<T>, T> test : this.midExpiryTests) {
                if (test.test(this, handledInstance)) {
                    event.getHandlers().unregister(listener);
                    this.active.set(false);
                    return;
                }
            }
            for (BiConsumer<MergedSubscription<T>, ? super T> handler : this.handlers) {
                handler.accept(this, handledInstance);
            }
            this.callCount.incrementAndGet();
        } catch (Throwable t) {
            this.exceptionConsumer.accept(event, t);
        }
        for (BiPredicate<MergedSubscription<T>, T> test : this.postExpiryTests) {
            if (test.test(this, handledInstance)) {
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
        for (Class<? extends Event> clazz : this.mappings.keySet()) {
            unregisterListener(clazz, this);
        }
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

    @Override
    public Class<? super T> getHandledClass() {
        return this.handledClass.getRawType();
    }

    @Override
    public Set<Class<? extends Event>> getEventClasses() {
        return this.mappings.keySet();
    }

    private static void unregisterListener(Class<? extends Event> eventClass, Listener listener) {
        try {
            Method getHandlerListMethod = eventClass.getMethod("getHandlerList");
            HandlerList handlerList = (HandlerList) getHandlerListMethod.invoke(null);
            handlerList.unregister(listener);
        } catch (Throwable ignore) {

        }
    }

    private static Class<? extends Event> getRegistrationClass(Class<? extends Event> clazz) {
        try {
            clazz.getDeclaredMethod("getHandlerList");
            return clazz;
        } catch (NoSuchMethodException var2) {
            if (clazz.getSuperclass() != null && !clazz.getSuperclass().equals(Event.class) && Event.class.isAssignableFrom(clazz.getSuperclass())) {
                return getRegistrationClass(clazz.getSuperclass().asSubclass(Event.class));
            } else {
                throw new IllegalPluginAccessException("Unable to find handler list for event " + clazz.getName() + ".");
            }
        }
    }
}