package me.hyfe.helper.scheduler;

import me.hyfe.helper.promise.Promise;
import me.hyfe.helper.promise.ThreadContext;
import me.hyfe.helper.utils.Delegates;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface Scheduler extends Executor {

    ThreadContext getContext();

    default <T> Promise<T> supply(Supplier<T> supplier) {
        Objects.requireNonNull(supplier, "supplier");
        return Promise.supplying(getContext(), supplier);
    }

    default <T> Promise<T> call(Callable<T> callable) {
        Objects.requireNonNull(callable, "callable");
        return Promise.supplying(getContext(), Delegates.callableToSupplier(callable));
    }

    default Promise<Void> run(Runnable runnable) {
        Objects.requireNonNull(runnable, "runnable");
        return Promise.supplying(getContext(), Delegates.runnableToSupplier(runnable));
    }

    default <T> Promise<T> supplyLater(Supplier<T> supplier, long delayTicks) {
        Objects.requireNonNull(supplier, "supplier");
        return Promise.supplyingDelayed(getContext(), supplier, delayTicks);
    }

    default <T> Promise<T> supplyLater(Supplier<T> supplier, long delay, TimeUnit unit) {
        Objects.requireNonNull(supplier, "supplier");
        return Promise.supplyingDelayed(getContext(), supplier, delay, unit);
    }

    default <T> Promise<T> callLater(Callable<T> callable, long delayTicks) {
        Objects.requireNonNull(callable, "callable");
        return Promise.supplyingDelayed(getContext(), Delegates.callableToSupplier(callable), delayTicks);
    }

    default <T> Promise<T> callLater(Callable<T> callable, long delay, TimeUnit unit) {
        Objects.requireNonNull(callable, "callable");
        return Promise.supplyingDelayed(getContext(), Delegates.callableToSupplier(callable), delay, unit);
    }

    default Promise<Void> runLater(Runnable runnable, long delayTicks) {
        Objects.requireNonNull(runnable, "runnable");
        return Promise.supplyingDelayed(getContext(), Delegates.runnableToSupplier(runnable), delayTicks);
    }

    default Promise<Void> runLater(Runnable runnable, long delay, TimeUnit unit) {
        Objects.requireNonNull(runnable, "runnable");
        return Promise.supplyingDelayed(getContext(), Delegates.runnableToSupplier(runnable), delay, unit);
    }

    Task runRepeating(Consumer<Task> consumer, long delayTicks, long intervalTicks);

    Task runRepeating(Consumer<Task> consumer, long delay, TimeUnit delayUnit, long interval, TimeUnit intervalUnit);

    default Task runRepeating(Runnable runnable, long delayTicks, long intervalTicks) {
        return runRepeating(Delegates.runnableToConsumer(runnable), delayTicks, intervalTicks);
    }

    default Task runRepeating(Runnable runnable, long delay, TimeUnit delayUnit, long interval, TimeUnit intervalUnit) {
        return runRepeating(Delegates.runnableToConsumer(runnable), delay, delayUnit, interval, intervalUnit);
    }

}
