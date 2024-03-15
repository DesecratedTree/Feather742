package com.feather.game.npc.combat.impl;

import com.feather.game.Animation;
import com.feather.game.Entity;
import com.feather.game.npc.NPC;
import com.feather.game.npc.combat.CombatScript;
import com.feather.game.npc.combat.NPCCombatDefinitions;
import com.feather.game.npc.familiar.Familiar;
import com.feather.game.player.Player;

public class TzKihCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Tz-Kih", 7361, 7362};
	}

	@Override
	public int attack(NPC npc, Entity target) {//yoa
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int damage = 0;
		if (npc instanceof Familiar) {// TODO get anim and gfx
			Familiar familiar = (Familiar) npc;
			boolean usingSpecial = familiar.hasSpecialOn();
			if (usingSpecial) {
				for (Entity entity : npc.getPossibleTargets()) {
					damage = getRandomMaxHit(npc, 70, NPCCombatDefinitions.MELEE, target);
					if (target instanceof Player)
						((Player) target).getPrayer().drainPrayer(damage);
					delayHit(npc, 0, entity, getMeleeHit(npc, damage));
				}
			}
			return defs.getAttackDelay();
		}
		npc.setNextAnimation(new Animation(defs.getAttackEmote()));
		damage = getRandomMaxHit(npc, defs.getMaxHit(), NPCCombatDefinitions.MELEE, target);
		if (target instanceof Player)
			((Player) target).getPrayer().drainPrayer(damage + 10);
		delayHit(npc, 0, target, getMeleeHit(npc, damage));
		return defs.getAttackDelay();
	}
}
