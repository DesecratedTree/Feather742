package com.feather.cores.processes;

import com.feather.game.tasks.WorldTasksManager;
import com.feather.utils.Logger;

public class WorldMainTickProcess extends BaseGameProcess {
    @Override
    protected void doProcess() {
        try {
            // Process all world tasks first
            WorldTasksManager.processTasks();
        } catch (Throwable e) {
            Logger.handle(e);
        }
    }
}
