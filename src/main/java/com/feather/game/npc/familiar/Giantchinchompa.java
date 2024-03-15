package com.feather.game.npc.familiar;

import com.feather.game.WorldTile;
import com.feather.game.player.Player;
import com.feather.game.player.actions.Summoning.Pouches;

public class Giantchinchompa extends Familiar {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7708802901929527088L;

	public Giantchinchompa(Player owner, Pouches pouch, WorldTile tile,
			int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(owner, pouch, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
	}

	@Override
	public String getSpecialName() {
		return "Explode";
	}

	@Override
	public String getSpecialDescription() {
		return "Explodes, damaging nearby enemies.";
	}

	@Override
	public int getBOBSize() {
		return 0;
	}

	@Override
	public int getSpecialAmount() {
		return 3;
	}

	@Override
	public SpecialAttack getSpecialAttack() {
		return SpecialAttack.ENTITY;
	}

	@Override
	public boolean submitSpecial(Object object) {
		return false;
	}

}
