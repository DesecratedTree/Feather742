package com.feather.game.npc.godwars.zammorak;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.feather.engine.GameEngine;
import com.feather.game.Animation;
import com.feather.game.Entity;
import com.feather.game.World;
import com.feather.game.WorldTile;
import com.feather.game.minigames.GodWarsBosses;
import com.feather.game.npc.NPC;
import com.feather.game.npc.combat.NPCCombatDefinitions;
import com.feather.game.player.Player;
import com.feather.engine.tasks.WorldTask;
import com.feather.engine.tasks.WorldTasksManager;

@SuppressWarnings("serial")
public class KrilTstsaroth extends NPC {

	public KrilTstsaroth(int id, WorldTile tile, int mapAreaNameHash,
                         boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
	}

	@Override
	public ArrayList<Entity> getPossibleTargets() {
		ArrayList<Entity> possibleTarget = new ArrayList<Entity>();
		for (int regionId : getMapRegionsIds()) {
			List<Integer> playerIndexes = World.getRegion(regionId)
					.getPlayerIndexes();
			if (playerIndexes != null) {
				for (int npcIndex : playerIndexes) {
					Player player = World.getPlayers().get(npcIndex);
					if (player == null
							|| player.isDead()
							|| player.hasFinished()
							|| !player.isRunning()
							|| !player.withinDistance(this, 64)
							|| ((!isAtMultiArea() || !player.isAtMultiArea())
									&& player.getAttackedBy() != this && player
									.getAttackedByDelay() > System
									.currentTimeMillis())
							|| !clipedProjectile(player, false))
						continue;
					possibleTarget.add(player);
				}
			}
		}
		return possibleTarget;
	}

	/*
	 * gotta override else setRespawnTask override doesnt work
	 */
	@Override
	public void sendDeath(Entity source) {
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		getCombat().removeTarget();
		setNextAnimation(null);
		WorldTasksManager.scheduleTask(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					setNextAnimation(new Animation(defs.getDeathEmote()));
				} else if (loop >= defs.getDeathDelay()) {
					drop();
					reset();
					setLocation(getRespawnTile());
					finish();
					setRespawnTask();
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}

	@Override
	public void setRespawnTask() {
		if (!hasFinished()) {
			reset();
			setLocation(getRespawnTile());
			finish();
		}
		final NPC npc = this;
		GameEngine.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				setFinished(false);
				World.addNPC(npc);
				npc.setLastRegionId(0);
				World.updateEntityRegion(npc);
				loadMapRegions();
				checkMultiArea();
				GodWarsBosses.respawnZammyMinions();
			}
		}, getCombatDefinitions().getRespawnDelay() * 600,
				TimeUnit.MILLISECONDS);
	}

}
