package com.feather.engine.tasks;

/**
 * Abstract base class for tasks that can be scheduled and executed by the WorldTasksManager.
 *
 * Tasks should be lightweight and complete quickly to avoid blocking the game loop.
 * For long-running operations, consider breaking them into smaller chunks or using
 * separate thread pools.
 */
public abstract class WorldTask {

    /**
     * Executes the task logic.
     * This method should be implemented to perform the actual work of the task.
     *
     * @throws RuntimeException if task execution fails (will be logged and task removed)
     */
    public abstract void run();

    /**
     * Flag indicating whether this task should be removed after execution.
     * Set to true to stop periodic tasks or remove one-time tasks.
     * This field maintains compatibility with the original API.
     */
    public boolean needRemove = false;

    /**
     * Stops this task by marking it for removal.
     * This is a convenience method that sets needRemove = true.
     */
    public void stop() {
        needRemove = true;
    }

    /**
     * Determines whether this task should be removed after execution.
     * For one-time tasks, this is handled automatically by the scheduler.
     * For periodic tasks, implement this method to provide custom removal logic.
     *
     * @return true if the task should be removed, false to continue periodic execution
     */
    public boolean shouldRemove() {
        return needRemove;
    }

    /**
     * Gets a description of this task for debugging/logging purposes.
     *
     * @return A human-readable description of the task
     */
    public String getDescription() {
        return getClass().getSimpleName();
    }

    /**
     * Gets the priority of this task (for future priority queue implementation).
     * Higher values indicate higher priority.
     *
     * @return Task priority (default: 0)
     */
    public int getPriority() {
        return 0;
    }
}