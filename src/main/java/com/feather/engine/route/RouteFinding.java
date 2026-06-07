package com.feather.engine.route;

import java.util.Arrays;

import static com.feather.engine.route.CollisionFlag.*;

public final class RouteFinding {
    private static final int SEARCH_MAP_SIZE = 128;
    private static final int RING_BUFFER_SIZE = (SEARCH_MAP_SIZE * SEARCH_MAP_SIZE) / 4;
    private static final int MAX_ALTERNATIVE_ROUTE_LOWEST_COST = Integer.MAX_VALUE;
    private static final int MAX_ALTERNATIVE_ROUTE_SEEK_RANGE = 100;
    private static final int MAX_ALTERNATIVE_ROUTE_DISTANCE_FROM_DESTINATION = 10;
    private static final int DEFAULT_DISTANCE_VALUE = 99999999;

    private final CollisionStrategy strategy;
    private CollisionFlagMap flags;
    private int level;

    private final int[] directions = new int[SEARCH_MAP_SIZE * SEARCH_MAP_SIZE];
    private final int[] distances = new int[SEARCH_MAP_SIZE * SEARCH_MAP_SIZE];
    private final int[] validLocalX = new int[RING_BUFFER_SIZE];
    private final int[] validLocalZ = new int[RING_BUFFER_SIZE];

    private int bufReaderIndex;
    private int bufWriterIndex;
    private int currLocalX;
    private int currLocalZ;

    public RouteFinding(CollisionStrategy strategy) {
        this.strategy = strategy;
    }

    public RouteFinding() {
        this(CollisionStrategy.NORMAL);
    }

    public RouteResult findRoute(CollisionFlagMap flags, int level,
                                  int srcX, int srcZ, int destX, int destZ,
                                  int srcWidth, int srcLength,
                                  int destWidth, int destLength,
                                  int destShape, int destAngle,
                                  int blockAccessFlags, boolean findAlternative) {
        return findRoute(flags, level, srcX, srcZ, destX, destZ, srcWidth, srcLength,
                destWidth, destLength, destShape, destAngle, blockAccessFlags, findAlternative, 0);
    }

    public RouteResult findRoute(CollisionFlagMap flags, int level,
                                  int srcX, int srcZ, int destX, int destZ,
                                  int srcWidth, int srcLength,
                                  int destWidth, int destLength,
                                  int destShape, int destAngle,
                                  int blockAccessFlags, boolean findAlternative,
                                  int objectWidth) {
        this.flags = flags;
        this.level = level;
        reset();

        int graphBaseX = srcX - (SEARCH_MAP_SIZE / 2);
        int graphBaseZ = srcZ - (SEARCH_MAP_SIZE / 2);

        int startLocalX = srcX - graphBaseX;
        int startLocalZ = srcZ - graphBaseZ;

        int localDestX = destX - graphBaseX;
        int localDestZ = destZ - graphBaseZ;

        setDistance(startLocalX, startLocalZ, 0);

        bufReaderIndex = 0;
        bufWriterIndex = 0;
        appendDirection(startLocalX, startLocalZ, DirectionFlag.DIR_NONE, 0);

        boolean reached = false;
        while (bufReaderIndex != bufWriterIndex) {
            int localX = validLocalX[bufReaderIndex];
            int localZ = validLocalZ[bufReaderIndex];
            bufReaderIndex = (bufReaderIndex + 1) & (RING_BUFFER_SIZE - 1);

            int baseX = graphBaseX + localX;
            int baseZ = graphBaseZ + localZ;

            if (ReachStrategy.check(flags, level, baseX, baseZ, destX, destZ,
                    srcWidth, srcLength, destWidth, destLength,
                    destShape, destAngle, blockAccessFlags)) {
                currLocalX = localX;
                currLocalZ = localZ;
                reached = true;
                break;
            }

            if (!canMoveDiagonally(baseX, baseZ, localX, localZ, srcWidth, srcLength, objectWidth)) {
                continue;
            }

            int nextDistance = getDistance(localX, localZ) + 1;

            checkWest(baseX, baseZ, localX, localZ, srcWidth, srcLength, nextDistance, objectWidth);
            checkEast(baseX, baseZ, localX, localZ, srcWidth, srcLength, nextDistance, objectWidth);
            checkSouth(baseX, baseZ, localX, localZ, srcWidth, srcLength, nextDistance, objectWidth);
            checkNorth(baseX, baseZ, localX, localZ, srcWidth, srcLength, nextDistance, objectWidth);
            checkSouthWest(baseX, baseZ, localX, localZ, srcWidth, srcLength, nextDistance, objectWidth);
            checkSouthEast(baseX, baseZ, localX, localZ, srcWidth, srcLength, nextDistance, objectWidth);
            checkNorthWest(baseX, baseZ, localX, localZ, srcWidth, srcLength, nextDistance, objectWidth);
            checkNorthEast(baseX, baseZ, localX, localZ, srcWidth, srcLength, nextDistance, objectWidth);
        }

        currLocalX = currLocalX + graphBaseX;
        currLocalZ = currLocalZ + graphBaseZ;

        if (!reached) {
            if (!findAlternative) {
                return new RouteResult(new RouteCoordinates[0], 0, false);
            }
            boolean foundAlternative = findClosestApproachPoint(localDestX, localDestZ, destWidth, destLength);
            if (!foundAlternative) {
                return new RouteResult(new RouteCoordinates[0], 0, false);
            }
        }

        int exitX = currLocalX;
        int exitZ = currLocalZ;

        if (exitX == srcX && exitZ == srcZ) {
            return new RouteResult(new RouteCoordinates[]{new RouteCoordinates(exitX, exitZ)}, 1, !reached);
        }

        RouteCoordinates[] steps = tracePath(exitX, exitZ, srcX, srcZ, graphBaseX, graphBaseZ);

        return new RouteResult(steps, steps.length, !reached);
    }

    private RouteCoordinates[] tracePath(int exitX, int exitZ, int srcX, int srcZ, int graphBaseX, int graphBaseZ) {
        RouteCoordinates[] temp = new RouteCoordinates[SEARCH_MAP_SIZE * SEARCH_MAP_SIZE];
        int tempLen = 0;

        int traceX = exitX;
        int traceZ = exitZ;
        int dir = getDirection(traceX - graphBaseX, traceZ - graphBaseZ);
        int lastDir = dir;

        temp[tempLen++] = new RouteCoordinates(traceX, traceZ);

        while (traceX != srcX || traceZ != srcZ) {
            if (lastDir != dir) {
                temp[tempLen++] = new RouteCoordinates(traceX, traceZ);
                lastDir = dir;
            }

            if ((dir & DirectionFlag.DIR_EAST) != 0) traceX--;
            else if ((dir & DirectionFlag.DIR_WEST) != 0) traceX++;

            if ((dir & DirectionFlag.DIR_NORTH) != 0) traceZ--;
            else if ((dir & DirectionFlag.DIR_SOUTH) != 0) traceZ++;

            dir = getDirection(traceX - graphBaseX, traceZ - graphBaseZ);
        }

        RouteCoordinates[] result = new RouteCoordinates[tempLen];
        for (int i = 0; i < tempLen; i++) {
            result[i] = temp[tempLen - 1 - i];
        }
        return result;
    }

    private boolean canMoveDiagonally(int baseX, int baseZ, int localX, int localZ,
                                       int srcWidth, int srcLength, int objectWidth) {
        if (objectWidth <= 0) return true;
        if (widthToObjectSize(objectWidth) < srcWidth + 1) return true;
        for (int x = 0; x < srcWidth; x++) {
            for (int z = 0; z < srcLength; z++) {
                if (!strategy.canMove(flags.get(baseX + x, baseZ + z, level), 0)) {
                    return false;
                }
            }
        }
        return true;
    }

    private void checkWest(int baseX, int baseZ, int localX, int localZ,
                            int srcWidth, int srcLength, int nextDistance, int objectWidth) {
        if (localX <= 0) return;
        if (getDirection(localX - 1, localZ) != 0) return;

        int destX = baseX - 1;
        for (int z = 0; z < srcLength; z++) {
            if (!strategy.canMove(flags.get(destX, baseZ + z, level), WALL_WEST)) return;
        }

        int srcFlag = flags.get(baseX, baseZ, level);
        if ((srcFlag & BLOCKED) != 0) return;

        if (objectWidth > 0 && widthToObjectSize(objectWidth) > srcWidth) {
            if ((srcFlag & WALL_WEST) != 0 && (srcFlag & BLOCKED_WEST) != 0) return;
            if (objectBlocksDiagonal(baseX, baseZ, -1, 0, srcWidth, srcLength, objectWidth)) return;
        }

        appendDirection(localX - 1, localZ, DirectionFlag.DIR_EAST, nextDistance);
    }

    private void checkEast(int baseX, int baseZ, int localX, int localZ,
                            int srcWidth, int srcLength, int nextDistance, int objectWidth) {
        if (localX >= SEARCH_MAP_SIZE - srcWidth) return;
        if (getDirection(localX + 1, localZ) != 0) return;

        int destX = baseX + srcWidth;
        for (int z = 0; z < srcLength; z++) {
            if (!strategy.canMove(flags.get(destX, baseZ + z, level), WALL_EAST)) return;
        }

        int srcFlag = flags.get(baseX + srcWidth - 1, baseZ, level);
        if ((srcFlag & BLOCKED) != 0) return;

        if (objectWidth > 0 && widthToObjectSize(objectWidth) > srcWidth) {
            if ((srcFlag & WALL_EAST) != 0 && (srcFlag & BLOCKED_EAST) != 0) return;
            if (objectBlocksDiagonal(baseX, baseZ, 1, 0, srcWidth, srcLength, objectWidth)) return;
        }

        appendDirection(localX + 1, localZ, DirectionFlag.DIR_WEST, nextDistance);
    }

    private void checkSouth(int baseX, int baseZ, int localX, int localZ,
                             int srcWidth, int srcLength, int nextDistance, int objectWidth) {
        if (localZ <= 0) return;
        if (getDirection(localX, localZ - 1) != 0) return;

        int destZ = baseZ - 1;
        for (int x = 0; x < srcWidth; x++) {
            if (!strategy.canMove(flags.get(baseX + x, destZ, level), WALL_SOUTH)) return;
        }

        int srcFlag = flags.get(baseX, baseZ, level);
        if ((srcFlag & BLOCKED) != 0) return;

        if (objectWidth > 0 && widthToObjectSize(objectWidth) > srcWidth) {
            if ((srcFlag & WALL_SOUTH) != 0 && (srcFlag & BLOCKED_SOUTH) != 0) return;
            if (objectBlocksDiagonal(baseX, baseZ, 0, -1, srcWidth, srcLength, objectWidth)) return;
        }

        appendDirection(localX, localZ - 1, DirectionFlag.DIR_NORTH, nextDistance);
    }

    private void checkNorth(int baseX, int baseZ, int localX, int localZ,
                             int srcWidth, int srcLength, int nextDistance, int objectWidth) {
        if (localZ >= SEARCH_MAP_SIZE - srcLength) return;
        if (getDirection(localX, localZ + 1) != 0) return;

        int destZ = baseZ + srcLength;
        for (int x = 0; x < srcWidth; x++) {
            if (!strategy.canMove(flags.get(baseX + x, destZ, level), WALL_NORTH)) return;
        }

        int srcFlag = flags.get(baseX, baseZ + srcLength - 1, level);
        if ((srcFlag & BLOCKED) != 0) return;

        if (objectWidth > 0 && widthToObjectSize(objectWidth) > srcWidth) {
            if ((srcFlag & WALL_NORTH) != 0 && (srcFlag & BLOCKED_NORTH) != 0) return;
            if (objectBlocksDiagonal(baseX, baseZ, 0, 1, srcWidth, srcLength, objectWidth)) return;
        }

        appendDirection(localX, localZ + 1, DirectionFlag.DIR_SOUTH, nextDistance);
    }

    private void checkSouthWest(int baseX, int baseZ, int localX, int localZ,
                                 int srcWidth, int srcLength, int nextDistance, int objectWidth) {
        if (localX <= 0 || localZ <= 0) return;
        if (getDirection(localX - 1, localZ - 1) != 0) return;

        int destX = baseX - 1;
        int destZ = baseZ - 1;

        int cornerFlag = flags.get(destX, destZ, level);
        if (!strategy.canMove(cornerFlag, 0)) return;
        if ((cornerFlag & WALL_SOUTH) != 0 || (cornerFlag & WALL_WEST) != 0) return;

        int westFlag = flags.get(destX, baseZ, level);
        if (!strategy.canMove(westFlag, WALL_WEST)) return;

        int southFlag = flags.get(baseX, destZ, level);
        if (!strategy.canMove(southFlag, WALL_SOUTH)) return;

        if (objectWidth > 0 && widthToObjectSize(objectWidth) > srcWidth) {
            if (objectBlocksDiagonal(baseX, baseZ, -1, -1, srcWidth, srcLength, objectWidth)) return;
        }

        appendDirection(localX - 1, localZ - 1, DirectionFlag.DIR_NORTHEAST, nextDistance);
    }

    private void checkSouthEast(int baseX, int baseZ, int localX, int localZ,
                                 int srcWidth, int srcLength, int nextDistance, int objectWidth) {
        if (localX >= SEARCH_MAP_SIZE - srcWidth || localZ <= 0) return;
        if (getDirection(localX + 1, localZ - 1) != 0) return;

        int destX = baseX + srcWidth;
        int destZ = baseZ - 1;

        int cornerFlag = flags.get(destX, destZ, level);
        if (!strategy.canMove(cornerFlag, 0)) return;
        if ((cornerFlag & WALL_SOUTH) != 0 || (cornerFlag & WALL_EAST) != 0) return;

        int eastFlag = flags.get(destX, baseZ, level);
        if (!strategy.canMove(eastFlag, WALL_EAST)) return;

        int southFlag = flags.get(baseX + srcWidth - 1, destZ, level);
        if (!strategy.canMove(southFlag, WALL_SOUTH)) return;

        if (objectWidth > 0 && widthToObjectSize(objectWidth) > srcWidth) {
            if (objectBlocksDiagonal(baseX, baseZ, 1, -1, srcWidth, srcLength, objectWidth)) return;
        }

        appendDirection(localX + 1, localZ - 1, DirectionFlag.DIR_NORTHWEST, nextDistance);
    }

    private void checkNorthWest(int baseX, int baseZ, int localX, int localZ,
                                 int srcWidth, int srcLength, int nextDistance, int objectWidth) {
        if (localX <= 0 || localZ >= SEARCH_MAP_SIZE - srcLength) return;
        if (getDirection(localX - 1, localZ + 1) != 0) return;

        int destX = baseX - 1;
        int destZ = baseZ + srcLength;

        int cornerFlag = flags.get(destX, destZ, level);
        if (!strategy.canMove(cornerFlag, 0)) return;
        if ((cornerFlag & WALL_NORTH) != 0 || (cornerFlag & WALL_WEST) != 0) return;

        int westFlag = flags.get(destX, baseZ + srcLength - 1, level);
        if (!strategy.canMove(westFlag, WALL_WEST)) return;

        int northFlag = flags.get(baseX, destZ, level);
        if (!strategy.canMove(northFlag, WALL_NORTH)) return;

        if (objectWidth > 0 && widthToObjectSize(objectWidth) > srcWidth) {
            if (objectBlocksDiagonal(baseX, baseZ, -1, 1, srcWidth, srcLength, objectWidth)) return;
        }

        appendDirection(localX - 1, localZ + 1, DirectionFlag.DIR_SOUTHEAST, nextDistance);
    }

    private void checkNorthEast(int baseX, int baseZ, int localX, int localZ,
                                 int srcWidth, int srcLength, int nextDistance, int objectWidth) {
        if (localX >= SEARCH_MAP_SIZE - srcWidth || localZ >= SEARCH_MAP_SIZE - srcLength) return;
        if (getDirection(localX + 1, localZ + 1) != 0) return;

        int destX = baseX + srcWidth;
        int destZ = baseZ + srcLength;

        int cornerFlag = flags.get(destX, destZ, level);
        if (!strategy.canMove(cornerFlag, 0)) return;
        if ((cornerFlag & WALL_NORTH) != 0 || (cornerFlag & WALL_EAST) != 0) return;

        int eastFlag = flags.get(destX, baseZ, level);
        if (!strategy.canMove(eastFlag, WALL_EAST)) return;

        int northFlag = flags.get(baseX + srcWidth - 1, destZ, level);
        if (!strategy.canMove(northFlag, WALL_NORTH)) return;

        if (objectWidth > 0 && widthToObjectSize(objectWidth) > srcWidth) {
            if (objectBlocksDiagonal(baseX, baseZ, 1, 1, srcWidth, srcLength, objectWidth)) return;
        }

        appendDirection(localX + 1, localZ + 1, DirectionFlag.DIR_SOUTHWEST, nextDistance);
    }

    private boolean objectBlocksDiagonal(int baseX, int baseZ, int dx, int dz,
                                          int srcWidth, int srcLength, int objectWidth) {
        int objSize = widthToObjectSize(objectWidth);
        int checkX = dx == 0 ? baseX : (dx > 0 ? baseX + srcWidth - 1 + dx : baseX + dx);
        int checkZ = dz == 0 ? baseZ : (dz > 0 ? baseZ + srcLength - 1 + dz : baseZ + dz);

        for (int x = 0; x < objSize; x++) {
            for (int z = 0; z < objSize; z++) {
                if ((flags.get(checkX + x, checkZ + z, level) & OCCUPIED) != 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private static int widthToObjectSize(int objectWidth) {
        return objectWidth;
    }

    private boolean findClosestApproachPoint(int localDestX, int localDestZ,
                                              int destWidth, int destLength) {
        int lowestCost = MAX_ALTERNATIVE_ROUTE_LOWEST_COST;
        int maxAlternativePath = MAX_ALTERNATIVE_ROUTE_SEEK_RANGE;
        int altRange = MAX_ALTERNATIVE_ROUTE_DISTANCE_FROM_DESTINATION;

        for (int x = localDestX - altRange; x <= localDestX + altRange; x++) {
            for (int z = localDestZ - altRange; z <= localDestZ + altRange; z++) {
                if (x < 0 || x >= SEARCH_MAP_SIZE || z < 0 || z >= SEARCH_MAP_SIZE) continue;
                if (getDistance(x, z) >= MAX_ALTERNATIVE_ROUTE_SEEK_RANGE) continue;

                int dx;
                if (x < localDestX) {
                    dx = localDestX - x;
                } else if (x > localDestX + destWidth - 1) {
                    dx = x - (destWidth + localDestX - 1);
                } else {
                    dx = 0;
                }

                int dz;
                if (z < localDestZ) {
                    dz = localDestZ - z;
                } else if (z > localDestZ + destLength - 1) {
                    dz = z - (localDestZ + destLength - 1);
                } else {
                    dz = 0;
                }

                int cost = dx * dx + dz * dz;
                if (cost < lowestCost || (cost == lowestCost && maxAlternativePath > getDistance(x, z))) {
                    currLocalX = x;
                    currLocalZ = z;
                    lowestCost = cost;
                    maxAlternativePath = getDistance(x, z);
                }
            }
        }

        return lowestCost != MAX_ALTERNATIVE_ROUTE_LOWEST_COST;
    }

    private void appendDirection(int localX, int localZ, int direction, int distance) {
        int index = (localX * SEARCH_MAP_SIZE) + localZ;
        directions[index] = direction;
        distances[index] = distance;
        validLocalX[bufWriterIndex] = localX;
        validLocalZ[bufWriterIndex] = localZ;
        bufWriterIndex = (bufWriterIndex + 1) & (RING_BUFFER_SIZE - 1);
    }

    private void reset() {
        Arrays.fill(directions, 0);
        Arrays.fill(distances, DEFAULT_DISTANCE_VALUE);
        bufReaderIndex = 0;
        bufWriterIndex = 0;
    }

    private int getDirection(int localX, int localZ) {
        return directions[(localX * SEARCH_MAP_SIZE) + localZ];
    }

    private void setDistance(int localX, int localZ, int distance) {
        distances[(localX * SEARCH_MAP_SIZE) + localZ] = distance;
    }

    private int getDistance(int localX, int localZ) {
        return distances[(localX * SEARCH_MAP_SIZE) + localZ];
    }

    public static RouteCoordinates naiveDestination(
            int sourceX, int sourceZ, int sourceWidth, int sourceLength,
            int targetX, int targetZ, int targetWidth, int targetLength,
            int targetAngle) {
        int diagonal = (sourceX - targetX) + (sourceZ - targetZ);
        int anti = (sourceX - targetX) - (sourceZ - targetZ);
        int rotatedWidth = Rotations.rotate(targetAngle, targetWidth, targetLength);
        int rotatedLength = Rotations.rotate(targetAngle, targetLength, targetWidth);
        boolean southWestClockwise = anti < 0;
        boolean northWestClockwise = diagonal >= (rotatedLength - 1) - (sourceWidth - 1);
        boolean northEastClockwise = anti > sourceWidth - sourceLength;
        boolean southEastClockwise = diagonal <= (rotatedWidth - 1) - (sourceLength - 1);

        RouteCoordinates target = new RouteCoordinates(targetX, targetZ);
        if (southWestClockwise && !northWestClockwise) {
            int offZ;
            if (diagonal >= -sourceWidth) {
                offZ = Math.min(diagonal + sourceWidth, rotatedLength - 1);
            } else if (anti > -sourceWidth) {
                offZ = -(sourceWidth + anti);
            } else {
                offZ = 0;
            }
            return target.translate(-sourceWidth, offZ);
        } else if (northWestClockwise && !northEastClockwise) {
            int offX;
            if (anti >= -rotatedLength) {
                offX = Math.min(anti + rotatedLength, rotatedWidth - 1);
            } else if (diagonal < rotatedLength) {
                offX = Math.max(diagonal - rotatedLength, -(sourceWidth - 1));
            } else {
                offX = 0;
            }
            return target.translate(offX, rotatedLength);
        } else if (northEastClockwise && !southEastClockwise) {
            int offZ;
            if (anti <= rotatedWidth) {
                offZ = rotatedLength - anti;
            } else if (diagonal < rotatedWidth) {
                offZ = Math.max(diagonal - rotatedWidth, -(sourceLength - 1));
            } else {
                offZ = 0;
            }
            return target.translate(rotatedWidth, offZ);
        } else {
            int offX;
            if (diagonal > -sourceLength) {
                offX = Math.min(diagonal + sourceLength, rotatedWidth - 1);
            } else if (anti < sourceLength) {
                offX = Math.max(anti - sourceLength, -(sourceLength - 1));
            } else {
                offX = 0;
            }
            return target.translate(offX, -sourceLength);
        }
    }
}
