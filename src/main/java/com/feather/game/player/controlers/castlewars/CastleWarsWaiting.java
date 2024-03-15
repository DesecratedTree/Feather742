package com.feather.game.player.controlers.castlewars;

import com.feather.game.WorldObject;
import com.feather.game.WorldTile;
import com.feather.game.minigames.CastleWars;
import com.feather.game.player.Equipment;
import com.feather.game.player.controlers.Controler;

public class CastleWarsWaiting extends Controler {

	private int team;

	@Override
	public void start() {
		team = (int) getArguments()[0];
		sendInterfaces();
	}

	// You can't leave just like that!

	public void leave() {
		player.getPackets().closeInterface(
				player.getInterfaceManager().hasResizableScreen() ?  34 : 0);
		CastleWars.removeWaitingPlayer(player, team);
	}

	@Override
	public void sendInterfaces() {
		player.getInterfaceManager().sendTab(
				player.getInterfaceManager().hasResizableScreen() ?  34 : 0, 57);
	}

	@Override
	public boolean processButtonClick(int interfaceId, int componentId,
			int slotId, int packetId) {
		if (interfaceId == 387) {
			if (componentId == 9 || componentId == 6) {
			player.getPackets().sendGameMessage(
					"You can't remove your team's colours.");
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean canEquip(int slotId, int itemId) {
		if (slotId == Equipment.SLOT_CAPE || slotId == Equipment.SLOT_HAT) {
			player.getPackets().sendGameMessage(
					"You can't remove your team's colours.");
			return false;
		}
		return true;
	}

	@Override
	public boolean sendDeath() {
		removeControler();
		leave();
		return true;
	}

	@Override
	public boolean logout() {
		player.setLocation(new WorldTile(CastleWars.LOBBY, 2));
		return true;
	}

	@Override
	public boolean processMagicTeleport(WorldTile toTile) {
		player.getDialogueManager().startDialogue("SimpleMessage",
				"You can't leave just like that!");
		return false;
	}

	@Override
	public boolean processItemTeleport(WorldTile toTile) {
		player.getDialogueManager().startDialogue("SimpleMessage",
				"You can't leave just like that!");
		return false;
	}

	@Override
	public boolean processObjectTeleport(WorldTile toTile) {
		player.getDialogueManager().startDialogue("SimpleMessage",
				"You can't leave just like that!");
		return false;
	}

	@Override
	public boolean processObjectClick1(WorldObject object) {
		int id = object.getId();
		if (id == 4389 || id == 4390) {
			removeControler();
			leave();
			return false;
		}
		return true;
	}

	@Override
	public void magicTeleported(int type) {
		removeControler();
		leave();
	}

	@Override
	public void forceClose() {
		leave();
	}
}
