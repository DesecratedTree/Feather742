package com.feather.game.player.dialogues.varrock;

import com.feather.cache.parser.NPCDefinitions;
import com.feather.game.player.dialogues.Dialogue;
import com.feather.utils.ShopsHandler;

/**
 *  Feather 2012 RuneScape Remake
 * @author Gircat <gircat101@gmail.com>
 *
 */

public class Iffie extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendEntityDialogue(
				SEND_1_TEXT_CHAT,
				new String[] {
						NPCDefinitions.getNPCDefinitions(npcId).name,
						"Hello, dearie! Were you wanting to collect a costum, or is "
								+ "there something else I can do for you today." },
				IS_NPC, npcId, 9827);
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			stage = 0;
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "I've come for a costume.",
					"Aren't you selling anything?", "I just came for a chat.");
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				stage = 1;
				sendPlayerDialogue(9827, "I've come for a costume.");
				break;
			case OPTION_2:
				stage = 4;
				sendPlayerDialogue(9827, "Aren't you selling anything?");
				break;
			case OPTION_3:
				stage = 2;
				sendPlayerDialogue(9827, "I just came for a chat.");
				break;
			}
			break;
		case 1:
			stage = 2;
			sendNPCDialogue(npcId, 9827,
					"Some of these costumes even come with a free emote!");
			break;
		case 2:
			stage = 3;
			sendNPCDialogue(
					npcId,
					9827,
					"Just buy one peice of the mime or zombie costume and I'll show you all the relevant emotes.");
			break;
		case 3:
			end();
			ShopsHandler.openShop(player, 1);
			break;
		case 4:
			stage = 6;
			sendNPCDialogue(
					npcId,
					9827,
					"Oh, yes, but only costumes. Thessalia sells other clothes and runs the makeover service.");
			break;
		case 5:
			stage = 6;
			sendNPCDialogue(
					npcId,
					9827,
					"Oh, I'm sorry, but I'll never get my knitting done if I stop for a chit-chat with every young lass who wanders through the shop!");
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
