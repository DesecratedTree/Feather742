package com.feather.engine.route;

import static com.feather.engine.route.CollisionFlag.*;

public final class LineRouteFinding {
    private static final int MAX_STEPS = 256;

    public static RayCast find(CollisionFlagMap flags, CollisionStrategy strategy, int level,
                                int srcX, int srcZ, int destX, int destZ, int width, int length) {
        return find(flags, strategy, level, srcX, srcZ, destX, destZ, width, length, MAX_STEPS);
    }

    public static RayCast find(CollisionFlagMap flags, CollisionStrategy strategy, int level,
                                int srcX, int srcZ, int destX, int destZ, int width, int length, int maxSteps) {
        int deltaX = destX - srcX;
        int deltaZ = destZ - srcZ;
        int steps = Math.max(Math.abs(deltaX), Math.abs(deltaZ));

        if (steps == 0) {
            return new RayCast(srcX, srcZ, 0, true);
        }

        if (steps > maxSteps) {
            steps = maxSteps;
        }

        int currX = srcX;
        int currZ = srcZ;

        for (int step = 1; step <= steps; step++) {
            int nextX = srcX + (deltaX * step) / steps;
            int nextZ = srcZ + (deltaZ * step) / steps;

            if (nextX != currX || nextZ != currZ) {
                if (!checkStep(flags, strategy, level, currX, currZ, nextX, nextZ, width, length)) {
                    return new RayCast(currX, currZ, step - 1, false);
                }
                currX = nextX;
                currZ = nextZ;
            }
        }

        return new RayCast(currX, currZ, steps, currX == destX && currZ == destZ);
    }

    private static boolean checkStep(CollisionFlagMap flags, CollisionStrategy strategy, int level,
                                      int currX, int currZ, int nextX, int nextZ,
                                      int width, int length) {
        int dx = Integer.signum(nextX - currX);
        int dz = Integer.signum(nextZ - currZ);

        if (dx != 0 && dz != 0) {
            return checkDiagonalStep(flags, strategy, level, currX, currZ, dx, dz, width, length);
        }

        if (dz == 1) {
            return checkNorthStep(flags, strategy, level, currX, currZ, width, length);
        }
        if (dz == -1) {
            return checkSouthStep(flags, strategy, level, currX, currZ, width, length);
        }
        if (dx == 1) {
            return checkEastStep(flags, strategy, level, currX, currZ, width, length);
        }
        if (dx == -1) {
            return checkWestStep(flags, strategy, level, currX, currZ, width, length);
        }

        return true;
    }

    public static boolean checkNorthStep(CollisionFlagMap flags, CollisionStrategy strategy, int level,
                                          int currX, int currZ, int width, int length) {
        int destZ = currZ + length;
        for (int x = 0; x < width; x++) {
            int flag = flags.get(currX + x, destZ, level);
            if (!strategy.canMove(flag, 0) || (flag & NORTH) != 0) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkSouthStep(CollisionFlagMap flags, CollisionStrategy strategy, int level,
                                          int currX, int currZ, int width, int length) {
        int destZ = currZ - 1;
        for (int x = 0; x < width; x++) {
            int flag = flags.get(currX + x, destZ, level);
            if (!strategy.canMove(flag, 0) || (flag & SOUTH) != 0) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkEastStep(CollisionFlagMap flags, CollisionStrategy strategy, int level,
                                         int currX, int currZ, int width, int length) {
        int destX = currX + width;
        for (int z = 0; z < length; z++) {
            int flag = flags.get(destX, currZ + z, level);
            if (!strategy.canMove(flag, 0) || (flag & EAST) != 0) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkWestStep(CollisionFlagMap flags, CollisionStrategy strategy, int level,
                                         int currX, int currZ, int width, int length) {
        int destX = currX - 1;
        for (int z = 0; z < length; z++) {
            int flag = flags.get(destX, currZ + z, level);
            if (!strategy.canMove(flag, 0) || (flag & WEST) != 0) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkDiagonalStep(CollisionFlagMap flags, CollisionStrategy strategy, int level,
                                             int currX, int currZ, int dx, int dz,
                                             int width, int length) {
        int destX = dx == 1 ? currX + width : currX - 1;
        int destZ = dz == 1 ? currZ + length : currZ - 1;

        int cornerFlag = flags.get(destX, destZ, level);
        if (!strategy.canMove(cornerFlag, 0)) {
            return false;
        }

        if (dx == 1 && dz == 1) {
            for (int z = 0; z < length; z++) {
                int xFlag = flags.get(currX + width, currZ + z, level);
                if (!strategy.canMove(xFlag, 0) || (xFlag & EAST) != 0) {
                    return false;
                }
            }
            for (int x = 0; x < width; x++) {
                int zFlag = flags.get(currX + x, currZ + length, level);
                if (!strategy.canMove(zFlag, 0) || (zFlag & NORTH) != 0) {
                    return false;
                }
            }
        } else if (dx == -1 && dz == 1) {
            for (int z = 0; z < length; z++) {
                int xFlag = flags.get(currX - 1, currZ + z, level);
                if (!strategy.canMove(xFlag, 0) || (xFlag & WEST) != 0) {
                    return false;
                }
            }
            for (int x = 0; x < width; x++) {
                int zFlag = flags.get(currX + x, currZ + length, level);
                if (!strategy.canMove(zFlag, 0) || (zFlag & NORTH) != 0) {
                    return false;
                }
            }
        } else if (dx == 1 && dz == -1) {
            for (int z = 0; z < length; z++) {
                int xFlag = flags.get(currX + width, currZ + z, level);
                if (!strategy.canMove(xFlag, 0) || (xFlag & EAST) != 0) {
                    return false;
                }
            }
            for (int x = 0; x < width; x++) {
                int zFlag = flags.get(currX + x, currZ - 1, level);
                if (!strategy.canMove(zFlag, 0) || (zFlag & SOUTH) != 0) {
                    return false;
                }
            }
        } else if (dx == -1 && dz == -1) {
            for (int z = 0; z < length; z++) {
                int xFlag = flags.get(currX - 1, currZ + z, level);
                if (!strategy.canMove(xFlag, 0) || (xFlag & WEST) != 0) {
                    return false;
                }
            }
            for (int x = 0; x < width; x++) {
                int zFlag = flags.get(currX + x, currZ - 1, level);
                if (!strategy.canMove(zFlag, 0) || (zFlag & SOUTH) != 0) {
                    return false;
                }
            }
        }

        return true;
    }
}
