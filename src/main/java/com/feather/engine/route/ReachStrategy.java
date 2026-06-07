package com.feather.engine.route;

import static com.feather.engine.route.CollisionFlag.SOLID;
import static com.feather.engine.route.CollisionFlag.WALL_DECO;
import static com.feather.engine.route.CollisionFlag.WALL_EAST;
import static com.feather.engine.route.CollisionFlag.WALL_NORTH;
import static com.feather.engine.route.CollisionFlag.WALL_SOUTH;
import static com.feather.engine.route.CollisionFlag.WALL_WEST;

public final class ReachStrategy {
    public static final int NO_STRATEGY = -1;
    public static final int WALL_STRATEGY = 0;
    public static final int WALL_DECO_STRATEGY = 1;
    public static final int RECTANGLE_STRATEGY = 2;
    public static final int RECTANGLE_EXCLUSIVE_STRATEGY = 3;

    private static final int WALLDECOR_DIAGONAL_NOOFFSET_SHAPE = 12;

    public static boolean check(
            CollisionFlagMap flags, int level,
            int srcX, int srcZ, int destX, int destZ,
            int srcSize, int destShape, int destAngle) {
        return check(flags, level, srcX, srcZ, destX, destZ, srcSize, srcSize, destShape, destAngle);
    }

    public static boolean check(
            CollisionFlagMap flags, int level,
            int srcX, int srcZ, int destX, int destZ,
            int srcWidth, int srcLength, int destShape, int destAngle) {
        return check(flags, level, srcX, srcZ, destX, destZ, srcWidth, srcLength, 1, 1, destShape, destAngle, 0);
    }

    public static boolean check(
            CollisionFlagMap flags, int level,
            int srcX, int srcZ, int destX, int destZ,
            int srcWidth, int srcLength, int destWidth, int destLength,
            int destShape, int destAngle, int blockAccessFlags) {
        return switch (exitStrategy(destShape)) {
            case WALL_STRATEGY -> reachWall(flags, level, srcX, srcZ, destX, destZ, srcWidth, srcLength, destAngle, destShape);
            case WALL_DECO_STRATEGY -> reachWallDeco(flags, level, srcX, srcZ, destX, destZ, srcWidth, srcLength, destAngle, destShape);
            case RECTANGLE_STRATEGY -> reachRectangle(flags, level, srcX, srcZ, destX, destZ, srcWidth, srcLength, destWidth, destLength, blockAccessFlags);
            case RECTANGLE_EXCLUSIVE_STRATEGY -> reachExclusiveRectangle(srcX, srcZ, destX, destZ, srcWidth, srcLength, destWidth, destLength);
            default -> false;
        };
    }

    public static boolean reachWall(
            CollisionFlagMap flags, int level,
            int srcX, int srcZ, int destX, int destZ,
            int srcWidth, int srcLength, int destAngle, int destShape) {
        boolean straight = destShape == 9;

        if (srcWidth == 1 && srcLength == 1) {
            return reachWall1(flags, level, srcX, srcZ, destX, destZ, destAngle, straight);
        }
        return reachWallN(flags, level, srcX, srcZ, destX, destZ, srcWidth, srcLength, destAngle, straight);
    }

    private static boolean reachWall1(
            CollisionFlagMap flags, int level,
            int srcX, int srcZ, int destX, int destZ,
            int destAngle, boolean straight) {
        if (srcX == destX && srcZ == destZ) {
            return true;
        }

        int angle = alteredAngle(destAngle, -1);

        if (straight) {
            if (angle == 0) {
                return srcX == destX - 1 && srcZ == destZ &&
                        (flags.get(srcX, srcZ, level) & WALL_EAST) == 0;
            }
            if (angle == 1) {
                return srcX == destX && srcZ == destZ + 1 &&
                        (flags.get(srcX, srcZ, level) & WALL_SOUTH) == 0;
            }
            if (angle == 2) {
                return srcX == destX + 1 && srcZ == destZ &&
                        (flags.get(srcX, srcZ, level) & WALL_WEST) == 0;
            }
            if (angle == 3) {
                return srcX == destX && srcZ == destZ - 1 &&
                        (flags.get(srcX, srcZ, level) & WALL_NORTH) == 0;
            }
            return false;
        }

        if (angle == 0) {
            if (srcX == destX - 1 && srcZ == destZ) {
                return true;
            }
            if (srcX == destX && srcZ == destZ + 1 &&
                    (flags.get(srcX, srcZ, level) & (SOLID | WALL_SOUTH)) == 0) {
                return true;
            }
            if (srcX == destX && srcZ == destZ - 1 &&
                    (flags.get(srcX, srcZ, level) & (SOLID | WALL_NORTH)) == 0) {
                return true;
            }
            return false;
        }

        if (angle == 1) {
            if (srcX == destX && srcZ == destZ + 1) {
                return true;
            }
            if (srcX == destX - 1 && srcZ == destZ &&
                    (flags.get(srcX, srcZ, level) & (SOLID | WALL_EAST)) == 0) {
                return true;
            }
            if (srcX == destX + 1 && srcZ == destZ &&
                    (flags.get(srcX, srcZ, level) & (SOLID | WALL_WEST)) == 0) {
                return true;
            }
            return false;
        }

        if (angle == 2) {
            if (srcX == destX + 1 && srcZ == destZ) {
                return true;
            }
            if (srcX == destX && srcZ == destZ + 1 &&
                    (flags.get(srcX, srcZ, level) & (SOLID | WALL_SOUTH)) == 0) {
                return true;
            }
            if (srcX == destX && srcZ == destZ - 1 &&
                    (flags.get(srcX, srcZ, level) & (SOLID | WALL_NORTH)) == 0) {
                return true;
            }
            return false;
        }

        if (angle == 3) {
            if (srcX == destX && srcZ == destZ - 1) {
                return true;
            }
            if (srcX == destX - 1 && srcZ == destZ &&
                    (flags.get(srcX, srcZ, level) & (SOLID | WALL_EAST)) == 0) {
                return true;
            }
            if (srcX == destX + 1 && srcZ == destZ &&
                    (flags.get(srcX, srcZ, level) & (SOLID | WALL_WEST)) == 0) {
                return true;
            }
            return false;
        }

        return false;
    }

    private static boolean reachWallN(
            CollisionFlagMap flags, int level,
            int srcX, int srcZ, int destX, int destZ,
            int srcWidth, int srcLength, int destAngle, boolean straight) {
        int srcEast = srcX + srcWidth - 1;
        int srcNorth = srcZ + srcLength - 1;

        if (destAngle == 0) {
            if (srcX == destX - srcWidth && srcZ <= destZ && srcNorth >= destZ) {
                return true;
            }
            if (destX >= srcX && destX <= srcEast && srcZ == destZ + 1 &&
                    (flags.get(destX, srcZ, level) & (SOLID | WALL_SOUTH)) == 0) {
                return true;
            }
            if (destX >= srcX && destX <= srcEast && srcNorth == destZ - 1 &&
                    (flags.get(destX, srcNorth, level) & (SOLID | WALL_NORTH)) == 0) {
                return true;
            }
            return false;
        }

        if (destAngle == 1) {
            if (destX >= srcX && destX <= srcEast && srcZ == destZ + 1) {
                return true;
            }
            if (srcX == destX - srcWidth && srcZ <= destZ && srcNorth >= destZ &&
                    (flags.get(srcEast, destZ, level) & (SOLID | WALL_EAST)) == 0) {
                return true;
            }
            if (srcEast == destX - 1 + 1 && srcZ <= destZ && srcNorth >= destZ &&
                    (flags.get(srcEast + 1, destZ, level) & (SOLID | WALL_WEST)) == 0) {
                return true;
            }
            return false;
        }

        if (destAngle == 2) {
            if (srcEast == destX - 1 && srcZ <= destZ && srcNorth >= destZ) {
                return true;
            }
            if (destX >= srcX && destX <= srcEast && srcZ == destZ + 1 &&
                    (flags.get(destX, srcZ, level) & (SOLID | WALL_SOUTH)) == 0) {
                return true;
            }
            if (destX >= srcX && destX <= srcEast && srcNorth == destZ - 1 &&
                    (flags.get(destX, srcNorth, level) & (SOLID | WALL_NORTH)) == 0) {
                return true;
            }
            return false;
        }

        if (destAngle == 3) {
            if (destX >= srcX && destX <= srcEast && srcNorth == destZ - 1) {
                return true;
            }
            if (srcX == destX - srcWidth && srcZ <= destZ && srcNorth >= destZ &&
                    (flags.get(srcEast, destZ, level) & (SOLID | WALL_EAST)) == 0) {
                return true;
            }
            if (srcEast == destX - 1 + 1 && srcZ <= destZ && srcNorth >= destZ &&
                    (flags.get(srcEast + 1, destZ, level) & (SOLID | WALL_WEST)) == 0) {
                return true;
            }
            return false;
        }

        return false;
    }

    public static boolean reachWallDeco(
            CollisionFlagMap flags, int level,
            int srcX, int srcZ, int destX, int destZ,
            int srcWidth, int srcLength, int destAngle, int destShape) {
        if (srcWidth == 1 && srcLength == 1) {
            return reachWallDeco1(flags, level, srcX, srcZ, destX, destZ, destAngle, destShape);
        }
        return reachWallDecoN(flags, level, srcX, srcZ, destX, destZ, srcWidth, srcLength, destAngle, destShape);
    }

    private static boolean reachWallDeco1(
            CollisionFlagMap flags, int level,
            int srcX, int srcZ, int destX, int destZ,
            int destAngle, int destShape) {
        if (srcX == destX && srcZ == destZ) {
            return true;
        }

        if (destShape != 6 && destShape != 7 && destShape != 8) {
            return false;
        }

        int angle = alteredAngle(destAngle, destShape);

        if (destShape == 6 || destShape == 7) {
            if (angle == 0) {
                if (srcX == destX + 1 && srcZ == destZ &&
                        (flags.get(srcX, srcZ, level) & WALL_WEST) == 0) {
                    return true;
                }
                if (srcX == destX && srcZ == destZ - 1 &&
                        (flags.get(srcX, srcZ, level) & WALL_NORTH) == 0) {
                    return true;
                }
                return false;
            }
            if (angle == 1) {
                if (srcX == destX - 1 && srcZ == destZ &&
                        (flags.get(srcX, srcZ, level) & WALL_EAST) == 0) {
                    return true;
                }
                if (srcX == destX && srcZ == destZ - 1 &&
                        (flags.get(srcX, srcZ, level) & WALL_NORTH) == 0) {
                    return true;
                }
                return false;
            }
            if (angle == 2) {
                if (srcX == destX - 1 && srcZ == destZ &&
                        (flags.get(srcX, srcZ, level) & WALL_EAST) == 0) {
                    return true;
                }
                if (srcX == destX && srcZ == destZ + 1 &&
                        (flags.get(srcX, srcZ, level) & WALL_SOUTH) == 0) {
                    return true;
                }
                return false;
            }
            if (angle == 3) {
                if (srcX == destX + 1 && srcZ == destZ &&
                        (flags.get(srcX, srcZ, level) & WALL_WEST) == 0) {
                    return true;
                }
                if (srcX == destX && srcZ == destZ + 1 &&
                        (flags.get(srcX, srcZ, level) & WALL_SOUTH) == 0) {
                    return true;
                }
                return false;
            }
            return false;
        }

        if (destShape == 8) {
            if (srcX == destX && srcZ == destZ + 1 &&
                    (flags.get(srcX, srcZ, level) & WALL_SOUTH) == 0) {
                return true;
            }
            if (srcX == destX && srcZ == destZ - 1 &&
                    (flags.get(srcX, srcZ, level) & WALL_NORTH) == 0) {
                return true;
            }
            if (srcX == destX - 1 && srcZ == destZ &&
                    (flags.get(srcX, srcZ, level) & WALL_EAST) == 0) {
                return true;
            }
            if (srcX == destX + 1 && srcZ == destZ &&
                    (flags.get(srcX, srcZ, level) & WALL_WEST) == 0) {
                return true;
            }
            return false;
        }

        return false;
    }

    private static boolean reachWallDecoN(
            CollisionFlagMap flags, int level,
            int srcX, int srcZ, int destX, int destZ,
            int srcWidth, int srcLength, int destAngle, int destShape) {
        if (destShape != 6 && destShape != 7 && destShape != 8) {
            return false;
        }

        int angle = alteredAngle(destAngle, destShape);
        int srcEast = srcX + srcWidth - 1;
        int srcNorth = srcZ + srcLength - 1;

        if (destShape == 6 || destShape == 7) {
            if (angle == 0) {
                if (srcEast >= destX && srcX <= destX && srcZ <= destZ && srcNorth >= destZ) {
                    if (srcX == destX + 1 && srcZ <= destZ && srcNorth >= destZ &&
                            (flags.get(srcX, destZ, level) & (WALL_WEST | WALL_DECO)) == 0) {
                        return true;
                    }
                    if (destX >= srcX && destX <= srcEast && srcZ == destZ - 1 &&
                            (flags.get(destX, srcZ, level) & (WALL_NORTH | WALL_DECO)) == 0) {
                        return true;
                    }
                }
                return false;
            }
            if (angle == 1) {
                if (destX >= srcX && destX <= srcEast && srcZ <= destZ && srcNorth >= destZ) {
                    if (srcX == destX - 1 && srcZ <= destZ && srcNorth >= destZ &&
                            (flags.get(srcEast, destZ, level) & (WALL_EAST | WALL_DECO)) == 0) {
                        return true;
                    }
                    if (destX >= srcX && destX <= srcEast && srcZ == destZ - 1 &&
                            (flags.get(destX, srcZ, level) & (WALL_NORTH | WALL_DECO)) == 0) {
                        return true;
                    }
                }
                return false;
            }
            if (angle == 2) {
                if (srcEast >= destX && srcX <= destX && srcZ <= destZ && srcNorth >= destZ) {
                    if (srcX == destX - 1 && srcZ <= destZ && srcNorth >= destZ &&
                            (flags.get(srcEast, destZ, level) & (WALL_EAST | WALL_DECO)) == 0) {
                        return true;
                    }
                    if (destX >= srcX && destX <= srcEast && srcNorth == destZ + 1 &&
                            (flags.get(destX, srcNorth, level) & (WALL_SOUTH | WALL_DECO)) == 0) {
                        return true;
                    }
                }
                return false;
            }
            if (angle == 3) {
                if (destX >= srcX && destX <= srcEast && srcZ <= destZ && srcNorth >= destZ) {
                    if (srcEast >= destX && srcX <= destX && srcNorth == destZ + 1 &&
                            (flags.get(destX, srcNorth, level) & (WALL_SOUTH | WALL_DECO)) == 0) {
                        return true;
                    }
                    if (srcX == destX + 1 && srcZ <= destZ && srcNorth >= destZ &&
                            (flags.get(srcX, destZ, level) & (WALL_WEST | WALL_DECO)) == 0) {
                        return true;
                    }
                }
                return false;
            }
            return false;
        }

        if (destShape == 8) {
            if (destX >= srcX && destX <= srcEast && srcZ == destZ + 1 &&
                    (flags.get(destX, srcZ, level) & (WALL_SOUTH | WALL_DECO)) == 0) {
                return true;
            }
            if (destX >= srcX && destX <= srcEast && srcNorth == destZ - 1 &&
                    (flags.get(destX, srcNorth, level) & (WALL_NORTH | WALL_DECO)) == 0) {
                return true;
            }
            if (srcX == destX - 1 && srcZ <= destZ && srcNorth >= destZ &&
                    (flags.get(srcEast, destZ, level) & (WALL_EAST | WALL_DECO)) == 0) {
                return true;
            }
            if (srcEast == destX + 1 && srcZ <= destZ && srcNorth >= destZ &&
                    (flags.get(destX + 1, destZ, level) & (WALL_WEST | WALL_DECO)) == 0) {
                return true;
            }
            return false;
        }

        return false;
    }

    public static boolean reachRectangle(
            CollisionFlagMap flags, int level,
            int srcX, int srcZ, int destX, int destZ,
            int srcWidth, int srcLength, int destWidth, int destLength,
            int blockAccessFlags) {
        if (RectangularBounds.collides(srcX, srcZ, destX, destZ, srcWidth, srcLength, destWidth, destLength)) {
            return true;
        }

        if (srcWidth == 1 && srcLength == 1) {
            return RectangularBounds.reachRectangle1(flags, level, srcX, srcZ, destX, destZ, destWidth, destLength, blockAccessFlags);
        }
        return RectangularBounds.reachRectangleN(flags, level, srcX, srcZ, destX, destZ, srcWidth, srcLength, destWidth, destLength, blockAccessFlags);
    }

    public static boolean reachExclusiveRectangle(
            int srcX, int srcZ, int destX, int destZ,
            int srcWidth, int srcLength, int destWidth, int destLength) {
        if (RectangularBounds.collides(srcX, srcZ, destX, destZ, srcWidth, srcLength, destWidth, destLength)) {
            return true;
        }
        if (srcWidth == 1 && srcLength == 1) {
            return reachExclusiveRectangle1(srcX, srcZ, destX, destZ, destWidth, destLength);
        }
        return reachExclusiveRectangleN(srcX, srcZ, destX, destZ, srcWidth, srcLength, destWidth, destLength);
    }

    private static boolean reachExclusiveRectangle1(
            int srcX, int srcZ, int destX, int destZ, int destWidth, int destLength) {
        return srcX >= destX && srcX <= destX + destWidth - 1 &&
                srcZ >= destZ && srcZ <= destZ + destLength - 1;
    }

    private static boolean reachExclusiveRectangleN(
            int srcX, int srcZ, int destX, int destZ,
            int srcWidth, int srcLength, int destWidth, int destLength) {
        int east = destX + destWidth;
        int north = destZ + destLength;
        int srcEast = srcX + srcWidth;
        int srcNorth = srcZ + srcLength;

        if (srcEast >= destX && srcX <= east && srcNorth >= destZ && srcZ <= north) {
            if (srcEast <= east && srcNorth <= north && srcX >= destX && srcZ >= destZ) {
                return true;
            }
            int overlapWest = Math.max(srcX, destX);
            int overlapEast = Math.min(srcEast, east);
            int overlapSouth = Math.max(srcZ, destZ);
            int overlapNorth = Math.min(srcNorth, north);
            int overlapWidth = overlapEast - overlapWest;
            int overlapLength = overlapNorth - overlapSouth;
            int srcArea = srcWidth * srcLength;
            int destArea = destWidth * destLength;
            int overlapArea = overlapWidth * overlapLength;
            return (overlapArea == srcArea || overlapArea == destArea);
        }

        return false;
    }

    private static int exitStrategy(int locShape) {
        if (locShape == -2) {
            return RECTANGLE_EXCLUSIVE_STRATEGY;
        }
        if (locShape == -1) {
            return NO_STRATEGY;
        }
        if ((locShape >= 0 && locShape <= 3) || locShape == 9) {
            return WALL_STRATEGY;
        }
        if (locShape < 9) {
            return WALL_DECO_STRATEGY;
        }
        if (locShape == 10 || locShape == 11 || locShape == 22) {
            return RECTANGLE_STRATEGY;
        }
        return NO_STRATEGY;
    }

    static int alteredAngle(int angle, int shape) {
        if (shape == WALLDECOR_DIAGONAL_NOOFFSET_SHAPE) {
            return (angle + 2) & 0x3;
        }
        return angle;
    }
}
