package com.feather.engine.route;

import static com.feather.engine.route.CollisionFlag.WALL_EAST;
import static com.feather.engine.route.CollisionFlag.WALL_NORTH;
import static com.feather.engine.route.CollisionFlag.WALL_SOUTH;
import static com.feather.engine.route.CollisionFlag.WALL_WEST;

public final class RectangularBounds {
    public static boolean collides(
            int srcX, int srcZ, int destX, int destZ,
            int srcWidth, int srcLength, int destWidth, int destLength) {
        if (srcX >= destX + destWidth || srcX + srcWidth <= destX) {
            return false;
        }
        return srcZ < destZ + destLength && destZ < srcLength + srcZ;
    }

    static boolean reachRectangle1(
            CollisionFlagMap flags, int level,
            int srcX, int srcZ, int destX, int destZ,
            int destWidth, int destLength, int blockAccessFlags) {
        int east = destX + destWidth - 1;
        int north = destZ + destLength - 1;

        if (srcX == destX - 1 &&
                srcZ >= destZ && srcZ <= north &&
                (flags.get(srcX, srcZ, level) & WALL_EAST) == 0 &&
                (blockAccessFlags & BlockAccessFlag.WEST) == 0) {
            return true;
        }

        if (srcX == east + 1 &&
                srcZ >= destZ && srcZ <= north &&
                (flags.get(srcX, srcZ, level) & WALL_WEST) == 0 &&
                (blockAccessFlags & BlockAccessFlag.EAST) == 0) {
            return true;
        }

        if (srcZ + 1 == destZ &&
                srcX >= destX && srcX <= east &&
                (flags.get(srcX, srcZ, level) & WALL_NORTH) == 0 &&
                (blockAccessFlags & BlockAccessFlag.SOUTH) == 0) {
            return true;
        }

        return srcZ == north + 1 &&
                srcX >= destX && srcX <= east &&
                (flags.get(srcX, srcZ, level) & WALL_SOUTH) == 0 &&
                (blockAccessFlags & BlockAccessFlag.NORTH) == 0;
    }

    static boolean reachRectangleN(
            CollisionFlagMap flags, int level,
            int srcX, int srcZ, int destX, int destZ,
            int srcWidth, int srcLength, int destWidth, int destLength,
            int blockAccessFlags) {
        int srcEast = srcX + srcWidth;
        int srcNorth = srcLength + srcZ;
        int destEast = destWidth + destX;
        int destNorth = destLength + destZ;

        if (destEast == srcX && (blockAccessFlags & BlockAccessFlag.EAST) == 0) {
            int fromZ = Math.max(srcZ, destZ);
            int toZ = Math.min(srcNorth, destNorth);
            for (int sideZ = fromZ; sideZ < toZ; sideZ++) {
                if ((flags.get(destEast - 1, sideZ, level) & WALL_EAST) == 0) {
                    return true;
                }
            }
        } else if (srcEast == destX && (blockAccessFlags & BlockAccessFlag.WEST) == 0) {
            int fromZ = Math.max(srcZ, destZ);
            int toZ = Math.min(srcNorth, destNorth);
            for (int sideZ = fromZ; sideZ < toZ; sideZ++) {
                if ((flags.get(destX, sideZ, level) & WALL_WEST) == 0) {
                    return true;
                }
            }
        } else if (srcZ == destNorth && (blockAccessFlags & BlockAccessFlag.NORTH) == 0) {
            int fromX = Math.max(srcX, destX);
            int toX = Math.min(srcEast, destEast);
            for (int sideX = fromX; sideX < toX; sideX++) {
                if ((flags.get(sideX, destNorth - 1, level) & WALL_NORTH) == 0) {
                    return true;
                }
            }
        } else if (destZ == srcNorth && (blockAccessFlags & BlockAccessFlag.SOUTH) == 0) {
            int fromX = Math.max(srcX, destX);
            int toX = Math.min(srcEast, destEast);
            for (int sideX = fromX; sideX < toX; sideX++) {
                if ((flags.get(sideX, destZ, level) & WALL_SOUTH) == 0) {
                    return true;
                }
            }
        }
        return false;
    }
}
