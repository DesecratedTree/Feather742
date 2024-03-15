package com.feather.game.player.content;

import com.feather.game.player.Player;

public final class AdventurersLog {

	private AdventurersLog() {
		
	}
	
	public static void open(Player player) {
		player.getInterfaceManager().sendInterface(623);
	}
}
