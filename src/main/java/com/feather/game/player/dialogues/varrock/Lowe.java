package com.feather.game.player.dialogues.varrock;

import com.feather.cache.parser.NPCDefinitions;
import com.feather.game.player.dialogues.Dialogue;
import com.feather.utils.ShopsHandler;

/**
 *  Feather 2012 RuneScape Remake
 * @author Gircat <gircat101@gmail.com>
 *
 */

public class Lowe extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendEntityDialogue(
				SEND_1_TEXT_CHAT,
				new String[] { NPCDefinitions.getNPCDefinitions(npcId).name,
						"Welcome to Lowe's Archery Emporium. Do you want to see my wares?" },
				IS_NPC, npcId, 9827);
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			stage = 0;
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Yes, please.", "No, I prefer to bash things close up.");
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				stage = 1;
				sendPlayerDialogue(9827, "Yes, please.");
				break;
			case OPTION_2:
				stage = 2;
				sendPlayerDialogue(9827, "No, I prefer to bash things close up.");
				break;
			}
			break;
		case 1:
			end();
			ShopsHandler.openShop(player, 19);
			break;
		case 2:
			stage = 3;
			sendNPCDialogue(npcId, 9827,
					"Humph, philistine.");
			break;
		case 3:
			default:
				end();
				break;
		}
	}

	@Override
	public void finish() {
		
	}

}
