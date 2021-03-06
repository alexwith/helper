package me.hyfe.helper.event;

import me.hyfe.helper.terminable.Terminable;

import java.util.Collection;

public interface Subscription extends Terminable {

    boolean isActive();

    long getCallCounter();

    boolean unregister();

    @Override
    default void close() {
        unregister();
    }

    @Deprecated
    Collection<Object> getFunctions();

}