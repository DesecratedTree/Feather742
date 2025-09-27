package com.feather.engine.processes;

import com.feather.game.World;
import com.feather.game.npc.NPC;
import com.feather.utils.Logger;

public class NpcMainProcess extends BaseGameProcess {
    @Override
    protected void doProcess() {
        try {
            // Main NPC processing - AI, combat, movement, etc.
            for (NPC npc : World.getNPCs()) {
                if (npc == null || npc.hasFinished())
                    continue;

                // Process the NPC's main logic
                npc.processEntity();
            }
        } catch (Throwable e) {
            Logger.handle(e);
        }
    }
}

