package com.feather.engine;

import com.feather.Settings;
import com.feather.utils.Logger;
import com.feather.utils.Utils;

public final class WorldThread extends Thread {

    public static long LAST_CYCLE_CTM = Utils.currentTimeMillis();

    WorldThread() {
        setPriority(Thread.MAX_PRIORITY);
        setName("World Thread");
    }

    @Override
    public final void run() {
        Logger.log("WorldThread", "Legacy WorldThread started - GameEngine will handle game loop");

        // This thread now serves as a legacy compatibility layer
        // The actual game processing is handled by GameEngine
        while (!GameEngine.shutdown) {
            try {
                // Just sleep to keep the thread alive for legacy compatibility
                Thread.sleep(Settings.WORLD_CYCLE_TIME);
            } catch (InterruptedException e) {
                Logger.handle(e);
                break;
            }
        }

        Logger.log("WorldThread", "Legacy WorldThread stopped");
    }
}