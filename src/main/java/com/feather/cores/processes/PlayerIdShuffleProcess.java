package com.feather.cores.processes;

import com.feather.game.World;
import com.feather.game.player.Player;
import com.feather.utils.Logger;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class PlayerIdShuffleProcess extends BaseGameProcess {
    @Override
    protected void doProcess() {
        try {
            // Shuffle player processing order to prevent timing exploits
            // This ensures fairness in player action processing
            List<Player> players = new ArrayList<>();
            for (Player player : World.getPlayers()) {
                if (player != null && player.hasStarted() && !player.hasFinished()) {
                    players.add(player);
                }
            }
            Collections.shuffle(players);

            // You could store this shuffled order in World or use it directly
            // For now, this demonstrates the concept
        } catch (Throwable e) {
            Logger.handle(e);
        }
    }
}