package com.feather.engine.processes;

import com.feather.game.World;
import com.feather.game.player.Player;
import com.feather.utils.Logger;

public class WorldPostTickProcess extends BaseGameProcess {
    @Override
    protected void doProcess() {
        try {
            // World post-tick processing (occurs after map clock)
            // Send updates to players
            for (Player player : World.getPlayers()) {
                if (player == null || !player.hasStarted() || player.hasFinished())
                    continue;

                // Send player and NPC updates
                player.getPackets().sendLocalPlayersUpdate();
                player.getPackets().sendLocalNPCsUpdate();
            }
        } catch (Throwable e) {
            Logger.handle(e);
        }
    }
}
