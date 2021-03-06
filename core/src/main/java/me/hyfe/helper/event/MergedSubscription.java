package me.hyfe.helper.event;

import org.bukkit.event.Event;

import java.util.Set;

public interface MergedSubscription<T> extends Subscription {

    Class<? super T> getHandledClass();

    Set<Class<? extends Event>> getEventClasses();
}
