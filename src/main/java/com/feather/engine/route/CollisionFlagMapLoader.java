package com.feather.engine.route;

import com.feather.game.Region;
import com.feather.game.RegionMap;
import com.feather.game.World;

public final class CollisionFlagMapLoader {

    public static void loadRegion(CollisionFlagMap target, int regionId) {
        Region region = World.getRegion(regionId, false);
        if (region == null) return;
        RegionMap map = region.forceGetRegionMap();
        if (map == null) return;
        int[][][] masks = map.getMasks();
        int baseX = map.getRegionX();
        int baseY = map.getRegionY();
        for (int plane = 0; plane < 4; plane++) {
            for (int x = 0; x < 64; x++) {
                for (int y = 0; y < 64; y++) {
                    int flag = masks[plane][x][y];
                    if (flag == 0) {
                        target.set(baseX + x, baseY + y, plane, CollisionFlag.OPEN | CollisionFlag.INIT);
                    } else {
                        target.set(baseX + x, baseY + y, plane, flag | CollisionFlag.INIT);
                    }
                }
            }
        }
    }

    public static void loadRegionAndNeighbors(CollisionFlagMap target, int regionId) {
        loadRegion(target, regionId);
        loadRegion(target, regionId - 1);
        loadRegion(target, regionId + 1);
        loadRegion(target, regionId - 256);
        loadRegion(target, regionId + 256);
        loadRegion(target, regionId - 257);
        loadRegion(target, regionId - 255);
        loadRegion(target, regionId + 255);
        loadRegion(target, regionId + 257);
    }

    public static void ensureRegionLoaded(CollisionFlagMap target, int worldX, int worldY, int plane) {
        int regionId = (((worldX >> 6) << 8) | (worldY >> 6));
        loadRegion(target, regionId);
    }

    public static void ensureSurroundingRegions(CollisionFlagMap target, int worldX, int worldY, int plane) {
        int regionX = worldX >> 6;
        int regionY = worldY >> 6;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int rx = regionX + dx;
                int ry = regionY + dy;
                int regionId = (rx << 8) | ry;
                loadRegion(target, regionId);
            }
        }
    }
}
