package com.feather.game.player.dialogues.catherby;
import com.feather.game.player.dialogues.Dialogue;

/**
 * The Feather Dialogue for Hickton.
 * 
 * @author Gircat <gircat101@gmail.com>
 * @author Feather RuneScape 2012
 */

public class Hickton extends Dialogue {

	private int npcId;
	
	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, 9827, "Hello there!");
	}
	@Override
	public void run(int interfaceId, int componentId) {
		switch(stage) {
		case -1:
			stage = 100;
			player.getPackets().sendGameMessage("We're sorry, that feature isn't added yet!");
			sendPlayerDialogue(9827, "May I have the cape you're wearing?");
			break;
		case 100:
			end();
			break;

		}
	}
	public void finish() {
	}
	}