package com.feather.cores.processes;

import com.feather.game.World;
import com.feather.game.npc.NPC;
import com.feather.game.player.Player;
import com.feather.utils.Logger;

public class PlayerPostTickProcess extends BaseGameProcess {
    @Override
    protected void doProcess() {
        try {
            // Player post-tick processing (occurs after map clock)
            // Reset masks and finalize tick data
            for (Player player : World.getPlayers()) {
                if (player == null || !player.hasStarted() || player.hasFinished())
                    continue;

                // Reset player masks for next tick
                player.resetMasks();
            }

            // Also reset NPC masks
            for (NPC npc : World.getNPCs()) {
                if (npc == null || npc.hasFinished())
                    continue;

                npc.resetMasks();
            }
        } catch (Throwable e) {
            Logger.handle(e);
        }
    }
}
