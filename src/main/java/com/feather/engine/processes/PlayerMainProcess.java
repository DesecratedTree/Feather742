package com.feather.engine.processes;

import com.feather.game.World;
import com.feather.game.player.Player;
import com.feather.utils.Logger;

public class PlayerMainProcess extends BaseGameProcess {
    @Override
    protected void doProcess() {
        try {
            // Main player processing - actions, combat, skills, etc.
            for (Player player : World.getPlayers()) {
                if (player == null || !player.hasStarted() || player.hasFinished())
                    continue;

                // Process the player's main logic
                player.processEntity();
            }
        } catch (Throwable e) {
            Logger.handle(e);
        }
    }
}
