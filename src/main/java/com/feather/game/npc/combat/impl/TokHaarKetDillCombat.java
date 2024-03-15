package com.feather.game.npc.combat.impl;

import com.feather.game.Animation;
import com.feather.game.Entity;
import com.feather.game.Graphics;
import com.feather.game.npc.NPC;
import com.feather.game.npc.combat.CombatScript;
import com.feather.game.npc.combat.NPCCombatDefinitions;
import com.feather.game.player.Player;
import com.feather.utils.Utils;

public class TokHaarKetDillCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "TokHaar-Ket-Dill" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		NPCCombatDefinitions defs = npc.getCombatDefinitions();
		if(Utils.random(6) == 0) {
			delayHit(
					npc,
					0,
					target,
					getRegularHit(
							npc,
							Utils.random(defs.getMaxHit()+1)));
			target.setNextGraphics(new Graphics(2999));
			if(target instanceof Player) {
				Player playerTarget = (Player) target;
				playerTarget.getPackets().sendGameMessage("The TokHaar-Ket-Dill slams it's tail to the ground.");
			}
		}else{
			delayHit(
					npc,
					0,
					target,
					getMeleeHit(
							npc,
							getRandomMaxHit(npc, defs.getMaxHit(), defs.getAttackStyle(),
									target)));
		}
		npc.setNextAnimation(new Animation(defs.getAttackEmote()));
		return defs.getAttackDelay();
	}
}
