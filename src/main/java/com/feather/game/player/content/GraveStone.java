package com.feather.game.player.content;

import java.util.TimerTask;

import com.feather.game.World;
import com.feather.game.WorldTile;
import com.feather.game.item.FloorItem;
import com.feather.game.item.Item;
import com.feather.game.npc.NPC;
import com.feather.game.player.Player;

public class GraveStone {

	/** The gravestone npc */
	private int gravestone;

	/** The time at which the gravestone has left */
	private int time;

	/** The drops under the gravestone */
	private Item[] drops;

	/** The gravestone position */
	private WorldTile pos;

	/** The gravestone npc */
	private NPC gs;

	/** The player associated with this gravestone */
	private Player player;

	/**
	 * Called when a player dies
	 * 
	 * @param tile
	 *            the position of this gravestone
	 * @param drop
	 *            if the player should drop
	 * @param player
	 *            the player dying
	 */
	public void deploy(WorldTile tile, boolean drop, int gravestoneNpcType, Player player,
			Item... drops) {
		this.player = player;
		this.gravestone = gravestoneNpcType;
		gs = new NPC(gravestone, tile, 0, false);
		GravestoneTimer timer = new GravestoneTimer();
		gs.setCantInteract(false);
		gs.setLocked(true);
		World.addNPC(gs);
		player.getHintIconsManager().addHintIcon(gs, 0, -1, false);
		timer.run();
		this.drops = drops;
		if (drop) {
			for(Item items : drops)
				 World.addGroundItem(items, tile, player, true, time, true);
		}
	}

	/**
	 * Causes this gravestone to collapse
	 */
	public void collapse() {
		for (Item items : drops)
			World.removeGroundItem(player, new FloorItem(items, pos, player,
					true, false));
		gs.sendDeath(null);
		World.removeNPC(gs);
		gs = null;
		player.getHintIconsManager().removeAll();
	}
	
	public static void sendRespawnInterface(Player player) {
		player.getInterfaceManager().sendInterface(18);
		player.getPackets().sendIComponentSettings(18, 9, 0, 3, 2);
		player.getPackets().sendIComponentSettings(18, 17, 0, 100, 2);
		player.getPackets().sendIComponentSettings(18, 45, 0, 5, 2);
//		Interface Id: 18 ComponentId: 9 From Slot: 0 To Slot 3 Hash: 2
//		Interface Id: 18 ComponentId: 17 From Slot: 0 To Slot 100 Hash: 2
//		Interface Id: 18 ComponentId: 45 From Slot: 0 To Slot 5 Hash: 2
	}
	
	class GravestoneTimer extends TimerTask {

		@Override
		public void run() {
			time--;
			refreshStatus();
		}

		/**
		 * Refreshes the interface and the interface configs to the
		 * corresponding gravestone time
		 */
		private void refreshStatus() {
			if (time == 0) {
				collapse();
				cancel();
				player.getPackets().sendGameMessage("You were late to your gravestone and it collapsed.");
				return;
			} else {
				// TODO configs and interface
			}
		}

	}

}
