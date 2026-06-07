package com.feather.engine.route;

public final class RayCast {
    private final int x;
    private final int z;
    private final int steps;
    private final boolean reached;

    public RayCast(int x, int z, int steps, boolean reached) {
        this.x = x;
        this.z = z;
        this.steps = steps;
        this.reached = reached;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public int getSteps() {
        return steps;
    }

    public boolean isReached() {
        return reached;
    }
}
