package com.feather.game.npc.fightkiln;

import java.util.ArrayList;
import java.util.List;

import com.feather.game.Animation;
import com.feather.game.Entity;
import com.feather.game.Graphics;
import com.feather.game.World;
import com.feather.game.WorldTile;
import com.feather.game.npc.NPC;
import com.feather.game.npc.combat.NPCCombatDefinitions;
import com.feather.game.player.Player;
import com.feather.game.player.controlers.FightKiln;
import com.feather.game.tasks.WorldTask;
import com.feather.game.tasks.WorldTasksManager;

@SuppressWarnings("serial")
public class FightKilnNPC extends NPC {

	private FightKiln controler;

	public FightKilnNPC(int id, WorldTile tile, FightKiln controler) {
		super(id, tile, -1, true, true);
		setForceMultiArea(true);
		setNoDistanceCheck(true);
		this.controler = controler;
	}
	
	
	private int getDeathGfx() {
		switch(getId()) {
		case 15201: return 2926;
		case 15202: return 2927;
		case 15203: return 2957;
		case 15213:
		case 15214:
		case 15204: return 2928;
		case 15205: return 2959;
		case 15206:
		case 15207:  return 2929;
		case 15208: 
		case 15211:
		case 15212: return 2973;
		default: return 2926;
		}
	}

	@Override
	public void sendDeath(Entity source) {
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		getCombat().removeTarget();
		setNextAnimation(null);
		controler.checkCrystal();
		setNextGraphics(new Graphics(getDeathGfx()));
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					setNextAnimation(new Animation(defs.getDeathEmote()));
				} else if (loop >= defs.getDeathDelay()) {
					reset();
					finish();
					controler.removeNPC();
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}

	@Override
	public ArrayList<Entity> getPossibleTargets() {
		ArrayList<Entity> possibleTarget = new ArrayList<Entity>(1);
		List<Integer> playerIndexes = World.getRegion(getRegionId()).getPlayerIndexes();
		if(playerIndexes != null) {
			for (int npcIndex : playerIndexes) {
				Player player = World.getPlayers().get(npcIndex);
				if (player == null
						|| player.isDead()
						|| player.hasFinished()
						|| !player.isRunning())
					continue;
				possibleTarget.add(player);
			}
		}
		return possibleTarget;
	}

	
	
	@Override
	public double getMagePrayerMultiplier() {
		return 0.1;
	}
	
	@Override
	public double getRangePrayerMultiplier() {
		return 0.1;
	}
	
	@Override
	public double getMeleePrayerMultiplier() {
		return 0.1;
	}
	
}

