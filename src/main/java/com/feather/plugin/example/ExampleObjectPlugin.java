package com.feather.plugin.example;

import com.feather.engine.action.WalkToAction;
import com.feather.game.World;
import com.feather.game.WorldObject;
import com.feather.game.WorldTile;
import com.feather.game.player.Player;
import com.feather.plugin.Plugin;
import com.feather.plugin.PluginMeta;
import com.feather.plugin.handler.ObjectHandler;

@PluginMeta(name = "Example Objects", version = "1.0", author = "Feather", description = "Example object interaction plugin")
public class ExampleObjectPlugin implements Plugin {

    @Override
    public void init() {
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @ObjectHandler(objectIds = {2452}, op = 1)
    public void onAirAltarClick(Player player, WorldObject object, int opNum) {
        WalkToAction.walkTo(player, object, () -> {
            int hatId = player.getEquipment().getHatId();
            if (hatId == 5527 || hatId == 19709 || player.getInventory().containsItem(1438, 1)) {
                player.getPackets().sendGameMessage("You enter the Air Altar.");
                player.setNextWorldTile(new WorldTile(2985, 3294, 0));
            } else {
                player.getPackets().sendGameMessage("You need an air tiara or a talisman to enter.");
            }
        });
    }

    @ObjectHandler(objectIds = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, op = -1)
    public void onDebugObjectClick(Player player, WorldObject object, int opNum) {
        player.getPackets().sendGameMessage("Plugin handling object " + object.getId() + " option " + opNum);
    }

}
