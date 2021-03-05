package me.hyfe.helper;

import me.hyfe.helper.interfaces.Delegate;
import me.hyfe.helper.internal.LoaderUtils;
import me.hyfe.helper.promise.ThreadContext;
import me.hyfe.helper.scheduler.HelperExecutors;
import me.hyfe.helper.scheduler.Scheduler;
import me.hyfe.helper.scheduler.Task;
import me.hyfe.helper.scheduler.Ticks;
import me.hyfe.helper.scheduler.builder.TaskBuilder;
import me.hyfe.helper.utils.Log;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Objects;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public final class Schedulers {
    private static final Scheduler SYNC_SCHEDULER = new SyncScheduler();
    private static final Scheduler ASYNC_SCHEDULER = new AsyncScheduler();

    public static Scheduler get(ThreadContext context) {
        switch (context) {
            case SYNC:
                return sync();
            case ASYNC:
                return async();
            default:
                throw new AssertionError();
        }
    }

    public static Scheduler sync() {
        return SYNC_SCHEDULER;
    }

    public static Scheduler async() {
        return ASYNC_SCHEDULER;
    }

    public static BukkitScheduler bukkit() {
        return Helper.bukkitScheduler();
    }

    /**
     * Gets a {@link TaskBuilder} instance
     *
     * @return a task builder
     */
    public static TaskBuilder builder() {
        return TaskBuilder.newBuilder();
    }

    private static final class SyncScheduler implements Scheduler {

        @Override
        public void execute(Runnable runnable) {
            HelperExecutors.sync().execute(runnable);
        }


        @Override
        public ThreadContext getContext() {
            return ThreadContext.SYNC;
        }


        @Override
        public Task runRepeating(Consumer<Task> consumer, long delayTicks, long intervalTicks) {
            Objects.requireNonNull(consumer, "consumer");
            HelperTask task = new HelperTask(consumer);
            task.runTaskTimer(LoaderUtils.getPlugin(), delayTicks, intervalTicks);
            return task;
        }


        @Override
        public Task runRepeating(Consumer<Task> consumer, long delay, TimeUnit delayUnit, long interval, TimeUnit intervalUnit) {
            return runRepeating(consumer, Ticks.from(delay, delayUnit), Ticks.from(interval, intervalUnit));
        }
    }

    private static final class AsyncScheduler implements Scheduler {

        @Override
        public void execute(Runnable runnable) {
            HelperExecutors.asyncHelper().execute(runnable);
        }


        @Override
        public ThreadContext getContext() {
            return ThreadContext.ASYNC;
        }


        @Override
        public Task runRepeating(Consumer<Task> consumer, long delayTicks, long intervalTicks) {
            Objects.requireNonNull(consumer, "consumer");
            HelperTask task = new HelperTask(consumer);
            task.runTaskTimerAsynchronously(LoaderUtils.getPlugin(), delayTicks, intervalTicks);
            return task;
        }


        @Override
        public Task runRepeating(Consumer<Task> consumer, long delay, TimeUnit delayUnit, long interval, TimeUnit intervalUnit) {
            Objects.requireNonNull(consumer, "consumer");
            return new HelperAsyncTask(consumer, delay, delayUnit, interval, intervalUnit);
        }
    }

    private static class HelperTask extends BukkitRunnable implements Task, Delegate<Consumer<Task>> {
        private final Consumer<Task> backingTask;

        private final AtomicInteger counter = new AtomicInteger(0);
        private final AtomicBoolean cancelled = new AtomicBoolean(false);

        private HelperTask(Consumer<Task> backingTask) {
            this.backingTask = backingTask;
        }

        @Override
        public void run() {
            if (this.cancelled.get()) {
                cancel();
                return;
            }

            try {
                this.backingTask.accept(this);
                this.counter.incrementAndGet();
            } catch (Throwable e) {
                Log.severe("[SCHEDULER] Exception thrown whilst executing task");
                e.printStackTrace();
            }

            if (this.cancelled.get()) {
                cancel();
            }
        }

        @Override
        public int getTimesRan() {
            return this.counter.get();
        }

        @Override
        public boolean stop() {
            return !this.cancelled.getAndSet(true);
        }

        @Override
        public int getBukkitId() {
            return getTaskId();
        }

        @Override
        public boolean isClosed() {
            return this.cancelled.get();
        }

        @Override
        public Consumer<Task> getDelegate() {
            return this.backingTask;
        }
    }

    private static class HelperAsyncTask implements Runnable, Task, Delegate<Consumer<Task>> {
        private final Consumer<Task> backingTask;
        private final ScheduledFuture<?> future;

        private final AtomicInteger counter = new AtomicInteger(0);
        private final AtomicBoolean cancelled = new AtomicBoolean(false);

        private HelperAsyncTask(Consumer<Task> backingTask, long delay, TimeUnit delayUnit, long interval, TimeUnit intervalUnit) {
            this.backingTask = backingTask;
            this.future = HelperExecutors.asyncHelper().scheduleAtFixedRate(this, delayUnit.toNanos(delay), intervalUnit.toNanos(interval), TimeUnit.NANOSECONDS);
        }

        @Override
        public void run() {
            if (this.cancelled.get()) {
                return;
            }

            try {
                this.backingTask.accept(this);
                this.counter.incrementAndGet();
            } catch (Throwable e) {
                Log.severe("[SCHEDULER] Exception thrown whilst executing task");
                e.printStackTrace();
            }
        }

        @Override
        public int getTimesRan() {
            return this.counter.get();
        }

        @Override
        public boolean stop() {
            if (!this.cancelled.getAndSet(true)) {
                this.future.cancel(false);
                return true;
            } else {
                return false;
            }
        }

        @Override
        public int getBukkitId() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isClosed() {
            return this.cancelled.get();
        }

        @Override
        public Consumer<Task> getDelegate() {
            return this.backingTask;
        }
    }

    private Schedulers() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

}