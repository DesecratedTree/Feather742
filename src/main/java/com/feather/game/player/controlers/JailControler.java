package com.feather.game.player.controlers;

import com.feather.game.Animation;
import com.feather.game.WorldObject;
import com.feather.game.WorldTile;
import com.feather.game.player.Player;
import com.feather.game.tasks.WorldTask;
import com.feather.game.tasks.WorldTasksManager;
import com.feather.utils.Utils;

public class JailControler extends Controler {

	@Override
	public void start() {
		if (player.getJailed() > Utils.currentTimeMillis())
			player.sendRandomJail(player);
	}

	@Override
	public void process() {
		if (player.getJailed() <= Utils.currentTimeMillis()) {
			player.getControlerManager().getControler().removeControler();
			player.getPackets().sendGameMessage(
					"Your account has been unjailed.", true);
			player.setNextWorldTile(new WorldTile(2677, 10379, 0));
		}
	}

	public static void stopControler(Player p) {
		p.getControlerManager().getControler().removeControler();
	}

	@Override
	public boolean sendDeath() {
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				player.stopAll();
				if (loop == 0) {
					player.setNextAnimation(new Animation(836));
				} else if (loop == 1) {
					player.getPackets().sendGameMessage(
							"Oh dear, you have died.");
				} else if (loop == 3) {
					player.setNextAnimation(new Animation(-1));
					player.reset();
					player.setCanPvp(false);
					player.sendRandomJail(player);
					player.unlock();
				}
				loop++;
			}
		}, 0, 1);
		return false;
	}

	@Override
	public boolean login() {

		return false;
	}

	@Override
	public boolean logout() {

		return false;
	}

	@Override
	public boolean processMagicTeleport(WorldTile toTile) {
		player.getPackets().sendGameMessage(
				"You are currently jailed for your delinquent acts.");
		return false;
	}

	@Override
	public boolean processItemTeleport(WorldTile toTile) {
		player.getPackets().sendGameMessage(
				"You are currently jailed for your delinquent acts.");
		return false;
	}

	@Override
	public boolean processObjectClick1(WorldObject object) {
		player.getPackets().sendGameMessage(
				"You cannot do any activities while being jailed.");
		return false;
	}

}
