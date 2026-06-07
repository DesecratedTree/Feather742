package com.feather.engine.route;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class CollisionFlagMap {
    private static final int ZONE_SIZE = 64;
    private static final int PLANE_COUNT = 4;

    private final Map<Long, int[]> map = new HashMap<>();

    public int get(int x, int z, int level) {
        int[] zone = getZone(x, z, level);
        if (zone == null) {
            return CollisionFlag.INIT;
        }
        return zone[(z & (ZONE_SIZE - 1)) * ZONE_SIZE + (x & (ZONE_SIZE - 1))];
    }

    public void set(int x, int z, int level, int flag) {
        int[] zone = getOrCreateZone(x, z, level);
        zone[(z & (ZONE_SIZE - 1)) * ZONE_SIZE + (x & (ZONE_SIZE - 1))] = flag;
    }

    public void add(int x, int z, int level, int flag) {
        int[] zone = getOrCreateZone(x, z, level);
        int index = (z & (ZONE_SIZE - 1)) * ZONE_SIZE + (x & (ZONE_SIZE - 1));
        zone[index] |= flag;
    }

    public void remove(int x, int z, int level, int flag) {
        int[] zone = getZone(x, z, level);
        if (zone == null) {
            return;
        }
        int index = (z & (ZONE_SIZE - 1)) * ZONE_SIZE + (x & (ZONE_SIZE - 1));
        zone[index] &= ~flag;
    }

    public void setAll(int x, int z, int level, int flag) {
        int[] zone = getOrCreateZone(x, z, level);
        int index = (z & (ZONE_SIZE - 1)) * ZONE_SIZE + (x & (ZONE_SIZE - 1));
        int existing = zone[index];
        if (existing == 0) {
            zone[index] = flag | CollisionFlag.INIT;
        } else {
            zone[index] = existing | flag;
        }
    }

    public int[] getZone(int x, int z, int level) {
        int zoneX = x >> 6;
        int zoneZ = z >> 6;
        return map.get(key(zoneX, zoneZ, level));
    }

    private int[] getOrCreateZone(int x, int z, int level) {
        int zoneX = x >> 6;
        int zoneZ = z >> 6;
        long k = key(zoneX, zoneZ, level);
        return map.computeIfAbsent(k, k2 -> new int[ZONE_SIZE * ZONE_SIZE]);
    }

    public void clearZone(int zoneX, int zoneZ, int level) {
        map.remove(key(zoneX, zoneZ, level));
    }

    public void fillZone(int zoneX, int zoneZ, int level) {
        int zoneRelX = zoneX << 6;
        int zoneRelZ = zoneZ << 6;
        for (int x = 0; x < ZONE_SIZE; x++) {
            for (int z = 0; z < ZONE_SIZE; z++) {
                set(zoneRelX + x, zoneRelZ + z, level, CollisionFlag.INIT);
            }
        }
    }

    public void flagZone(int zoneX, int zoneZ, int level) {
        int zoneRelX = zoneX << 6;
        int zoneRelZ = zoneZ << 6;
        for (int x = 0; x < ZONE_SIZE; x++) {
            for (int z = 0; z < ZONE_SIZE; z++) {
                set(zoneRelX + x, zoneRelZ + z, level, CollisionFlag.OPEN);
            }
        }
    }

    public boolean zoneLoaded(int zoneX, int zoneZ, int level) {
        return map.containsKey(key(zoneX, zoneZ, level));
    }

    private static long key(int zoneX, int zoneZ, int level) {
        return ((long) zoneX << 32) | ((zoneZ & 0xFFFFFFFFL) << 8) | (level & 0xFFL);
    }
}
