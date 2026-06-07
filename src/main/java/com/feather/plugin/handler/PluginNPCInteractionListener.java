package com.feather.plugin.handler;

import com.feather.game.npc.NPC;
import com.feather.game.player.Player;

@FunctionalInterface
public interface PluginNPCInteractionListener {
    void accept(Player player, NPC npc, int opNum);
}
