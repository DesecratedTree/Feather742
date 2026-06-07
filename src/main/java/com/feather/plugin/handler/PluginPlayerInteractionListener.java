package com.feather.plugin.handler;

import com.feather.game.player.Player;

@FunctionalInterface
public interface PluginPlayerInteractionListener {
    void accept(Player player, Player target, int opNum);
}
