package com.feather.content.utils;

import java.util.Random;

import com.feather.game.Animation;
import com.feather.game.ForceTalk;
import com.feather.game.player.Player;

/**
 * Begins the toy horsey's.
 * @author Gircat <gircat101@gmail.com>
 * @author Feather - RuneScape 2012 Remake
 */

public class ToyHorsey {

	private static String speech [][] = 
		{{"Come-on Dobbin, we can win the race!"}, 
		{"Hi-ho Silver, and away!"},
		{"Neaahhhyyy!"}};


	/**
	 * 
	 * @param player
	 */
	public static void play(Player player) {
		int rand = RandomSpeech();
		player.setNextAnimation(new Animation(918));
		player.setNextForceTalk(new ForceTalk(speech[rand][0]));
	}
	
	/**
	 * 
	 * @return
	 */
	public static int RandomSpeech() {
		int random = 0;
		Random rand = new Random();
		random = rand.nextInt(speech.length);
		return random;
	}
	
}