package com.feather.plugin.example;

import com.feather.game.player.Player;
import com.feather.plugin.Plugin;
import com.feather.plugin.PluginMeta;
import com.feather.plugin.handler.PlayerHandler;

@PluginMeta(name = "Example Player Interactions", version = "1.0", author = "Feather", description = "Example player interaction plugin")
public class ExamplePlayerPlugin implements Plugin {

    @Override
    public void init() {
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @PlayerHandler(op = 2)
    public void onFollowPlayer(Player player, Player target, int opNum) {
        player.getPackets().sendGameMessage("Following " + target.getDisplayName() + " via plugin system.");
    }

    @PlayerHandler(op = 4)
    public void onTradePlayer(Player player, Player target, int opNum) {
        player.getPackets().sendGameMessage("Trading with " + target.getDisplayName() + " via plugin system.");
    }
}
