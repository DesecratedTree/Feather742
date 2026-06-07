package com.feather.engine.route;

public final class CollisionFlag {
    public static final int OPEN = 0x0;
    public static final int WALL_NORTH_WEST = 0x1;
    public static final int WALL_NORTH = 0x2;
    public static final int WALL_NORTH_EAST = 0x4;
    public static final int WALL_EAST = 0x8;
    public static final int WALL_SOUTH_EAST = 0x10;
    public static final int WALL_SOUTH = 0x20;
    public static final int WALL_SOUTH_WEST = 0x40;
    public static final int WALL_WEST = 0x80;
    public static final int LOC = 0x100;
    public static final int WALL_NORTH_WEST_PROJ_BLOCKER = 0x200;
    public static final int WALL_NORTH_PROJ_BLOCKER = 0x400;
    public static final int WALL_NORTH_EAST_PROJ_BLOCKER = 0x800;
    public static final int WALL_EAST_PROJ_BLOCKER = 0x1000;
    public static final int WALL_SOUTH_EAST_PROJ_BLOCKER = 0x2000;
    public static final int WALL_SOUTH_PROJ_BLOCKER = 0x4000;
    public static final int WALL_SOUTH_WEST_PROJ_BLOCKER = 0x8000;
    public static final int WALL_WEST_PROJ_BLOCKER = 0x10000;
    public static final int LOC_PROJ_BLOCKER = 0x20000;
    public static final int GROUND_DECOR = 0x40000;
    public static final int BLOCK_NPCS = 0x80000;
    public static final int BLOCK_PLAYERS = 0x100000;
    public static final int BLOCK_WALK = 0x200000;
    public static final int WALL_NORTH_WEST_ROUTE_BLOCKER = 0x400000;
    public static final int WALL_NORTH_ROUTE_BLOCKER = 0x800000;
    public static final int WALL_NORTH_EAST_ROUTE_BLOCKER = 0x1000000;
    public static final int WALL_EAST_ROUTE_BLOCKER = 0x2000000;
    public static final int WALL_SOUTH_EAST_ROUTE_BLOCKER = 0x4000000;
    public static final int WALL_SOUTH_ROUTE_BLOCKER = 0x8000000;
    public static final int WALL_SOUTH_WEST_ROUTE_BLOCKER = 0x10000000;
    public static final int WALL_WEST_ROUTE_BLOCKER = 0x20000000;
    public static final int LOC_ROUTE_BLOCKER = 0x40000000;
    public static final int ROOF = 0x80000000;

    public static final int NORTH_WEST = WALL_NORTH_WEST;
    public static final int NORTH = WALL_NORTH;
    public static final int NORTH_EAST = WALL_NORTH_EAST;
    public static final int EAST = WALL_EAST;
    public static final int SOUTH_EAST = WALL_SOUTH_EAST;
    public static final int SOUTH = WALL_SOUTH;
    public static final int SOUTH_WEST = WALL_SOUTH_WEST;
    public static final int WEST = WALL_WEST;
    public static final int OCCUPIED = LOC;
    public static final int SOLID = BLOCK_WALK;
    public static final int INIT = 0x1000000;
    public static final int BLOCKED = 0x400000;
    public static final int CLOSED = 0x800000;
    public static final int BLOCKED_NORTH = WALL_NORTH_PROJ_BLOCKER;
    public static final int BLOCKED_EAST = WALL_EAST_PROJ_BLOCKER;
    public static final int BLOCKED_SOUTH = WALL_SOUTH_PROJ_BLOCKER;
    public static final int BLOCKED_WEST = WALL_WEST_PROJ_BLOCKER;
    public static final int BLOCKED_NORTHEAST = WALL_NORTH_EAST_PROJ_BLOCKER;
    public static final int BLOCKED_NORTHWEST = WALL_NORTH_WEST_PROJ_BLOCKER;
    public static final int BLOCKED_SOUTHEAST = WALL_SOUTH_EAST_PROJ_BLOCKER;
    public static final int BLOCKED_SOUTHWEST = WALL_SOUTH_WEST_PROJ_BLOCKER;
    public static final int LOW_OBJECT = GROUND_DECOR;
    public static final int WALKABLE = 0x20000000;
    public static final int BLOCKED_NORTH_WALL = WALL_NORTH_ROUTE_BLOCKER;
    public static final int BLOCKED_EAST_WALL = WALL_EAST_ROUTE_BLOCKER;
    public static final int BLOCKED_SOUTH_WALL = WALL_SOUTH_ROUTE_BLOCKER;
    public static final int BLOCKED_WEST_WALL = WALL_WEST_ROUTE_BLOCKER;
    public static final int BLOCKED_NORTHEAST_WALL = WALL_NORTH_EAST_ROUTE_BLOCKER;
    public static final int BLOCKED_NORTHWEST_WALL = WALL_NORTH_WEST_ROUTE_BLOCKER;
    public static final int BLOCKED_SOUTHEAST_WALL = WALL_SOUTH_EAST_ROUTE_BLOCKER;
    public static final int BLOCKED_SOUTHWEST_WALL = WALL_SOUTH_WEST_ROUTE_BLOCKER;
    public static final int WATER = 0x100000;
    public static final int FLOOR_DECORATION = GROUND_DECOR;
    public static final int WALL_DECO = GROUND_DECOR;

    public static boolean blocked(int flag, int shape) {
        if ((flag & SOLID) != 0) return true;
        if ((flag & CLOSED) != 0) return true;
        if ((flag & INIT) == 0) return true;
        if ((shape & NORTH) != 0 && (flag & BLOCKED_NORTH) != 0) return true;
        if ((shape & EAST) != 0 && (flag & BLOCKED_EAST) != 0) return true;
        if ((shape & SOUTH) != 0 && (flag & BLOCKED_SOUTH) != 0) return true;
        if ((shape & WEST) != 0 && (flag & BLOCKED_WEST) != 0) return true;
        return false;
    }

    public static boolean blockedWall(int destFlag, int currFlag, int dirFlag) {
        if ((destFlag & SOLID) != 0) return true;
        if ((destFlag & CLOSED) != 0) return true;
        if ((destFlag & INIT) == 0) return true;
        if ((destFlag & BLOCKED) != 0) return true;
        if ((dirFlag & NORTH) != 0 && (destFlag & BLOCKED_NORTH) != 0) return true;
        if ((dirFlag & EAST) != 0 && (destFlag & BLOCKED_EAST) != 0) return true;
        if ((dirFlag & SOUTH) != 0 && (destFlag & BLOCKED_SOUTH) != 0) return true;
        if ((dirFlag & WEST) != 0 && (destFlag & BLOCKED_WEST) != 0) return true;
        if ((dirFlag & NORTH) != 0 && (currFlag & BLOCKED_NORTH) != 0) return true;
        if ((dirFlag & EAST) != 0 && (currFlag & BLOCKED_EAST) != 0) return true;
        if ((dirFlag & SOUTH) != 0 && (currFlag & BLOCKED_SOUTH) != 0) return true;
        if ((dirFlag & WEST) != 0 && (currFlag & BLOCKED_WEST) != 0) return true;
        return false;
    }
}
