package com.feather.game.player.content;

import com.feather.Settings;
import com.feather.cache.parser.ItemDefinitions;
import com.feather.game.item.Item;
import com.feather.game.player.Player;
import com.feather.game.player.Skills;

/**
 * 
 * @author Tyler, Justin, & Clayton
 * 
 */
public class XPLamp {

	Player player;
	private static int itemId;
	private static int slotId;
	private static int giantExp[] = { 499, 612, 616, 680, 746, 832, 984, 1016, 1142, 1228, 1360, 1504, 1645, 1832, 2016, 2093, 2192, 2280, 2384, 2480, 2596, 2696, 2816, 2940, 3071, 3192, 3331, 3312, 3624, 3784, 3946, 4112, 4288, 4129, 4664, 4872, 5080, 5296, 5532, 5764, 6016, 6276, 6544, 6828, 7116, 7432, 7764, 8100, 7440, 8808, 9185, 9600, 9996, 10436, 10896, 11376, 11880, 12368, 12932, 13474, 14060, 14684, 15290, 16036, 16864, 17372, 18152, 19032, 19760, 20741, 21543, 22474, 23572, 24657, 25709, 26716, 27960, 29173, 30338, 31840, 33328, 34780, 36174, 38097, 39347, 40264, 43003, 44739, 47380, 48972, 51612, 52916, 55428, 57887, 60260, 64516, 66780, 68815, 68815 };

	public XPLamp(Player player, int itemId, int slotId) {
		XPLamp.itemId = itemId;
		this.player = player;
		XPLamp.slotId = slotId;
	}

	public void openInterface(final Player player, boolean xp) {
		XPLamp.xp = xp;
		player.getInterfaceManager().sendInterface(1263);
		player.getPackets().sendIComponentText(1263, 6, "Xlite");
		for (int i = 0; i < 40; i++) {
			if (i == 35) {
				continue;
			}
			player.getPackets().sendIComponentSettings(1263, i, 0, 300, 2150);
		}
	}

	public static final int ATTACK = 0, DEFENCE = 1, STRENGTH = 2, HITPOINTS = 3, RANGE = 4, PRAYER = 5, MAGIC = 6, COOKING = 7, WOODCUTTING = 8, FLETCHING = 9, FISHING = 10, FIREMAKING = 11, CRAFTING = 12, SMITHING = 13, MINING = 14, HERBLORE = 15, AGILITY = 16, THIEVING = 17, SLAYER = 18, FARMING = 19, RUNECRAFTING = 20, CONSTRUCTION = 22, HUNTER = 21, SUMMONING = 23, DUNGEONEERING = 24;

	public static void handleButtons(Player player, int buttonId) {
		int component = buttonId - 13;
		int[][] EXP_LAMP = { { 0, ATTACK }, { 1, MAGIC }, { 2, MINING }, { 3, WOODCUTTING }, { 4, AGILITY }, { 5, FLETCHING }, { 6, THIEVING }, { 7, STRENGTH }, { 8, RANGE }, { 9, SMITHING }, { 10, FIREMAKING }, { 11, HERBLORE }, { 12, SLAYER }, { 13, CONSTRUCTION }, { 14, DEFENCE }, { 15, PRAYER }, { 16, FISHING }, { 17, CRAFTING }, { 18, FARMING }, { 19, HUNTER }, { 20, SUMMONING }, { 21, HITPOINTS }, { 22, DUNGEONEERING }, { 23, COOKING },// For
																																																																																																																// women
				{ 24, RUNECRAFTING }, };
		ItemDefinitions defs = ItemDefinitions.getItemDefinitions(itemId);
		if (defs.getName().toLowerCase().contains("shiny")) {
			int skillId = EXP_LAMP[component][1];
			if (skillId == 19 || skillId == 22 || skillId == 21 || skillId == 24) {
				player.getPackets().sendGameMessage("You cannot add experience to that skill.");
				player.closeInterfaces();
				return;
			}
			if (xp) {
				player.getSkills().addXp(skillId, 1000000 / Settings.XP_RATE);
				player.closeInterfaces();
				player.getInventory().deleteItem(slotId, new Item(itemId));
				player.getDialogueManager().startDialogue("SimpleMessage", "1m xp has been granted in " + Skills.SKILL_NAME[skillId]);
			}

		}
		if (defs.getName().equalsIgnoreCase("Small XP lamp")) {
			int skillId = EXP_LAMP[component][1];
			int x = player.getSkills().getLevel(skillId);
			if (skillId == 19 || skillId == 22 || skillId == 21 || skillId == 24) {
				player.getPackets().sendGameMessage("You cannot add experience to that skill.");
				player.closeInterfaces();
				return;
			}
			if (x != 2) {

				player.getSkills().addXp(skillId, (giantExp[x - 1] / 8) / 2);
				player.getDialogueManager().startDialogue("SimpleMessage", giantExp[x - 1] / 8 + " xp has been granted in " + Skills.SKILL_NAME[skillId]);
			} else {

				player.getSkills().addXp(skillId, 69 / 2);
			}
			player.closeInterfaces();
			player.getInventory().deleteItem(slotId, new Item(itemId));
		} else if (defs.getName().equalsIgnoreCase("Medium XP lamp")) {
			int skillId = EXP_LAMP[component][1];
			int x = player.getSkills().getLevel(skillId);
			if (skillId == 19 || skillId == 22 || skillId == 21 || skillId == 24) {
				player.getPackets().sendGameMessage("You cannot add experience to that skill.");
				player.closeInterfaces();
				return;
			}
			if (x != 2) {

				player.getSkills().addXp(skillId, (giantExp[x - 1] / 4) / 2);
				player.getDialogueManager().startDialogue("SimpleMessage", giantExp[x - 1] / 4 + " xp has been granted in " + Skills.SKILL_NAME[skillId]);
			} else {

				player.getSkills().addXp(skillId, 138 / 2);
			}
			player.closeInterfaces();
			player.getInventory().deleteItem(slotId, new Item(itemId));
		} else if (defs.getName().equalsIgnoreCase("Large XP lamp")) {
			int skillId = EXP_LAMP[component][1];
			int x = player.getSkills().getLevel(skillId);
			if (skillId == 19 || skillId == 22 || skillId == 21 || skillId == 24) {
				player.getPackets().sendGameMessage("You cannot add experience to that skill.");
				player.closeInterfaces();
				return;
			}
			if (x != 2) {

				player.getSkills().addXp(skillId, (giantExp[x - 1] / 2) / 2);
				player.getDialogueManager().startDialogue("SimpleMessage", giantExp[x - 1] / 2 + " xp has been granted in " + Skills.SKILL_NAME[skillId]);
			} else {

				player.getSkills().addXp(skillId, 276 / 2);
			}
			player.closeInterfaces();
			player.getInventory().deleteItem(slotId, new Item(itemId));
		} else if (defs.getName().equalsIgnoreCase("Huge XP lamp")) {
			int skillId = EXP_LAMP[component][1];
			int x = player.getSkills().getLevel(skillId);
			if (skillId == 19 || skillId == 22 || skillId == 21 || skillId == 24) {
				player.getPackets().sendGameMessage("You cannot add experience to that skill.");
				player.closeInterfaces();
				return;
			}
			if (x != 2) {

				player.getSkills().addXp(skillId, (giantExp[x - 1]) / 2);
				player.getDialogueManager().startDialogue("SimpleMessage", giantExp[x - 1] + " xp has been granted in " + Skills.SKILL_NAME[skillId]);
			} else {

				player.getSkills().addXp(skillId, (giantExp[x - 1]) / 2);
			}
			player.closeInterfaces();
			player.getInventory().deleteItem(slotId, new Item(itemId));
		}
	}

	private static boolean xp;

}
