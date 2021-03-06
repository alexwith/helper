package me.hyfe.helper;

import com.google.common.reflect.TypeToken;
import me.hyfe.helper.event.functional.merged.MergedSubscriptionBuilder;
import me.hyfe.helper.event.functional.single.SingleSubscriptionBuilder;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;

public final class Events {

    public static <T extends Event> SingleSubscriptionBuilder<T> subscribe(Class<T> eventClass) {
        return SingleSubscriptionBuilder.newBuilder(eventClass);
    }
    public static <T extends Event> SingleSubscriptionBuilder<T> subscribe(Class<T> eventClass, EventPriority priority) {
        return SingleSubscriptionBuilder.newBuilder(eventClass, priority);
    }

    public static <T> MergedSubscriptionBuilder<T> merge(Class<T> handledClass) {
        return MergedSubscriptionBuilder.newBuilder(handledClass);
    }

    public static <T> MergedSubscriptionBuilder<T> merge(TypeToken<T> type) {
        return MergedSubscriptionBuilder.newBuilder(type);
    }

    @SafeVarargs
    public static <S extends Event> MergedSubscriptionBuilder<S> merge(Class<S> superClass, Class<? extends S>... eventClasses) {
        return MergedSubscriptionBuilder.newBuilder(superClass, eventClasses);
    }

    @SafeVarargs
    public static <S extends Event> MergedSubscriptionBuilder<S> merge(Class<S> superClass, EventPriority priority, Class<? extends S>... eventClasses) {
        return MergedSubscriptionBuilder.newBuilder(superClass, priority, eventClasses);
    }

    public static void call(Event event) {
        Helper.plugins().callEvent(event);
    }

    public static void callAsync(Event event) {
        Schedulers.async().run(() -> call(event));
    }

    public static void callSync(Event event) {
        Schedulers.sync().run(() -> call(event));
    }

    public static <T extends Event> T callAndReturn(T event) {
        Helper.plugins().callEvent(event);
        return event;
    }

    public static <T extends Event> T callAsyncAndJoin(T event) {
        return Schedulers.async().supply(() -> callAndReturn(event)).join();
    }

    public static <T extends Event> T callSyncAndJoin(T event) {
        return Schedulers.sync().supply(() -> callAndReturn(event)).join();
    }
}