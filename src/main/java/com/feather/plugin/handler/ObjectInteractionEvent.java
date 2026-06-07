package com.feather.plugin.handler;

import com.feather.game.WorldObject;
import com.feather.game.player.Player;

public final class ObjectInteractionEvent {
    private final Player player;
    private final WorldObject object;
    private final int opNum;

    public ObjectInteractionEvent(Player player, WorldObject object, int opNum) {
        this.player = player;
        this.object = object;
        this.opNum = opNum;
    }

    public Player getPlayer() { return player; }
    public WorldObject getObject() { return object; }
    public int getOpNum() { return opNum; }
}
