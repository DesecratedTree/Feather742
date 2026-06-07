package com.feather.engine.action;

import com.feather.engine.processes.BaseGameProcess;
import com.feather.game.World;
import com.feather.game.player.Player;
import com.feather.utils.Logger;

public class PlayerActionQueueProcess extends BaseGameProcess {

    @Override
    protected void doProcess() {
        try {
            for (Player player : World.getPlayers()) {
                if (player == null || !player.hasStarted() || player.hasFinished())
                    continue;

                ActionQueue queue = player.getActionQueue();
                if (queue != null && !queue.isEmpty()) {
                    queue.process(
                        player.isLocked() || player.getFreezeDelay() > System.currentTimeMillis(),
                        player.getInterfaceManager().containsScreenInter(),
                        () -> player.closeInterfaces()
                    );
                }
            }
        } catch (Throwable e) {
            Logger.handle(e);
        }
    }
}
