package com.feather.game.npc.others;

import java.util.concurrent.TimeUnit;

import com.feather.cores.CoresManager;
import com.feather.game.Animation;
import com.feather.game.Entity;
import com.feather.game.WorldTile;
import com.feather.game.npc.NPC;
import com.feather.game.npc.combat.NPCCombatDefinitions;
import com.feather.game.player.Player;
import com.feather.game.tasks.WorldTask;
import com.feather.game.tasks.WorldTasksManager;
import com.feather.utils.Logger;
import com.feather.utils.Utils;

@SuppressWarnings("serial")
public class LivingRock extends NPC {

	private Entity source;
	private long deathTime;
	
	public LivingRock(int id, WorldTile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setForceTargetDistance(4);
	}
	
	@Override
	public void sendDeath(final Entity source) {
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		getCombat().removeTarget();
		setNextAnimation(null);
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					setNextAnimation(new Animation(defs.getDeathEmote()));
				} else if (loop >= defs.getDeathDelay()) {
					drop();
					reset();
					transformIntoRemains(source);
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}
	
	public void transformIntoRemains(Entity source) {
		this.source = source;
		deathTime = Utils.currentTimeMillis();
		final int remainsId = getId() + 5;
		transformIntoNPC(remainsId);
		setRandomWalk(false);
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					if(remainsId == getId())
						takeRemains();
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 3, TimeUnit.MINUTES);
		
	}
	
	public boolean canMine(Player player) {
		return Utils.currentTimeMillis() - deathTime > 60000 || player == source;
	}
	
	
	public void takeRemains() {
		setNPC(getId() - 5);
		setLocation(getRespawnTile());
		setRandomWalk(true);
		finish();
		if (!isSpawned())
			setRespawnTask();
	}
	

}
