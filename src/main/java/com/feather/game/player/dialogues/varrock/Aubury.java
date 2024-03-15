package com.feather.game.player.dialogues.varrock;

import com.feather.cache.parser.NPCDefinitions;
import com.feather.game.Animation;
import com.feather.game.ForceTalk;
import com.feather.game.Graphics;
import com.feather.game.World;
import com.feather.game.WorldTile;
import com.feather.game.npc.NPC;
import com.feather.game.player.Player;
import com.feather.game.player.content.Magic;
import com.feather.game.player.dialogues.Dialogue;
import com.feather.utils.ShopsHandler;


public class Aubury extends Dialogue {

	int npcId;
	public NPC npc;
	
	public NPC findNPC(int id) {
		for (NPC npc : World.getNPCs()) {
			if (npc == null || npc.getId() != id)
				continue;
			return npc;
		}
		return null;
	}

	@Override
	public void start() {
		npcId = (int) parameters[0];
		sendEntityDialogue(SEND_1_TEXT_CHAT,
				new String[] { NPCDefinitions.getNPCDefinitions(npcId).name,
						"Do you want to buy some runes?" }, IS_NPC, npcId, 9827);

	}

	@Override
	public void run(int interfaceId, int optionId) {
		switch (stage) {
		case -1:
			stage = 0;
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Yes please.",
					"No thanks.",
					"Can you teleport me to rune essence, please?");
			break;
		case 0:
			switch (optionId) {
			case OPTION_1:
				stage = 1;
				sendPlayerDialogue(9827, "Yes please.");
				break;
			case OPTION_2:
				stage = 2;
				sendPlayerDialogue(9827, "No thanks.");
				break;
			case OPTION_3:
				stage = 3;
				sendPlayerDialogue(9827,
						"Can you teleport me to rune essence, please?");
				break;
			}
			break;
		case 1:
			ShopsHandler.openShop(player, 11);
			end();
			break;
		case 2:
		default:
			end();
			break;
		case 3:
			stage = 4;
			sendNPCDialogue(npcId, 9827, "Sure.");
			break;
		case 4:
			NPC aubury = findNPC(5913);
			aubury.setNextForceTalk(new ForceTalk("Senventior disthine molenko!"));
			aubury.setNextAnimation(new Animation(1818));
			aubury.faceEntity(player);
			World.sendProjectile(aubury, player, 110, 1, 1, 1, 1, 1, 1);
			player.setNextGraphics(new Graphics(110));
			player.setNextWorldTile(new WorldTile(2910, 4832, 0));
			end();
			break;
		}
	}

	@Override
	public void finish() {

	}
}
