package com.feather.game.player.dialogues.falador;
import com.feather.game.player.dialogues.Dialogue;

/**
 * The Feather Dialogue for Emily.
 * 
 * @author Gircat <gircat101@gmail.com>
 * @author Feather RuneScape 2012
 */

public class Emily extends Dialogue {

	private int npcId;
	
	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, 9827, "Oh hi there, " + player.getDisplayName() + ". What can I help you with?");
	}
	@Override
	public void run(int interfaceId, int componentId) {
		switch(stage) {
		case -1:
			stage = 0;
			sendPlayerDialogue(9827, "I was just wondering... Do you have any adult beverages available?");
			break;
		case 0:
			stage = 1;
			sendNPCDialogue(npcId, 9827, "No I don't, sorry.");
			break;
		case 1:
			stage = 100;
			sendPlayerDialogue(9827, "Okay thank you.");
			break;
		case 100:
			end();
			break;

		}
	}
		public void finish() {
			}
		}
