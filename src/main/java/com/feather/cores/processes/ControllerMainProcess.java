package com.feather.cores.processes;

import com.feather.game.World;
import com.feather.game.player.Player;
import com.feather.utils.Logger;

public class ControllerMainProcess extends BaseGameProcess {
    @Override
    protected void doProcess() {
        try {
            // Process game controllers (mini-games, activities, etc.)
            // Handle special game modes and activities
            for (Player player : World.getPlayers()) {
                if (player == null || !player.hasStarted() || player.hasFinished())
                    continue;

                // Process any active controllers (minigames, activities, etc.)
                if (player.getControlerManager() != null) {
                    player.getControlerManager().process();
                }
            }
        } catch (Throwable e) {
            Logger.handle(e);
        }
    }
}
