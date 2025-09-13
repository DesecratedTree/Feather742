package com.feather.cores.processes;

import com.feather.game.World;
import com.feather.game.player.Player;
import com.feather.utils.Logger;

public class PlayerRouteRequestProcess extends BaseGameProcess {
    @Override
    protected void doProcess() {
        try {
            // Handle player movement/routing requests
            // Process pathfinding and movement validation
            for (Player player : World.getPlayers()) {
                if (player == null || !player.hasStarted() || player.hasFinished())
                    continue;

                // Process any pending route requests
                // This would handle movement validation, pathfinding, etc.
                //player.processRouteRequests();
            }
        } catch (Throwable e) {
            Logger.handle(e);
        }
    }
}
