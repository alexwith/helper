/*
 * This file is part of helper, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package me.hyfe.helper.promise;

import me.hyfe.helper.interfaces.Delegate;
import me.hyfe.helper.internal.LoaderUtils;
import me.hyfe.helper.scheduler.HelperExecutors;
import me.hyfe.helper.scheduler.Ticks;
import me.hyfe.helper.utils.Log;
import org.bukkit.Bukkit;

import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

final class HelperPromise<V> implements Promise<V> {
    private static final Consumer<Throwable> EXCEPTION_CONSUMER = throwable -> {
        Log.severe("[SCHEDULER] Exception thrown whilst executing task");
        throwable.printStackTrace();
    };


    static <U> HelperPromise<U> empty() {
        return new HelperPromise<>();
    }


    static <U> HelperPromise<U> completed(U value) {
        return new HelperPromise<>(value);
    }


    static <U> HelperPromise<U> exceptionally(Throwable t) {
        return new HelperPromise<>(t);
    }

    private final AtomicBoolean supplied = new AtomicBoolean(false);

    /**
     * If the execution of the promise is cancelled
     */
    private final AtomicBoolean cancelled = new AtomicBoolean(false);

    /**
     * The completable future backing this promise
     */

    private final CompletableFuture<V> fut;

    private HelperPromise() {
        this.fut = new CompletableFuture<>();
    }

    private HelperPromise(V v) {
        this.fut = CompletableFuture.completedFuture(v);
        this.supplied.set(true);
    }

    private HelperPromise(Throwable t) {
        (this.fut = new CompletableFuture<>()).completeExceptionally(t);
        this.supplied.set(true);
    }

    private HelperPromise(CompletableFuture<V> fut) {
        this.fut = Objects.requireNonNull(fut, "future");
        this.supplied.set(true);
        this.cancelled.set(fut.isCancelled());
    }

    /* utility methods */

    private void executeSync(Runnable runnable) {
        if (ThreadContext.forCurrentThread() == ThreadContext.SYNC) {
            HelperExecutors.wrapRunnable(runnable).run();
        } else {
            HelperExecutors.sync().execute(runnable);
        }
    }

    private void executeAsync(Runnable runnable) {
        HelperExecutors.asyncHelper().execute(runnable);
    }

    private void executeDelayedSync(Runnable runnable, long delayTicks) {
        if (delayTicks <= 0) {
            executeSync(runnable);
        } else {
            Bukkit.getScheduler().runTaskLater(LoaderUtils.getPlugin(), HelperExecutors.wrapRunnable(runnable), delayTicks);
        }
    }

    private void executeDelayedAsync(Runnable runnable, long delayTicks) {
        if (delayTicks <= 0) {
            executeAsync(runnable);
        } else {
            Bukkit.getScheduler().runTaskLaterAsynchronously(LoaderUtils.getPlugin(), HelperExecutors.wrapRunnable(runnable), delayTicks);
        }
    }

    private void executeDelayedSync(Runnable runnable, long delay, TimeUnit unit) {
        if (delay <= 0) {
            executeSync(runnable);
        } else {
            Bukkit.getScheduler().runTaskLater(LoaderUtils.getPlugin(), HelperExecutors.wrapRunnable(runnable), Ticks.from(delay, unit));
        }
    }

    private void executeDelayedAsync(Runnable runnable, long delay, TimeUnit unit) {
        if (delay <= 0) {
            executeAsync(runnable);
        } else {
            HelperExecutors.asyncHelper().schedule(HelperExecutors.wrapRunnable(runnable), delay, unit);
        }
    }

    private boolean complete(V value) {
        return !this.cancelled.get() && this.fut.complete(value);
    }

    private boolean completeExceptionally(Throwable t) {
        return !this.cancelled.get() && this.fut.completeExceptionally(t);
    }

    private void markAsSupplied() {
        if (!this.supplied.compareAndSet(false, true)) {
            throw new IllegalStateException("Promise is already being supplied.");
        }
    }

    /* future methods */

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        this.cancelled.set(true);
        return this.fut.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return this.fut.isCancelled();
    }

    @Override
    public boolean isDone() {
        return this.fut.isDone();
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        return this.fut.get();
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.fut.get(timeout, unit);
    }

    @Override
    public V join() {
        return this.fut.join();
    }

    @Override
    public V getNow(V valueIfAbsent) {
        return this.fut.getNow(valueIfAbsent);
    }

    @Override
    public CompletableFuture<V> toCompletableFuture() {
        return this.fut.thenApply(Function.identity());
    }

    @Override
    public void close() {
        cancel();
    }

    @Override
    public boolean isClosed() {
        return isCancelled();
    }

    /* implementation */


    @Override
    public Promise<V> supply(V value) {
        markAsSupplied();
        complete(value);
        return this;
    }


    @Override
    public Promise<V> supplyException(Throwable exception) {
        markAsSupplied();
        completeExceptionally(exception);
        return this;
    }


    @Override
    public Promise<V> supplySync(Supplier<V> supplier) {
        markAsSupplied();
        executeSync(new SupplyRunnable(supplier));
        return this;
    }


    @Override
    public Promise<V> supplyAsync(Supplier<V> supplier) {
        markAsSupplied();
        executeAsync(new SupplyRunnable(supplier));
        return this;
    }


    @Override
    public Promise<V> supplyDelayedSync(Supplier<V> supplier, long delayTicks) {
        markAsSupplied();
        executeDelayedSync(new SupplyRunnable(supplier), delayTicks);
        return this;
    }


    @Override
    public Promise<V> supplyDelayedSync(Supplier<V> supplier, long delay, TimeUnit unit) {
        markAsSupplied();
        executeDelayedSync(new SupplyRunnable(supplier), delay, unit);
        return this;
    }


    @Override
    public Promise<V> supplyDelayedAsync(Supplier<V> supplier, long delayTicks) {
        markAsSupplied();
        executeDelayedAsync(new SupplyRunnable(supplier), delayTicks);
        return this;
    }


    @Override
    public Promise<V> supplyDelayedAsync(Supplier<V> supplier, long delay, TimeUnit unit) {
        markAsSupplied();
        executeDelayedAsync(new SupplyRunnable(supplier), delay, unit);
        return this;
    }


    @Override
    public Promise<V> supplyExceptionallySync(Callable<V> callable) {
        markAsSupplied();
        executeSync(new ThrowingSupplyRunnable(callable));
        return this;
    }


    @Override
    public Promise<V> supplyExceptionallyAsync(Callable<V> callable) {
        markAsSupplied();
        executeAsync(new ThrowingSupplyRunnable(callable));
        return this;
    }


    @Override
    public Promise<V> supplyExceptionallyDelayedSync(Callable<V> callable, long delayTicks) {
        markAsSupplied();
        executeDelayedSync(new ThrowingSupplyRunnable(callable), delayTicks);
        return this;
    }


    @Override
    public Promise<V> supplyExceptionallyDelayedSync(Callable<V> callable, long delay, TimeUnit unit) {
        markAsSupplied();
        executeDelayedSync(new ThrowingSupplyRunnable(callable), delay, unit);
        return this;
    }


    @Override
    public Promise<V> supplyExceptionallyDelayedAsync(Callable<V> callable, long delayTicks) {
        markAsSupplied();
        executeDelayedAsync(new ThrowingSupplyRunnable(callable), delayTicks);
        return this;
    }


    @Override
    public Promise<V> supplyExceptionallyDelayedAsync(Callable<V> callable, long delay, TimeUnit unit) {
        markAsSupplied();
        executeDelayedAsync(new ThrowingSupplyRunnable(callable), delay, unit);
        return this;
    }


    @Override
    public <U> Promise<U> thenApplySync(Function<? super V, ? extends U> fn) {
        HelperPromise<U> promise = empty();
        this.fut.whenComplete((value, t) -> {
            if (t != null) {
                promise.completeExceptionally(t);
            } else {
                executeSync(new ApplyRunnable<>(promise, fn, value));
            }
        });
        return promise;
    }


    @Override
    public <U> Promise<U> thenApplyAsync(Function<? super V, ? extends U> fn) {
        HelperPromise<U> promise = empty();
        this.fut.whenComplete((value, t) -> {
            if (t != null) {
                promise.completeExceptionally(t);
            } else {
                executeAsync(new ApplyRunnable<>(promise, fn, value));
            }
        });
        return promise;
    }


    @Override
    public <U> Promise<U> thenApplyDelayedSync(Function<? super V, ? extends U> fn, long delayTicks) {
        HelperPromise<U> promise = empty();
        this.fut.whenComplete((value, t) -> {
            if (t != null) {
                promise.completeExceptionally(t);
            } else {
                executeDelayedSync(new ApplyRunnable<>(promise, fn, value), delayTicks);
            }
        });
        return promise;
    }


    @Override
    public <U> Promise<U> thenApplyDelayedSync(Function<? super V, ? extends U> fn, long delay, TimeUnit unit) {
        HelperPromise<U> promise = empty();
        this.fut.whenComplete((value, t) -> {
            if (t != null) {
                promise.completeExceptionally(t);
            } else {
                executeDelayedSync(new ApplyRunnable<>(promise, fn, value), delay, unit);
            }
        });
        return promise;
    }


    @Override
    public <U> Promise<U> thenApplyDelayedAsync(Function<? super V, ? extends U> fn, long delayTicks) {
        HelperPromise<U> promise = empty();
        this.fut.whenComplete((value, t) -> {
            if (t != null) {
                promise.completeExceptionally(t);
            } else {
                executeDelayedAsync(new ApplyRunnable<>(promise, fn, value), delayTicks);
            }
        });
        return promise;
    }


    @Override
    public <U> Promise<U> thenApplyDelayedAsync(Function<? super V, ? extends U> fn, long delay, TimeUnit unit) {
        HelperPromise<U> promise = empty();
        this.fut.whenComplete((value, t) -> {
            if (t != null) {
                promise.completeExceptionally(t);
            } else {
                executeDelayedAsync(new ApplyRunnable<>(promise, fn, value), delay, unit);
            }
        });
        return promise;
    }


    @Override
    public <U> Promise<U> thenComposeSync(Function<? super V, ? extends Promise<U>> fn) {
        HelperPromise<U> promise = empty();
        this.fut.whenComplete((value, t) -> {
            if (t != null) {
                promise.completeExceptionally(t);
            } else {
                executeSync(new ComposeRunnable<>(promise, fn, value, true));
            }
        });
        return promise;
    }


    @Override
    public <U> Promise<U> thenComposeAsync(Function<? super V, ? extends Promise<U>> fn) {
        HelperPromise<U> promise = empty();
        this.fut.whenComplete((value, t) -> {
            if (t != null) {
                promise.completeExceptionally(t);
            } else {
                executeAsync(new ComposeRunnable<>(promise, fn, value, false));
            }
        });
        return promise;
    }


    @Override
    public <U> Promise<U> thenComposeDelayedSync(Function<? super V, ? extends Promise<U>> fn, long delayTicks) {
        HelperPromise<U> promise = empty();
        this.fut.whenComplete((value, t) -> {
            if (t != null) {
                promise.completeExceptionally(t);
            } else {
                executeDelayedSync(new ComposeRunnable<>(promise, fn, value, true), delayTicks);
            }
        });
        return promise;
    }


    @Override
    public <U> Promise<U> thenComposeDelayedSync(Function<? super V, ? extends Promise<U>> fn, long delay, TimeUnit unit) {
        HelperPromise<U> promise = empty();
        this.fut.whenComplete((value, t) -> {
            if (t != null) {
                promise.completeExceptionally(t);
            } else {
                executeDelayedSync(new ComposeRunnable<>(promise, fn, value, true), delay, unit);
            }
        });
        return promise;
    }


    @Override
    public <U> Promise<U> thenComposeDelayedAsync(Function<? super V, ? extends Promise<U>> fn, long delayTicks) {
        HelperPromise<U> promise = empty();
        this.fut.whenComplete((value, t) -> {
            if (t != null) {
                promise.completeExceptionally(t);
            } else {
                executeDelayedAsync(new ComposeRunnable<>(promise, fn, value, false), delayTicks);
            }
        });
        return promise;
    }


    @Override
    public <U> Promise<U> thenComposeDelayedAsync(Function<? super V, ? extends Promise<U>> fn, long delay, TimeUnit unit) {
        HelperPromise<U> promise = empty();
        this.fut.whenComplete((value, t) -> {
            if (t != null) {
                promise.completeExceptionally(t);
            } else {
                executeDelayedAsync(new ComposeRunnable<>(promise, fn, value, false), delay, unit);
            }
        });
        return promise;
    }


    @Override
    public Promise<V> exceptionallySync(Function<Throwable, ? extends V> fn) {
        HelperPromise<V> promise = empty();
        this.fut.whenComplete((value, t) -> {
            if (t == null) {
                promise.complete(value);
            } else {
                executeSync(new ExceptionallyRunnable<>(promise, fn, t));
            }
        });
        return promise;
    }


    @Override
    public Promise<V> exceptionallyAsync(Function<Throwable, ? extends V> fn) {
        HelperPromise<V> promise = empty();
        this.fut.whenComplete((value, t) -> {
            if (t == null) {
                promise.complete(value);
            } else {
                executeAsync(new ExceptionallyRunnable<>(promise, fn, t));
            }
        });
        return promise;
    }


    @Override
    public Promise<V> exceptionallyDelayedSync(Function<Throwable, ? extends V> fn, long delayTicks) {
        HelperPromise<V> promise = empty();
        this.fut.whenComplete((value, t) -> {
            if (t == null) {
                promise.complete(value);
            } else {
                executeDelayedSync(new ExceptionallyRunnable<>(promise, fn, t), delayTicks);
            }
        });
        return promise;
    }


    @Override
    public Promise<V> exceptionallyDelayedSync(Function<Throwable, ? extends V> fn, long delay, TimeUnit unit) {
        HelperPromise<V> promise = empty();
        this.fut.whenComplete((value, t) -> {
            if (t == null) {
                promise.complete(value);
            } else {
                executeDelayedSync(new ExceptionallyRunnable<>(promise, fn, t), delay, unit);
            }
        });
        return promise;
    }


    @Override
    public Promise<V> exceptionallyDelayedAsync(Function<Throwable, ? extends V> fn, long delayTicks) {
        HelperPromise<V> promise = empty();
        this.fut.whenComplete((value, t) -> {
            if (t == null) {
                promise.complete(value);
            } else {
                executeDelayedAsync(new ExceptionallyRunnable<>(promise, fn, t), delayTicks);
            }
        });
        return promise;
    }


    @Override
    public Promise<V> exceptionallyDelayedAsync(Function<Throwable, ? extends V> fn, long delay, TimeUnit unit) {
        HelperPromise<V> promise = empty();
        this.fut.whenComplete((value, t) -> {
            if (t == null) {
                promise.complete(value);
            } else {
                executeDelayedAsync(new ExceptionallyRunnable<>(promise, fn, t), delay, unit);
            }
        });
        return promise;
    }

    /* delegating behaviour runnables */

    private final class ThrowingSupplyRunnable implements Runnable, Delegate<Callable<V>> {
        private final Callable<V> supplier;

        private ThrowingSupplyRunnable(Callable<V> supplier) {
            this.supplier = supplier;
        }

        @Override
        public Callable<V> getDelegate() {
            return this.supplier;
        }

        @Override
        public void run() {
            if (HelperPromise.this.cancelled.get()) {
                return;
            }
            try {
                HelperPromise.this.fut.complete(this.supplier.call());
            } catch (Throwable t) {
                EXCEPTION_CONSUMER.accept(t);
                HelperPromise.this.fut.completeExceptionally(t);
            }
        }
    }

    private final class SupplyRunnable implements Runnable, Delegate<Supplier<V>> {
        private final Supplier<V> supplier;

        private SupplyRunnable(Supplier<V> supplier) {
            this.supplier = supplier;
        }

        @Override
        public Supplier<V> getDelegate() {
            return this.supplier;
        }

        @Override
        public void run() {
            if (HelperPromise.this.cancelled.get()) {
                return;
            }
            try {
                HelperPromise.this.fut.complete(this.supplier.get());
            } catch (Throwable t) {
                EXCEPTION_CONSUMER.accept(t);
                HelperPromise.this.fut.completeExceptionally(t);
            }
        }
    }

    private final class ApplyRunnable<U> implements Runnable, Delegate<Function> {
        private final HelperPromise<U> promise;
        private final Function<? super V, ? extends U> function;
        private final V value;

        private ApplyRunnable(HelperPromise<U> promise, Function<? super V, ? extends U> function, V value) {
            this.promise = promise;
            this.function = function;
            this.value = value;
        }

        @Override
        public Function getDelegate() {
            return this.function;
        }

        @Override
        public void run() {
            if (HelperPromise.this.cancelled.get()) {
                return;
            }
            try {
                this.promise.complete(this.function.apply(this.value));
            } catch (Throwable t) {
                EXCEPTION_CONSUMER.accept(t);
                this.promise.completeExceptionally(t);
            }
        }
    }

    private final class ComposeRunnable<U> implements Runnable, Delegate<Function> {
        private final HelperPromise<U> promise;
        private final Function<? super V, ? extends Promise<U>> function;
        private final V value;
        private final boolean sync;

        private ComposeRunnable(HelperPromise<U> promise, Function<? super V, ? extends Promise<U>> function, V value, boolean sync) {
            this.promise = promise;
            this.function = function;
            this.value = value;
            this.sync = sync;
        }

        @Override
        public Function getDelegate() {
            return this.function;
        }

        @Override
        public void run() {
            if (HelperPromise.this.cancelled.get()) {
                return;
            }
            try {
                Promise<U> p = this.function.apply(this.value);
                if (p == null) {
                    this.promise.complete(null);
                } else {
                    if (this.sync) {
                        p.thenAcceptSync(this.promise::complete);
                    } else {
                        p.thenAcceptAsync(this.promise::complete);
                    }
                }
            } catch (Throwable t) {
                EXCEPTION_CONSUMER.accept(t);
                this.promise.completeExceptionally(t);
            }
        }
    }

    private final class ExceptionallyRunnable<U> implements Runnable, Delegate<Function> {
        private final HelperPromise<U> promise;
        private final Function<Throwable, ? extends U> function;
        private final Throwable t;

        private ExceptionallyRunnable(HelperPromise<U> promise, Function<Throwable, ? extends U> function, Throwable t) {
            this.promise = promise;
            this.function = function;
            this.t = t;
        }

        @Override
        public Function getDelegate() {
            return this.function;
        }

        @Override
        public void run() {
            if (HelperPromise.this.cancelled.get()) {
                return;
            }
            try {
                this.promise.complete(this.function.apply(this.t));
            } catch (Throwable t) {
                EXCEPTION_CONSUMER.accept(t);
                this.promise.completeExceptionally(t);
            }
        }
    }

}
