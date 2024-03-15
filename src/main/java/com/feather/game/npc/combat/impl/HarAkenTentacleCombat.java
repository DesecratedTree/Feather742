package com.feather.game.npc.combat.impl;

import com.feather.game.Animation;
import com.feather.game.Entity;
import com.feather.game.World;
import com.feather.game.npc.NPC;
import com.feather.game.npc.combat.CombatScript;
import com.feather.game.npc.combat.NPCCombatDefinitions;
import com.feather.utils.Utils;

public class HarAkenTentacleCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] {15209, 15210};
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int distanceX = target.getX() - npc.getX();
		int distanceY = target.getY() - npc.getY();
		int size = npc.getSize();
		int attackStyle = Utils.random(2);
		if (attackStyle == 0 && (distanceX > size || distanceX < -1 || distanceY > size
				|| distanceY < -1)) {
			attackStyle = 1;
		}
		switch(attackStyle) {
		case 0:
			npc.setNextAnimation(new Animation(defs.getAttackEmote()));
			delayHit(npc, 0, target, getMeleeHit(npc, getRandomMaxHit(npc, defs.getMaxHit() - 36, NPCCombatDefinitions.MELEE, target)));
			break;
		case 1:
			npc.setNextAnimation(new Animation(npc.getId() == 15209 ? 16253 : 16242));
			World.sendProjectile(npc, target, npc.getId() == 15209 ? 3004 : 2922, 140, 35, 80, 35, 16, 0);
			if(npc.getId() == 15209)
				delayHit(npc, 2, target, getRangeHit(npc, getRandomMaxHit(npc, defs.getMaxHit(), NPCCombatDefinitions.RANGE, target)));
			else
				delayHit(npc, 2, target, getMagicHit(npc, getRandomMaxHit(npc, defs.getMaxHit(), NPCCombatDefinitions.MAGE, target)));
			break;
		}
		return defs.getAttackDelay();
	}
}
