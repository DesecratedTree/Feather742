package com.feather.game.player.controlers;

import java.util.concurrent.TimeUnit;

import com.feather.game.Animation;
import com.feather.game.Graphics;
import com.feather.game.WorldObject;
import com.feather.game.tasks.WorldTask;
import com.feather.game.tasks.WorldTasksManager;

public class GodWars extends Controler {

	@Override
	public void start() {
		setArguments(new Object[] { 0, 0, 0, 0, 0, 0 });
		sendInterfaces();
	}

	@Override
	public boolean logout() {
		return false; // so doesnt remove script
	}

	@Override
	public boolean login() {
		sendInterfaces();
		return false; // so doesnt remove script
	}

	@Override
	public boolean processObjectClick1(final WorldObject object) {
		if (object.getId() == 57225) {
			player.getDialogueManager().startDialogue("NexEntrance");
			return false;
		}
		return true;
	}

	@Override
	public void sendInterfaces() {
		player.getInterfaceManager().sendGodwars();
	}

//	private int getInterface() {
//		switch ((Integer) getArguments()[0]) {
//		case 1: // zamorak area
//			return 599;
//		case 2:// zamorak boss area
//			return 598;
//		default:
//			return 601;
//		}
//	}

	@Override
	public boolean sendDeath() {
		remove();
		removeControler();
		return true;
	}

	@Override
	public void magicTeleported(int type) {
		remove();
		removeControler();
	}

	@Override
	public void forceClose() {
		remove();
	}

	public void remove() {
		player.getPackets().closeInterface(
				player.getInterfaceManager().hasResizableScreen() ? 34 : 8);
	}

}
