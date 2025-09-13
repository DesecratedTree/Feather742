package com.feather.cores.processes;

import com.feather.Settings;
import com.feather.utils.Utils;

public class MapClock implements GameProcess {
    private long currentTick = 0;
    public static long LAST_CYCLE_CTM;

    @Override
    public void process() {
        tick();
    }

    public void tick() {
        currentTick++;

        // Handle timing operations here
        // This is where loc_del and other time-based operations are processed

        // Update the cycle time for legacy compatibility
        LAST_CYCLE_CTM = Utils.currentTimeMillis();
    }

    public long getCurrentTick() {
        return currentTick;
    }

    public static long getLastCycleTime() {
        return LAST_CYCLE_CTM;
    }
}
