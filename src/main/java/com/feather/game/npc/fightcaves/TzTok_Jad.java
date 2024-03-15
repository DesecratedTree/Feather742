package com.feather.game.npc.fightcaves;

import com.feather.game.Animation;
import com.feather.game.Entity;
import com.feather.game.Graphics;
import com.feather.game.WorldTile;
import com.feather.game.npc.combat.NPCCombatDefinitions;
import com.feather.game.player.controlers.FightCaves;
import com.feather.game.tasks.WorldTask;
import com.feather.game.tasks.WorldTasksManager;

@SuppressWarnings("serial")
public class TzTok_Jad extends FightCavesNPC {

	private boolean spawnedMinions;
	private FightCaves controler;
	
	public TzTok_Jad(int id, WorldTile tile, FightCaves controler) {
		super(id, tile);
		this.controler = controler;
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if(!spawnedMinions && getHitpoints() < getMaxHitpoints() / 2) {
			spawnedMinions = true;
			controler.spawnHealers();
		}
	}
	@Override
	public void sendDeath(Entity source) {
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
					setNextGraphics(new Graphics(2924 + getSize()));
				} else if (loop >= defs.getDeathDelay()) {
					reset();
					finish();
					controler.win();
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}

}
