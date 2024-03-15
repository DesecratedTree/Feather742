package com.feather.game.npc.combat.impl;

import com.feather.game.Animation;
import com.feather.game.Entity;
import com.feather.game.Graphics;
import com.feather.game.npc.NPC;
import com.feather.game.npc.combat.CombatScript;
import com.feather.game.npc.combat.NPCCombatDefinitions;
import com.feather.game.npc.familiar.Familiar;

public class SpiritKalphiteCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 6995, 6994 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		Familiar familiar = (Familiar) npc;
		boolean usingSpecial = familiar.hasSpecialOn();
		int damage = 0;
		if (usingSpecial) {// TODO find special
			npc.setNextAnimation(new Animation(8519));
			npc.setNextGraphics(new Graphics(8519));
			damage = getRandomMaxHit(npc, 20, NPCCombatDefinitions.MELEE,
					target);
			delayHit(npc, 1, target, getMeleeHit(npc, damage));
		} else {
			npc.setNextAnimation(new Animation(8519));
			damage = getRandomMaxHit(npc, 50, NPCCombatDefinitions.MELEE,
					target);
			delayHit(npc, 1, target, getMeleeHit(npc, damage));
		}
		return defs.getAttackDelay();
	}
}
