package com.feather.engine.route;

public final class LineValidator {
    public static boolean check(CollisionFlagMap flags, CollisionStrategy strategy, int level,
                                 int srcX, int srcZ, int destX, int destZ,
                                 int width, int length) {
        RayCast result = LineRouteFinding.find(flags, strategy, level, srcX, srcZ, destX, destZ, width, length);
        return result.getX() == destX && result.getZ() == destZ && result.isReached();
    }

    public static boolean check(CollisionFlagMap flags, CollisionStrategy strategy, int level,
                                 int srcX, int srcZ, int destX, int destZ) {
        return check(flags, strategy, level, srcX, srcZ, destX, destZ, 1, 1);
    }
}
