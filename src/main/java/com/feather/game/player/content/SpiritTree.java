package com.feather.game.player.content;

import com.feather.game.Animation;
import com.feather.game.Graphics;
import com.feather.game.WorldTile;
import com.feather.game.player.Player;
import com.feather.game.tasks.WorldTask;
import com.feather.game.tasks.WorldTasksManager;

/**
 * 
 * @author _Jordan / Apollo <citellumrsps@gmail.com>
 * @author Feather RuneScape 2012 Remake
 */
public class SpiritTree {

	// Variables of the set locations.
	public static final WorldTile TREE_GNOME_VILLAGE = new WorldTile(2542,
			3168, 0), TREE_GNOME_STRONGHOLD = new WorldTile(2460, 3446, 0),
			BATTLEFIELD_OF_KHAZARD = new WorldTile(2553, 3256, 0),
			GRAND_EXCHANGE = new WorldTile(3186, 3508, 0),
			MOBILISING_ARMIES = new WorldTile(2416, 2848, 0),
			MAIN_SPIRIT_TREE = new WorldTile(2542, 3168, 0);

	/**
	 * Handles the buttons of the spirit tree interface.
	 * 
	 * @param player
	 * @param slotId
	 */
	public static void handleButtons(final Player player, int slotId) {
		if (slotId == 0) {
			sendSpiritTreeTeleport(player, TREE_GNOME_VILLAGE);
		} else if (slotId == 1) {
			sendSpiritTreeTeleport(player, TREE_GNOME_STRONGHOLD);
		} else if (slotId == 2) {
			sendSpiritTreeTeleport(player, BATTLEFIELD_OF_KHAZARD);
		} else if (slotId == 3) {
			sendSpiritTreeTeleport(player, GRAND_EXCHANGE);
		} else if (slotId == 4) {
			sendSpiritTreeTeleport(player, MOBILISING_ARMIES);
		}

	}

	/**
	 * The method that starts the actual animation and graphics of the spirit
	 * tree teleport.
	 * 
	 * @param player
	 * @param tile
	 */
	public static void sendSpiritTreeTeleport(final Player player,
			final WorldTile tile) {
		if (!player.getControlerManager().processObjectTeleport(tile))
			return;
		player.closeInterfaces();
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					player.setNextAnimation(new Animation(7082));
					player.setNextGraphics(new Graphics(1228));
				} else if (loop == 2) {
					player.setNextWorldTile(tile);
				} else if (loop == 3) {
					player.setNextAnimation(new Animation(7084));
					player.setNextGraphics(new Graphics(1229));
				}
				loop++;
			}
		}, 0, 1);
	}
	
	/**
	 * Sends and unlocks the interface for the player to use.
	 * 
	 * @param player
	 */
	public static void sendSpiritTreeInterface(final Player player) {
		player.getInterfaceManager().sendInterface(864);
		// Not necessary but oh well.
		for (int id = 0; id < 100; id++) {
			player.getPackets().sendUnlockIComponentOptionSlots(864, id, 0, 100, 0);
		}
	}

}
