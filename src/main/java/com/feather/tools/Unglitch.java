package com.feather.tools;

import java.io.File;

import com.feather.game.player.Player;
import com.feather.utils.SerializableFilesManager;


public class Unglitch {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		File dir = new File("./checkacc/");
		File[] accs = dir.listFiles();
		for (File acc : accs) {
			Player player = (Player) SerializableFilesManager
					.loadSerializedFile(acc);
			if (player.getRights() > 0) {
				player.setPermBanned(false);
				player.setBanned(0);
				System.out.println(player.getUsername());
				SerializableFilesManager.storeSerializableClass(player, acc);
			}
		}
	}

}
