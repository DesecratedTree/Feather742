package com.feather.game.npc.familiar;

import com.feather.game.WorldObject;
import com.feather.game.WorldTile;
import com.feather.game.player.Player;
import com.feather.game.player.actions.Woodcutting;
import com.feather.game.player.actions.Summoning.Pouches;
import com.feather.game.player.actions.Woodcutting.TreeDefinitions;

public class Beaver extends Familiar {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9181393770444014076L;

	public Beaver(Player owner, Pouches pouch, WorldTile tile,
			int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(owner, pouch, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
	}

	@Override
	public String getSpecialName() {
		return "Multichop";
	}

	@Override
	public String getSpecialDescription() {
		return "Chops a tree, giving the owner its logs. There is also a chance that random logs may be produced.";
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
		return SpecialAttack.OBJECT;
	}

	@Override
	public boolean submitSpecial(Object context) {
		WorldObject object = (WorldObject) context;
		getOwner().getActionManager().setAction(
				new Woodcutting(object, TreeDefinitions.NORMAL));
		return true;
	}
}
