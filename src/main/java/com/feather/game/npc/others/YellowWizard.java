package com.feather.game.npc.others;

import com.feather.game.WorldTile;
import com.feather.game.npc.NPC;
import com.feather.game.player.Player;
import com.feather.game.player.controlers.RunespanControler;
import com.feather.utils.Utils;

@SuppressWarnings("serial")
public class YellowWizard extends NPC {
	
	private RunespanControler controler;
	private long spawnTime;
	public YellowWizard(WorldTile tile, RunespanControler controler) {
		super(15430, tile, -1, true, true);
		spawnTime = Utils.currentTimeMillis();
		this.controler = controler;
	}
	
	@Override
	public void processNPC() {
		if(spawnTime + 300000 < Utils.currentTimeMillis()) 
			finish();
	}
	
	@Override
	public void finish() {
		controler.removeWizard();
		super.finish();
	}
	public static void giveReward(Player player) {
		
	}
	@Override
	public boolean withinDistance(Player tile, int distance) {
		return tile == controler.getPlayer() && super.withinDistance(tile, distance);
	}

}
