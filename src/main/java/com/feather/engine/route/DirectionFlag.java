package com.feather.engine.route;

public final class DirectionFlag {
    public static final int DIR_NORTH = 0x1;
    public static final int DIR_EAST = 0x2;
    public static final int DIR_SOUTH = 0x4;
    public static final int DIR_WEST = 0x8;
    public static final int DIR_NORTHEAST = DIR_NORTH | DIR_EAST;
    public static final int DIR_NORTHWEST = DIR_NORTH | DIR_WEST;
    public static final int DIR_SOUTHEAST = DIR_SOUTH | DIR_EAST;
    public static final int DIR_SOUTHWEST = DIR_SOUTH | DIR_WEST;

    public static final int DIR_NONE = 99;
}
