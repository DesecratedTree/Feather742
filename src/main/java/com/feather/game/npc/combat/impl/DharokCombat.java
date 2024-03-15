package com.feather.game.npc.combat.impl;

import com.feather.game.Animation;
import com.feather.game.Entity;
import com.feather.game.npc.NPC;
import com.feather.game.npc.combat.CombatScript;
import com.feather.game.npc.combat.NPCCombatDefinitions;

public class DharokCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] {2026};
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		npc.setNextAnimation(new Animation(defs.getAttackEmote()));
		int damage = getRandomMaxHit(npc, defs.getMaxHit(), NPCCombatDefinitions.MELEE, target);
		if(damage != 0) {	
			double perc = 1 - (npc.getHitpoints() / npc.getMaxHitpoints());
			damage += perc * 380;
		}
		delayHit(npc, 0, target, getMeleeHit(npc, damage));
		return defs.getAttackDelay();
	}
}
