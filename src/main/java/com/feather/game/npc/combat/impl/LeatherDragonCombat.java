package com.feather.game.npc.combat.impl;

import com.feather.game.Animation;
import com.feather.game.Entity;
import com.feather.game.Graphics;
import com.feather.game.npc.NPC;
import com.feather.game.npc.combat.CombatScript;
import com.feather.game.npc.combat.NPCCombatDefinitions;
import com.feather.game.player.Player;
import com.feather.game.player.content.Combat;
import com.feather.utils.Utils;

public class LeatherDragonCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Green dragon", "Blue dragon", "Red dragon",
				"Black dragon", 742, 14548 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int distanceX = target.getX() - npc.getX();
		int distanceY = target.getY() - npc.getY();
		int size = npc.getSize();
		if (distanceX > size || distanceX < -1 || distanceY > size
				|| distanceY < -1)
			return 0;
		if (Utils.getRandom(3) != 0) {
			npc.setNextAnimation(new Animation(defs.getAttackEmote()));
			delayHit(
					npc,
					0,
					target,
					getMeleeHit(
							npc,
							getRandomMaxHit(npc, defs.getMaxHit(),
									NPCCombatDefinitions.MELEE, target)));
		} else {
			int damage = Utils.getRandom(650);
			npc.setNextAnimation(new Animation(12259));
			npc.setNextGraphics(new Graphics(1, 0, 100));
			final Player player = target instanceof Player ? (Player) target
					: null;
			if (Combat.hasAntiDragProtection(target)
					|| (player != null && (player.getPrayer()
							.usingPrayer(0, 17) || player.getPrayer()
							.usingPrayer(1, 7)))) {
				damage = 0;
				player.getPackets()
						.sendGameMessage(
								"Your "
										+ (Combat.hasAntiDragProtection(target) ? "shield"
												: "prayer")
										+ " absorb's most of the dragon's breath!",
								true);
			}
			if (player != null
					&& player.getFireImmune() > Utils.currentTimeMillis()) {
				if (damage != 0)
					damage = Utils.getRandom(50);
			} else if (damage == 0)
				damage = Utils.getRandom(50);
			else if (player != null)
				player.getPackets().sendGameMessage(
						"You are hit by the dragon's fiery breath!", true);
			delayHit(npc, 1, target, getRegularHit(npc, damage));
		}
		return defs.getAttackDelay();
	}
}
