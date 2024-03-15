package com.feather.game.player.content;

import javax.xml.stream.Location;

import com.feather.game.Graphics;
import com.feather.game.WorldTile;
import com.feather.game.player.Player;
import com.feather.game.tasks.WorldTask;
import com.feather.game.tasks.WorldTasksManager;
import com.feather.utils.Utils;

public final class FairyRing {

	public static final int[] UNUSED_COMPONENTS = {14, 16, 17, 18, 21, 22, 24, 28};

    public static void openFairyRing(Player p) {
        p.getInterfaceManager().sendInterface(734);
        p.getInterfaceManager().sendInventoryInterface(735);
        for (int i : UNUSED_COMPONENTS) {
            p.getPackets().sendIComponentText(735, i, "");
        }
        p.getPackets().sendIComponentText(735, 18 , " Asgarnia: Mudskipper Point");
        p.getPackets().sendIComponentText(735, 19 , " Dungeons: Dark cave south of Dorgesh-Kaan");
        p.getPackets().sendIComponentText(735, 20 , " Kandarin: Slayer cave south-east of Rellekka");
        p.getPackets().sendIComponentText(735, 23 , " Kandarin: Piscatoris Hunter area");
        p.getPackets().sendIComponentText(735, 25 , " Feldip Hills: Feldip Hunter area");
        p.getPackets().sendIComponentText(735, 26 , " Kandarin: Feldip Hills");
        p.getPackets().sendIComponentText(735, 27 , " Morytania: Haunted Woods east of Canifis");
        p.getPackets().sendIComponentText(735, 29 , " Kandarin: McGrubor's Wood");
        p.getPackets().sendIComponentText(735, 30 , " Islands: Polypore Dungeon");
        p.getPackets().sendIComponentText(735, 31 , " Kharidian Desert: Near Kalphite hive");
        for (int x = 32; x < 75; x++) {
        	p.getPackets().sendIComponentText(735, x , "");
        }
    }
    
        public static void ringTele (Player player , int firstColumn, int secondColumn, int thirdColumn) {
            final int a = 1;
            final int d = 2;
            final int k = 3;
            final int r = 3;
            final int j = 4;
            final int q = 4;
    		if (player.getTemporaryAttribute("teleblocked") != null) {
    			player.getPackets().sendGameMessage("You cannot use the fairyring when teleblocked!");
                        return;
                }

                /**
                 * START OF SEQUENCES
                 */
                if (player.firstColumn == a && player.secondColumn == j && player.thirdColumn == q) { //First,Second,Third stand for each number in the interface.
                    player.getInterfaceManager().sendInterface(578);
                        refresh(player);
                }
           else if (player.firstColumn == d && player.secondColumn == k && player.thirdColumn == r) {
   			            player.closeInterfaces();
                        fairyTeleport(player, 3129, 3496, 0);
                        refresh(player);
                        }
                }
                
                

        public static void warningInterface(Player player){
        	player.closeInterfaces();
            fairyTeleport(player, 2735, 5221, 0);
        }
			
    	public static void fairyTeleport(final Player p, final int x, final int y, final int z) {
    		long currentTime = Utils.currentTimeMillis();
    		if (p.getAttackedByDelay() + 10000 > currentTime) {
    			p.getPackets().sendGameMessage(
    					"You can't use the Fairy Magic until 10 seconds after the end of combat.");
    			return;
    		}
    		            p.stopAll();
                        refresh(p);
            			WorldTasksManager.schedule(new WorldTask() {
            				int loop;
            				@Override
            				public void run() {
            					if (loop == 0) {
    						        p.setNextGraphics(new Graphics(569));
            					} else if (loop == 2) {
    						        p.setNextWorldTile(new WorldTile(x, y, z));
                                    p.setNextGraphics(new Graphics(569));
                                	p.closeInterfaces();
                                    this.stop();
    						        stop();
            					stop();
            					}
            					loop++;
            			  	}
            }, 0, 1);
   }
                
        public static void refresh(Player player) {                
                player.firstColumn = 1;
                player.secondColumn = 1;
                player.thirdColumn = 1;         
        }
        public static boolean interactWithfairyRing(final Player p, Location loc) {
			WorldTasksManager.schedule(new WorldTask() {
    			public void run() {
					openFairyRing(p);    			}
    		});
    		return true;
    	}
        
        
 
}