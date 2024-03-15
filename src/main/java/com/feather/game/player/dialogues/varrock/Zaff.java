package com.feather.game.player.dialogues.varrock;

import com.feather.cache.parser.NPCDefinitions;
import com.feather.game.player.dialogues.Dialogue;
import com.feather.utils.ShopsHandler;

/**
 *  Feather 2012 RuneScape Remake
 * @author Gircat <gircat101@gmail.com>
 *
 */

public class Zaff extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendEntityDialogue(
				SEND_1_TEXT_CHAT,
				new String[] { NPCDefinitions.getNPCDefinitions(npcId).name,
						"Would you like to buy or sell some staves?" },
				IS_NPC, npcId, 9827);
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			stage = 0;
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Yes, please.", "No, thank you.");
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				stage = 1;
				sendPlayerDialogue(9827, "Yes, please.");
				break;
			case OPTION_2:
				stage = 2;
				sendPlayerDialogue(9827, "No, thank you.");
				break;
			}
			break;
		case 1:
			end();
			ShopsHandler.openShop(player, 20);
			break;
		case 2:
			stage = 3;
			sendNPCDialogue(npcId, 9827,
					"Well, 'stick' your head in again if you change your mind.");
			break;
		case 3:
			stage = 4;
			sendPlayerDialogue(9827,
					"Huh, terrible pun. You just can't get the 'staff' these days!");
			break;
		case 4:
			stage = 5;
			sendNPCDialogue(
					npcId,
					9827,
					"Actually, I have an assistant now, but he's a bit thin. You might even call a bit of a beanpole.");
			break;
		case 5:
			stage = 6;
			sendPlayerDialogue(9827,
					"*groans* okay, I'm leaving now!");
			break;
		case 6:
			default:
				end();
				break;
		}
	}

	@Override
	public void finish() {
		
	}

}
