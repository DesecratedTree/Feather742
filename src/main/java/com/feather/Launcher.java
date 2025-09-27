package com.feather;

import java.util.concurrent.TimeUnit;

import com.alex.store.Index;
import com.feather.cache.Cache;
import com.feather.cache.parser.ItemDefinitions;
import com.feather.cache.parser.ItemsEquipIds;
import com.feather.cache.parser.NPCDefinitions;
import com.feather.cache.parser.ObjectDefinitions;
import com.feather.engine.GameEngine;
import com.feather.game.Region;
import com.feather.game.RegionBuilder;
import com.feather.game.World;
import com.feather.game.npc.combat.CombatScriptsHandler;
import com.feather.game.player.LendingManager;
import com.feather.game.player.Player;
import com.feather.game.player.content.FishingSpotsHandler;
import com.feather.game.player.content.FriendChatsManager;
import com.feather.game.player.controlers.ControlerHandler;
import com.feather.game.player.cutscenes.CutscenesHandler;
import com.feather.game.player.dialogues.DialogueHandler;
import com.feather.game.worldlist.WorldList;
import com.feather.net.ServerChannelHandler;
import com.feather.utils.*;
import com.feather.utils.huffman.Huffman;

public final class Launcher {

	public static void main(String[] args) throws Exception {
		Settings.HOSTED = false;
		Settings.DEBUG = true;
		long currentTime = Utils.currentTimeMillis();

        Logger.log("Launcher", "Initializing GameEngine...");
        GameEngine.init();

		Logger.log("Launcher", "Initiating cache...");
		Cache.init();

		ItemsEquipIds.init();
		Huffman.init();
		Logger.log("Launcher", "Initing Data Files...");
		DisplayNames.init();
		IPBanL.init();
		PkRank.init();
		DTRank.init();
		MapArchiveKeys.init();
		MapAreas.init();
		ObjectSpawns.init();
		NPCSpawns.init();
		NPCCombatDefinitionsL.init();
		NPCBonuses.init();
		NPCDrops.init();
		ItemExamines.init();
		MusicHints.init();
		ShopsHandler.init();
		NPCExamines.init();
		Logger.log("Launcher", "Initing WorldList...");
		WorldList.init();
		Logger.log("Launcher", "Initing Lent Items...");
		LendingManager.init();
		Logger.log("Launcher", "Initing Fishing Spots...");
		FishingSpotsHandler.init();
		Logger.log("Launcher", "Initing NPC Combat Scripts...");
		CombatScriptsHandler.init();
		Logger.log("Launcher", "Initing Dialogues...");
		DialogueHandler.init();
		Logger.log("Launcher", "Initing Controlers...");
		ControlerHandler.init();
		Logger.log("Launcher", "Initing Cutscenes...");
		CutscenesHandler.init();
		Logger.log("Launcher", "Initing Friend Chats Manager...");
		FriendChatsManager.init();
		Logger.log("Launcher", "Initing World...");
		World.init();
		Logger.log("Launcher", "Initing Region Builder...");
		RegionBuilder.init();
		Logger.log("Launcher", "Initing Server Channel Handler...");
		try {
			ServerChannelHandler.init();
		} catch (Throwable e) {
			Logger.handle(e);
			Logger.log("Launcher",
					"Failed initing Server Channel Handler. Shutting down...");
			System.exit(1);
			return;
		}
		Logger.log("Launcher", "Server took "
				+ (Utils.currentTimeMillis() - currentTime)
				+ " milli seconds to launch.");

		addAccountsSavingTask();
		addCleanMemoryTask();
		// Donations.init();
	}

    private static void addCleanMemoryTask() {
        GameEngine.slowExecutor.scheduleWithFixedDelay(() -> {
            try {
                Runtime runtime = Runtime.getRuntime();

                // Take memory snapshot BEFORE cleanup
                long beforeFree = runtime.freeMemory();
                long beforeTotal = runtime.totalMemory();
                long beforeUsed = beforeTotal - beforeFree;

                // Perform cleanup
                cleanMemory();

                // Suggest GC (optional)
                System.gc();

                // Take memory snapshot AFTER cleanup
                long afterFree = runtime.freeMemory();
                long afterTotal = runtime.totalMemory();
                long afterUsed = afterTotal - afterFree;

                long freedMemory = beforeUsed - afterUsed;

                System.out.println();
                Logger.log("Launcher", "Memory Freed: " + formatGB(freedMemory));
                System.out.println();

            } catch (Throwable e) {
                Logger.handle(e);
            }
        }, 0, 5, TimeUnit.MINUTES);
    }

    // Utility method to format bytes to GB string with 2 decimal places
    private static String formatGB(long bytes) {
        double gb = bytes / (1024.0 * 1024 * 1024);
        return String.format("%.2f GB", gb);
    }


	private static void addAccountsSavingTask() {
		GameEngine.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					saveFiles();
				} catch (Throwable e) {
					Logger.handle(e);
				}

			}
		}, 1, 1, TimeUnit.SECONDS);
	}

	public static void saveFiles() {
		for (Player player : World.getPlayers()) {
			if (player == null || !player.hasStarted() || player.hasFinished())
				continue;
			SerializableFilesManager.savePlayer(player);
		}
		DisplayNames.save();
		IPBanL.save();
		PkRank.save();
		DTRank.save();
	}

	public static void cleanMemory() {
			ItemDefinitions.clearItemDefinitions();
			NPCDefinitions.clearNPCDefinitions();
			ObjectDefinitions.clearObjectDefinitions();
			for (Region region : World.getRegions().values()) region.removeMapFromMemory();
		    for (Index index : Cache.store.getIndexes()) index.resetCachedFiles();
		    GameEngine.fastExecutor.purge();
		    System.gc();
	}

	public static void shutdown() {
		try {
			closeServices();
		} finally {
            System.exit(0);
        }
    }

	public static void closeServices() {
		ServerChannelHandler.shutdown();
		GameEngine.shutdown();
	}

	public static void restart() {
		closeServices();
		System.gc();
		try {
			Runtime.getRuntime().exec("java -server -Xms256m -Xmx2048m Launcher false false true false");
			System.exit(0);
		} catch (Throwable e) {
			Logger.handle(e);
		}

	}

	private Launcher() {

	}

}
