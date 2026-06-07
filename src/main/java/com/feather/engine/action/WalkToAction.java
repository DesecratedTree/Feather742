package com.feather.engine.action;

import com.feather.engine.route.*;
import com.feather.game.Region;
import com.feather.game.RegionMap;
import com.feather.game.World;
import com.feather.game.WorldTile;
import com.feather.game.player.Player;

public final class WalkToAction {
    private static final ThreadLocal<RouteFinding> routeFinder = ThreadLocal.withInitial(RouteFinding::new);

    public static void walkTo(Player player, WorldTile target, Runnable onArrive) {
        walkTo(player, target, false, onArrive);
    }

    public static void walkTo(Player player, WorldTile target, boolean findAlternative, Runnable onArrive) {
        player.getActionQueue().queue(QueueType.NORMAL, () -> {
            if (player.isLocked() || player.getPlane() != target.getPlane()) {
                if (onArrive != null) onArrive.run();
                return;
            }

            CollisionFlagMap collisionMap = loadCollisionAround(player);

            RouteFinding finder = routeFinder.get();
            RouteResult result = finder.findRoute(
                    collisionMap, player.getPlane(),
                    player.getX(), player.getY(), target.getX(), target.getY(),
                    player.getSize(), player.getSize(),
                    1, 1,
                    -1, 0,
                    0, findAlternative
            );

            if (!result.hasRoute()) {
                if (onArrive != null) onArrive.run();
                return;
            }

            player.resetWalkSteps();
            if (result.reachedDestination() && result.getLength() <= 1) {
                if (onArrive != null) onArrive.run();
                return;
            }

            RouteCoordinates[] steps = result.getSteps();
            boolean added = false;
            for (int i = 1; i < steps.length; i++) {
                int x = steps[i].getX();
                int z = steps[i].getZ();
                if (player.addWalkSteps(x, z, 25, true)) {
                    added = true;
                } else {
                    break;
                }
            }

            if (added && onArrive != null) {
                player.getActionQueue().queue(QueueType.SOFT, () -> {
                    if (!player.hasWalkSteps()) {
                        onArrive.run();
                    }
                });
            }
        });
    }

    private static CollisionFlagMap loadCollisionAround(Player player) {
        CollisionFlagMap map = new CollisionFlagMap();
        int plane = player.getPlane();
        int regionX = (player.getX() >> 6);
        int regionY = (player.getY() >> 6);

        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                int rx = regionX + dx;
                int ry = regionY + dy;
                int regionId = (rx << 8) | ry;
                Region region = World.getRegion(regionId, false);
                if (region == null) continue;
                RegionMap regionMap = region.forceGetRegionMap();
                if (regionMap == null) continue;
                int[][][] masks = regionMap.getMasks();
                int baseX = regionMap.getRegionX();
                int baseY = regionMap.getRegionY();

                for (int p = 0; p < 4; p++) {
                    for (int x = 0; x < 64; x++) {
                        for (int y = 0; y < 64; y++) {
                            int flag = masks[p][x][y];
                            if (flag == 0) {
                                map.set(baseX + x, baseY + y, p, CollisionFlag.OPEN | CollisionFlag.INIT);
                            } else {
                                map.set(baseX + x, baseY + y, p, flag | CollisionFlag.INIT);
                            }
                        }
                    }
                }
            }
        }
        return map;
    }
}
