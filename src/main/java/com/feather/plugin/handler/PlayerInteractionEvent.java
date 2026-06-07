package com.feather.plugin.handler;

import com.feather.game.player.Player;

public final class PlayerInteractionEvent {
    private final Player player;
    private final Player target;
    private final int opNum;

    public PlayerInteractionEvent(Player player, Player target, int opNum) {
        this.player = player;
        this.target = target;
        this.opNum = opNum;
    }

    public Player getPlayer() { return player; }
    public Player getTarget() { return target; }
    public int getOpNum() { return opNum; }
}
