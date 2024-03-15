package com.feather.game.npc.others;

import com.feather.game.Entity;
import com.feather.game.WorldTile;
import com.feather.game.npc.NPC;

@SuppressWarnings("serial")
public class PestPortal extends NPC {

	public PestPortal(int id, WorldTile tile) {
		super(id, tile, -1, true, true);
		setCantFollowUnderCombat(true);
	}

	@Override
	public void processNPC() {
		if (isDead())
			return;
		cancelFaceEntityNoCheck();
	}

	@Override
	public void sendDeath(Entity killer) {
		resetWalkSteps();
		getCombat().removeTarget();
		super.sendDeath(killer);
	}
}