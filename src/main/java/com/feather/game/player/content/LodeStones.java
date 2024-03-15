package com.feather.game.player.content;

import com.feather.game.Graphics;
import com.feather.game.WorldObject;
import com.feather.game.WorldTile;
import com.feather.game.player.Player;
import com.feather.game.player.actions.HomeTeleport;

/**
 * @author _Jordan / Apollo <citellumrsps@gmail.com>
 * @author Feather RuneScape 2012 Oct 12, 2013 2:09:58 PM
 * 
 */
public class LodeStones {

	// Config - Object
	public static int[][] lodeStones = new int[][] { { 10904, 69833 }, { 10906, 69835 } };

	/**
	 * Handles the interface of the lodestone network. Checks if the player is
	 * able to teleport to the selected lodestone.
	 * 
	 * @param player
	 * @param componentId
	 */
	public static void handleButtons(final Player player, int componentId) {
		player.stopAll();
		WorldTile stoneTile = null;
		switch (componentId) {
		case 47: // Lumbridge is auto unlocked.
			stoneTile = HomeTeleport.LUMBRIDGE_LODE_STONE;
			break;
		case 44:
			if (player.activatedDraynor == false) {
				player.getPackets().sendGameMessage(
						"You must unlock this lodestone to use it.");
				return;
			}
			stoneTile = HomeTeleport.DRAYNOR_VILLAGE_LODE_STONE;
			break;
		case 46:
			if (player.activatedFalador == false) {
				player.getPackets().sendGameMessage(
						"You must unlock this lodestone to use it.");
				return;
			}
			stoneTile = HomeTeleport.FALADOR_LODE_STONE;
			break;
		}
		if (stoneTile != null) {
			player.getActionManager().setAction(new HomeTeleport(stoneTile));
		}
	}

	/**
	 * Checks the object id then sends the necessary config. Activates the
	 * lodestone for the player.
	 * 
	 * @param player
	 * @param object
	 */
	public static void activateLodestone(final Player player, WorldObject object) {
		if (object.getId() == lodeStones[1][1]) {
			sendReward(player);
			player.getPackets().sendConfigByFile(lodeStones[0][0], 1);
			player.getPackets().sendGraphics(new Graphics(3019), object);
			player.activatedDraynor = true;
		} else if (object.getId() == lodeStones[2][1]) {
			sendReward(player);
			player.getPackets().sendConfigByFile(lodeStones[1][0], 1);
			player.getPackets().sendGraphics(new Graphics(3019), object);
			player.activatedFalador = true;
		}
	}

	/**
	 * Sends the player their reward for activating the lodestone.
	 * 
	 * @param player
	 */
	public static void sendReward(final Player player) {
		player.getPouch().handleMoneyPouch(true, false, false, 0, 375);
	}

	/**
	 * Checks if the player has unlocked the lodestone during login.
	 * 
	 * @param player
	 */
	public static void checkActivation(final Player player) {
		// Lumbridge is auto unlocked.
		player.getPackets().sendConfigByFile(10907, 1);
		if (player.activatedDraynor == true) {
			player.getPackets().sendConfigByFile(lodeStones[0][0], 1);
		}
		if (player.activatedFalador == true) {
			player.getPackets().sendConfigByFile(lodeStones[1][0], 1);
		}

	}

}
