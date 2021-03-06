package me.hyfe.helper.event;

import org.bukkit.event.Event;

public interface SingleSubscription<T extends Event> extends Subscription {

    Class<T> getEventClass();
}