package com.feather.engine.tasks;

import com.feather.utils.Logger;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Iterator;

/**
 * Efficient task scheduler for world tasks with support for delayed and periodic execution.
 * Thread-safe implementation using lock-free data structures.
 */
public class WorldTasksManager {

    // Use ConcurrentLinkedQueue for better performance in concurrent scenarios
    private static final ConcurrentLinkedQueue<TaskWrapper> taskQueue = new ConcurrentLinkedQueue<>();
    private static final AtomicInteger taskCount = new AtomicInteger(0);

    // Statistics for monitoring
    private static final AtomicInteger totalTasksProcessed = new AtomicInteger(0);
    private static final AtomicInteger totalTasksRemoved = new AtomicInteger(0);

    /**
     * Processes all scheduled tasks. Should be called from the main game loop.
     * Uses iterator for safe concurrent modification handling.
     */
    public static void processTasks() {
        if (taskQueue.isEmpty()) {
            return;
        }

        Iterator<TaskWrapper> iterator = taskQueue.iterator();
        int processed = 0;
        int removed = 0;

        while (iterator.hasNext()) {
            TaskWrapper wrapper = iterator.next();

            // Handle delay countdown
            if (wrapper.remainingDelay > 0) {
                wrapper.remainingDelay--;
                continue;
            }

            try {
                // Execute the task
                wrapper.task.run();
                processed++;

                // Determine if task should be removed
                if (wrapper.shouldRemove()) {
                    iterator.remove();
                    taskCount.decrementAndGet();
                    removed++;
                } else {
                    // Reset delay for next execution (periodic tasks)
                    wrapper.remainingDelay = wrapper.period;
                }

            } catch (Exception e) {
                Logger.log("WorldTasksManager", "Error executing task: " + e.getMessage());
                // Remove problematic tasks
                iterator.remove();
                taskCount.decrementAndGet();
                removed++;
            }
        }

        // Update statistics
        totalTasksProcessed.addAndGet(processed);
        totalTasksRemoved.addAndGet(removed);
    }

    /**
     * Schedules a task with specified delay and period.
     * @param task The task to execute
     * @param delayTicks Initial delay in ticks before first execution
     * @param periodTicks Period between executions (use -1 for one-time execution)
     * @throws IllegalArgumentException if parameters are invalid
     */
    public static void scheduleTask(WorldTask task, int delayTicks, int periodTicks) {
        validateTask(task);
        validateDelay(delayTicks);
        validatePeriod(periodTicks);

        TaskWrapper wrapper = new TaskWrapper(task, delayTicks, periodTicks);
        taskQueue.offer(wrapper);
        taskCount.incrementAndGet();
    }

    /**
     * Schedules a one-time task with specified delay.
     * @param task The task to execute
     * @param delayTicks Delay in ticks before execution
     */
    public static void scheduleTask(WorldTask task, int delayTicks) {
        scheduleTask(task, delayTicks, -1);
    }

    /**
     * Schedules a task for immediate execution on next tick.
     * @param task The task to execute
     */
    public static void scheduleTask(WorldTask task) {
        scheduleTask(task, 0, -1);
    }

    /**
     * Schedules a periodic task starting immediately.
     * @param task The task to execute
     * @param periodTicks Period between executions
     */
    public static void schedulePeriodicTask(WorldTask task, int periodTicks) {
        scheduleTask(task, 0, periodTicks);
    }

    /**
     * Cancels all tasks matching the given task instance.
     * @param task The task to cancel
     * @return Number of tasks cancelled
     */
    public static int cancelTask(WorldTask task) {
        if (task == null) {
            return 0;
        }

        int cancelled = 0;
        Iterator<TaskWrapper> iterator = taskQueue.iterator();

        while (iterator.hasNext()) {
            TaskWrapper wrapper = iterator.next();
            if (wrapper.task.equals(task)) {
                iterator.remove();
                taskCount.decrementAndGet();
                cancelled++;
            }
        }

        return cancelled;
    }

    /**
     * Clears all scheduled tasks.
     * @return Number of tasks cleared
     */
    public static int clearAllTasks() {
        int cleared = taskCount.getAndSet(0);
        taskQueue.clear();
        return cleared;
    }

    /**
     * Gets the current number of scheduled tasks.
     * @return Task count
     */
    public static int getTaskCount() {
        return taskCount.get();
    }

    /**
     * Gets performance statistics.
     * @return TaskStats object containing performance metrics
     */
    public static TaskStats getStats() {
        return new TaskStats(
                taskCount.get(),
                totalTasksProcessed.get(),
                totalTasksRemoved.get()
        );
    }

    /**
     * Resets performance statistics.
     */
    public static void resetStats() {
        totalTasksProcessed.set(0);
        totalTasksRemoved.set(0);
    }

    // Validation methods
    private static void validateTask(WorldTask task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
    }

    private static void validateDelay(int delay) {
        if (delay < 0) {
            throw new IllegalArgumentException("Delay cannot be negative");
        }
    }

    private static void validatePeriod(int period) {
        if (period < -1) {
            throw new IllegalArgumentException("Period must be -1 (one-time) or >= 0 (periodic)");
        }
    }

    // Prevent instantiation
    private WorldTasksManager() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Internal wrapper class for task information.
     * Immutable except for execution state.
     */
    private static final class TaskWrapper {
        final WorldTask task;
        final int period;
        final boolean isOneTime;
        volatile int remainingDelay;

        TaskWrapper(WorldTask task, int delay, int period) {
            this.task = task;
            this.remainingDelay = delay;
            this.period = Math.max(period, 0); // Ensure non-negative for periodic tasks
            this.isOneTime = period == -1;
        }

        boolean shouldRemove() {
            return isOneTime || task.shouldRemove();
        }
    }

    /**
     * Performance statistics data class.
     */
    public static class TaskStats {
        public final int currentTasks;
        public final int totalProcessed;
        public final int totalRemoved;

        TaskStats(int currentTasks, int totalProcessed, int totalRemoved) {
            this.currentTasks = currentTasks;
            this.totalProcessed = totalProcessed;
            this.totalRemoved = totalRemoved;
        }

        @Override
        public String toString() {
            return String.format("TaskStats{current=%d, processed=%d, removed=%d}",
                    currentTasks, totalProcessed, totalRemoved);
        }
    }


}