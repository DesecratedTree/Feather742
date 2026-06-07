package com.feather.plugin.handler;

import com.feather.game.WorldObject;
import com.feather.game.player.Player;

@FunctionalInterface
public interface PluginObjectInteractionListener {
    void accept(Player player, WorldObject object, int opNum);
}
