package com.feather.game.npc.combat.impl;

import com.feather.game.Entity;
import com.feather.game.npc.NPC;
import com.feather.game.npc.combat.CombatScript;

public class BarricadeCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		// TODO Auto-generated method stub
		return new Object[] { "Barricade" };
	}

	/*
	 * empty
	 */
	@Override
	public int attack(NPC npc, Entity target) {
		return 0;
	}

}
