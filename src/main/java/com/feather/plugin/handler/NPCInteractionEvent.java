package com.feather.plugin.handler;

import com.feather.game.npc.NPC;
import com.feather.game.player.Player;

public final class NPCInteractionEvent {
    private final Player player;
    private final NPC npc;
    private final int opNum;

    public NPCInteractionEvent(Player player, NPC npc, int opNum) {
        this.player = player;
        this.npc = npc;
        this.opNum = opNum;
    }

    public Player getPlayer() { return player; }
    public NPC getNpc() { return npc; }
    public int getOpNum() { return opNum; }
}
