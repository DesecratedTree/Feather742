package com.feather.game.npc.combat.impl;

import com.feather.game.Animation;
import com.feather.game.Entity;
import com.feather.game.Graphics;
import com.feather.game.npc.NPC;
import com.feather.game.npc.combat.CombatScript;
import com.feather.game.npc.combat.NPCCombatDefinitions;
import com.feather.game.player.Player;
import com.feather.utils.Utils;

public class ToragCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] {2029};
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		npc.setNextAnimation(new Animation(defs.getAttackEmote()));
		int damage = getRandomMaxHit(npc, defs.getMaxHit(), NPCCombatDefinitions.MELEE, target);
		if(damage != 0 && target instanceof Player && Utils.random(3) == 0) {	
			target.setNextGraphics(new Graphics(399));
			Player targetPlayer = (Player) target;
			targetPlayer.setRunEnergy(targetPlayer.getRunEnergy() > 4 ? targetPlayer.getRunEnergy() - 4 : 0);
		}
		delayHit(npc, 0, target, getMeleeHit(npc, damage));
		return defs.getAttackDelay();
	}
}
