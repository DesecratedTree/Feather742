package com.feather.game.player;

import com.feather.game.item.Item;

public class KeptOnDeath {
	
	public static void handleButtons(final Player player, final Item item, int interfaceId, int componentId) {
		if (interfaceId == 17) {
			switch (componentId) {
			case 28:
				if (player.clickedButton == false) {
					player.getPackets().sendConfig2(1747, 218186896);
					player.clickedButton = true;
				} else {
					String info = "The number of items kept on<br>death is normally 3.<br><br><br>Your gravestone will not<br>appear.<br><br>Carried wealth:<br>TODO<br><br>Risked wealth:<br>TODO<br><br>Your hub will be set to:<br>TODO.";
					player.getPackets().sendConfig2(1747, 201397392);
					player.getPackets().sendGlobalString(352, info);
					player.clickedButton = false;
				}
				break;
			}
		}
	}

}
