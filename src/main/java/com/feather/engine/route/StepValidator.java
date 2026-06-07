package com.feather.engine.route;

import static com.feather.engine.route.CollisionFlag.EAST;
import static com.feather.engine.route.CollisionFlag.NORTH;
import static com.feather.engine.route.CollisionFlag.SOUTH;
import static com.feather.engine.route.CollisionFlag.WEST;

public final class StepValidator {
    private final CollisionFlagMap flags;
    private final CollisionStrategy strategy;
    private final int level;
    private final int width;
    private final int length;

    public StepValidator(CollisionFlagMap flags, CollisionStrategy strategy, int level, int width, int length) {
        this.flags = flags;
        this.strategy = strategy;
        this.level = level;
        this.width = width;
        this.length = length;
    }

    public boolean canMove(int x, int z) {
        return strategy.canMove(flags.get(x, z, level), 0);
    }

    public boolean canMove(int x, int z, int dirFlag) {
        return strategy.canMove(flags.get(x, z, level), dirFlag);
    }

    public int get(int x, int z) {
        return flags.get(x, z, level);
    }

    public boolean canExit(int srcX, int srcZ, int destX, int destZ, int destWidth, int destLength) {
        if (width == 1 && length == 1) {
            return ReachStrategy.reachRectangle(flags, level, srcX, srcZ, destX, destZ, width, length, destWidth, destLength, 0);
        }
        return ReachStrategy.reachRectangle(flags, level, srcX, srcZ, destX, destZ, width, length, destWidth, destLength, 0);
    }

    public boolean blockedNorth(int flag) {
        return (flag & NORTH) != 0 && (flag & CollisionFlag.BLOCKED_NORTH) != 0;
    }

    public boolean blockedEast(int flag) {
        return (flag & EAST) != 0 && (flag & CollisionFlag.BLOCKED_EAST) != 0;
    }

    public boolean blockedSouth(int flag) {
        return (flag & SOUTH) != 0 && (flag & CollisionFlag.BLOCKED_SOUTH) != 0;
    }

    public boolean blockedWest(int flag) {
        return (flag & WEST) != 0 && (flag & CollisionFlag.BLOCKED_WEST) != 0;
    }
}
