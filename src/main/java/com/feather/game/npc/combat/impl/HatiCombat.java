package com.feather.game.npc.combat.impl;

import com.feather.game.Entity;
import com.feather.game.npc.NPC;
import com.feather.game.npc.combat.CombatScript;
import com.feather.game.npc.combat.NPCCombatDefinitions;

public class HatiCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Hati" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		
		return defs.getAttackDelay();
	}
}
