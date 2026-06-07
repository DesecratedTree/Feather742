package com.feather.engine.route;

public final class RouteResult {
    private final RouteCoordinates[] steps;
    private final int length;
    private final boolean alternative;

    public RouteResult(RouteCoordinates[] steps, int length, boolean alternative) {
        this.steps = steps;
        this.length = length;
        this.alternative = alternative;
    }

    public RouteCoordinates[] getSteps() {
        return steps;
    }

    public int getLength() {
        return length;
    }

    public boolean isAlternative() {
        return alternative;
    }

    public boolean hasRoute() {
        return length > 0;
    }

    public boolean reachedDestination() {
        return !alternative;
    }
}
