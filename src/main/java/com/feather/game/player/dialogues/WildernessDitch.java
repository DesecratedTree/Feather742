package com.feather.game.player.dialogues;

import com.feather.game.Animation;
import com.feather.game.ForceMovement;
import com.feather.game.WorldObject;
import com.feather.game.WorldTile;
import com.feather.game.tasks.WorldTask;
import com.feather.game.tasks.WorldTasksManager;

public class WildernessDitch extends Dialogue {

	private WorldObject ditch;

	@Override
	public void start() {
		ditch = (WorldObject) parameters[0];
		player.getInterfaceManager().sendInterface(382);
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (interfaceId == 382 && componentId == 19) {
			player.stopAll();
			player.lock(4);
			player.setNextAnimation(new Animation(6132));
			final WorldTile toTile = new WorldTile(ditch.getRotation() == 3 || ditch.getRotation() == 1 ? ditch.getX() - 1 : player.getX(),
					ditch.getRotation() == 0 || ditch.getRotation() == 2 ? ditch.getY() + 2 : player.getY(), ditch.getPlane());
			player.setNextForceMovement(new ForceMovement(
					new WorldTile(player), 1, toTile, 2, 	ditch.getRotation() == 0 || ditch.getRotation() == 2 ? ForceMovement.NORTH : ForceMovement.WEST));
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					player.setNextWorldTile(toTile);
					player.faceObject(ditch);
					player.getControlerManager().startControler("Wilderness");
					player.resetReceivedDamage();
				}
			}, 2);
		} else
			player.closeInterfaces();
		end();
	}

	@Override
	public void finish() {

	}

}
