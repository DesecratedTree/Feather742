package com.feather.engine.action;

public class QueueScript {

    private final QueueType type;
    private final Runnable execution;
    private int delay;
    private boolean processed;
    private boolean reusable;

    public QueueScript(QueueType type, Runnable execution, int delay) {
        this.type = type;
        this.execution = execution;
        this.delay = delay;
    }

    public QueueScript(QueueType type, Runnable execution) {
        this(type, execution, 0);
    }

    public QueueType getType() {
        return type;
    }

    public int getDelay() {
        return delay;
    }

    public void tick() {
        if (delay > 0) {
            delay--;
        }
    }

    public boolean canProcess(boolean playerDelayed, boolean hasModalOpen) {
        if (type == QueueType.SOFT) {
            return true;
        }
        if (playerDelayed) {
            return false;
        }
        if (type == QueueType.NORMAL && hasModalOpen) {
            return false;
        }
        return delay <= 0;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public void execute() {
        if (!processed) {
            execution.run();
            processed = true;
        }
    }

    public boolean isProcessed() {
        return processed;
    }

    public void reset() {
        processed = false;
    }
}
