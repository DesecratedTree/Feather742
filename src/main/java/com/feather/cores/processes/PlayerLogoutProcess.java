package com.feather.cores.processes;

import com.feather.game.World;
import com.feather.game.player.Player;
import com.feather.utils.Logger;
import java.util.Iterator;

public class PlayerLogoutProcess extends BaseGameProcess {
    @Override
    protected void doProcess() {
        try {
            // Handle player logouts - clean up disconnected players
            Iterator<Player> playerIterator = World.getPlayers().iterator();
            while (playerIterator.hasNext()) {
                Player player = playerIterator.next();
                if (player == null)
                    continue;

                // Check if player should be logged out
                if (player.hasFinished() || !player.getSession().getChannel().isOpen()) {
                    // Perform logout cleanup
                    player.finish();
                    playerIterator.remove();
                    Logger.log("PlayerLogoutProcess", "Player logged out: " + player.getDisplayName());
                }
            }
        } catch (Throwable e) {
            Logger.handle(e);
        }
    }
}
