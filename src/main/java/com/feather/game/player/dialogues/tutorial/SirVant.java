package com.feather.game.player.dialogues.tutorial;

import com.feather.cache.parser.NPCDefinitions;
import com.feather.game.player.controlers.StartTutorial;
import com.feather.game.player.dialogues.Dialogue;

/**
 * 
 * @author Gircat <gircat101@gmail.com>
 *
 */
public class SirVant extends Dialogue {

	int npcId;
	StartTutorial controler;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		controler = (StartTutorial) parameters[1];
		if (controler == null) {
			sendEntityDialogue(SEND_1_TEXT_CHAT,
					new String[] { player.getDisplayName(),
					"Hello. My name's " + player.getDisplayName() + "."  }, IS_PLAYER,
					player.getIndex(), 9827);
		}
}

	@Override
	public void run(int interfaceId, int componentId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		
	}
}
