package com.feather.game.npc.combat.impl;

import com.feather.game.Animation;
import com.feather.game.Entity;
import com.feather.game.npc.NPC;
import com.feather.game.npc.combat.CombatScript;
import com.feather.game.npc.combat.NPCCombatDefinitions;

public class LivingRockStrickerCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
	
		return new Object[] {8833};
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int distanceX = target.getX() - npc.getX();
		int distanceY = target.getY() - npc.getY();
		int size = npc.getSize();
		if (distanceX > size || distanceX < -1 || distanceY > size
				|| distanceY < -1) {
			//TODO add projectile
			npc.setNextAnimation(new Animation(12196));
			delayHit(
					npc,
					1,
					target,
					getRangeHit(
							npc,
							getRandomMaxHit(npc, defs.getMaxHit(),
									NPCCombatDefinitions.RANGE, target)));
		} else{
			npc.setNextAnimation(new Animation(defs.getAttackEmote()));
			delayHit(
					npc,
					0,
					target,
					getMeleeHit(
							npc,
							getRandomMaxHit(npc, 84,
									NPCCombatDefinitions.MELEE, target)));
			return defs.getAttackDelay();
		}
		
		return defs.getAttackDelay();
	}

}
