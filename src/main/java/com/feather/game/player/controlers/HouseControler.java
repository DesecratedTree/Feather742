package com.feather.game.player.controlers;

import com.feather.game.RegionBuilder;
import com.feather.game.WorldObject;
import com.feather.game.WorldTile;
import com.feather.game.player.content.construction.House;
import com.feather.game.player.content.construction.House.Room;
import com.feather.game.player.content.construction.House.RoomReference;

public class HouseControler extends Controler {
	
	private House house;
	private int[] boundChuncks;
	
	@Override
	public void start() {
		house = new House();
		boundChuncks = RegionBuilder.findEmptyChunkBound(8, 8); 
		house.constructHouse(boundChuncks, false);
		player.setNextWorldTile(new WorldTile(boundChuncks[0]*8 + 35, boundChuncks[1]*8 + 35,0));
	}
	
	boolean remove = true;
	/**
	 * return process normaly
	 */
	@Override
	public boolean processObjectClick5(WorldObject object) {
		house.previewRoom(player, boundChuncks, new RoomReference(Room.PARLOUR, 4, 5, 0, 0), remove = !remove);
		return true;
	}

}
