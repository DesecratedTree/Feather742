package com.feather.game.npc.others;

import java.util.concurrent.TimeUnit;

import com.feather.cores.CoresManager;
import com.feather.game.Animation;
import com.feather.game.Entity;
import com.feather.game.WorldTile;
import com.feather.game.npc.NPC;
import com.feather.game.player.Player;
import com.feather.game.tasks.WorldTask;
import com.feather.game.tasks.WorldTasksManager;

@SuppressWarnings("serial")
public class Bork extends NPC {
	
	public static long deadTime;

	public Bork(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setLureDelay(0);
		setForceAgressive(true);
	}

	@Override
	public void sendDeath(Entity source) {
		deadTime = System.currentTimeMillis() + (1000*60*60);
		resetWalkSteps();
		for (Entity e : getPossibleTargets()) {
			if (e instanceof Player) {
				final Player player = (Player) e;
				player.getInterfaceManager().sendInterface(693);
				player.getDialogueManager().startDialogue("DagonHai", 7137, player, 1);
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						player.stopAll();
					}
				}, 8);
			}
		}
		getCombat().removeTarget();
		setNextAnimation(new Animation(getCombatDefinitions().getDeathEmote()));
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				drop();
				reset();
				setLocation(getRespawnTile());
				finish();
				if (!isSpawned())
					setRespawnTask();
				stop();
			}
			
		}, 4);
	}
	
	@Override
	public void setRespawnTask() {
		if (!hasFinished()) {
			reset();
			setLocation(getRespawnTile());
			finish();
		}
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					spawn();
				} catch (Exception e) {
					e.printStackTrace();
				} catch (Error e) {
					e.printStackTrace();
				}
			}
		}, 1,
			TimeUnit.HOURS);
	}
	
	public static String convertToTime() {
		String time = "You have to wait "+(getTime() == 0 ? "few more seconds" : getTime()+" mins")+" to kill bork again.";
		return time;
	}
	
	public static int getTime() {
		return (int) (deadTime-System.currentTimeMillis()/60000);
	}
	
	public static boolean atBork(WorldTile tile) {
		if ((tile.getX() >= 3083 && tile.getX() <= 3120) && (tile.getY() >= 5522 && tile.getY() <= 5550))
			return true;
		return false;
	}
}
