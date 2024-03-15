package com.feather.game.npc.fightpits;

import java.util.ArrayList;

import com.feather.game.Entity;
import com.feather.game.Graphics;
import com.feather.game.WorldTile;
import com.feather.game.minigames.FightPits;
import com.feather.game.npc.NPC;
import com.feather.game.player.Player;

@SuppressWarnings("serial")
public class FightPitsNPC extends NPC {


	public FightPitsNPC(int id, WorldTile tile) {
		super(id, tile, -1, true, true);
		setForceMultiArea(true);
		setNoDistanceCheck(true);
	}
	
	@Override
	public void sendDeath(Entity source) {
		setNextGraphics(new Graphics(2924 + getSize()));
		super.sendDeath(source);
	}

	@Override
	public ArrayList<Entity> getPossibleTargets() {
		ArrayList<Entity> possibleTarget = new ArrayList<Entity>();
		for(Player player : FightPits.arena)
			possibleTarget.add(player);
		return possibleTarget;
	}

}
