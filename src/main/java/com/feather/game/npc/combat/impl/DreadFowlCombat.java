package com.feather.game.npc.combat.impl;

import com.feather.game.Animation;
import com.feather.game.Entity;
import com.feather.game.Graphics;
import com.feather.game.World;
import com.feather.game.npc.NPC;
import com.feather.game.npc.combat.CombatScript;
import com.feather.game.npc.combat.NPCCombatDefinitions;
import com.feather.game.npc.familiar.Familiar;
import com.feather.utils.Utils;

public class DreadFowlCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 6825, 6824 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		Familiar familiar = (Familiar) npc;
		boolean usingSpecial = familiar.hasSpecialOn();
		if (usingSpecial) {// priority over regular attack
			npc.setNextAnimation(new Animation(7810));
			npc.setNextGraphics(new Graphics(1318));
			delayHit(
					npc,
					1,
					target,
					getMagicHit(
							npc,
							getRandomMaxHit(npc, 40, NPCCombatDefinitions.MAGE,
									target)));
			World.sendProjectile(npc, target, 1376, 34, 16, 30, 35, 16, 0);
		} else {
			if (Utils.getRandom(10) == 0) {// 1/10 chance of random special
											// (weaker)
				npc.setNextAnimation(new Animation(7810));
				npc.setNextGraphics(new Graphics(1318));
				delayHit(
						npc,
						1,
						target,
						getMagicHit(
								npc,
								getRandomMaxHit(npc, 30,
										NPCCombatDefinitions.MAGE, target)));
				World.sendProjectile(npc, target, 1376, 34, 16, 30, 35, 16, 0);
			} else {
				npc.setNextAnimation(new Animation(7810));
				delayHit(
						npc,
						1,
						target,
						getMeleeHit(
								npc,
								getRandomMaxHit(npc, 30,
										NPCCombatDefinitions.MELEE, target)));
			}
		}
		return defs.getAttackDelay();
	}
}
