package com.feather.cores.processes;

import com.feather.game.World;
import com.feather.game.npc.NPC;
import com.feather.utils.Logger;

public class NpcPreTickProcess extends BaseGameProcess {
    @Override
    protected void doProcess() {
        try {
            // Pre-process NPCs - prepare for main processing
            for (NPC npc : World.getNPCs()) {
                if (npc == null || npc.hasFinished())
                    continue;

                // Any pre-processing logic for NPCs can go here
                // This might include AI preparation, target acquisition, etc.
            }
        } catch (Throwable e) {
            Logger.handle(e);
        }
    }
}