package com.feather.game;

import com.feather.game.player.Player;

public class ClanManager {

	public static void handleButtons(final Player player, int interfaceId, int componentId) {
		if (interfaceId == 1110) {
			switch (componentId) {
			case 82:
				String username = player.getDisplayName();
				String clanName = "Feather";
				player.getPackets().sendJoinClanChat(username, clanName);
				player.getClanSize += 1;
				break;
			case 75:
				sendClanDetails(player);
				break;
			case 78:
				player.getPackets().sendGlobalString(347, player.getDisplayName());
				sendClanMates(player);
				break;
			}
		}
		if (interfaceId == 1096) {
			switch (componentId) {
			case 113:
				sendClanMates(player);
				break;
			case 120:
				sendClanSettings(player);
				break;
			case 386:
				sendClanPermissions(player);
				break;
			case 47:
				player.getPackets().sendRunScript(5136, 1);
				player.getPackets().sendGlobalConfig1(1500, 126);
				player.getPackets().sendGlobalConfig1(1501, 0);
				player.getPackets().sendGlobalConfig1(1564, 0);
				player.getPackets().sendGlobalConfig1(1565, 0);
				player.getPackets().sendGlobalConfig1(1566, 0);
				player.getPackets().sendGlobalConfig1(1567, 0);
				player.getPackets().sendGlobalConfig1(1569, 1);
				break;
			case 346:
				player.getInterfaceManager().sendInterface(1089);
				player.getPackets().sendUnlockIComponentOptionSlots(1089, 30, 0, 241, 2);
				player.getPackets().sendIComponentSettings(1089, 30, 0, 241, 2);
				break;
			}
		}
	}

	public static void sendClanDetails(final Player player) {
		String clanName = "Feather";
		player.getInterfaceManager().sendInterface(1107);
		player.getPackets().sendIComponentText(1107, 155, "" + clanName); // Clan
																			// Name
		player.getPackets().sendIComponentText(1107, 35,
				player.getDisplayName()); // Clan Owner
		player.getPackets().sendIComponentText(1107, 37,
				"" + player.getClanSize); // Clan Size
		player.getPackets().sendHideIComponent(1107, 139, true);
		player.getPackets().sendHideIComponent(1107, 90, true);
		player.getPackets().sendHideIComponent(1107, 60, true);
	}

	public static void sendClanMates(final Player player) {
		player.getInterfaceManager().sendInterface(1096);
		player.getPackets().sendHideIComponent(1096, 83, false);
		player.getPackets().sendHideIComponent(1096, 110, false);
		player.getPackets().sendHideIComponent(1096, 84, true);
		player.getPackets().sendHideIComponent(1096, 117, true);
		player.getPackets().sendHideIComponent(1096, 85, true);
		player.getPackets().sendHideIComponent(1096, 385, true);
		player.getPackets().sendHideIComponent(1096, 202, false);
		player.getPackets().sendHideIComponent(1096, 203, false);
		player.getPackets().sendHideIComponent(1096, 217, false);
		player.getPackets().sendHideIComponent(1096, 218, false);
		player.getPackets().sendUnlockIComponentOptionSlots(1096, 240, 0, 144, 2);
		player.getPackets().sendUnlockIComponentOptionSlots(1096, 290, 0, 200, 2);
		player.getPackets().sendUnlockIComponentOptionSlots(1096, 41, 0, 500, 2);
		player.getPackets().sendUnlockIComponentOptionSlots(1096, 276, 0, 125, 2);
		player.getPackets().sendUnlockIComponentOptionSlots(1096, 262, 0, 500, 2);
		player.getPackets().sendIComponentSettings(1096, 240, 0, 144, 2);
		player.getPackets().sendIComponentSettings(1096, 290, 0, 200, 2);
		player.getPackets().sendIComponentSettings(1096, 41, 0, 500, 2);
		player.getPackets().sendIComponentSettings(1096, 276, 0, 125, 2);
		player.getPackets().sendIComponentSettings(1096, 262, 0, 500, 2);
	}

	public static void sendClanPermissions(final Player player) {
		player.getInterfaceManager().sendInterface(1096);
		player.getPackets().sendHideIComponent(1096, 83, true);
		player.getPackets().sendHideIComponent(1096, 110, true);
		player.getPackets().sendHideIComponent(1096, 84, true);
		player.getPackets().sendHideIComponent(1096, 117, true);
		player.getPackets().sendHideIComponent(1096, 85, false);
		player.getPackets().sendHideIComponent(1096, 385, false);
	}

	public static void sendClanSettings(final Player player) {
		player.getInterfaceManager().sendInterface(1096);
		player.getPackets().sendHideIComponent(1096, 83, true);
		player.getPackets().sendHideIComponent(1096, 110, true);
		player.getPackets().sendHideIComponent(1096, 84, false);
		player.getPackets().sendHideIComponent(1096, 117, false);
		player.getPackets().sendHideIComponent(1096, 85, true);
		player.getPackets().sendHideIComponent(1096, 385, true);

	}

}
