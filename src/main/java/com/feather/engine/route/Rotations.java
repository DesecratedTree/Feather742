package com.feather.engine.route;

public final class Rotations {
    public static int rotate(int rotation, int width, int length) {
        if ((rotation & 0x1) == 0) {
            return width;
        }
        return length;
    }

    public static int rotateWidth(int rotation, int width, int length) {
        if ((rotation & 0x1) == 0) {
            return width;
        }
        return length;
    }

    public static int rotateLength(int rotation, int width, int length) {
        if ((rotation & 0x1) == 0) {
            return length;
        }
        return width;
    }
}
