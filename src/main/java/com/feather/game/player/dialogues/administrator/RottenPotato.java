package com.feather.game.player.dialogues.administrator;

import com.feather.game.player.dialogues.Dialogue;
/**
 * Begins the dialogue for the Rotten Potato.
 * @author Gircat <gircat101@gmail.com>
 * Feather - RuneScape 2012
 */
public class RottenPotato extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, 9827, " My house! There I was, sat in the living room, having a ",
				"nice cup of tea and minding my own business, and BANG!",
				"My house collapses around me!");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			stage = 0;
			sendNPCDialogue(npcId, 9827, "I went to look - after all I am an explorer - and there, ",
					"right outside my house, I find Saradomin and Zamorak fighting it out! ");
			break;

			}
	}
	public void finish() {
	}
	}
