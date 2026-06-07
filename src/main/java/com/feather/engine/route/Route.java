package com.feather.engine.route;

import java.util.Arrays;

public final class Route {
    private final int[] x;
    private final int[] z;
    private final int length;
    private final boolean alternative;

    public Route(int[] x, int[] z, int length, boolean alternative) {
        this.x = x;
        this.z = z;
        this.length = length;
        this.alternative = alternative;
    }

    public int[] getX() {
        return x;
    }

    public int[] getZ() {
        return z;
    }

    public int getLength() {
        return length;
    }

    public boolean isAlternative() {
        return alternative;
    }

    @Override
    public String toString() {
        return "Route{length=" + length + ", alternative=" + alternative + ", x=" + Arrays.toString(Arrays.copyOf(x, length)) + ", z=" + Arrays.toString(Arrays.copyOf(z, length)) + "}";
    }
}
