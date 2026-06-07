package com.feather.plugin.example;

import com.feather.engine.action.WalkToAction;
import com.feather.game.npc.NPC;
import com.feather.game.player.Player;
import com.feather.plugin.Plugin;
import com.feather.plugin.PluginMeta;
import com.feather.plugin.handler.NPCHandler;

@PluginMeta(name = "Hans NPC", version = "1.0", author = "Feather", description = "Hans NPC interaction plugin")
public class HansPlugin implements Plugin {

    @Override
    public void init() {
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @NPCHandler(npcIds = {0}, op = 1)
    public void onTalkToHans(Player player, NPC npc, int opNum) {
        WalkToAction.walkTo(player, npc, () -> {
            player.getPackets().sendGameMessage("Hans: Hello there! I see you're new around here.");
            player.getPackets().sendGameMessage("Hans: Feel free to explore the world of Gielinor!");
        });
    }

    @NPCHandler(npcIds = {0}, op = 2)
    public void onHansOption2(Player player, NPC npc, int opNum) {
        WalkToAction.walkTo(player, npc, () -> {
            player.getPackets().sendGameMessage("You inspect Hans closely. He seems friendly.");
        });
    }

    @NPCHandler(npcIds = {0}, op = 3)
    public void onHansOption3(Player player, NPC npc, int opNum) {
        WalkToAction.walkTo(player, npc, () -> {
            player.getPackets().sendGameMessage("Hans gives you a warm smile.");
        });
    }

    @NPCHandler(npcIds = {0}, op = 4)
    public void onHansOption4(Player player, NPC npc, int opNum) {
        WalkToAction.walkTo(player, npc, () -> {
            player.getPackets().sendGameMessage("You wave goodbye to Hans.");
        });
    }
}
