package com.feather.engine.route;

public final class RouteCoordinates {
    private final int x;
    private final int z;

    public RouteCoordinates(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public RouteCoordinates translate(int dx, int dz) {
        return new RouteCoordinates(x + dx, z + dz);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RouteCoordinates)) return false;
        RouteCoordinates that = (RouteCoordinates) o;
        return x == that.x && z == that.z;
    }

    @Override
    public int hashCode() {
        return (x << 4) | z;
    }

    @Override
    public String toString() {
        return "RouteCoordinates(" + x + ", " + z + ")";
    }
}
