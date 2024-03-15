package com.feather.game.player.controlers.pestcontrol;

import com.feather.game.Entity;
import com.feather.game.Hit;
import com.feather.game.WorldObject;
import com.feather.game.WorldTile;
import com.feather.game.minigames.pest.PestControl;
import com.feather.game.player.controlers.Controler;
import com.feather.game.tasks.WorldTask;
import com.feather.game.tasks.WorldTasksManager;

public class PestControlGame extends Controler {

	private PestControl control;

	@Override
	public void start() {
		control = (PestControl) getArguments()[0];
		player.getTemporaryAttributtes().put("pestPoints", 0);
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				sendInterfaces();
			}
		}, 8);
	}

	@Override
	public void sendInterfaces() {
		updatePestPoints();
		player.getPackets().sendIComponentText(408, 1, "" + control.getKnight().getHitpoints());
		player.getInterfaceManager().sendTab(player.getInterfaceManager().hasResizableScreen() ? 1 : 11, 408);
	}

	private void updatePestPoints() {
		player.getPackets().sendIComponentText(408, 11, "" + player.getTemporaryAttributtes().get("pestPoints"));
	}

	@Override
	public void forceClose() {
		player.closeInterfaces();
		player.setNextWorldTile(PestControl.OUTSIDE_AREA);
		player.reset();
		control.getPlayers().remove(player);
	}

	@Override
	public boolean login() {
		forceClose();
		return true;
	}

	@Override
	public boolean logout() {
		forceClose();
		return true;
	}

	@Override
	public boolean processMagicTeleport(WorldTile toTile) {
		player.getDialogueManager().startDialogue("SimpleMessage", "You can't leave the pest control area like this.");
		return false;
	}

	@Override
	public boolean processItemTeleport(WorldTile toTile) {
		player.getDialogueManager().startDialogue("SimpleMessage", "You can't leave the pest control area like this.");
		return false;
	}

	@Override
	public boolean keepCombating(Entity target) {
		for (Hit hit : player.getNextHits()) {
			if (hit == null) return false;
			player.getTemporaryAttributtes().put("pestPoints", hit.getDamage());
			updatePestPoints();
		}
		return true;
	}

	/**
	 * 
	 * @param player
	 *            The player.
	 * @param object
	 *            The object.
	 * @return {@code true} if the object action got handled, {@code false} if
	 *         not.
	 */
	@Override
	public boolean processObjectClick1(WorldObject object) {
		switch (object.getId()) {
		}
		return true;
	}
}
