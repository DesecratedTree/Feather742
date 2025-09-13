package com.feather.cores.processes;

public abstract class BaseGameProcess implements GameProcess {
    protected boolean enabled = true;

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public final void process() {
        if (enabled) {
            doProcess();
        }
    }

    protected abstract void doProcess();
}
